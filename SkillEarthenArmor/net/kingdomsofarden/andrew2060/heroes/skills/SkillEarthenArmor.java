package net.kingdomsofarden.andrew2060.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.Monster;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SkillEarthenArmor extends ActiveSkill {
	private String applyText;
	private String expireText;

	public SkillEarthenArmor(Heroes plugin) {
		super(plugin, "EarthenArmor");
		setDescription("Passive: Reduces all incoming damage by $1% Active: Shatters the armor, dealing $2 damage to all targets in an area. The armor takes $3 seconds to reaccumulate.");
		setUsage("/skill earthenarmor");
		setArgumentRange(0, 0);
    	setIdentifiers(new String[] { "skill earthenarmor" });
    	setTypes(new SkillType[] { SkillType.COUNTER, SkillType.EARTH, SkillType.PHYSICAL });
    	Bukkit.getServer().getPluginManager().registerEvents(new EAListener(), plugin);
	}
	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection section = super.getDefaultConfig();
		section.set(SkillSetting.DURATION.node(), Integer.valueOf(300000));
		section.set(SkillSetting.APPLY_TEXT.node(), "%hero% shattered their armor!");
		section.set(SkillSetting.EXPIRE_TEXT.node(), "%hero% has reaccumulated their earthen armor!");
		return section;
	}

	public void init() {
		super.init();
		this.applyText = SkillConfigManager.getRaw(this, SkillSetting.APPLY_TEXT, "%hero% shattered their earthen armor!").replace("%hero%", "$1");
		this.expireText = SkillConfigManager.getRaw(this, SkillSetting.EXPIRE_TEXT, "%hero% has reaccumulated their earthen armor!").replace("%hero%", "$1");
	}
	public SkillResult use(Hero hero, String[] args) {
		if (hero.hasEffect("EAEffect")) {
			hero.getPlayer().sendMessage("Your Earthen Armor hasn't re-accumulated yet!");
			return SkillResult.NORMAL;
		}
		broadcastExecuteText(hero);
		int duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 300000, false);
		List<Entity> entitylist = hero.getEntity().getNearbyEntities(10.0D, 10.0D, 10.0D);
		Player p = hero.getPlayer();
		double damage = p.getHealth() * 2;
		for (Entity entity : entitylist) {
			if (!(entity instanceof Player)) {
				if (((entity instanceof LivingEntity)) && (!(entity instanceof Player))) {
					Monster m = this.plugin.getCharacterManager().getMonster((LivingEntity)entity);
					Skill.damageCheck(p, m.getEntity());
					damageEntity(m.getEntity(), p, damage);
				} else if (((entity instanceof LivingEntity)) && ((entity instanceof Player))) {
					Hero m = this.plugin.getCharacterManager().getHero((Player)entity);
					Skill.damageCheck(p, m.getEntity());
					damageEntity(m.getEntity(), p, damage);
				}
			}
		}

		hero.addEffect(new EAEffect(this, duration));
		return SkillResult.NORMAL;
	}
	
	public String getDescription(Hero hero) {
		int duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 300000, false);
		double reduct = hero.getLevel() * 0.5D;
		int dmg = hero.getLevel() * 2;
		return getDescription().replace("$1", reduct+"").replace("$2", dmg+"").replace("$3", duration*0.001+"");
	}
	public class EAEffect extends ExpirableEffect {
		public EAEffect(Skill skill, long duration) {
			super(skill, "EAEffect", duration);
			this.types.add(EffectType.BENEFICIAL);
		}
		public void applyToHero(Hero hero) {
			super.applyToHero(hero);
			Player player = hero.getPlayer();
			broadcast(player.getLocation(), SkillEarthenArmor.this.applyText, new Object[] { player.getDisplayName() });
		}
		public void removeFromHero(Hero hero) {
			super.removeFromHero(hero);
			Player player = hero.getPlayer();
			broadcast(player.getLocation(), SkillEarthenArmor.this.expireText, new Object[] { player.getDisplayName() });
		}
	}
	public class EAListener implements Listener {
		public EAListener() {}

		@EventHandler(priority=EventPriority.HIGHEST)
		public void onWeaponDamage(WeaponDamageEvent event) { if (!event.isCancelled()) {
			return;
		}
		if (!(event.getDamager() instanceof Player)) {
			return;
		}
		Player p = (Player)event.getDamager();
		Hero h = SkillEarthenArmor.this.plugin.getCharacterManager().getHero(p);
		if (!h.hasAccessToSkill("EarthenArmor")) {
			return;
		}
		if (h.hasEffect("EAEffect")) {
			return;
		}
		double levelMultiplier = (100.0D - h.getLevel() * 0.5D) * 0.01D;
		event.setDamage((int)(event.getDamage() * levelMultiplier));
		}
	}
}