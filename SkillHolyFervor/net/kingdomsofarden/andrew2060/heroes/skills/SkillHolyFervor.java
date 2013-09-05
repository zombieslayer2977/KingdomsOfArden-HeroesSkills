package net.kingdomsofarden.andrew2060.heroes.skills;

import java.text.DecimalFormat;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.PeriodicHealEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillSetting;


public class SkillHolyFervor extends ActiveSkill {

    public SkillHolyFervor(Heroes plugin) {
        super(plugin, "HolyFervor");
        setDescription("Heals self for $1% of missing health over $2 seconds.");
    }

    @Override
    public String getDescription(Hero hero) {
        DecimalFormat dF = new DecimalFormat("##.##");
        double amount = SkillConfigManager.getUseSetting(hero, this, SkillSetting.AMOUNT, 0.5, false);
        long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 10000, false);
        return getDescription().replace("$1", dF.format(amount * 100)).replace("$2", dF.format(duration  * 0.001));
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        double amount = SkillConfigManager.getUseSetting(hero, this, SkillSetting.AMOUNT, 0.5, false);
        long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 10000, false);
        double missingHealth = hero.getPlayer().getMaxHealth() - hero.getPlayer().getHealth();
        double totalHealed = missingHealth * amount;
        double healTick = totalHealed / (duration * 0.001);
        PeriodicHealEffect effect = new PeriodicHealEffect(plugin, "HolyFervorEffect", 1000, duration, healTick, hero.getPlayer());
        effect.types.add(EffectType.DISPELLABLE);
        hero.addEffect(effect);
        return SkillResult.NORMAL;
    }

}
