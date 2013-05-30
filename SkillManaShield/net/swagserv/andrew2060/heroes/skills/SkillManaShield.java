package net.swagserv.andrew2060.heroes.skills;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.CharacterDamageEvent;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.PeriodicEffect;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;

public class SkillManaShield extends PassiveSkill {

	public SkillManaShield(Heroes plugin) {
		super(plugin,"ManaShield");
		setDescription("Passive: A shield that holds 3 charges is created. " +
				"Every time damage is taken from combat, a charge will be consumed and the damage completely negated. " +
				"Shield charges replenish at a rate of one per 20 seconds when out of combat.");
		Bukkit.getPluginManager().registerEvents(new ManaShieldListener(this.plugin), plugin);
	}
	
	private class ManaShieldEffect extends PeriodicEffect {
		private int stacks;
		public ManaShieldEffect(Heroes plugin) {
			super(plugin, "ManaShieldEffect", 20000L);
			this.stacks = 3;
		}
		public boolean checkStackAndDecrease(Hero h) {
			if(stacks > 0) {
				switch(stacks) {
				case 3:
					h.getPlayer().playEffect(h.getPlayer().getLocation(),Effect.POTION_BREAK,1);
					break;
				case 2:
					h.getPlayer().playEffect(h.getPlayer().getLocation(),Effect.POTION_BREAK,3);
					break;
				case 1:
					h.getPlayer().playEffect(h.getPlayer().getLocation(),Effect.POTION_BREAK,2);
					break;
				}
				stacks--;
				return true;
			} else {
				return false;
			}
		}
		private void regenerateStack() {
			if(stacks >= 3) {
				return;
			} else {
				stacks++;
				return;
			}
		}
		@Override
		public void tickHero(Hero h) {
			if(h.isInCombat()) {
				return;
			} else {
				regenerateStack();
			}
		}
		
	}
	@Override
	public void apply(Hero hero) {
		ManaShieldEffect eff = new ManaShieldEffect(this.plugin);
		hero.addEffect(eff);	
	}
	
	@Override
	public String getDescription(Hero arg0) {
		return getDescription();
	}
	public class ManaShieldListener implements Listener {
		private Heroes plugin;
		public ManaShieldListener(Heroes plugin) {
			this.plugin = plugin;
		}
		//Weapon Damage
		@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
		public void onWeaponDamage(WeaponDamageEvent event) {
			if(!(event.getEntity() instanceof Player)) {
				return;
			}
			Hero h = plugin.getCharacterManager().getHero((Player) event.getEntity());
			if(!h.hasEffect("ManaShieldEffect")) {
				return;
			}
			ManaShieldEffect eff = (ManaShieldEffect)h.getEffect("ManaShieldEffect");

			if(eff.checkStackAndDecrease(h)) {
				event.setCancelled(true);
				return;
			} else {
				return;
			}
		}
		//Skill Damage
		@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
		public void onSkillDamage(SkillDamageEvent event) {
			if(!(event.getEntity() instanceof Player)) {
				return;
			}
			Hero h = plugin.getCharacterManager().getHero((Player) event.getEntity());
			if(!h.hasEffect("ManaShieldEffect")) {
				return;
			}
			if(((ManaShieldEffect)h.getEffect("ManaShieldEffect")).checkStackAndDecrease(h)) {
				event.setCancelled(true);
				return;
			} else {
				return;
			}
		}
	}
}