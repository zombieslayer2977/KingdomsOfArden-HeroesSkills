package net.kingdomsofarden.andrew2060.heroes.skills;

import java.text.DecimalFormat;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;

public class SkillRighteousFury extends PassiveSkill implements Listener {

    public SkillRighteousFury(Heroes plugin) {
        super(plugin, "RighteousFury");
        setDescription("For every additional $1 in combat, $2 true damage (up to a maximum of $3) is added to the total damage every attack.");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public String getDescription(Hero hero) {
        DecimalFormat dF = new DecimalFormat("##.##");
        long seconds = SkillConfigManager.getUseSetting(hero, this, "tickSeconds", 5000, false);
        double damagePerTickSecond = SkillConfigManager.getUseSetting(hero, this, "damagePerTickSecond", Double.valueOf(1), false);
        double maxDamageAdd = SkillConfigManager.getUseSetting(hero, this, "maxDamageAdd", 10, false);
        return getDescription().replace("$1", seconds != 1000L ? dF.format(seconds/1000) + " seconds": "1 second")
                .replace("$2", dF.format(damagePerTickSecond))
                .replace("$3", dF.format(maxDamageAdd));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWeaponDamage(WeaponDamageEvent event) {
        if(!(event.getDamager() instanceof Hero)) {
            return;
        }
        if(!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        Hero h = (Hero) event.getDamager();
        long combatDuration = System.currentTimeMillis() - h.getCombatEffect().getApplyTime();
        long seconds = SkillConfigManager.getUseSetting(h, this, "tickSeconds", 5000, false);
        int secondsInCombat = (int) Math.floor(combatDuration/seconds);
        double damagePerTickSecond = SkillConfigManager.getUseSetting(h, this, "damagePerTickSecond", Double.valueOf(1), false);
        double maxDamageAdd = SkillConfigManager.getUseSetting(h, this, "maxDamageAdd", 10, false);
        double damageAdd = secondsInCombat*damagePerTickSecond;
        if(damageAdd > maxDamageAdd) {
            damageAdd = maxDamageAdd;
        }
        LivingEntity target = (LivingEntity) event.getEntity();
        addSpellTarget(target,h);
        Skill.damageEntity(target, h.getEntity(), damageAdd, DamageCause.CUSTOM);
    }
}
