package net.kingdomsofarden.andrew2060.heroes.skills;

import java.text.DecimalFormat;

import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.CharacterDamageEvent;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;

public class SkillTargettedJustice extends TargettedSkill implements Listener {

    public SkillTargettedJustice(Heroes plugin) {
        super(plugin, "TargettedJustice");
        this.setDescription("Selects a target up to $1 blocks away. That target is slowed by $2% and all incoming damage to it is amplified by $3% for $4 seconds");
        this.setTypes(SkillType.HARMFUL, SkillType.SILENCABLE, SkillType.DAMAGING);
        this.setIdentifiers("skill targettedjustice","skill justice","skill tjustice");
        this.setUsage("/skill targettedjustice");
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        if(target == null) {
            return SkillResult.INVALID_TARGET;
        }
        long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false);
        double damageBoost = SkillConfigManager.getUseSetting(hero, this, "damageBoost", Double.valueOf(0.3), false);
        int slowAmp = SkillConfigManager.getUseSetting(hero, this, "slowAmount", Integer.valueOf(2), false);
        ToolHandlerPlugin.instance.getPotionEffectHandler().addPotionEffectStacking(PotionEffectType.SLOW.createEffect((int) (duration * 0.001 * 20), slowAmp), target, false);
        plugin.getCharacterManager().getCharacter(target).addEffect(new TargettedJusticeEffect(this, plugin, duration,damageBoost));
        return SkillResult.NORMAL;
    }

    @Override
    public String getDescription(Hero hero) {
        int maxDistance = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MAX_DISTANCE.node(), 20, false);
        int duration = (int) Math.floor(SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) * 0.001);
        double damageBoost = SkillConfigManager.getUseSetting(hero, this, "damageBoost", Double.valueOf(0.3), false) * 100;
        int slowAmp = SkillConfigManager.getUseSetting(hero, this, "slowAmount", Integer.valueOf(2), false);

        return getDescription().replace("$1", maxDistance + "").replace("$2", 20*slowAmp + "").replace("$3", new DecimalFormat("##.##").format(damageBoost)).replace("$4", duration + "");
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.MAX_DISTANCE.node(), Integer.valueOf(20));
        node.set(SkillSetting.DURATION.node(), Long.valueOf(10000));
        node.set("damageBoost",Double.valueOf(0.3));
        node.set("slowAmount", Integer.valueOf(2));
        return node;
    }

    private class TargettedJusticeEffect extends ExpirableEffect {

        private double amplifier;

        public TargettedJusticeEffect(Skill skill, Heroes plugin, long duration, double amplifier) {
            super(skill, plugin, "TargettedJusticeEffect", duration);
            this.amplifier = amplifier;
        }

        public double getAmplifier() {
            return this.amplifier;
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) 
    public void onWeaponDamage(WeaponDamageEvent event) {
        if(event.getEntity() instanceof LivingEntity) {
            CharacterTemplate cT = plugin.getCharacterManager().getCharacter((LivingEntity)event.getEntity());
            if(cT.hasEffect("TargettedJusticeEffect")) {
                event.setDamage(event.getDamage() * (1 + (((TargettedJusticeEffect) cT.getEffect("TargettedJusticeEffect")).getAmplifier())));
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) 
    public void onSkillDamage(SkillDamageEvent event) {
        if(event.getEntity() instanceof LivingEntity) {
            CharacterTemplate cT = plugin.getCharacterManager().getCharacter((LivingEntity)event.getEntity());
            if(cT.hasEffect("TargettedJusticeEffect")) {
                event.setDamage(event.getDamage() * (1 + (((TargettedJusticeEffect) cT.getEffect("TargettedJusticeEffect")).getAmplifier())));
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) 
    public void onCharacterDamage(CharacterDamageEvent event) {
        if(event.getEntity() instanceof LivingEntity) {
            CharacterTemplate cT = plugin.getCharacterManager().getCharacter((LivingEntity)event.getEntity());
            if(cT.hasEffect("TargettedJusticeEffect")) {
                event.setDamage((event.getDamage() * (1 + (((TargettedJusticeEffect) cT.getEffect("TargettedJusticeEffect")).getAmplifier()))));
            }
        }
    }
}
