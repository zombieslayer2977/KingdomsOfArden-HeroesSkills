package net.kingdomsofarden.andrew2060.heroes.skills;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.CharacterDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;

public class SkillHardenedSkin extends PassiveSkill{
	public SkillHardenedSkin(Heroes plugin) {
		super(plugin, "HardenedSkin");
		setDescription("Passive: Prolonged exposure to working with earthen materials renders bearer immune to lava/fire damage.");
		setEffectTypes(new EffectType[] { EffectType.BENEFICIAL });
		Bukkit.getServer().getPluginManager().registerEvents(new SkillFireListener(), plugin);
	}
	public String getDescription(Hero hero) {
		return getDescription();
	}
	public class SkillFireListener implements Listener {
		@EventHandler(priority=EventPriority.HIGHEST)
		public void onCharacterDamage(CharacterDamageEvent event) {
			if(event.isCancelled()) {
				return;
			}
			if(!(event.getEntity() instanceof Player)) {
				return;
			}
			if(event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE) {
				Player p = (Player)event.getEntity();
				Hero h = SkillHardenedSkin.this.plugin.getCharacterManager().getHero(p);
				if(h.hasEffect("HardenedSkin")) {
					event.setCancelled(true);
					p.setFireTicks(0);
					return;
				}
				return;
			}
			return;
		}
	}
}
