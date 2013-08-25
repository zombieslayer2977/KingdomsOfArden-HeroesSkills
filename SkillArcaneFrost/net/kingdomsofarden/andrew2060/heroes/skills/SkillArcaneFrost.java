package net.kingdomsofarden.andrew2060.heroes.skills;

import java.util.List;

import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillType;

public class SkillArcaneFrost extends ActiveSkill {

    public SkillArcaneFrost(Heroes plugin) {
        super(plugin, "ArcaneFrost");
        setDescription("Summon a blast of arcane energy to slow everyone in an area for 5 seconds. People still within the radius of the slow after the 5 seconds are rooted in place and dealt 50 magic damage.");
        setUsage("/skill arcanefrost");
        setArgumentRange(0,0);
        setIdentifiers("skill arcanefrost");
        setTypes(SkillType.SILENCABLE, SkillType.DAMAGING);
    }

    @Override
    public SkillResult use(Hero h, String[] args) {
        if(h.hasEffect("PowerLocusEffect")) {
            applyEffects(h,true);
        } else {
            applyEffects(h,false);
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
                        addSpellTarget(lE, hero);
                        Skill.damageEntity(lE, hero.getPlayer(), 50D, DamageCause.MAGIC);
                    }
                }
            }
            a.remove();    
        }

    }

}
