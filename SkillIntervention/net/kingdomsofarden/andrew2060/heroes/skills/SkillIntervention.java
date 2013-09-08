package net.kingdomsofarden.andrew2060.heroes.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.HeroRegainHealthEvent;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;

public class SkillIntervention extends TargettedSkill implements Listener {

    public SkillIntervention(Heroes plugin) {
        super(plugin, "Intervention");
        setDescription("Shields target for 10% of incoming damage, and heals caster for same amount, for 30 seconds if within 16 blocks of caster. Target must not be self and must be in same party.");
        setUsage("/skill intervention");
        setIdentifiers("skill intervention","skill intervene");
        setTypes(SkillType.BUFF, SkillType.SILENCABLE, SkillType.HEAL, SkillType.LIGHT);
        setArgumentRange(0,1);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        if(!(target instanceof Player)) {
            return SkillResult.INVALID_TARGET;
        }
        Hero tHero = plugin.getCharacterManager().getHero((Player) target);
        if(tHero == hero) {
            return SkillResult.INVALID_TARGET;
        }
        if(!tHero.getParty().isPartyMember(hero)){
            return SkillResult.INVALID_TARGET;
        }
        tHero.addEffect(new InterventionEffect(this,plugin,30000,hero));
        return SkillResult.NORMAL;
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }

    private class InterventionEffect extends ExpirableEffect {

        private Hero caster;

        public InterventionEffect(Skill skill, Heroes plugin, long duration, Hero caster) {
            super(skill, plugin, "Intervention", duration);
            this.caster = caster;
            this.types.add(EffectType.BENEFICIAL);
            this.types.add(EffectType.DISPELLABLE);
            this.types.add(EffectType.LIGHT);
        }
        
        public Hero getCaster() {
            return caster;
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onWeaponDamage(WeaponDamageEvent event) {
        if(event.getEntity() instanceof LivingEntity) {
            CharacterTemplate cT = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
            if(cT.hasEffect("Intervention")) {
                double blocked = event.getDamage() * 0.1;
                Hero caster = ((InterventionEffect) cT.getEffect("Intervention")).getCaster();
                HeroRegainHealthEvent hrEvent = new HeroRegainHealthEvent(caster, blocked, this, caster);
                plugin.getServer().getPluginManager().callEvent(hrEvent);
                if(!hrEvent.isCancelled()) {
                    caster.heal(hrEvent.getAmount());
                }
                event.setDamage(event.getDamage()-blocked);
            }
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void onSkillDamage(SkillDamageEvent event) {
        if(event.getEntity() instanceof LivingEntity) {
            CharacterTemplate cT = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
            if(cT.hasEffect("Intervention")) {
                double blocked = event.getDamage() * 0.1;
                Hero caster = ((InterventionEffect) cT.getEffect("Intervention")).getCaster();
                HeroRegainHealthEvent hrEvent = new HeroRegainHealthEvent(caster, blocked, this, caster);
                plugin.getServer().getPluginManager().callEvent(hrEvent);
                if(!hrEvent.isCancelled()) {
                    caster.heal(hrEvent.getAmount());
                }
                event.setDamage(event.getDamage()-blocked);
            }
        }
    }
}
