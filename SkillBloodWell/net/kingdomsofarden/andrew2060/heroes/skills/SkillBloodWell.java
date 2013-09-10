package net.kingdomsofarden.andrew2060.heroes.skills;

import java.text.DecimalFormat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.HeroRegainHealthEvent;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillSetting;

public class SkillBloodWell extends PassiveSkill implements Listener {

    public SkillBloodWell(Heroes plugin) {
        super(plugin, "BloodWell");
        setDescription("Abilities cost health on cast. Successful spell damage will return health equal to $1% of spell damage dealt");
    }

    @Override
    public String getDescription(Hero hero) {
        DecimalFormat dF = new DecimalFormat("##.##");
        double heal = SkillConfigManager.getUseSetting(hero,this,SkillSetting.HEALTH,0.5,false);
        return getDescription().replace("$1", dF.format(heal*0.5));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSkillDamage(SkillDamageEvent event) {
        if(!(event.getDamager() instanceof Hero)) {
            return;
        }
        if(event.getDamager().hasEffect("BloodWell")) {
            Hero h = (Hero) event.getDamager();
            double heal = SkillConfigManager.getUseSetting(h,this,SkillSetting.HEALTH,0.5,false);
            heal *= event.getDamage();
            HeroRegainHealthEvent hEvent = new HeroRegainHealthEvent(h, heal, this);
            plugin.getServer().getPluginManager().callEvent(hEvent);
            if(!hEvent.isCancelled()) {
                h.heal(heal);
            }
        }
    }

}
