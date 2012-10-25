package net.swagserv.andrew2060.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.Monster;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.PeriodicExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.util.Setting;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class SkillEarthquake extends ActiveSkill {
	public SkillEarthquake(Heroes plugin) {
		super(plugin, "Earthquake");
		setUsage("/skill earthquake");
		setArgumentRange(0, 0);
		setIdentifiers(new String[] { "skill earthquake" });
		setDescription("Using extensive knowledge of faultlines that exist below the surface of the earth, the user triggers shockwaves that lasts for $1 seconds, dealing $2 damage (enemies only) and applying a slight knockup effect to everyone (including allies) in a $3 block radius every second.");
	}

	public String getDescription(Hero hero) {
		int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 30000, false);
		int damage = SkillConfigManager.getUseSetting(hero, this, "damage", 10, false);
		int range = SkillConfigManager.getUseSetting(hero, this, "range", 10, false);
		return getDescription().replace("$1", duration*0.001 +"").replace("$2", damage +"").replace("$3", range +"");
	}

	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection node = super.getDefaultConfig();
		node.set("damage", Integer.valueOf(10));
		node.set("range", Integer.valueOf(10));
		node.set(Setting.DURATION.node(), Integer.valueOf(30000));
		node.set(Setting.COOLDOWN.node(), Integer.valueOf(300000));
		return node;
	}
	public class EarthquakeEffect extends PeriodicExpirableEffect {

		public EarthquakeEffect(Skill skill, Heroes heroes, long period, long duration) {
			super(skill, "EarthquakeEffect", period, duration);
			this.types.add(EffectType.BENEFICIAL);
			this.types.add(EffectType.PHYSICAL);
		}

		@Override
		public void tickHero(Hero h) {
			int range = SkillConfigManager.getUseSetting(h, skill, "range", 10, false);
			List<Entity> near = h.getPlayer().getNearbyEntities(range, range, range);
			for(int x = 0; x<near.size(); x++) {
				if(!(near.get(x) instanceof LivingEntity)) {
					break;
				}
				LivingEntity e = (LivingEntity) near.get(x);
				Vector original = e.getLocation().toVector();
				Vector to = e.getLocation().add(0, 1, 0).toVector();
				Vector applied = to.subtract(original).normalize();
				Random random = new Random();
				if(e instanceof Player) {		
					int roll = random.nextInt(100);
					if(roll < 30) {
						((Player)e).addPotionEffect(PotionEffectType.CONFUSION.createEffect(400,10));
					}
				}
				e.setVelocity(applied);
				e.getWorld().createExplosion(e.getLocation().subtract(0, 6, 0), 0F);
				if(!SkillEarthquake.damageCheck(h.getPlayer(), e)) {
					continue;
				}
				SkillEarthquake.damageEntity(e, h.getEntity(), SkillConfigManager.getUseSetting(h, skill, "damage", 10, false), DamageCause.ENTITY_ATTACK);
			}
		}

		@Override
		public void tickMonster(Monster m) {
			// TODO Auto-generated method stub
			
		}
		
	}
	public SkillResult use(Hero hero, String[] arg1) {
		broadcast(hero.getPlayer().getLocation(), hero.getName() + ChatColor.GRAY + " triggered an earthquake!");
		int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 30000, false);
		hero.addEffect(new EarthquakeEffect(this, this.plugin, 1000L, duration));
		return SkillResult.NORMAL;
	}
}