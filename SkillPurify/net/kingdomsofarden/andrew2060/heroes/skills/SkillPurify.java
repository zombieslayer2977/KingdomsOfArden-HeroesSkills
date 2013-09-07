package net.kingdomsofarden.andrew2060.heroes.skills;

import java.util.Iterator;

import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.HeroRegainHealthEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;

public class SkillPurify extends TargettedSkill {

	public SkillPurify(Heroes plugin) {
		super(plugin, "Purify");
		setDescription("On use, purifies a target or self, removing all negative effects and healing the target for $1 health on friendly targets, and removing all positive effects and damaging the target for $2 health on enemy targets.");
		setArgumentRange(0,1);
		setIdentifiers("skill purify");
		setUsage("/skill purify");
	}

	@Override
	public SkillResult use(Hero h, LivingEntity entity, String[] arg1) {
		if(entity != null) {
			boolean mode = damageCheck(h.getPlayer(), entity) && !h.getEntity().equals(entity);
			if(!mode) {
				executeSkillPurifyFriendly((Player)entity,h);
				return SkillResult.NORMAL;
			} else {
				executeSkillPurifyHostile((Player)entity,h);
				return SkillResult.NORMAL;
			}
		} else {
			return SkillResult.INVALID_TARGET;
		}
	}


	private void executeSkillPurifyHostile(Player p, Hero h) {
		CharacterTemplate cT = this.plugin.getCharacterManager().getCharacter(p);
		Iterator<Effect> activeEffects = cT.getEffects().iterator();
		while(activeEffects.hasNext()) {
			Effect eff = activeEffects.next();
			if(eff.isType(EffectType.BENEFICIAL) && eff.isType(EffectType.DISPELLABLE)) {
				cT.removeEffect(eff);
			}
			continue;
		}
		Iterator<PotionEffect> potions = cT.getEntity().getActivePotionEffects().iterator();
		while(potions.hasNext()) {
			PotionEffectType next = potions.next().getType();
			if(next.equals(PotionEffectType.FAST_DIGGING) 
					|| next.equals(PotionEffectType.FIRE_RESISTANCE) 
					|| next.equals(PotionEffectType.INCREASE_DAMAGE) 
					|| next.equals(PotionEffectType.DAMAGE_RESISTANCE) 
					|| next.equals(PotionEffectType.INVISIBILITY) 
					|| next.equals(PotionEffectType.REGENERATION) 
					|| next.equals(PotionEffectType.SPEED) 
					|| next.equals(PotionEffectType.WATER_BREATHING) 
					|| next.equals(PotionEffectType.JUMP)) {
			    ToolHandlerPlugin.instance.getPotionEffectHandler().removePotionEffect(next, cT.getEntity());
			}
			continue;
		}
		addSpellTarget(p,h);
		Skill.damageEntity(p, h.getEntity(), h.getLevel()*0.5, DamageCause.MAGIC);
		return;
	}

	private void executeSkillPurifyFriendly(Player p, Hero h) {
		CharacterTemplate cT = this.plugin.getCharacterManager().getCharacter(p);
		Iterator<Effect> activeEffects = cT.getEffects().iterator();
		while(activeEffects.hasNext()) {
			Effect eff = activeEffects.next();
			if(eff.isType(EffectType.DISPELLABLE) && eff.isType(EffectType.HARMFUL)) {
				cT.removeEffect(eff);
			} 
			continue;
		}
		Iterator<PotionEffect> potions = cT.getEntity().getActivePotionEffects().iterator();
		while(potions.hasNext()) {
			PotionEffectType next = potions.next().getType();
			if(next.equals(PotionEffectType.BLINDNESS) 
					|| next.equals(PotionEffectType.CONFUSION) 
					|| next.equals(PotionEffectType.POISON) 
					|| next.equals(PotionEffectType.SLOW_DIGGING)
					|| next.equals(PotionEffectType.HUNGER)
					|| next.equals(PotionEffectType.SLOW)
					|| next.equals(PotionEffectType.WEAKNESS)
					|| next.equals(PotionEffectType.WITHER)) {
				ToolHandlerPlugin.instance.getPotionEffectHandler().removePotionEffect(next, cT.getEntity());
			}
			continue;
		}
		cT.getEntity().setFireTicks(0);
		if(cT instanceof Hero) {
			((Hero)cT).getPlayer().sendMessage(ChatColor.GRAY + "Purified by " + h.getName() + "!");
		}
		LivingEntity lE = cT.getEntity();
		HeroRegainHealthEvent event = new HeroRegainHealthEvent((Hero)cT, h.getLevel()*1.0D, this, h);
		Bukkit.getPluginManager().callEvent(event);
		lE.setHealth(lE.getHealth() + event.getAmount() > lE.getMaxHealth() ? lE.getMaxHealth() : lE.getHealth() + event.getAmount());
		return;
	}

	@Override
	public String getDescription(Hero h) {
		return getDescription()
				.replace("$1", h.getLevel()+"")
				.replace("$2", h.getLevel()/2+"");
	}

}