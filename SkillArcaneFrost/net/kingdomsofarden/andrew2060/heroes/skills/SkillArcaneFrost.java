package net.kingdomsofarden.andrew2060.heroes.skills;

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
import org.bukkit.util.Vector;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.Monster;
import com.herocraftonline.heroes.characters.effects.PeriodicExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillType;

public class SkillArcaneFrost extends ActiveSkill {

    public SkillArcaneFrost(Heroes plugin) {
        super(plugin, "ArcaneFrost");
        setDescription("Summon a blast of arcane energy to slow everyone in an area for 5 seconds. Every second, the power of the slow increases. People still within the radius of the slow after the 5 seconds are rooted in place and dealt 50 magic damage.");
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
                CharacterTemplate cT = plugin.getCharacterManager().getCharacter(lE);
                cT.addEffect(new ArcaneFrostEffect(this,plugin,loc,h));
            }
        }
        a.remove();
        return true;
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
    public class ArcaneFrostEffect extends PeriodicExpirableEffect {
        private Location center;
        private Location upone;
        private Location uptwo;
        private int iterations;
        private Hero applier;
        public ArcaneFrostEffect(Skill skill, Heroes plugin, Location center, Hero applier) {
            super(skill, plugin, "ArcaneFrostEffect" , 1000L, 5000L);
            this.center = center;
            this.upone = center.clone().add(0,1,0);
            this.uptwo = center.clone().add(0,2,0);
            this.iterations = 0;
            this.applier = applier;
        }

        @Override
        public void applyToHero(Hero hero) {
            super.applyToHero(hero);
            Player p = hero.getPlayer();
            p.sendBlockChange(center, Material.SOUL_SAND.getId(), (byte)0);
            p.sendBlockChange(upone, Material.SOUL_SAND.getId(), (byte)0);
            p.sendBlockChange(uptwo, Material.SOUL_SAND.getId(), (byte)0);
        }
        @Override
        public void tickMonster(Monster monster) {
            if(iterations > 5) {
                root(monster.getEntity());
                monster.removeEffect(this);
                return;
            }
            Vector v = monster.getEntity().getLocation().toVector().subtract(center.toVector());
            v.setY(0);
            if(v.lengthSquared() >= 100) {
                monster.removeEffect(this);
            } else {
                ToolHandlerPlugin.instance.getPotionEffectHandler().addPotionEffectStacking(PotionEffectType.SLOW.createEffect(20, iterations), monster.getEntity(), false);
            }
        }
        
        @Override
        public void removeFromHero(Hero hero) {
            super.removeFromHero(hero);
            World world = center.getWorld();
            Player p = hero.getPlayer();
            p.sendBlockChange(center, world.getBlockTypeIdAt(center), center.getBlock().getData());
            p.sendBlockChange(upone, world.getBlockTypeIdAt(upone), upone.getBlock().getData());
            p.sendBlockChange(uptwo, world.getBlockTypeIdAt(uptwo), uptwo.getBlock().getData());
        }

        @Override
        public void tickHero(Hero hero) {
            if(iterations > 5) {
                root(hero.getEntity());
                hero.removeEffect(this);
                return;
            }
            Vector v = hero.getPlayer().getLocation().toVector().subtract(center.toVector());
            v.setY(0);
            if(v.lengthSquared() >= 100) {
                hero.removeEffect(this);
            } else {
                ToolHandlerPlugin.instance.getPotionEffectHandler().addPotionEffectStacking(PotionEffectType.SLOW.createEffect(20, iterations), hero.getEntity(), false);
            }
            
        }
        private void root(LivingEntity victim) {
            addSpellTarget(victim,applier);
            Skill.damageEntity(victim, applier.getEntity(), 60.00, DamageCause.MAGIC);
            ToolHandlerPlugin.instance.getPotionEffectHandler().addPotionEffectStacking(PotionEffectType.JUMP.createEffect(100, -100), victim, false);
            ToolHandlerPlugin.instance.getPotionEffectHandler().addPotionEffectStacking(PotionEffectType.SLOW.createEffect(100, 100), victim, false);
            if(victim instanceof Player) {
                ((Player)victim).sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "Skill" + ChatColor.GRAY + "] "
                        + "Rooted by arcane frost!");
            }
            
        }
        
    }

}
