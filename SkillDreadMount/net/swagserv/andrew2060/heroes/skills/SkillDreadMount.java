package net.swagserv.andrew2060.heroes.skills;

import java.util.logging.Level;

import net.minecraft.server.EntityHorse;
import net.minecraft.server.World;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.Monster;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.effects.PeriodicEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;

public class SkillDreadMount extends ActiveSkill {

    public SkillDreadMount(Heroes plugin) {
        super(plugin, "DreadMount");
        try {   //Attempt creating a new NMS Horse instance, disable and remove skill if an exception occurs
            new NMSHorse(SkillDreadMountEntityHorse.class, new Location(Bukkit.getWorlds().get(0), 0, 0, 0, 0, 0));
        } catch (Exception e) {
            Heroes.log(Level.SEVERE, "SkillDreadMount: failed to start: the NMS Horse Utility is out of date!!");
            e.printStackTrace();
            final Skill skillInstance = this;
            //Run this on a delay to allow for skill to show up in skill manager first
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

                @Override
                public void run() {
                    ((Heroes) Bukkit.getPluginManager().getPlugin("Heroes")).getSkillManager().removeSkill(skillInstance);
                    
                }
                
            }, 20L);
            return;
        }
        setDescription("Summons an undead mount for $1 seconds that tramples nearby enemies");
        setIdentifiers("skill dreadmount");
        setUsage("/skill dreadmount");
        setArgumentRange(0,0);
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        Location loc = hero.getPlayer().getLocation();
        NMSHorse nmsHorse = new NMSHorse(SkillDreadMountEntityHorse.class,loc,SpawnReason.CUSTOM);
        nmsHorse.setTamed(true);
        nmsHorse.setSaddled(true);
        nmsHorse.setArmorItem(new ItemStack(Material.DIAMOND_BARDING));
        LivingEntity horse = nmsHorse.getBukkitEntity();
        Monster m = plugin.getCharacterManager().getMonster(horse);
        m.setMaxHealth(1000D);
        m.addEffect(new DreadMountEffect(this.plugin));
        horse.setPassenger(hero.getPlayer());      
        int summonDuration = SkillConfigManager.getUseSetting(hero, this, "summon-duration", Integer.valueOf(600),false);
        m.addEffect(new ExpirableEffect(this, plugin, "HorseExpiry", summonDuration*20) {
            @Override
            public void removeFromMonster(Monster m) {
                m.getEntity().remove();
            }
        });
        return SkillResult.NORMAL;
    }
    private class DreadMountEffect extends PeriodicEffect {

        public DreadMountEffect(Heroes plugin) {
            super(plugin, "DreadMountEffect", 1000);
        }
        
        @Override
        public void tickMonster(Monster m) {
            Entity passengerEntity = m.getEntity().getPassenger();
            if(passengerEntity == null) {
                return;
            }
            if(!(passengerEntity instanceof Player)) {
                return;
            }
            Player passenger = (Player) m.getEntity().getPassenger();
            for(Entity e: m.getEntity().getNearbyEntities(2, 2, 2)) {
                if(!(e instanceof LivingEntity)) {
                    continue;
                }
                LivingEntity lE = (LivingEntity)e;
                if(Skill.damageCheck(passenger, lE)) {
                    Skill.damageEntity(lE, passenger, 5, DamageCause.ENTITY_ATTACK, false);
                }
            }
        }
    }
    @Override
    public String getDescription(Hero hero) {
        int summonDuration = SkillConfigManager.getUseSetting(hero, this, "summon-duration", Integer.valueOf(600),false);
        return getDescription().replace("$1", summonDuration + "");
    }
    
    
}
