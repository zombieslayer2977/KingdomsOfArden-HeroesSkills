package net.swagserv.andrew2060.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.SkillUseEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

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

	public class RequiemEffect extends ExpirableEffect {
		public RequiemEffect(Skill skill, long duration) {
			super(skill, "RequiemEffect", duration);
			this.types.add(EffectType.INVULNERABILITY);
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
			if ((event.getDamager() instanceof Player)) {
				Hero h2 = this.skill.plugin.getCharacterManager().getHero((Player)event.getDamager());
				if (h2.hasEffectType(EffectType.INVULNERABILITY)) {
					event.setCancelled(true);
					h2.getPlayer().sendMessage(ChatColor.RED + "Cannot attack while charging up to self-destruct!");
				}
			}
			final Player p = (Player)event.getEntity();
			final Hero h = this.skill.plugin.getCharacterManager().getHero(p);
			if (!h.hasEffect("Requiem")) {
				return;
			}
			if (h.getHealth() - event.getDamage() > 1) {
				return;
			}
			LivingEntity finalHit = event.getDamager().getEntity();

			if (h.hasEffectType(EffectType.INVULNERABILITY)) {
				event.setDamage(0);
				if ((event.getDamager() instanceof Player)) {
					((Player)event.getDamager()).sendMessage(ChatColor.GRAY + "Invulnerable!");
				}
				return;
			}

			final LivingEntity finalDamager = finalHit;
			event.setCancelled(true);
			this.skill.broadcast(p.getLocation(), ChatColor.RED + p.getName() + "is charging up to self-destruct!", new Object[0]);
			SkillRequiem.RequiemEffect reqEffect = new SkillRequiem.RequiemEffect(SkillRequiem.this, 10000L);
			h.addEffect(reqEffect);
			Bukkit.getScheduler().scheduleSyncDelayedTask(this.skill.plugin, new Runnable() {
				public void run() {
					h.removeEffect(h.getEffect("RequiemEffect"));
					SkillRequiem.SkillListener.this.skill.damageEntity(h.getEntity(), finalDamager, 5000);
					p.getWorld().createExplosion(p.getLocation(), 0.0F);
					int dmg = h.getMana();
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
						SkillRequiem.SkillListener.this.skill.damageEntity((LivingEntity)nearby.get(x), h.getEntity(), dmg);
					}
				}
			}
			, 100L);
		}
		@EventHandler(priority=EventPriority.HIGHEST)
		public void onSkillUse(SkillUseEvent event) {
			if (event.getHero().hasEffect("RequiemEffect")) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.GRAY + "Cannot Use Skills While Charging Up To Self-Destruct!");
			}
		}

		@EventHandler(priority=EventPriority.MONITOR)
		public void onPlayerMove(PlayerMoveEvent event) { 
			if (event.isCancelled()) {
				return;
			}
			Hero h = SkillRequiem.this.plugin.getCharacterManager().getHero(event.getPlayer());
			if (h.hasEffect("RequiemEffect")) {
				h.getPlayer().getWorld().playEffect(h.getPlayer().getLocation(), Effect.SMOKE, BlockFace.UP);
				return;
			}
		}
	}
}