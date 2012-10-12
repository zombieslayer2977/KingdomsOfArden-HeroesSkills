package net.swagserv.andrew2060.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.effects.PeriodicDamageEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.util.Messaging;
import com.herocraftonline.heroes.util.Setting;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class SkillBladeWaltz extends ActiveSkill {
	private String applyText;
	private String expireText;

	public SkillBladeWaltz(Heroes plugin) {
		super(plugin, "BladeWaltz");
		setDescription("When active, prowess with the blade allows the user to strike multiple targets in one swing. Additionally, the user strikes at weak points in armor, applying bleed effects on hit. Finally, while this skill is active, all incoming attacks are parried, returning half of all incoming damage. D: $1 seconds CD: $2 seconds");
		setUsage("/skill bladewaltz");
		setArgumentRange(0, 0);
		setIdentifiers(new String[] { "skill bladewaltz" });
		setTypes(new SkillType[] { SkillType.PHYSICAL, SkillType.DAMAGING });
	}
	public void init() {
		super.init();
		this.applyText = SkillConfigManager.getRaw(this, Setting.APPLY_TEXT.node(), "%target% is bleeding!").replace("%target%", "$1");
		this.expireText = SkillConfigManager.getRaw(this, Setting.EXPIRE_TEXT.node(), "%target% has stopped bleeding!").replace("%target%", "$1");
	}
	public String getDescription(Hero hero) {
		int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION.node(), 5000, false) + 
				SkillConfigManager.getUseSetting(hero, this, Setting.DURATION_INCREASE.node(), 200, false) * hero.getSkillLevel(this);
		int cooldown = SkillConfigManager.getUseSetting(hero, this, Setting.COOLDOWN.node(), 120000, false) - 
				SkillConfigManager.getUseSetting(hero, this, Setting.COOLDOWN_REDUCE.node(), 1000, false);
		String description = getDescription().replace("$1", duration*0.001+"").replace("$2", cooldown*0.001+"");
		return description;
	}
	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection node = super.getDefaultConfig();
		node.set(Setting.DURATION.node(), Integer.valueOf(5000));
		node.set(Setting.DURATION_INCREASE.node(), Integer.valueOf(100));
		node.set(Setting.COOLDOWN.node(), Integer.valueOf(120000));
		node.set(Setting.COOLDOWN_REDUCE.node(), Integer.valueOf(1000));
		return node;
	}
	public SkillResult use(Hero hero, String[] args) {
		int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION.node(), 5000, false) + 
				SkillConfigManager.getUseSetting(hero, this, Setting.DURATION_INCREASE.node(), 200, false) * hero.getSkillLevel(this);
		hero.addEffect(new BladeWaltzEffect(this, duration));
		return SkillResult.NORMAL;
	}
	public class BladeWaltzEffect extends ExpirableEffect {
		public BladeWaltzEffect(Skill skill, long duration) {
			super(skill, "BladeWaltzEffect", duration);
			this.types.add(EffectType.BENEFICIAL);
		}
		public void applyToHero(Hero hero) {
			super.applyToHero(hero);
			Player player = hero.getPlayer();
			broadcast(player.getLocation(), player.getName() + " has just entered a state of extreme concentration, beginning a blade waltz!", new Object[] { player.getDisplayName() });
		}
		public void removeFromHero(Hero hero) {
			super.removeFromHero(hero);
			Player player = hero.getPlayer();
			broadcast(player.getLocation(), player.getName() + "'s blade waltz has ended!", new Object[] { player.getDisplayName() });
		}
	}

	public class BleedSkillEffect extends PeriodicDamageEffect {
		public BleedSkillEffect(Skill skill, long duration, long period, int tickDamage, Player applier) { 	
			super(skill, "Bleed", period, duration, tickDamage, applier);
			this.types.add(EffectType.BLEED); 
		}

		public void apply(LivingEntity lEntity) {
			super.apply((CharacterTemplate)lEntity);
		}
		public void apply(Hero hero) {
			super.apply(hero);
			Player player = hero.getPlayer();
			broadcast(player.getLocation(), SkillBladeWaltz.this.applyText, new Object[] { player.getDisplayName() });
		}
		public void remove(LivingEntity lEntity) {
			super.remove((CharacterTemplate)lEntity);
			broadcast(lEntity.getLocation(), SkillBladeWaltz.this.expireText, new Object[] { Messaging.getLivingEntityName(lEntity).toLowerCase() });
		}

		public void remove(Hero hero) {
			super.remove(hero);
			Player player = hero.getPlayer();
			broadcast(player.getLocation(), SkillBladeWaltz.this.expireText, new Object[] { player.getDisplayName() });
		}
	}
	public class SkillListener implements Listener {
		public SkillListener() { }

		@EventHandler(priority=EventPriority.MONITOR)
		public void onOutgoingWeaponDamage(WeaponDamageEvent event) { 
			if (event.isCancelled()) {
				return;
			}
			if (!(event.getDamager() instanceof Player)) {
				return;
			}
			Hero h = SkillBladeWaltz.this.plugin.getCharacterManager().getHero((Player)event.getDamager());
			if (!h.hasEffect("BladeWaltzEffect")) {
				return;
			}
			Entity target = event.getEntity();
			if ((target instanceof Player)) {
				SkillBladeWaltz.this.plugin.getCharacterManager().getHero((Player)target).addEffect(new SkillBladeWaltz.BleedSkillEffect(SkillBladeWaltz.this, 10000L, 2000L, 1, h.getPlayer()));
			}
			if ((target instanceof LivingEntity)) {
				SkillBladeWaltz.this.plugin.getCharacterManager().getMonster((LivingEntity)target).addEffect(new SkillBladeWaltz.BleedSkillEffect(SkillBladeWaltz.this, 10000L, 2000L, 1, h.getPlayer()));
			}
			List<Entity> nearby = target.getNearbyEntities(5.0D, 5.0D, 5.0D);
			for (int x = 0; x < nearby.size(); x++) {
				if (!(nearby.get(x) instanceof LivingEntity)) {
					return;
				}
				if (!Skill.damageCheck(h.getPlayer(), (LivingEntity)nearby.get(x))) {
					return;
				}
				LivingEntity damager = (LivingEntity)event.getDamager();
				SkillBladeWaltz.this.damageEntity((LivingEntity)nearby.get(x), damager, event.getDamage() / 2);
			} 
		}

		@EventHandler(priority=EventPriority.MONITOR)
		public void onIncomingWeaponDamage(WeaponDamageEvent event) {
			if (event.isCancelled()) {
				return;
			}
			if (!(event.getEntity() instanceof Player)) {
				return;
			}
			Hero h = SkillBladeWaltz.this.plugin.getCharacterManager().getHero((Player)event.getEntity());
			if (!h.hasEffect("BladeWaltzEffect")) {
				return;
			}
			if (!Skill.damageCheck(h.getPlayer(), (LivingEntity)event.getDamager())) {
				return;
			}
			Skill.damageEntity((LivingEntity)event.getDamager(), h.getPlayer(), event.getDamage() / 2, DamageCause.ENTITY_ATTACK);
			if ((event.getDamager() instanceof Player))
				((Player)event.getDamager()).sendMessage(ChatColor.GRAY + "Parried!");
		}
	}
}