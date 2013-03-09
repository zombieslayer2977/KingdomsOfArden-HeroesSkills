package net.swagserv.andrew2060.heroes.skills;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillDesperateFury extends PassiveSkill {

	public SkillDesperateFury(Heroes plugin) {
		super(plugin, "DesperateFury");
		setDescription("Passive: Attacks gain 20% splash true damage but loses 25% base damage when the bearer reaches less than 30% health");
		Bukkit.getPluginManager().registerEvents(new SkillListener(), this.plugin);
	}

	@Override
	public String getDescription(Hero arg0) {
		return getDescription();
	}
	public class SkillListener implements Listener {
		@EventHandler(priority = EventPriority.HIGH)
		public void onWeaponDamage(WeaponDamageEvent event) {
			if(event.isCancelled()) {
				return;
			}
			if(!(event.getDamager() instanceof Hero)) {
				return;
			}
			Hero h = (Hero) event.getDamager();
			Player p = h.getPlayer();
			if(!h.hasEffect("DesperateFury")) {
				return;
			}
			if((p.getHealth()/p.getMaxHealth()) > 0.3) {
				return;
			}
			if(!(event.getEntity() instanceof LivingEntity)) {
				return;
			}
			LivingEntity e = (LivingEntity)event.getEntity();
			Iterator<Entity> nearby = e.getNearbyEntities(5, 5, 5).iterator();
			while(nearby.hasNext()) {
				Entity ent = nearby.next();
				if(!(ent instanceof LivingEntity)) {
					continue;
				}
				if(!Skill.damageCheck(h.getPlayer(), (LivingEntity) ent)) {
					return;
				}
				Skill.damageEntity((LivingEntity)ent, h.getEntity(), (int) (event.getDamage()*0.2), DamageCause.CUSTOM);
			}
			event.setDamage((int) (event.getDamage()*0.75));		
		}
	}

}
