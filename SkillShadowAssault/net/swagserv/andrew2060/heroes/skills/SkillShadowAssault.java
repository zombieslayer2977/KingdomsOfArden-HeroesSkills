package net.swagserv.andrew2060.heroes.skills;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffectType;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.util.Messaging;

public class SkillShadowAssault extends ActiveSkill {

	public class ShadowAssaultListener implements Listener {
		@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST) 
		public void onWeaponDamage(WeaponDamageEvent event) {
			CharacterTemplate cT = event.getDamager();
			event.setDamage(event.getDamage()*1.25);
			if(cT.hasEffect("ShadowAssaultEffect")) {
				cT.removeEffect(cT.getEffect("ShadowAssaultEffect"));
				event.setDamage(event.getDamage()*1.25);
			}
		}

	}

	public class ShadowAssaultEffect extends ExpirableEffect {

		public ShadowAssaultEffect(Skill skill, Heroes plugin,
				long duration) {
			super(skill, "ShadowAssaultEffect", duration);
		}
		@Override
		public void applyToHero(Hero h) {
            super.applyToHero(h);
			h.getPlayer().addPotionEffect(PotionEffectType.SPEED.createEffect(3000, 3),true);
			Player toHide = h.getPlayer();
			Player[] online = Bukkit.getServer().getOnlinePlayers();
			for(int i = 0; i < online.length; i++) {
				Player p = online[i];
				p.hidePlayer(toHide);
			}
			h.getPlayer().sendMessage(ChatColor.GRAY + "You begin your shadow assault. You have 15 seconds to enter combat before the energy used overloads and hurts you.");
		}
		@Override
		public void removeFromHero(Hero h) {
            super.removeFromHero(h);
			h.getPlayer().sendMessage(ChatColor.GRAY + "Your shadow assault has ended.");
			Player toShow = h.getPlayer();
			toShow.removePotionEffect(PotionEffectType.SPEED);
			Player[] online = Bukkit.getServer().getOnlinePlayers();
			for(int i = 0; i < online.length; i++) {
				Player p = online[i];
				p.showPlayer(toShow);
			}
			if(!h.isInCombat()) {
				Skill.damageEntity(h.getEntity(), h.getEntity(), (int) (h.getPlayer().getMaxHealth()*0.5), DamageCause.MAGIC);
				Messaging.send(h.getPlayer(), "The energy used for this assault turns against you as you have not expended it on a target", new Object[0]);
			}
		}
		

	}

	public SkillShadowAssault(Heroes plugin) {
		super(plugin, "ShadowAssault");
		setDescription("Becomes invisible and gains 45% movement speed for 15 seconds. If after these 15 seconds no attacks are made, 50% max health damage is dealt. The first attack done while shadow assault is active deals 25% bonus damage. and ends shadow assault.");
		setIdentifiers("skill shadowassault");
		setUsage("/skill shadowassault");
		setArgumentRange(0,0);
		Bukkit.getPluginManager().registerEvents(new ShadowAssaultListener(), this.plugin);
	}

	@Override
		public SkillResult use(Hero h, String[] args) {
			h.addEffect(new ShadowAssaultEffect(this, this.plugin, 15000));
			broadcast(h.getPlayer().getLocation(), "§7[§2Skill§7]$1 started a Shadow Assault", new Object[] {h.getName()});
			return SkillResult.NORMAL;
		}

	@Override
	public String getDescription(Hero h) {
		return getDescription();
	}
	
}
