package net.swagserv.andrew2060.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SkillManaShield extends PassiveSkill {
	public SkillManaShield(Heroes plugin) {
		super(plugin, "ManaShield");
		setDescription("When mana is above $1% full, a translucent barrier is erected that absorbs $2% of all incoming weapon damage. However, each hit taken also drains mana equivalent to $3% of damage taken.");
		Bukkit.getServer().getPluginManager().registerEvents(new SkillListener(this), plugin);
	}
	public String getDescription(Hero hero) {
		return getDescription()
				.replace("$1", SkillConfigManager.getUseSetting(hero, this, "deactivatePercentage", 50, false) +"")
				.replace("$2", SkillConfigManager.getUseSetting(hero, this, "damagePercentageBlocked", 75, false) +"")
				.replace("$3", SkillConfigManager.getUseSetting(hero, this, "percentDamageManaDrainonHit", 25, false) +"");
	}

	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection node = super.getDefaultConfig();
		node.set("deactivatePercentage", Integer.valueOf(50));
		node.set("damagePercentageBlocked", Integer.valueOf(75));
		node.set("percentDamageManaDrainonHit", Integer.valueOf(25));
		return node;
	}

	public void init() {
		super.init();
	}

	public class SkillListener implements Listener {
		private Skill skill;

		public SkillListener(Skill skill) {
			this.skill = skill;
		}

		@EventHandler(priority=EventPriority.HIGHEST)
		public void onWeaponDamage(WeaponDamageEvent event) {
			if (!(event.getEntity() instanceof Player)) {
				return;
			}

			Player p = (Player)event.getEntity();
			Hero h = SkillManaShield.this.plugin.getCharacterManager().getHero(p);
			int d = event.getDamage();
			int m = h.getMana();

			if (h.hasEffect("ManaShield")) {
				int threshold = SkillConfigManager.getUseSetting(h, this.skill, "deactivatePercentage", 50, false);
				if (m >= h.getMaxMana() * (threshold * 0.01D)) {
					int percentageBlocked = SkillConfigManager.getUseSetting(h, this.skill, "damagePercentageBlocked", 75, false);
					double damageMultiplier = 100 - percentageBlocked * 0.01;
					event.setDamage((int)(d * damageMultiplier));
					int manaLossPercentage = SkillConfigManager.getUseSetting(h, this.skill, "percentDamageManaDrainonHit", 25, false);
					int manaLoss = (int)(d * (manaLossPercentage * 0.01));
					h.setMana(h.getMana() - manaLoss);
					Location loc = h.getPlayer().getLocation();
					loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.GLASS);
					if ((event.getDamager().getEntity() instanceof Player)) {
						double damageAbsorbed = d - d * damageMultiplier;
						double r = m - h.getMaxMana() * (threshold * 0.01D);
						((Player)event.getDamager().getEntity()).sendMessage(ChatColor.GRAY + p.getName() + "'s mana shield absorbed " + ChatColor.RED + damageAbsorbed + ChatColor.GRAY + " damage.");
						((Player)event.getDamager().getEntity()).sendMessage(ChatColor.GRAY + "Remaining mana shield charge:" + ChatColor.AQUA + " " + r);
					}
				}
			}
		}
	}
}