package net.swagserv.andrew2060.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.util.Setting;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SkillShockWave extends ActiveSkill {
	public SkillShockWave(Heroes plugin) {
		super(plugin, "ShockWave");
		setUsage("/skill shockwave");
		setArgumentRange(0, 0);
		setIdentifiers(new String[] { "skill shockwave" });
		setDescription("A shockwave of radius $1 is created that converges upon the user, pushing anything in its path towards the user and applying damage equal to twice the distance pulled.");
	}

	public String getDescription(Hero hero) {
		int range = SkillConfigManager.getUseSetting(hero, this, "range", 10, false);
		return getDescription().replace("$1", range +"");
	}

	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection node = super.getDefaultConfig();
		node.set("range", Integer.valueOf(10));
		node.set(Setting.COOLDOWN.node(), Integer.valueOf(60000));
		return node;
	}

	public SkillResult use(Hero hero, String[] arg1) {
		Player p = hero.getPlayer();
		broadcast(hero.getPlayer().getLocation(), hero.getName() + ChatColor.GRAY + " triggered a shockwave!");
		int range = SkillConfigManager.getUseSetting(hero, this, "range", 10, false);
		List<Entity> nearby = p.getNearbyEntities(range, range, range);
		for (int x = 0; x < nearby.size(); x++) {
			if ((nearby.get(x) instanceof LivingEntity)) {
				if (damageCheck(p, (LivingEntity)nearby.get(x))) {
					Vector v = p.getLocation().add(0.0D, 1.0D, 0.0D).toVector().subtract(((Entity)nearby.get(x)).getLocation().toVector()).normalize();
					v = v.multiply(0.9D);
					((Entity)nearby.get(x)).setVelocity(v);
					p.getWorld().playEffect(p.getLocation(), Effect.ZOMBIE_CHEW_WOODEN_DOOR, 1);
					double dmg = p.getLocation().toVector().distance(((Entity)nearby.get(x)).getLocation().toVector()) * 2.0D;
					damageEntity((LivingEntity)nearby.get(x), p, (int)dmg);
				}
			}
		}
		return SkillResult.NORMAL;
	}
}