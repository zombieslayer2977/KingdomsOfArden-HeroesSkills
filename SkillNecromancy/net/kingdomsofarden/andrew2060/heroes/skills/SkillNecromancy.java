package net.kingdomsofarden.andrew2060.heroes.skills;

import java.text.DecimalFormat;

import net.kingdomsofarden.andrew2060.heroes.skills.api.necromancy.NecromancyMobManager;
import net.kingdomsofarden.andrew2060.heroes.skills.api.necromancy.NecromancyTargetManager;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillSetting;

public class SkillNecromancy extends PassiveSkill {

    public SkillNecromancy(Heroes plugin) {
        super(plugin, "Necromancy");
        setDescription("Can control summoned mobs. Summoned mobs last for $1 seconds.");
    }

    @Override
    public String getDescription(Hero hero) {
        DecimalFormat dF = new DecimalFormat("##.##");
        double duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 30000, false);
        double durationIncrease = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION_INCREASE, 5000, false);
        return getDescription().replace("$1", dF.format((duration + hero.getLevel() * durationIncrease) * 0.001));
    }

    @Override
    public void apply(Hero hero) {
        NecromancyMobManager mobMan = new NecromancyMobManager(plugin,hero);
        hero.addEffect(mobMan);
        hero.addEffect(new NecromancyTargetManager(this,mobMan));
    }

    @Override
    public void unapply(Hero hero) {
        if(hero.hasEffect("NecromancyMobManager")) {
            hero.removeEffect(hero.getEffect("NecromancyMobManager"));
        }
        if(hero.hasEffect("NecromancyTargetManager")) {
            hero.removeEffect(hero.getEffect("NecromancyTargetManager"));
        }

    }

}
