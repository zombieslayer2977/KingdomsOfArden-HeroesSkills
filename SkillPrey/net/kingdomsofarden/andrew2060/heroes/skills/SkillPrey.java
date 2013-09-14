package net.kingdomsofarden.andrew2060.heroes.skills;

import net.kingdomsofarden.andrew2060.heroes.skills.api.necromancy.NecromancyTargetManager;

import org.bukkit.entity.LivingEntity;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;

public class SkillPrey extends TargettedSkill {

    public SkillPrey(Heroes plugin) {
        super(plugin, "Prey");
        setDescription("Sets the target for summoned undead minions to attack.");
        setTypes(SkillType.SILENCABLE, SkillType.HARMFUL, SkillType.DAMAGING, SkillType.DARK);
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        if(hero.hasEffect("NecromancyTargetManager")) {
            NecromancyTargetManager targetManager = (NecromancyTargetManager) hero.getEffect("NecromancyTargetManager");
            targetManager.setTarget(target);
            return SkillResult.NORMAL;
        }
        hero.getPlayer().sendMessage("Internal Necromancy Error! Please Report to Andrew2060");
        return SkillResult.INVALID_TARGET_NO_MSG;
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
    

}
