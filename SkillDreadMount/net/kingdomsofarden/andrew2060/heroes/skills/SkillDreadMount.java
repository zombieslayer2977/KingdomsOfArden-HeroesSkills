package net.kingdomsofarden.andrew2060.heroes.skills;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.HorseInventory;
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
        setDescription("Summons an undead mount for $1 seconds that tramples nearby enemies");
        setIdentifiers("skill dreadmount");
        setUsage("/skill dreadmount");
        setArgumentRange(0,0);
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        Location loc = hero.getPlayer().getLocation();
        Horse horse = loc.getWorld().spawn(loc, Horse.class);
        Monster m = plugin.getCharacterManager().getMonster(horse);
        m.setMaxHealth(1000D);
        m.addEffect(new DreadMountEffect(this.plugin));
        horse.setStyle(Style.BLACK_DOTS);
        HorseInventory hInv = horse.getInventory();
        hInv.setArmor(new ItemStack(Material.DIAMOND_BARDING));
        hInv.setSaddle(new ItemStack(Material.SADDLE));
        horse.setTamed(true);
        horse.setVariant(Variant.UNDEAD_HORSE);
        horse.setPassenger(hero.getPlayer());      
        
        int summonDuration = SkillConfigManager.getUseSetting(hero, this, "summon-duration", Integer.valueOf(600),false);
        m.addEffect(new ExpirableEffect(this, plugin, "HorseExpiry", summonDuration*1000) {
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
