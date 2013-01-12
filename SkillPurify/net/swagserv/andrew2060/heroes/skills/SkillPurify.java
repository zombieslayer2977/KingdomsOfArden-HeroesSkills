package net.swagserv.andrew2060.heroes.skills;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import com.herocraftonline.heroes.characters.skill.ActiveSkill;

public class SkillPurify extends ActiveSkill {

	public SkillPurify(Heroes plugin) {
		super(plugin, "Purify");
		setDescription("On use, draws upon the properties of water to purify a target, removing all suppresses, roots, binds, slows, silences, and other negative effects, and heals the target for $1% health");
		setArgumentRange(0,1);
		setIdentifiers("skill purify");
		setUsage("/skill purify");
	}

	@Override
	public SkillResult use(Hero h, String[] arg1) {
		if(arg1.length > 0) {
			try {
				Player p = Bukkit.getServer().getPlayer(arg1[0]);
				Location loc1 = h.getPlayer().getLocation();
				Location loc2 = p.getLocation();
				if(loc1.distanceSquared(loc2) > 169) {
					h.getPlayer().sendMessage("Target out of range!");
					return SkillResult.INVALID_TARGET_NO_MSG;
				}
				ExecuteSkillPurifyFriendly(p,h);
			} catch (NullPointerException e) {
				h.getPlayer().sendMessage("This player could not be found!");
				return SkillResult.INVALID_TARGET_NO_MSG;
			}
		} else {
		}
		return SkillResult.NORMAL;
	}


	private void ExecuteSkillPurifyFriendly(Player p, Hero h) {
		CharacterTemplate ct = this.plugin.getCharacterManager().getCharacter(p);
		Iterator<Effect> activeEffects = ct.getEffects().iterator();
		while(activeEffects.hasNext()) {
			Effect eff = activeEffects.next();
			if(eff.isType(EffectType.DISPELLABLE) && eff.isType(EffectType.HARMFUL) && !eff.isPersistent()) {
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
		double percentHeal = h.getLevel()*0.005;
		Bukkit.getPluginManager().callEvent(new HeroRegainHealthEvent((Hero)ct, (int) (ct.getMaxHealth()*percentHeal), this, h));
		return;
	}

	@Override
	public String getDescription(Hero h) {
		return getDescription()
				.replace("$1", h.getLevel()/2+"");
	}

}