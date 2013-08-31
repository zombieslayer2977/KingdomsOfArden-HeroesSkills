package net.kingdomsofarden.andrew2060.heroes.skills;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;

public class SkillEnhancedAgility extends ActiveSkill {

    public SkillEnhancedAgility(Heroes plugin) {
        super(plugin, "EnhancedAgility");
        setDescription("Magically enhances the user's agility, boosting jump height by 40% and speed by 20% for $1 seconds.");
        setIdentifiers("skill enhancedagility");
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        ToolHandlerPlugin.instance.getPotionEffectHandler().addPotionEffectStacking(PotionEffectType.JUMP.createEffect((int) (hero.getLevel()*0.5*20), 2), hero.getEntity(), false);
        return SkillResult.NORMAL;
    }

    @Override
    public String getDescription(Hero hero) {
        DecimalFormat dF = new DecimalFormat("##.#");
        return getDescription().replace("$1", dF.format(hero.getLevel()*0.5) + "");
    }

}
