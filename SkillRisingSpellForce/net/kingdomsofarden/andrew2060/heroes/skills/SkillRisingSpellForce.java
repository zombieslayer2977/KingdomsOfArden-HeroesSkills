package net.kingdomsofarden.andrew2060.heroes.skills;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.SkillUseEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;

public class SkillRisingSpellForce extends PassiveSkill implements Listener {

    private HashMap<CharacterTemplate,Long> spellCastTimes;
    public SkillRisingSpellForce(Heroes plugin) {
        super(plugin, "RisingSpellForce");
        setDescription("Passive: every successive spell hit within a $1 second period will "
                + "increase the damage of the next spell cast by $2% up to a maximum of $3%. "
                + "This bonus resets if no spell damage is dealt within the $1 seconds.");
        this.spellCastTimes = new HashMap<CharacterTemplate,Long>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    

    @Override
    public String getDescription(Hero hero) {
        DecimalFormat dF = new DecimalFormat("##.##");
        long expireTime = SkillConfigManager.getUseSetting(hero, this, "expireTime", 10000, true);
        double maxBonus = SkillConfigManager.getUseSetting(hero, this, "maxbonus", 0.5, true);
        double bonus = SkillConfigManager.getUseSetting(hero, this, "bonusperhit", 0.05, true);
        return getDescription().replace("$1", dF.format(expireTime*0.001)).replace("$2", dF.format(bonus*100))
                .replace("$3", dF.format(maxBonus * 100));
        
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR) 
    public void onSkillUse(SkillUseEvent event) {
        Hero h = event.getHero();
        if(!h.hasEffect("RisingSpellForce")) {
            return;
        }
        if(event.getSkill().getTypes().contains(SkillType.DAMAGING)) {
            spellCastTimes.put(h, System.currentTimeMillis());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR) 
    public void onSkillDamage(SkillDamageEvent event) {
        CharacterTemplate cT = event.getDamager();
        if(cT.hasEffect("RisingSpellForce")) {
            if(spellCastTimes.containsKey(cT)) {
                long expireTime = SkillConfigManager.getUseSetting((Hero)cT, this, "expireTime", 10000, true);
                long castTime = spellCastTimes.get(cT);
                if(System.currentTimeMillis() - castTime <= 10000) {    //Allow up to 10 seconds between Skill Use and Skill Hit
                    double maxBonus = SkillConfigManager.getUseSetting((Hero)cT, this, "maxbonus", 0.5, true);
                    double bonus = SkillConfigManager.getUseSetting((Hero)cT, this, "bonusperhit", 0.05, true);
                    if(cT.hasEffect("spellforceboost")) {
                        SpellForceEffect sfEffect = (SpellForceEffect) cT.getEffect("spellforceboost");
                        event.setDamage((1 + sfEffect.getDamageBoost()) * event.getDamage());
                        cT.addEffect(new SpellForceEffect(this, plugin, expireTime, sfEffect.getDamageBoost() + bonus > maxBonus ? maxBonus : sfEffect.getDamageBoost() + bonus));
                    } else {
                        cT.addEffect(new SpellForceEffect(this, plugin, expireTime, 0));
                    }
                } 
                spellCastTimes.remove(cT);
            } else {
                return; //Did not cast a tracked spell.
            }
        }
    }
    
    private class SpellForceEffect extends ExpirableEffect {

        private double damageBoost;

        public SpellForceEffect(Skill skill, Heroes plugin, long duration, double damageBoost) {
            super(skill, plugin, "spellforceboost", duration);
            this.damageBoost = damageBoost;
        }
        
        public double getDamageBoost() {
            return damageBoost;
        }
        
    }
}
