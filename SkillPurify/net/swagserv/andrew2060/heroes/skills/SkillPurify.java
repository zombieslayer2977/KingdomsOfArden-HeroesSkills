package net.swagserv.andrew2060.heroes.skills;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.HeroRegainHealthEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;

public class SkillPurify extends TargettedSkill {

	public SkillPurify(Heroes plugin) {
		super(plugin, "Purify");
		setDescription("On use, draws upon the properties of water to purify a target, removing all negative effects and healing the target for $1 health on friendly targets, and removing all positive effects and damaging the target for $2 health on enemy targets.");
		setArgumentRange(0,1);
		setIdentifiers("skill purify");
		setUsage("/skill purify");
	}

	@Override
	public SkillResult use(Hero h, LivingEntity entity, String[] arg1) {
		if(entity != null) {
			if(!(entity instanceof Player)) {
				h.getPlayer().sendMessage("This is not a hero and cannot be purified!");
				return SkillResult.INVALID_TARGET_NO_MSG;
			}
			Location loc1 = h.getPlayer().getLocation();
			Location loc2 = entity.getLocation();
			if(loc1.distanceSquared(loc2) > 169) {
				h.getPlayer().sendMessage("Target out of range!");
				return SkillResult.INVALID_TARGET_NO_MSG;
			}
			boolean mode = damageCheck(h.getPlayer(), entity);
			if(!mode) {
				ExecuteSkillPurifyFriendly((Player)entity,h);
				return SkillResult.NORMAL;
			} else {
				ExecuteSkillPurifyHostile((Player)entity,h);
				return SkillResult.NORMAL;
			}
		} else {
			if(arg1.length > 0) {
				try {
					Player p = Bukkit.getServer().getPlayer(arg1[0]);
					Location loc1 = h.getPlayer().getLocation();
					Location loc2 = p.getLocation();
					if(loc1.distanceSquared(loc2) > 169) {
						h.getPlayer().sendMessage("Target out of range!");
						return SkillResult.INVALID_TARGET_NO_MSG;
					}
					boolean mode = damageCheck(h.getPlayer(), entity);
					if(mode) {
						ExecuteSkillPurifyFriendly(p,h);
					} else {
						ExecuteSkillPurifyHostile(p,h);
					}
				} catch (NullPointerException e) {
					h.getPlayer().sendMessage("This player could not be found!");
					return SkillResult.INVALID_TARGET_NO_MSG;
				}
			}
		}
		return SkillResult.NORMAL;
	}


	private void ExecuteSkillPurifyHostile(Player p, Hero h) {
		CharacterTemplate ct = this.plugin.getCharacterManager().getCharacter(p);
		Iterator<Effect> activeEffects = ct.getEffects().iterator();
		while(activeEffects.hasNext()) {
			Effect eff = activeEffects.next();
			if(eff.isType(EffectType.BENEFICIAL) && eff.isType(EffectType.DISPELLABLE)) {
				ct.removeEffect(eff);
			}
			continue;
		}
		Iterator<PotionEffect> potions = ct.getEntity().getActivePotionEffects().iterator();
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
				ct.getEntity().removePotionEffect(next);
			}
			continue;
		}
		this.damageEntity(p, h.getEntity(), h.getLevel()/2);
		return;
	}

	private void ExecuteSkillPurifyFriendly(Player p, Hero h) {
		CharacterTemplate ct = this.plugin.getCharacterManager().getCharacter(p);
		Iterator<Effect> activeEffects = ct.getEffects().iterator();
		while(activeEffects.hasNext()) {
			Effect eff = activeEffects.next();
			if(eff.isType(EffectType.DISPELLABLE) && eff.isType(EffectType.HARMFUL)) {
				ct.removeEffect(eff);
			} 
			continue;
		}
		Iterator<PotionEffect> potions = ct.getEntity().getActivePotionEffects().iterator();
		while(potions.hasNext()) {
			PotionEffectType next = potions.next().getType();
			if(next.equals(PotionEffectType.BLINDNESS) 
					|| next.equals(PotionEffectType.CONFUSION) 
					|| next.equals(PotionEffectType.POISON) 
					|| next.equals(PotionEffectType.SLOW_DIGGING)) {
				ct.getEntity().removePotionEffect(next);
			}
			continue;
		}
		ct.getEntity().setFireTicks(0);
		if(ct instanceof Hero) {
			((Hero)ct).getPlayer().sendMessage(ChatColor.GRAY + "Purified by " + h.getName() + "!");
		}
		LivingEntity lE = ct.getEntity();
		HeroRegainHealthEvent event = new HeroRegainHealthEvent((Hero)ct, h.getLevel(), this, h);
		Bukkit.getPluginManager().callEvent(event);
		int finalAmount = lE.getHealth()+event.getAmount();
		if(finalAmount > lE.getMaxHealth()) {
			finalAmount = lE.getMaxHealth();
		}
		lE.setHealth(finalAmount);
		return;
	}

	@Override
	public String getDescription(Hero h) {
		return getDescription()
				.replace("$1", h.getLevel()+"")
				.replace("$2", h.getLevel()/2+"");
	}

}