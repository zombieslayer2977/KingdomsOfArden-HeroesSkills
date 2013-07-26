package net.kingdomsofarden.andrew2060.heroes.skills;

import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.SkillSetting;

public class SkillParanoia extends ActiveSkill {

	public SkillParanoia(Heroes plugin) {
		super(plugin, "Paranoia");
		setDescription("On activation, the user projects a fearsome aura, consuming unfriendly players in a $1 block radius in fear for $2 seconds. After these $2 seconds, the user leeches on the fear of his prey to revitalize himself, healing himself for $3 health for every person still within a $4 block radius.");
		setUsage("/skill paranoia");
		setArgumentRange(0, 0);
		setIdentifiers(new String[] { "skill paranoia" });
		setTypes(new SkillType[] { SkillType.DARK, SkillType.HEAL });
	}

	@Override
	public SkillResult use(Hero hero, String[] arg1) {
		int radius1 = SkillConfigManager.getUseSetting(hero, this, "radius1", 15, false);
		final int radius2 = SkillConfigManager.getUseSetting(hero, this, "radius2", 10, false);
		int timebeforeheal = SkillConfigManager.getUseSetting(hero, this, "timebeforeheal", 5, false);
		final int heal = SkillConfigManager.getUseSetting(hero, this, "heal", 5, false);
		final Player p = hero.getPlayer();
		p.getWorld().playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 300, 0);
		Iterator<Entity> nearby = p.getNearbyEntities(radius1, radius1, radius1).iterator();
		while(nearby.hasNext()) {
			Entity e = nearby.next();
			if(!(e instanceof LivingEntity)) {
				continue;
			}
			if(!Skill.damageCheck(hero.getPlayer(), (LivingEntity) e)) {
				continue;
			}
			((LivingEntity) e).addPotionEffect(PotionEffectType.BLINDNESS.createEffect(timebeforeheal*80, 3));
		}
		this.broadcast(hero.getPlayer().getLocation(), ChatColor.BLACK + "Paranoiaaaaaaaa");
		Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {

			@Override
			public void run() {
				Iterator<Entity> nearbyEntities = p.getNearbyEntities(radius2, radius2, radius2).iterator();
				int count = 0;
				while(nearbyEntities.hasNext()) {
					Entity e = nearbyEntities.next();
					if(!(e instanceof LivingEntity)) {
						continue;
					}
					if(!Skill.damageCheck(p, (LivingEntity) e)) {
						continue;
					}
					++count;
				}
				int amounthealed = count * heal;
				if(p.getHealth() + amounthealed > p.getMaxHealth()) {
					p.setHealth(p.getMaxHealth());
					return;
				}
				p.setHealth(p.getHealth() + amounthealed);
				p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 3);
				p.sendMessage(ChatColor.DARK_GRAY + "[" 
						+ ChatColor.RED + "Paranoia" 
						+ ChatColor.DARK_GRAY + "]: Regained " + amounthealed + " health.");
			}
		}
		, timebeforeheal*20);
		return SkillResult.NORMAL;
	}

	@Override
	public String getDescription(Hero hero) {
		String description = getDescription()
				.replace("$1", SkillConfigManager.getUseSetting(hero, this, "radius1", 15, false) + "")
				.replace("$2", SkillConfigManager.getUseSetting(hero, this, "timebeforeheal", 5, false) + "")
				.replace("$3", SkillConfigManager.getUseSetting(hero, this, "heal", 5, false) + "")
				.replace("$2", SkillConfigManager.getUseSetting(hero, this, "radius2", 10, false) + "");
		return description;
	}
	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection node = super.getDefaultConfig();
		node.set("radius1", Integer.valueOf(15));
		node.set("timebeforeheal", Integer.valueOf(5));
		node.set("heal", Integer.valueOf(5));
		node.set("healradius", Integer.valueOf(10));
		node.set(SkillSetting.COOLDOWN.node(), Integer.valueOf(120000));
		return node;
	}

}
