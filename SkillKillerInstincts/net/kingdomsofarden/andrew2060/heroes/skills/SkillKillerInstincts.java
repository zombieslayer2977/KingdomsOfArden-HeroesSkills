package net.kingdomsofarden.andrew2060.heroes.skills;

import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillKillerInstincts extends ActiveSkill implements Listener {

    public SkillKillerInstincts(Heroes plugin) {
        super(plugin, "KillerInstincts");
        setDescription("The next attack w/in 10 seconds deals a bonus 30% damage and will dispel all positive effects on a target. A player kill reduces the cooldown of this skill by 30 seconds.");
        setUsage("/skill killerinstincts");
        setIdentifiers("skill killerinstincts");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        hero.addEffect(new KillerInstinctsEffect(this,plugin));
        return SkillResult.NORMAL;
    }
    private class KillerInstinctsEffect extends ExpirableEffect {

        public KillerInstinctsEffect(Skill skill, Heroes plugin) {
            super(skill, plugin, "KillerInstinctsEffect", 10000);
            this.types.add(EffectType.BENEFICIAL);
            this.types.add(EffectType.PHYSICAL);
        }
        
        @Override
        public void applyToHero(Hero h) {
            broadcast(h.getPlayer().getLocation(), ChatColor.GRAY + "[" 
                    + ChatColor.GREEN + "Skill" 
                    + ChatColor.GRAY + "] " + h.getName() + " used Killer Instincts!", new Object[] {});
        }
        
    }
    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
    
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWeaponDamage(WeaponDamageEvent event) {
        if(!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        CharacterTemplate damager = event.getDamager();
        if(!damager.hasEffect("KillerInstinctsEffect")) {
            return;
        }
        damager.removeEffect(damager.getEffect("KillerInstinctsEffect"));
        event.setDamage(event.getDamage()*1.3);
        CharacterTemplate cT = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
        for(PotionEffect pE : cT.getEntity().getActivePotionEffects()) {
            switch(pE.getType().getId()) {
            
            case 1:
            case 3:
            case 5:
            case 6:
            case 8:
            case 10:
            case 12:
            case 13:
            case 14:
            case 16: {
                ToolHandlerPlugin.instance.getPotionEffectHandler().removePotionEffect(pE.getType(), cT.getEntity());
                continue;
            }
            default: {
                continue;
            }
            
            }
        }
        for(Effect e : cT.getEffects()) {
            if(e.types.contains(EffectType.BENEFICIAL) && e.types.contains(EffectType.DISPELLABLE)) {
                cT.removeEffect(e);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) 
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if(killer == null) {
            return;
        }
        Hero h = plugin.getCharacterManager().getHero(killer);
        Long cd = h.getCooldown("KillerInstincts");
        if(cd == null) {
            return;
        } else {
            h.setCooldown("KillerInstincts", cd - 30000);
        }
    }
}
