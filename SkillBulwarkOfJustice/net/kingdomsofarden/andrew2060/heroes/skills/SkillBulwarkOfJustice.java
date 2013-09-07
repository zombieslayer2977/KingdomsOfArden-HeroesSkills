package net.kingdomsofarden.andrew2060.heroes.skills;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillSetting;

public class SkillBulwarkOfJustice extends ActiveSkill implements Listener {

    DecimalFormat dF;

    public SkillBulwarkOfJustice(Heroes plugin) {
        super(plugin, "BulwarkOfJustice");
        dF = new DecimalFormat("##.##");
        setIdentifiers("skill bulwark", "skill bulwarkofjustice");
        setDescription("Reduces all non-environmental incoming damage by $1% for $2 seconds. In addition, renders user completely immune to projectile damage for $3 seconds.");
        setUsage("skill bulwarkofjustice");
        setArgumentRange(0,0);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 30000, false);
        long projDuration = SkillConfigManager.getUseSetting(hero, this, "projimmunityduration", 10000, false);
        ExpirableEffect protEffect = new ExpirableEffect(this, plugin, "JusticeBulwarkEffect", duration);
        protEffect.types.add(EffectType.BENEFICIAL);
        protEffect.types.add(EffectType.DISPELLABLE);
        hero.addEffect(protEffect);
        ExpirableEffect projEffect = new ExpirableEffect(this, plugin, "JusticeProjEffect", projDuration);
        projEffect.types.add(EffectType.BENEFICIAL);
        projEffect.types.add(EffectType.DISPELLABLE);
        hero.addEffect(projEffect);
        broadcast(hero.getPlayer().getLocation(), ChatColor.GRAY + "[" + ChatColor.GREEN + "Skill" + ChatColor.GRAY + "] $1 used Bulwark Of Justice!", new Object[] {hero.getName()});
        return SkillResult.NORMAL;
    }

    @Override
    public String getDescription(Hero hero) {
        double amount = SkillConfigManager.getUseSetting(hero, this, SkillSetting.AMOUNT, 0.30, false);
        long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 30000, false);
        long projDuration = SkillConfigManager.getUseSetting(hero, this, "projimmunityduration", 10000, false);
        return getDescription().replace("$1", dF.format(amount)).replace("$2", dF.format(duration * 0.001).replace("$3", dF.format(projDuration * 0.001)));
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.AMOUNT.node(), Double.valueOf(0.30));
        node.set(SkillSetting.DURATION.node(), Long.valueOf(30000));
        node.set("projimmunityduration", Long.valueOf(10000));
        return node;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) 
    public void onWeaponDamage(WeaponDamageEvent event) {
        if(!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        CharacterTemplate cT = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
        if(event.getAttackerEntity() instanceof Projectile) {
            if(cT.hasEffect("JusticeProjEffect")) {
                event.setCancelled(true);
                if(event.getDamager() instanceof Hero) {
                    String message = ChatColor.GRAY + "[" + ChatColor.GREEN + "Skill" + ChatColor.GRAY + "] $1 is immune to projectile damage for another $2 seconds due to Bulwark Of Justice!";
                    double timeRemaining = ((ExpirableEffect)cT.getEffect("JusticeProjEffect")).getExpiry() - System.currentTimeMillis();
                    timeRemaining *= 0.001;
                    message.replace("$1", cT.getEntity().getCustomName() != null ? cT.getEntity().getCustomName() : cT.getName()).replace("$2", dF.format(timeRemaining));
                    ((Player)event.getDamager().getEntity()).sendMessage(message);
                }
                return;
            }
        }
        if(cT.hasEffect("JusticeBulwarkEffect")) {
            double amount = 0.30;
            if(cT instanceof Hero) {
                amount = SkillConfigManager.getUseSetting((Hero)cT, this, SkillSetting.AMOUNT, 0.30, false);
            }
            event.setDamage(event.getDamage()*(1-amount));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) 
    public void onSkillDamage(SkillDamageEvent event) {
        if(!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        CharacterTemplate cT = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
        if(cT.hasEffect("JusticeBulwarkEffect")) {
            double amount = 0.30;
            if(cT instanceof Hero) {
                amount = SkillConfigManager.getUseSetting((Hero)cT, this, SkillSetting.AMOUNT, 0.30, false);
            }
            event.setDamage(event.getDamage()*(1-amount));
        }
    }
}
