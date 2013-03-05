package net.swagserv.andrew2060.heroes.skills;

import java.util.ArrayList;
import java.util.Iterator;


import net.swagserv.andrew2060.heroes.skills.turretModules.Turret;
import net.swagserv.andrew2060.heroes.skills.turretModules.TurretEffect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;

public class SkillTurret extends ActiveSkill {
	final public ArrayList<Turret> turrets;

	public SkillTurret(Heroes plugin) {
		super(plugin, "Turret");
		setArgumentRange(0,0);
		setUsage("/skill turret");
		setIdentifiers("skill turret");
		setDescription("Places a turret at target location that has range $1 blocks and lasts for $2 seconds. The effects of the turrets can be controlled by various command skills.");
		//List of turrets
		turrets = new ArrayList<Turret>();
		//Every second access this list of turret locations and then fire arrows at nearby entities
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				//An iterator is basically a way to go up/down a list
				Iterator<Turret> turretIt= turrets.iterator();
				while(turretIt.hasNext()) {
					Turret turret = turretIt.next();
					//If turret is past the expiration time
					if(System.currentTimeMillis() > turret.getExpirationTime()) {
						//remove the turret and fire nothing
						turrets.remove(turret);
						turret.destroyTurret();
						return;
					} else {
						turret.fireTurret();
					}
				}
				
			}
			
		}, 0, 20L);
	}
	//This function defines what happens when someone uses the skill
	@Override
	public SkillResult use(Hero h, String[] args) {
		if(!h.hasEffect("TurretEffect")) {
			h.addEffect(new TurretEffect(plugin, this));
		}
		//First we get the location where the user is looking (up to 10 blocks away)
		Location loc = h.getPlayer().getTargetBlock(null, 10).getRelative(BlockFace.UP).getLocation();
		//We determine how long this guy's turret lasts
		long duration = SkillConfigManager.getUseSetting(h, this, "base-duration", 15000, false) + SkillConfigManager.getUseSetting(h, this, "duration-per-level", 1000, false) * h.getLevel();
		double range = SkillConfigManager.getUseSetting(h, this, "range", 5, false) + SkillConfigManager.getUseSetting(h, this, "range-increase-per-level", 0.1, false) * h.getLevel();
		//We create a new ArrowTurret object
		Turret newTurret = new Turret(loc, range , System.currentTimeMillis() + duration,h);
		//We spawn the turret into the world, if not enough space, function will return false
		if(!newTurret.createTurret()) {
			h.getPlayer().sendMessage("Not enough space for a turret!");
			return SkillResult.INVALID_TARGET_NO_MSG;
		}
		//We add the turret to a list of turrets so that the scheduled task can fire it
		h.getPlayer().sendMessage("A turret was successfully created!");
		turrets.add(newTurret);
		return SkillResult.NORMAL;
	}

	@Override
	public String getDescription(Hero h) {
		long duration = SkillConfigManager.getUseSetting(h, this, "base-duration", 15000, false) + SkillConfigManager.getUseSetting(h, this, "duration-per-level", 1000, false) * h.getLevel();
		double range = SkillConfigManager.getUseSetting(h, this, "range", 5, false) + SkillConfigManager.getUseSetting(h, this, "range-increase-per-level", 0.1, false) * h.getLevel();
		return getDescription()
				.replace("$1", range + "")
				.replace("$2", duration*0.001 + "");
	}
	
	@Override
	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection node = super.getDefaultConfig();
		node.set("range", Integer.valueOf(5));
		node.set("range-increase-per-level", Double.valueOf(0.1));
		node.set("base-duration", Integer.valueOf(15000));
		node.set("duration-per-level", Integer.valueOf(1000));
		return node;
	}
}
