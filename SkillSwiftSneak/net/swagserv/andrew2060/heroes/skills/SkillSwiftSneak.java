package net.swagserv.andrew2060.heroes.skills;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;

public class SkillSwiftSneak extends PassiveSkill {

	public SkillSwiftSneak(Heroes plugin) {
		super(plugin, "SwiftSneak");
		setDescription("Passive: Become naturally difficult to detect (permanent sneak with no speed penalty)");
		Bukkit.getPluginManager().registerEvents(new SneakListener(), this.plugin);
	}

	@Override
	public String getDescription(Hero arg0) {
		return getDescription();
	}
	private class SneakListener implements Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		public void onPlayerLogin(PlayerLoginEvent event) {
			Player p = event.getPlayer();
			Hero h = SkillSwiftSneak.this.plugin.getCharacterManager().getHero(p);
			if(h.hasEffect("SwiftSneak")) {
				p.setSneaking(true);
				p.setWalkSpeed(1);
			}
		}
	}
	

}
