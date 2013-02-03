package net.swagserv.andrew2060.heroes.skills;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;

public class SkillMarksman extends PassiveSkill {

	public SkillMarksman(Heroes plugin) {
		super(plugin, "Marksman");
		setDescription("Passive: Arrows fired gain damage based on the range from which they are fired ($1 extra damage per block beyond $2 blocks)");
		Bukkit.getServer().getPluginManager().registerEvents(new MarksmanListener(this), this.plugin);
	}

	@Override
	public String getDescription(Hero h) {
		int multiplier = SkillConfigManager.getUseSetting(h, this, "damagePerBlock", 5, false);
		int minrange = SkillConfigManager.getUseSetting(h, this, "activationThreshold", 16, false);
		return getDescription()
				.replace("$1", multiplier + "")
				.replace("$2", minrange + "");
	}
	
	@Override
	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection node = super.getDefaultConfig();
		node.set("damagePerBlock", Integer.valueOf(5));
		node.set("activationThreshold", Integer.valueOf(16));
		return node;
	}
	private class MarksmanListener implements Listener {
		private Skill skill;
		public MarksmanListener(Skill skill) {
			this.skill = skill;
		}

		@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled = true)
		public void onWeaponDamage(WeaponDamageEvent event) {
			if(!event.isProjectile()) {
				return;
			}
			if(!(event.getDamager() instanceof Hero)) {
				return;
			}
			Hero h = (Hero)event.getDamager();
			if(!h.hasEffect("Marksman")) {
				return;
			}
			double dist = h.getPlayer().getLocation().distance(event.getEntity().getLocation());
			int minrange = SkillConfigManager.getUseSetting(h, skill, "activationThreshold", 16, false);
			dist -= minrange;
			int multiplier = SkillConfigManager.getUseSetting(h, skill, "damagePerBlock", 5, false);
			int addDamage = (int) (dist*multiplier);
			event.setDamage(event.getDamage() + addDamage);

		}
	}
}
