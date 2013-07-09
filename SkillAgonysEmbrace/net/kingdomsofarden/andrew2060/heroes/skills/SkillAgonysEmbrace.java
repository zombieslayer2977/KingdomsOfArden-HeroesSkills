package net.kingdomsofarden.andrew2060.heroes.skills;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.HeroRegainHealthEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;

public class SkillAgonysEmbrace extends ActiveSkill{

	public SkillAgonysEmbrace(Heroes plugin) {
		super(plugin, "AgonysEmbrace");
		setIdentifiers("skill agonysembrace");
		setArgumentRange(0,0);
		setUsage("/skill agonysembrace");
		setDescription("The bearer revels in the agony of his opponents, dealing $1% splash damage on the next hit. $2% of the splash damage is returned as health to the user");
		Bukkit.getPluginManager().registerEvents(new SkillListener(this),  this.plugin);
	}

	@Override
	public SkillResult use(Hero h, String[] args) {
		h.addEffect(new Effect(this, "agonyEffect"));
		return SkillResult.NORMAL;
	}

	@Override
	public String getDescription(Hero h) {
		double splash = SkillConfigManager.getUseSetting(h, this, "splashPercent", 50, false);
		double heal = SkillConfigManager.getUseSetting(h, this, "healPercent", 50, false);
		return getDescription()
				.replace("$1", splash + "")
				.replace("$2", heal + "");
	}
	private class SkillListener implements Listener {
		private Skill skill;
		public SkillListener(Skill skill) {
			this.skill = skill;
		}
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		public void onWeaponDamage(WeaponDamageEvent event) {
			if(!(event.getDamager() instanceof Hero)) {
				return;
			}
			Hero h = (Hero)event.getDamager();
			Player p = h.getPlayer();
			if(!h.hasEffect("agonyEffect")) {
				return;
			}
			h.removeEffect(h.getEffect("agonyEffect"));
			double splash = SkillConfigManager.getUseSetting(h, skill, "splashPercent", 50, false);
			double heal = SkillConfigManager.getUseSetting(h, skill, "healPercent", 50, false);
			Iterator<Entity> nearby = event.getEntity().getNearbyEntities(5, 5, 5).iterator();
			double damage = event.getDamage()*splash*0.01;
			double healAmount = damage*heal*0.01;
			while(nearby.hasNext()) {
				Entity next = nearby.next();
				if(!(next instanceof LivingEntity)) {
					continue;
				}
				LivingEntity lE = (LivingEntity)next;
				if(!Skill.damageCheck(h.getPlayer(),lE)) {
					continue;
				}
				Skill.damageEntity(lE, h.getEntity(), damage, DamageCause.ENTITY_ATTACK);
				HeroRegainHealthEvent healthEvent = new HeroRegainHealthEvent(h, healAmount, skill);
				Bukkit.getPluginManager().callEvent(healthEvent);
				if(healthEvent.isCancelled()) {
					continue;
				}
				double actualHeal = healthEvent.getAmount();
				double newHealth = p.getHealth()+actualHeal;
				if(newHealth > p.getMaxHealth()) {
					p.setHealth(p.getMaxHealth());
					continue;
				} else {
					p.setHealth(p.getHealth()+actualHeal);
					continue;
				}
			}
			
		}
	}
	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection node = super.getDefaultConfig();
		node.set("splashPercent", 50);
		node.set("healPercent", 50);
		return node;
	}

}
