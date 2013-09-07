package net.kingdomsofarden.andrew2060.heroes.skills;

import java.text.DecimalFormat;

import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffectType;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.common.StunEffect;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;

public class SkillJudgment extends TargettedSkill {

    public SkillJudgment(Heroes plugin) {
        super(plugin, "Judgment");
        setDescription("Deals physical damage equal to $1% of the user's missing health up to $2 blocks away. If target has a higher health percent compared to the user, the target is stunned, otherwise it slowed for $3 seconds.");
        setUsage("/skill judgment <target>");
        setIdentifiers("skill judgment", "skill judge");
        setArgumentRange(0,1);
        setTypes(SkillType.HARMFUL, SkillType.SILENCABLE, SkillType.DAMAGING, SkillType.INTERRUPT, SkillType.LIGHT);
    }

    

    @Override
    public String getDescription(Hero hero) {
        DecimalFormat dF = new DecimalFormat("##.##");
        double amount = SkillConfigManager.getUseSetting(hero, this, SkillSetting.AMOUNT, 0.30, false) * 100;
        int range = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MAX_DISTANCE, 20, false);
        long duration = (long) (SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 3000, false) * 0.001);
        return getDescription().replace("$1",dF.format(amount)).replace("$2", range + "").replace("$3", dF.format(duration));
    }



    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        if(target == null) {
            return SkillResult.INVALID_TARGET;
        }
        double amount = SkillConfigManager.getUseSetting(hero, this, SkillSetting.AMOUNT, 0.30, false);
        long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 3000, false);
        double userHealthPercent = hero.getPlayer().getHealth()/hero.getPlayer().getMaxHealth();
        double targetHealthPercent = target.getHealth()/target.getMaxHealth();
        if(userHealthPercent <= targetHealthPercent) {
            plugin.getCharacterManager().getCharacter(target).addEffect(new StunEffect(this, duration));
        } else {
            ToolHandlerPlugin.instance.getPotionEffectHandler().addPotionEffectStacking(PotionEffectType.SLOW.createEffect((int) (duration * 0.001 * 20), 2), target, false);
        }
        double missingHealth = hero.getPlayer().getMaxHealth() - hero.getPlayer().getHealth();
        double damage = missingHealth * amount;
        addSpellTarget(target,hero);
        Skill.damageEntity(target, hero.getEntity(), damage, DamageCause.ENTITY_ATTACK);
        return SkillResult.NORMAL;
    }

}
