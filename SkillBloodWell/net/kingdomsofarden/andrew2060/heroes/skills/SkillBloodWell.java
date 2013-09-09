package net.kingdomsofarden.andrew2060.heroes.skills;

import java.text.DecimalFormat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.HeroRegainHealthEvent;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.SkillUseEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillSetting;

public class SkillBloodWell extends PassiveSkill implements Listener {

    public SkillBloodWell(Heroes plugin) {
        super(plugin, "BloodWell");
        setDescription("Abilities cost $1 health to use. Successful spell damage will return $2 health");
    }

    @Override
    public String getDescription(Hero hero) {
        DecimalFormat dF = new DecimalFormat("##.##");
        double cost = SkillConfigManager.getUseSetting(hero,this,SkillSetting.HEALTH_COST,20,false);
        double heal = SkillConfigManager.getUseSetting(hero,this,SkillSetting.HEALTH,40,false);
        return getDescription().replace("$1", dF.format(cost)).replace("$2",dF.format(heal));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onSkillUse(SkillUseEvent event) {
        if(event.getHero().hasEffect("BloodWell")) {
            Hero h = event.getHero();
            if(event.getHealthCost() == 0) {
                double cost = SkillConfigManager.getUseSetting(h,this,SkillSetting.HEALTH_COST,20,false);
                event.setHealthCost((int) (event.getHealthCost() + cost));
            } else {
                return;
            }
        }
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onSkillDamage(SkillDamageEvent event) {
        if(!(event.getDamager() instanceof Hero)) {
            return;
        }
        if(event.getDamager().hasEffect("BloodWell")) {
            Hero h = (Hero) event.getDamager();
            double cost = SkillConfigManager.getUseSetting(h,event.getSkill(),SkillSetting.HEALTH_COST,0.00D,false);
            double heal = SkillConfigManager.getUseSetting(h,this,SkillSetting.HEALTH,40,false);
            if(cost != 0.00D) {
                heal = cost*2;
            }
            HeroRegainHealthEvent hEvent = new HeroRegainHealthEvent(h, heal, this);
            plugin.getServer().getPluginManager().callEvent(hEvent);
            if(!hEvent.isCancelled()) {
                h.heal(heal);
            }
        }
    }

}
