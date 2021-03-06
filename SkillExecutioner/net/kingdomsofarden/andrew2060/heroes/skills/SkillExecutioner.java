package net.kingdomsofarden.andrew2060.heroes.skills;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;

public class SkillExecutioner extends PassiveSkill {

	public SkillExecutioner(Heroes plugin) {
		super(plugin, "Executioner");
		setDescription("Passive: For every 1% max health missing from a target, deals an additional 0.2% damage on strike");
		Bukkit.getPluginManager().registerEvents(new SkillListener(), this.plugin);
	}

	@Override
	public String getDescription(Hero arg0) {
		return getDescription();
	}
	public class SkillListener implements Listener {
		@EventHandler(priority = EventPriority.LOWEST)
		public void onWeaponDamage(WeaponDamageEvent event) {
			if(event.isCancelled()) {
				return;
			}
			if(!(event.getDamager() instanceof Hero)) {
				return;
			}
			Hero h = (Hero) event.getDamager();
			if(!h.hasEffect("Executioner")) {
				return;
			}
			if(!(event.getEntity() instanceof LivingEntity)) {
				return;
			}
			LivingEntity e = (LivingEntity)event.getEntity();
			double dmgpercent = 1 + (((e.getMaxHealth() - e.getHealth())/e.getMaxHealth()) * 0.2);
			event.setDamage(event.getDamage() * dmgpercent);
		}
	}

}
