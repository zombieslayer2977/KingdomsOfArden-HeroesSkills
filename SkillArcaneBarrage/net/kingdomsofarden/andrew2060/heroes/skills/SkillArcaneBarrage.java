package net.kingdomsofarden.andrew2060.heroes.skills;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillArcaneBarrage extends ActiveSkill{

    ConcurrentHashMap<Hero,BarrageData> skillData;
    public SkillArcaneBarrage(Heroes plugin) {
        super(plugin, "ArcaneBarrage");
        setIdentifiers("skill arcanebarrage");
        setUsage("/skill arcanebarrage");
        setDescription("Fires a linear barrage (16 blocks) of arcane artillery. "
                + "While in power locus, the user can define start point and direction. "
                + "While outside of power locus, user can define direction with start point being the user's location. "
                + "Artillery strikes also apply a 40% 5 second slow.");
        this.skillData = new ConcurrentHashMap<Hero,BarrageData>();
    }
    private class BarrageData {
        private Location origin;
        private BukkitTask removalTask;
        private BukkitTask guiTask;

        public BarrageData(Location origin,BukkitTask removalTask, BukkitTask guiTask) {
            this.origin = origin;
            this.removalTask = removalTask;
            this.guiTask = guiTask;
        }
        private Location getOrigin() {
            return origin;
        }
        private void cancelTask() {
            removalTask.cancel();
            guiTask.cancel();
        }
    }
    private boolean fire(Location origin, Location vector, Hero hero) {
        if(!origin.getWorld().equals(vector.getWorld())) {
            return false;
        }
       
        for(int i = 0; i < 16; i++) {
            for(Location loc: getAffectedLocations(origin,vector, false)) {
                new StrikeTask(loc, hero).runTaskLater(plugin, i*5);
            }
        }
        return true;
    }
    private class StrikeTask extends BukkitRunnable {
        private Location hitLoc;
        private Hero hero;

        public StrikeTask(Location loc, Hero h) {
            this.hitLoc = loc;
            this.hero = h;
        }

        @Override
        public void run() {
            hitLoc.getWorld().strikeLightningEffect(hitLoc);
            applyDamage(hitLoc, hero);
        }
    }
    private void applyDamage(Location origin, Hero hero) {
        Arrow a = origin.getWorld().spawn(origin, Arrow.class);
        for(Entity e : a.getNearbyEntities(3, 3, 3)) {
            if(!(e instanceof LivingEntity)) {
                continue;
            }
            LivingEntity lE = (LivingEntity)e;
            if(!Skill.damageCheck(hero.getPlayer(), lE)) {
                continue;
            } else {
                CharacterTemplate cT = plugin.getCharacterManager().getCharacter(lE);
                if(cT.hasEffect("ArcaneBarrageCooldown")) {
                    continue;
                }
                addSpellTarget(lE,hero);
                Skill.damageEntity(lE, hero.getPlayer(), 60.00, DamageCause.MAGIC);
                ToolHandlerPlugin.instance.getPotionEffectHandler().addPotionEffectStacking(PotionEffectType.SLOW.createEffect(100, 2), lE, false);
                cT.addEffect(new ExpirableEffect(this, plugin, "ArcaneBarrageCooldown", 5000));
            }
        }
        a.remove();
    }
    @Override
    public SkillResult use(final Hero h, String[] args) {
        if(skillData.containsKey(h)) {
            List<Block> los = h.getPlayer().getLastTwoTargetBlocks(null, 100);
            final Location loc = los.get(los.size()-1).getLocation();
            final BarrageData data = skillData.get(h);
            fire(data.getOrigin(),loc,h);
            data.cancelTask();
            skillData.remove(h);
            return SkillResult.NORMAL;
        } else {
            List<Block> los;
            if(h.hasEffect("PowerLocusEffect")) {
                los = h.getPlayer().getLastTwoTargetBlocks(null, 100);
                BarrageData data = new BarrageData(los.get(los.size()-1).getLocation(), new CancellationTask(h).runTaskLater(plugin,400), new DisplayStrikeTask(h).runTaskTimer(plugin, 0, 20));
                skillData.put(h, data);
            } else {
                los = h.getPlayer().getLastTwoTargetBlocks(null, 16);
                fire(h.getPlayer().getLocation(),los.get(los.size()-1).getLocation(),h);
                return SkillResult.NORMAL;
            }
            return SkillResult.INVALID_TARGET_NO_MSG;
        }
    }
    private class CancellationTask extends BukkitRunnable {

        private Hero hero;
        public CancellationTask(Hero h) {
            this.hero = h;
        }
        @Override
        public void run() {
            if(skillData.containsKey(hero)) {
                hero.getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "Skill" + ChatColor.GRAY + "] Arcane Barrage Selection Cancelled");
                skillData.get(hero).guiTask.cancel();
                skillData.remove(hero);
            }
        }
        
    }
    private class DisplayStrikeTask extends BukkitRunnable {

        private Hero hero;
        public DisplayStrikeTask(Hero h) {
            this.hero = h;
        }
        @Override
        public void run() {
            if(skillData.containsKey(hero)) {
                Location origin = skillData.get(hero).getOrigin().clone().add(0,1,0);
                origin = origin.getWorld().getHighestBlockAt(origin).getLocation();
                List<Block> los = hero.getPlayer().getLastTwoTargetBlocks(null, 100);
                Location vector = los.get(los.size()-1).getLocation();
                if(!origin.getWorld().equals(vector.getWorld())) {
                    try {
                        this.cancel();
                        hero.getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "Skill" + ChatColor.GRAY + "] Arcane Barrage Selection Cancelled due to Changing Worlds");
                        
                    } catch (IllegalStateException e) {
                        return;
                    }
                    return;
                }
                final Player p = hero.getPlayer();
                for(final Location l : getAffectedLocations(origin,vector,true)) {
                    p.sendBlockChange(l, Material.GLOWSTONE.getId(), (byte) 0);
                    Bukkit.getScheduler().runTaskLater(plugin, new BukkitRunnable() {

                        @Override
                        public void run() {
                            Block replace = l.getWorld().getBlockAt(l);
                            p.sendBlockChange(l, replace.getTypeId(), replace.getData());
                        }
                        
                    }, 19);
                }
            }
        }
        
    }
    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
    private List<Location> getAffectedLocations(Location origin, Location vector, boolean validBlockCheck) {
        Vector multiplier = vector.toVector().subtract(origin.toVector()).setY(0.00).normalize();
        List<Location> toDisplay = new LinkedList<Location>();
        int originY = origin.getBlockY();
        for(int i = 0; i < 16; i++) {
            Location blockLocation = origin.clone();
            for(int downwardsY = originY + 3; downwardsY >= originY - 3; downwardsY--) {
                blockLocation.setY(downwardsY);
                if(blockLocation.getBlock().getType() == Material.AIR) {
                    continue;
                } else {
                    break;
                }
            }
            if(!validBlockCheck ||(blockLocation.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR && blockLocation.getBlock().getType().isSolid())) {
                if(!toDisplay.contains(blockLocation)) {
                    toDisplay.add(blockLocation);
                }
            }
            origin.add(multiplier);
        }
        return toDisplay;
    }
}
