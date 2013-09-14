package net.kingdomsofarden.andrew2060.heroes.skills;

import org.bukkit.entity.LivingEntity;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;

public class SkillPrey extends TargettedSkill {

    public SkillPrey(Heroes plugin) {
        super(plugin, "Prey");
        setDescription("Sets the target for summoned undead minions to attack.");
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        if(hero.hasEffect("NecromancyTargetManager")) {
            
        }
        return null;
    }

    @Override
    public String getDescription(Hero hero) {
        // TODO Auto-generated method stub
        return null;
    }
    

}
