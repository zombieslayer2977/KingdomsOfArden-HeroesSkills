package net.kingdomsofarden.andrew2060.heroes.skills;


import java.text.DecimalFormat;
import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;

public class SkillRepair extends PassiveSkill {


    public SkillRepair(Heroes plugin) {
        super(plugin, "Repair");
        setDescription("Passive: Grants ability to use the anvil to repair weapons/tools/armor. Each repair has a $1% chance of breaking the item (decreases with blacksmith level)");
        setEffectTypes(new EffectType[] { EffectType.BENEFICIAL });
    }

    @Override
    public String getDescription(Hero h) {
        double breakChance = Math.pow(h.getLevel(h.getSecondClass()), -1) * (1.0D/3.0D) * 100;
        DecimalFormat dF = new DecimalFormat("##.###");
        return getDescription().replace("$1",dF.format(breakChance)+"%");
    }

    
}
