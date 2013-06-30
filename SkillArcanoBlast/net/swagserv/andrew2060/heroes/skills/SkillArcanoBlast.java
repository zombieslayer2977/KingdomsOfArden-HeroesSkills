package net.swagserv.andrew2060.heroes.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillArcanoBlast extends ActiveSkill implements Listener {
    private HashMap<WitherSkull,Long> trackedSkulls;
    public SkillArcanoBlast(Heroes plugin) {
        super(plugin, "ArcanoBlast");
        setDescription("On use, calls down magical artillery that strikes target location after 1.5 seconds");
        setIdentifiers("skill arcanoblast");
        setUsage("/skill arcanoblast");
        this.trackedSkulls = new HashMap<WitherSkull,Long>();
        //Auto-Despawn skulls after 30 seconds
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable(){

            @Override
            public void run() {
                Set<WitherSkull> keySet = trackedSkulls.keySet();
                for(WitherSkull w : keySet) {
                    long time = trackedSkulls.get(w);
                    if(time<(System.currentTimeMillis()-30000)) {
                        trackedSkulls.remove(w);
                        if(w.isValid()) {
                            w.remove();
                        }
                    }
                }
            }

        }, 1200, 100);
        //Register Events
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public SkillResult use(final Hero h, String[] args) {
        List<Block> los = null;
        boolean inPowerLocus = h.hasEffect("PowerLocusEffect");
        if(inPowerLocus) {
            los = h.getPlayer().getLastTwoTargetBlocks(null, 100);
        } else {
            los = h.getPlayer().getLastTwoTargetBlocks(null, 10);
        }
        final Location loc = los.get(los.size()-1).getLocation();
        List<Location> circleLoc = circle(h.getPlayer(), loc, 2, 1, false, false, 1);
        for(Location effectLoc : circleLoc) {
            effectLoc.getWorld().playEffect(effectLoc, Effect.MOBSPAWNER_FLAMES, 1);
        }
        Location spawnLoc = los.get(los.size()-1).getLocation().add(0,10,0);
        loc.getWorld().playSound(spawnLoc, Sound.ENDERMAN_TELEPORT, 1, 1);
        spawnLoc.getWorld().playEffect(spawnLoc, Effect.ENDER_SIGNAL, 1);
        List<Location> circleEnderLoc = circle(h.getPlayer(), loc, 2, 1, false, false, 0);
        for(Location effectLoc : circleEnderLoc) {
            effectLoc.getWorld().playEffect(effectLoc, Effect.ENDER_SIGNAL, 1);
        }
        WitherSkull skull = h.getPlayer().launchProjectile(WitherSkull.class);
        skull.setShooter(h.getEntity());
        skull.teleport(spawnLoc);
        Vector v = loc.subtract(spawnLoc).toVector().normalize().multiply(1.0);
        skull.setVelocity(v);
        trackedSkulls.put(skull, System.currentTimeMillis());
        broadcast(h.getEntity().getLocation(),"§7[§2Skill§7] $1 used ArcanoBlast!", new Object[] {h.getPlayer().getName()});
        return SkillResult.NORMAL;
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity e = event.getEntity();
        if(!(e instanceof WitherSkull)) {
            return;
        }
        WitherSkull w = (WitherSkull)e;
        if(!trackedSkulls.containsKey(w)) {
            return;
        }
        trackedSkulls.remove(w);
        w.setYield(0);
        Location loc = w.getLocation();
        Hero h = plugin.getCharacterManager().getHero((Player) w.getShooter());
        Arrow a = loc.getWorld().spawnArrow(loc,new Vector(0,0,0), 0.6f, 1.6f);
        Iterator<Entity> near = a.getNearbyEntities(3, 3, 3).iterator();
        boolean inPowerLocus = h.hasEffect("PowerLocusEffect");
        while(near.hasNext()) {
            Entity next = near.next();
            if(!(next instanceof LivingEntity)) {
                continue;
            }
            LivingEntity lE = (LivingEntity)next;
            if(!Skill.damageCheck(h.getPlayer(), lE)) {
                continue;
            } 
            this.addSpellTarget(lE, h);
            
            int dmg = 75;
            if(inPowerLocus) {
                dmg = 40;
            }
            Skill.damageEntity(lE, h.getEntity(), dmg, DamageCause.MAGIC, false);
        }
        a.remove();
    }
    @Override
    public String getDescription(Hero arg0) {
        return getDescription();
    }
    private List<Location> circle(Player player, Location loc, Integer r, Integer h, boolean hollow, boolean sphere, int plus_y) {
        List<Location> circleblocks = new ArrayList<Location>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        for (int x = cx - r; x <= cx +r; x++)
            for (int z = cz - r; z <= cz +r; z++)
                for (int y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r*r && !(hollow && dist < (r-1)*(r-1))) {
                        Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }

        return circleblocks;
    }
}
