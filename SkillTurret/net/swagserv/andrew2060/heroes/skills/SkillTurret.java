package net.swagserv.andrew2060.heroes.skills;

import java.util.ArrayList;
import java.util.Iterator;


import net.swagserv.andrew2060.heroes.skills.turretModules.Turret;
import net.swagserv.andrew2060.heroes.skills.turretModules.TurretEffect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.server.PluginDisableEvent;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;

public class SkillTurret extends ActiveSkill {
	final private ArrayList<Turret> turrets;
	
	public class DestroyTurretListener implements Listener {

		private SkillTurret skill;
		public DestroyTurretListener(SkillTurret skill) {	
			this.skill = skill;
		}
		@EventHandler
		public void onPluginDisable(PluginDisableEvent event) {
			if(event.getPlugin() == plugin) {
				Iterator<Turret> turrets = skill.getTurrets().iterator();
				while(turrets.hasNext()) {
					Turret turret = turrets.next();
					turret.destroyTurretNonCancellable();
				}
			}
		}
		@EventHandler
		public void onPlayerDeath(PlayerDeathEvent event) {
			Hero h = skill.plugin.getCharacterManager().getHero(event.getEntity());
			if(h.hasEffect("TurretEffect")) {
				TurretEffect tE = (TurretEffect) h.getEffect("TurretEffect");
				Iterator<Turret> turrets = tE.getCreatedTurrets().iterator();
				while(turrets.hasNext()) {
					Turret t = turrets.next();
					t.destroyTurretNonCancellable();
					skill.turrets.remove(t);
				}
			}
		}

	}
	public SkillTurret(Heroes plugin) {
		super(plugin, "Turret");
		setArgumentRange(0,0);
		setUsage("/skill turret");
		setIdentifiers("skill turret");
		setDescription("Places a turret at target location that has range $1 blocks and lasts for $2 seconds (Maximum $3 turrets). The effects of the turrets can be controlled by various command skills.");
		//List of turrets
		turrets = new ArrayList<Turret>();
		//Every second access this list of turret locations and then fire arrows at nearby entities
		Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			@Override
			public void run() {
				//An iterator is basically a way to go up/down a list
				Iterator<Turret> turretIt= getTurrets().iterator();
				while(turretIt.hasNext()) {
					Turret turret = turretIt.next();
					//If turret is past the expiration time
					if(System.currentTimeMillis() > turret.getExpirationTime()) {
						//remove the turret and fire nothing
						if(!turret.destroyTurret()) {
							return;
						}
						getTurrets().remove(turret);
						return;
					} else {
						turret.fireTurret();
					}
				}
			}
			
		}, 0, 20L);
		//Registers a listener for when plugin disables to destroy remaining turrets
		Bukkit.getPluginManager().registerEvents(new DestroyTurretListener(this), plugin);
	}
	//This function defines what happens when someone uses the skill
	@Override
	public SkillResult use(Hero h, String[] args) {
		if(!h.hasEffect("TurretEffect")) {
			h.addEffect(new TurretEffect(plugin, this));
		}
		TurretEffect tE = (TurretEffect) h.getEffect("TurretEffect");
		//Get the amount of turrets this guy already has, if greater than the allowed amount, remove his oldest turret.
		int level = h.getLevel();
		int number = 0;
		if(level < 25) {
			number = 1;
		}
		if(level >=25 && level < 50) {
			number = 2;
		}
		if(level >= 50 && level < 75) {
			number = 3;
		}
		if(level >= 75) {
			number = 4;
		}
		//First we get the location where the user is looking (up to 10 blocks away)
		Location loc = h.getPlayer().getTargetBlock(null, 10).getRelative(BlockFace.UP).getLocation();
		//We determine how long this guy's turret lasts
		long duration = SkillConfigManager.getUseSetting(h, this, "base-duration", 60000, false) + SkillConfigManager.getUseSetting(h, this, "duration-per-level", 1000, false) * h.getLevel();
		double range = SkillConfigManager.getUseSetting(h, this, "range", 5, false) + SkillConfigManager.getUseSetting(h, this, "range-increase-per-level", 0.1, false) * h.getLevel();
		//We create a new ArrowTurret object
		Turret newTurret = new Turret(loc, range , System.currentTimeMillis() + duration,h);
		//We spawn the turret into the world, if not enough space, function will return false
		if(!newTurret.createTurret()) {
			h.getPlayer().sendMessage("Not enough space for a turret!");
			return SkillResult.INVALID_TARGET_NO_MSG;
		}
		//We add the turret to a list of turrets so that the scheduled task can fire it
		tE.addNewTurret(newTurret);
		h.getPlayer().sendMessage("A turret was successfully created!");
		turrets.add(newTurret);
		//Remove if over the limit here
		while(tE.getTurretNumber() > number) {
			Turret oldest = tE.getOldest();
			if(oldest==null) {
				break;
			}
			oldest.destroyTurretNonCancellable();
			getTurrets().remove(oldest);
		}
		return SkillResult.NORMAL;
	}

	@Override
	public String getDescription(Hero h) {
		long duration = SkillConfigManager.getUseSetting(h, this, "base-duration", 6015000, false) + SkillConfigManager.getUseSetting(h, this, "duration-per-level", 1000, false) * h.getLevel();
		double range = SkillConfigManager.getUseSetting(h, this, "range", 5, false) + SkillConfigManager.getUseSetting(h, this, "range-increase-per-level", 0.1, false) * h.getLevel();
		int level = h.getLevel();
		int number = 0;
		if(level < 25) {
			number = 1;
		}
		if(level >=25 && level < 50) {
			number = 2;
		}
		if(level >= 50 && level < 75) {
			number = 3;
		}
		if(level >= 75) {
			number = 4;
		}
		return getDescription()
				.replace("$1", range + "")
				.replace("$2", duration*0.001 + "")
				.replace("$3", number + "");
	}
	
	@Override
	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection node = super.getDefaultConfig();
		node.set("range", Integer.valueOf(5));
		node.set("range-increase-per-level", Double.valueOf(0.1));
		node.set("base-duration", Integer.valueOf(60000));
		node.set("duration-per-level", Integer.valueOf(1000));
		return node;
	}
	public ArrayList<Turret> getTurrets() {
		return turrets;
	}
}
