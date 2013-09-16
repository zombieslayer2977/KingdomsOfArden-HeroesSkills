package net.kingdomsofarden.andrew2060.heroes.skills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import net.kingdomsofarden.andrew2060.heroes.skills.aura.AuraEffect;
import net.kingdomsofarden.andrew2060.heroes.skills.aura.AuraWrapper;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillAuraOfCommand extends ActiveSkill implements Listener {

    public SkillAuraOfCommand(Heroes plugin) {
        super(plugin, "AuraOfCommand");
        setDescription("Exudes a commanding aura, increasing damage of self and nearby allies within 10 blocks by 10%. In addition will deal 20 true damage to nearby enemies within 5 blocks on switch");
        setIdentifiers("skill command", "skill auraofcommand");
        setArgumentRange(0,0);
        setUsage("/skill auraofcommand");
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        AuraEffect eff = (AuraEffect) hero.getEffect("AuraEffect");
        if(eff == null) {
            hero.addEffect(new AuraEffect(plugin, new CommandAuraWrapper()));
            return SkillResult.NORMAL;
        } else {
            return eff.setFWrapper(new CommandAuraWrapper(),hero);
        }
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription().replace("$1",20 + "");
    }
    
    private class CommandAuraWrapper extends AuraWrapper {

        public CommandAuraWrapper() {
            super("Command");
        }

        @Override
        public void onApply(Hero h) {
            for(Entity e : h.getPlayer().getNearbyEntities(5, 5, 5)) {
                if(!(e instanceof LivingEntity)) {
                    continue;
                }
                LivingEntity lE = (LivingEntity)e;
                if(damageCheck(h.getPlayer(), lE)) {
                    addSpellTarget(lE,h);
                    Skill.damageEntity(lE, h.getEntity(), 20.0, DamageCause.CUSTOM);
                }
            }
            
        }

        @Override
        public void onTick(Hero h) {
            if(h.hasParty()) {
                for(Hero member : h.getParty().getMembers()) {
                    if(member == h) {
                        member.addEffect(new ExpirableEffect(null, "CommandAuraEffect", 2000));
                        continue;
                    }
                    if(member.getPlayer().getLocation().distanceSquared(h.getPlayer().getLocation()) <= 100) {
                        member.addEffect(new ExpirableEffect(null, "CommandAuraEffect", 2000));
                        continue;
                    }
                    continue;
                }
            } else {
                h.addEffect(new ExpirableEffect(null, "CommandAuraEffect", 2000));
            }
            return;
        }

        @Override
        public void onEnd(Hero h) {
            return;
        }
        
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWeaponDamage(WeaponDamageEvent event) {
        if(event.getDamager().hasEffect("CommandAuraEffect")) {
            event.setDamage(event.getDamage() * 1.1);
        }
    }
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSkillDamage(SkillDamageEvent event) {
        if(event.getDamager().hasEffect("CommandAuraEffect")) {
            event.setDamage(event.getDamage() * 1.1);
        }
    }
    
}
