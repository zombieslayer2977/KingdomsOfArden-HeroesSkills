package net.kingdomsofarden.andrew2060.heroes.skills;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.CharacterDamageEvent;
import com.herocraftonline.heroes.api.events.HeroRegainHealthEvent;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import com.herocraftonline.heroes.characters.skill.SkillType;

public class SkillDivineReckoning extends ActiveSkill implements Listener {
    
    private String invulnerableText;
    DecimalFormat dF;
    public SkillDivineReckoning(Heroes plugin) {
        super(plugin, "DivineReckoning");
        this.setDescription("Renders user invulnerable for $1, during which time period $2% of all damage output gets converted into life steal.");
        this.invulnerableText = ChatColor.GRAY + "[" + ChatColor.GREEN + "Skill" + ChatColor.GRAY + "] $1 is invulnerable due to Divine Reckoning for another $2!";
        this.dF = new DecimalFormat("##.##");
        this.setIdentifiers(new String[] {"skill reckoning", "skill divinereckoning", "skill invuln"});
        this.setUsage("/skill divinereckoning");
        this.setTypes(SkillType.HEAL, SkillType.LIGHT, SkillType.SILENCABLE);
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        double damageOutputHealPercent = SkillConfigManager.getUseSetting(hero, this, "damageOutputHealPercent", 0.50, false);
        long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION , 10000, false);
        hero.addEffect(new DivineReckoningEffect(this,this.plugin,duration,damageOutputHealPercent));
        return SkillResult.NORMAL;
    }

    @Override
    public String getDescription(Hero hero) {
        long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION , 10000, false);
        double damageOutputHealPercent = SkillConfigManager.getUseSetting(hero, this, "damageOutputHealPercent", 0.50, false);
        return getDescription().replace("$1", duration == 1000 ? "1 second" : dF.format(duration * 0.001) + " seconds").replace("$2", dF.format(damageOutputHealPercent*100));
    }
    
    private class DivineReckoningEffect extends ExpirableEffect {

        private double healPercent;

        public DivineReckoningEffect(Skill skill, Heroes plugin, long duration, double healPercent) {
            super(skill, plugin, "DivineReckoningEffect", duration);
            this.healPercent = healPercent;
        }

        public double getHealPercent() {
            return healPercent;
        }
        
    }
    
    //If defender has divine reckoning active on weapon damage: we process this first in case attacker also has divine reckoning
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) 
    public void onWeaponDamageDefender(WeaponDamageEvent event) {
        if(event.getEntity() instanceof LivingEntity) {
            CharacterTemplate cT = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
            if(cT.hasEffect("DivineReckoningEffect")) {
                event.setCancelled(true);
                if(event.getDamager() instanceof Hero) {
                    long remainingTime = (((ExpirableEffect)cT.getEffect("DivineReckoningEffect")).getExpiry() - System.currentTimeMillis());
                    ((Hero)event.getDamager()).getPlayer().sendMessage(invulnerableText.replace("$1", cT.getName()).replace("$2", remainingTime == 1000 ? "1 second" : dF.format(remainingTime * 0.001) + " seconds"));
                }
            }
        }    
    }
    
    //If attacker has divine reckoning active on weapon damage
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) 
    public void onWeaponDamageAttacker(WeaponDamageEvent event) {
        if(event.getDamager() instanceof Hero) {
            Hero h = (Hero) event.getDamager();
            if(h.hasEffect("DivineReckoningEffect")) {
                
                double amount = event.getDamage()*((DivineReckoningEffect)h.getEffect("DivineReckoningEffect")).getHealPercent();
                HeroRegainHealthEvent hEvent = new HeroRegainHealthEvent(h, amount, this);
                plugin.getServer().getPluginManager().callEvent(hEvent);
                if(!hEvent.isCancelled()) {
                    h.heal(hEvent.getAmount());
                }
            }
        }
    }

    //If defender has divine reckoning active on skill damage: we process this first in case attacker also has divine reckoning
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) 
    public void onSkillDamageDefender(SkillDamageEvent event) {
        if(event.getEntity() instanceof LivingEntity) {
            CharacterTemplate cT = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
            if(cT.hasEffect("DivineReckoningEffect")) {
                event.setCancelled(true);
                if(event.getDamager() instanceof Hero) {
                    long remainingTime = (((ExpirableEffect)cT.getEffect("DivineReckoningEffect")).getExpiry() - System.currentTimeMillis());
                    ((Hero)event.getDamager()).getPlayer().sendMessage(invulnerableText.replace("$1", cT.getName()).replace("$2", remainingTime == 1000 ? "1 second" : dF.format(remainingTime * 0.001) + " seconds"));
                }
            }
        }    
    }
    
    //If attacker has divine reckoning active on skill damage
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) 
    public void onSkillDamageAttacker(SkillDamageEvent event) {
        if(event.getDamager() instanceof Hero) {
            Hero h = (Hero) event.getDamager();
            if(h.hasEffect("DivineReckoningEffect")) {
                double damageOutputHealPercent = SkillConfigManager.getUseSetting(h, this, "damageOutputHealPercent", 0.50, false);
                double amount = event.getDamage()*damageOutputHealPercent;
                HeroRegainHealthEvent hEvent = new HeroRegainHealthEvent(h, amount, this);
                plugin.getServer().getPluginManager().callEvent(hEvent);
                if(!hEvent.isCancelled()) {
                    h.heal(hEvent.getAmount());
                }
            }
        }
    }
    
    //Environmental damage processing
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) 
    public void onCharacterDamage(CharacterDamageEvent event) {
        if(event.getEntity() instanceof LivingEntity) {
            CharacterTemplate cT = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
            if(cT.hasEffect("DivineReckoningEffect")) {
                event.setCancelled(true);
            }
        }    
    }
}
