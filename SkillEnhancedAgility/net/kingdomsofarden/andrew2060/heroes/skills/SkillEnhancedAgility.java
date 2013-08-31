package net.kingdomsofarden.andrew2060.heroes.skills;

import java.text.DecimalFormat;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.CharacterDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;

public class SkillEnhancedAgility extends ActiveSkill implements Listener {

    public SkillEnhancedAgility(Heroes plugin) {
        super(plugin, "EnhancedAgility");
        setDescription("Magically enhances the user's agility, boosting jump height by 40% for $1 seconds.");
        setIdentifiers("skill enhancedagility");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        ToolHandlerPlugin.instance.getPotionEffectHandler().addPotionEffectStacking(PotionEffectType.JUMP.createEffect((int) (hero.getLevel()*0.5*20), 2), hero.getEntity(), false);
        hero.addEffect(new ExpirableEffect(this,plugin,"EnhancedAgility",(long) (hero.getLevel() * 0.5 * 1000)));
        return SkillResult.NORMAL;
    }

    @Override
    public String getDescription(Hero hero) {
        DecimalFormat dF = new DecimalFormat("##.#");
        return getDescription().replace("$1", dF.format(hero.getLevel()*0.5) + "");
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onCharacterDamage(CharacterDamageEvent event) {
        if(event.getEntity() instanceof LivingEntity) {
            if(plugin.getCharacterManager().getCharacter((LivingEntity)event.getEntity()).hasEffect("EnhancedAgility")) {
                event.setCancelled(true);
            }
        }
    }

}
