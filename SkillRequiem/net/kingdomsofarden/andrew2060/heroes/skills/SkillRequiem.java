package net.kingdomsofarden.andrew2060.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.CharacterDamageEvent;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.SkillUseEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

public class SkillRequiem extends PassiveSkill {
	public SkillRequiem(Heroes plugin) {
		super(plugin, "Requiem");
		setDescription("Passive: Upon death, bearer is resurrected and rendered invulnerable for 5 seconds. After these 5 seconds, the player explodes, dealing damage equal to their remaining mana at time of death.");
		setEffectTypes(new EffectType[] { EffectType.BENEFICIAL });
		Bukkit.getServer().getPluginManager().registerEvents(new SkillListener(this), plugin);
	}

	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection node = super.getDefaultConfig();
		return node;
	}

	public String getDescription(Hero hero) {
		return getDescription();
	}

	public class RequiemEffect extends Effect {
		public RequiemEffect(Skill skill) {
			super(skill, "RequiemEffect");
		}

		public void applyToHero(Hero hero) {
			super.applyToHero(hero);
		}

		public void removeFromHero(Hero hero) {
			super.removeFromHero(hero);
		}
	}

	public class SkillListener implements Listener {
		Skill skill;

		public SkillListener(Skill skill) {
			this.skill = skill;
		}

		@EventHandler(priority=EventPriority.HIGHEST)
		public void onWeaponDamage(WeaponDamageEvent event) {
			if (event.isCancelled()) {
				return;
			}
			if (!(event.getEntity() instanceof Player)) {
				return;
			}
			final Player p = (Player)event.getEntity();
			final Hero h = this.skill.plugin.getCharacterManager().getHero(p);
			if (!h.hasEffect("Requiem")) {
				return;
			}
			if(h.hasEffect("RequiemEffect")) {
				return;
			}
			if (h.getPlayer().getHealth() - event.getDamage() > 1) {
				return;
			}
			LivingEntity finalHit = event.getDamager().getEntity();

			final LivingEntity finalDamager = finalHit;
			event.setDamage(0D);
			this.skill.broadcast(p.getLocation(), ChatColor.RED + p.getName() + "is charging up to self-destruct!", new Object[0]);
			SkillRequiem.RequiemEffect reqEffect = new SkillRequiem.RequiemEffect(SkillRequiem.this);
			h.addEffect(reqEffect);
			p.setHealth(p.getMaxHealth());
			scheduleExplosion(h, p, skill, finalDamager, DamageCause.ENTITY_ATTACK);
			return;
		}
		@EventHandler(priority = EventPriority.HIGH)
		public void onSkillDamage(SkillDamageEvent event) {
			if(event.isCancelled()) {
				return;
			}
			if (!(event.getEntity() instanceof Player)) {
				return;
			}

			final Player p = (Player)event.getEntity();
			final Hero h = this.skill.plugin.getCharacterManager().getHero(p);
			if (!h.hasEffect("Requiem")) {
				return;
			}
			if(h.hasEffect("RequiemEffect")) {
				return;
			}
			if (p.getHealth() - event.getDamage() > 1) {
				return;
			}
			LivingEntity finalHit = event.getDamager().getEntity();

			final LivingEntity finalDamager = finalHit;
			event.setDamage(0D);
			this.skill.broadcast(p.getLocation(), ChatColor.RED + p.getName() + "is charging up to self-destruct!", new Object[0]);
			SkillRequiem.RequiemEffect reqEffect = new SkillRequiem.RequiemEffect(SkillRequiem.this);
			h.addEffect(reqEffect);
			scheduleExplosion(h, p, skill, finalDamager, DamageCause.ENTITY_ATTACK);
			return;
		}
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onEnvironmentalDamage(CharacterDamageEvent event) {
			if(event.isCancelled()) {
				return;
			}
			if (!(event.getEntity() instanceof Player)) {
				return;
			}
			final Player p = (Player)event.getEntity();
			final Hero h = this.skill.plugin.getCharacterManager().getHero(p);
			if (!h.hasEffect("Requiem")) {
				return;
			}
			if(h.hasEffect("RequiemEffect")) {
				return;
			}
			if (p.getHealth() - event.getDamage() > 1) {
				return;
			}
			DamageCause cause = event.getCause();
			event.setDamage(0);
			this.skill.broadcast(p.getLocation(), ChatColor.RED + p.getName() + "is charging up to self-destruct!", new Object[0]);
			SkillRequiem.RequiemEffect reqEffect = new SkillRequiem.RequiemEffect(SkillRequiem.this);
			h.addEffect(reqEffect);
			scheduleExplosion(h, p, skill, (LivingEntity) event.getEntity(), cause);
			return;
		}
		@EventHandler(priority=EventPriority.HIGHEST)
		public void onSkillUse(SkillUseEvent event) {
			if (event.getHero().hasEffect("RequiemEffect")) {
				event.setCancelled(true);
				if(event.getPlayer() != null) {
					event.getPlayer().sendMessage(ChatColor.GRAY + "Cannot Use Skills While Charging Up To Self-Destruct!");
				}
			}
			return;
		}
		@EventHandler(priority=EventPriority.HIGHEST)
		public void onWeaponDamageGiver(WeaponDamageEvent event) {
			if (event.getDamager().hasEffect("RequiemEffect")) {
				event.setCancelled(true);
				if(event.getDamager() instanceof Hero) {
					((Hero)event.getDamager()).getPlayer().sendMessage(ChatColor.GRAY + "Cannot attack While Charging Up To Self-Destruct!");
				}
			}
			return;
		}
		@EventHandler(priority=EventPriority.HIGHEST)
		public void onEnvironmentalDamageGiven(CharacterDamageEvent event) {
			if (!(event.getEntity() instanceof Player)) {
				return;
			}
			if (this.skill.plugin.getCharacterManager().getHero((Player) event.getEntity()).hasEffect("RequiemEffect")) {
				event.setCancelled(true);
			}
		}
		@EventHandler(priority=EventPriority.HIGHEST)
		public void onSkillDamageGiven(SkillDamageEvent event) {
			if (!(event.getEntity() instanceof Player)) {
				return;
			}
			if (this.skill.plugin.getCharacterManager().getHero((Player) event.getEntity()).hasEffect("RequiemEffect")) {
				event.setDamage(0D);
				if(event.getDamager() instanceof Hero) {
					((Hero)event.getDamager()).getPlayer().sendMessage(ChatColor.GRAY + "Invulnerable!");
				}
			}
		}
		@EventHandler(priority=EventPriority.HIGHEST)
		public void onWeaponDamageGiven(WeaponDamageEvent event) {
			if (!(event.getEntity() instanceof Player)) {
				return;
			}
			if (this.skill.plugin.getCharacterManager().getHero((Player) event.getEntity()).hasEffect("RequiemEffect")) {
				event.setDamage(0D);
				if(event.getDamager() instanceof Hero) {
					((Hero)event.getDamager()).getPlayer().sendMessage(ChatColor.GRAY + "Invulnerable!");
				}
			}
		}


	}

	public void scheduleExplosion(final Hero h, final Player p, Skill skill, final LivingEntity finalDamager, final DamageCause dmgCause) {
		new BukkitRunnable() {
			public void run() {
				h.removeEffect(h.getEffect("RequiemEffect"));
				p.getWorld().createExplosion(p.getLocation(), 0.0F);
				double dmg = h.getMana();
				if (dmg < 20) {
					dmg = 20;
				}
				List<Entity> nearby = p.getNearbyEntities(5.0D, 5.0D, 5.0D);
				for (int x = 0; x < nearby.size(); x++) {
					if (!(nearby.get(x) instanceof LivingEntity)) {
						continue;
					}
					if (!Skill.damageCheck(h.getPlayer(), (LivingEntity)nearby.get(x))) {
						continue;
					}
					addSpellTarget(nearby.get(x),h);
					Skill.damageEntity((LivingEntity)nearby.get(x), h.getEntity(), dmg, DamageCause.ENTITY_ATTACK);
				}
				if(finalDamager != null) {
				    if(plugin.getCharacterManager().getCharacter(finalDamager) instanceof Hero) {
				        addSpellTarget(h.getEntity(), plugin.getCharacterManager().getHero((Player) finalDamager));
				    }
					Skill.damageEntity(h.getEntity(), finalDamager, 50000D, dmgCause);
				} else {
					p.setHealth(0);
					h.syncExperience();
				}
			}
		}.runTaskLater(this.plugin, 100L);
		
	}
}