package net.kingdomsofarden.andrew2060.heroes.skills;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillType;

public class SkillArcaneFrost extends ActiveSkill {

    private HashMap<Hero,BukkitTask> activeDisplayTasks;
    public SkillArcaneFrost(Heroes plugin) {
        super(plugin, "ArcaneFrost");
        setDescription("Summon a blast of arcane energy to slow everyone in an area for 5 seconds. People still within the radius of the slow after the 5 seconds are rooted in place and dealt 50 magic damage.");
        setUsage("/skill arcanefrost");
        setArgumentRange(0,0);
        setIdentifiers("skill arcanefrost");
        setTypes(SkillType.SILENCABLE, SkillType.ICE, SkillType.DAMAGING);
        activeDisplayTasks = new HashMap<Hero,BukkitTask>();
    }

    @Override
    public SkillResult use(Hero h, String[] args) {
        if(h.hasEffect("PowerLocusEffect")) {
            if(activeDisplayTasks.containsKey(h)) {
                if(!applyEffects(h, true)) {
                    return SkillResult.INVALID_TARGET_NO_MSG;
                } else{
                    activeDisplayTasks.get(h).cancel();
                    activeDisplayTasks.remove(h);
                    return SkillResult.NORMAL;
                }
            } else {
                activeDisplayTasks.put(h, new ArcaneFrostTargettingSchedulerTask(h).runTaskTimer(plugin, 0, 20));
                h.getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "Skill" + ChatColor.GRAY + "] "
                        + "Activated targetting mode for arcane frost: you have 10 seconds to make a selection"
                        + "before targetting is cancelled.");
                return SkillResult.INVALID_TARGET_NO_MSG;
            }
        } else {
            if(activeDisplayTasks.containsKey(h)) {
                activeDisplayTasks.get(h).cancel();
                activeDisplayTasks.remove(h);
            }
            applyEffects(h, false);
        }
        return SkillResult.NORMAL;
    }

    private boolean applyEffects(Hero h, Boolean ranged) {
        Location loc = null;
        if(ranged) {
            List<Block> los = h.getPlayer().getLastTwoTargetBlocks(null, 100);
            loc = los.get(los.size()-1).getLocation();
            if(los.get(los.size()-1).getType() == Material.AIR) {
                return false;
            }
        } else {
            loc = h.getPlayer().getLocation();
        }
        Arrow a = loc.getWorld().spawn(loc, Arrow.class);
        List<Location> affected = calculateAffectedArea(loc, loc.getWorld());
        for(Entity e : a.getNearbyEntities(6, 3, 6)) {
            if(!(e instanceof LivingEntity)) {
                continue;
            } else {
                LivingEntity lE = (LivingEntity) e;
                if(Skill.damageCheck(h.getPlayer(), lE)) {
                    ToolHandlerPlugin.instance.getPotionEffectHandler().addPotionEffectStacking(PotionEffectType.SLOW.createEffect(100, 1), lE, false);
                }
            }
        }
        a.remove();
        for(Player p : plugin.getServer().getOnlinePlayers()) {
            if(p.getLocation().getWorld() == loc.getWorld() && p.getLocation().distanceSquared(loc) <= 10000) {
                new ArcaneFrostDisplayTask(p,affected).runTaskLater(plugin, 100);
            }
            continue;
        }
        return true;
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
    public class ArcaneFrostDisplayTask extends BukkitRunnable {
        private Player player;
        private List<Location> toDisplay;

        public ArcaneFrostDisplayTask(Player p, List<Location> toDisplay) {
            this.player = p;
            this.toDisplay = toDisplay;
            for(Location loc : toDisplay) {
                p.sendBlockChange(loc, Material.SNOW_BLOCK, (byte)0);
            }
        }

        @Override
        public void run() {
            if(player.isOnline()) {
                for(Location loc : toDisplay) {
                    player.sendBlockChange(loc, loc.getWorld().getBlockTypeIdAt(loc), (byte)0);
                }           
            }

        }
    }
    public class ArcaneFrostRootTask extends BukkitRunnable {
        private Location loc;
        private Hero hero;
        public ArcaneFrostRootTask(Hero h, Location center) {
            this.loc = center;
            this.hero = h;
        }
        @Override
        public void run() {
            Arrow a = loc.getWorld().spawn(loc, Arrow.class);
            for(Entity e : a.getNearbyEntities(6, 3, 6)) {
                if(!(e instanceof LivingEntity)) {
                    continue;
                } else {
                    LivingEntity lE = (LivingEntity) e;
                    if(Skill.damageCheck(hero.getPlayer(), lE)) {
                        ToolHandlerPlugin.instance.getPotionEffectHandler().addPotionEffectStacking(PotionEffectType.SLOW.createEffect(100, 100), lE, false);
                        ToolHandlerPlugin.instance.getPotionEffectHandler().addPotionEffectStacking(PotionEffectType.JUMP.createEffect(100, -100), lE, false);
                        if(lE instanceof Player) {
                            ((Player)lE).sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "Skill" + ChatColor.GRAY + "] "
                                    + "Rooted by arcane frost!");
                        }
                        Skill.damageEntity(lE, hero.getPlayer(), 50D, DamageCause.MAGIC);
                    }
                }
            }
            a.remove();    
        }

    }

    public class ArcaneFrostTargettingSchedulerTask extends BukkitRunnable {

        private Hero h;
        private int iterations;
        public ArcaneFrostTargettingSchedulerTask(Hero h) {
            this.h = h;
            this.iterations = 0;
        }
        @Override
        public void run() {
            if(h.getPlayer().isOnline()) {
                if(iterations >= 10) {
                    this.cancel();
                    if(activeDisplayTasks.containsKey(h)) {
                        activeDisplayTasks.remove(h);
                        h.getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "Skill" + ChatColor.GRAY + "] "
                                + "Targetting Mode for Arcane Frost Expired.");
                    }
                } else {
                    new ArcaneFrostTargettingDisplayTask(this.h).runTaskLater(plugin, 19L);
                    iterations++;
                    return;
                }
            } else {
                this.cancel();
                if(activeDisplayTasks.containsKey(h)) {
                    activeDisplayTasks.remove(h);
                }
            }
        }

    }
    public class ArcaneFrostTargettingDisplayTask extends BukkitRunnable {
        private Hero h;
        private World world;
        private boolean outOfRange;
        private List<Location> display;
        public ArcaneFrostTargettingDisplayTask(Hero user) {
            outOfRange = false;
            h = user;
            List<Block> los = h.getPlayer().getLastTwoTargetBlocks(null, 100);
            Location center = los.get(los.size()-1).getLocation();
            if(los.get(los.size()-1).getType() == Material.AIR) {
                this.outOfRange = true;
                return;
            }
            world = center.getWorld();
            display = calculateAffectedArea(center, world);
            Player p = h.getPlayer();
            for(Location loc : display) {
                p.sendBlockChange(loc, Material.GLOWSTONE, (byte)0);
            }
        }

        @Override
        public void run() {
            if(outOfRange) {
                return;
            }
            Player p = h.getPlayer();
            if(p.isOnline()) {
                for(Location loc : display) {
                    p.sendBlockChange(loc, world.getBlockAt(loc).getType(), world.getBlockAt(loc).getData());
                }
            }
        }

    }
    public List<Location> calculateAffectedArea(Location center, World world) {
        //Construct a square centered around that location
        List<Location> locations = new LinkedList<Location>();
        int upperLeftX = center.getBlockX()-6;
        int upperLeftZ = center.getBlockZ()-6;
        int lowerRightX = center.getBlockX()+6;
        int lowerRightZ = center.getBlockZ()+6;
        int centerY = center.getBlockY(); 
        for(int x = upperLeftX; x <= lowerRightX; x++) {
            for(int z = upperLeftZ; x <= lowerRightZ; z++) {
                Location constructLoc = world.getHighestBlockAt(x,z).getLocation();
                if(constructLoc.getBlockY() - centerY <= 3) {
                    locations.add(constructLoc);
                }
            }
        }
        return locations;
    }
}
