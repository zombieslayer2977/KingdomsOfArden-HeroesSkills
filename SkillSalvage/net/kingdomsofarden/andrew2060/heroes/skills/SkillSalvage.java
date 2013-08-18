package net.kingdomsofarden.andrew2060.heroes.skills;

import java.text.DecimalFormat;

import org.bukkit.event.Listener;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;

public class SkillSalvage extends PassiveSkill implements Listener {

    public SkillSalvage(Heroes plugin) {
        super(plugin, "Salvage");
        setDescription("Allows for the recovery of up to $1 of the materials used to create a tool/armor piece in a furnace. The amount recovered determines on the durability of the item in question");
    }

    @Override
    public String getDescription(Hero hero) {
        DecimalFormat dF = new DecimalFormat("##.###");
        double threshold = 80+hero.getLevel(hero.getSecondClass())*0.15;
        return getDescription().replace("$1", dF.format(threshold)+"%");
    }
    
    
}
