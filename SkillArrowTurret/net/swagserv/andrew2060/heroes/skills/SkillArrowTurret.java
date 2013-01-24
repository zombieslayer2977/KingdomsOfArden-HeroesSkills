package net.swagserv.andrew2060.heroes.skills;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;

public class SkillArrowTurret extends ActiveSkill {
	final public ArrayList<ArrowTurret> turrets;

	public SkillArrowTurret(Heroes plugin) {
		super(plugin, "ArrowTurret");
		setArgumentRange(0,0);
		setUsage("/skill arrowturret");
		setIdentifiers("skill arrowturret");
		setDescription("Creates an arrow turret that fires arrows at nearby hostile targets within a $1 block radius that lasts for $2 seconds");
		//List of turrets
		turrets = new ArrayList<ArrowTurret>();
		//Every second access this list of turret locations and then fire arrows at nearby entities
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				//An iterator is basically a way to go up/down a list
				Iterator<ArrowTurret> turretIt= turrets.iterator();
				while(turretIt.hasNext()) {
					ArrowTurret turret = turretIt.next();
					//If turret is past the expiration time
					if(System.currentTimeMillis() > turret.expirationTime) {
						//remove the turret and fire nothing
						turrets.remove(turret);
						turret.destroyTurret();
						return;
					} else {
						turret.fireTurret();
					}
				}
				
			}
			
		}, 0, 10L);
	}
	//This function defines what happens when someone uses the skill
	@Override
	public SkillResult use(Hero h, String[] args) {
		//First we get the location where the user is looking (up to 10 blocks away)
		Location loc = h.getPlayer().getTargetBlock(null, 10).getRelative(BlockFace.UP).getLocation();
		//We determine how long this guy's turret lasts
		long duration = SkillConfigManager.getUseSetting(h, this, "base-duration", 15000, false) + SkillConfigManager.getUseSetting(h, this, "duration-per-level", 1000, false) * h.getLevel();
		double range = SkillConfigManager.getUseSetting(h, this, "range", 5, false) + SkillConfigManager.getUseSetting(h, this, "range-increase-per-level", 0.1, false) * h.getLevel();
		//We create a new ArrowTurret object
		ArrowTurret newTurret = new ArrowTurret(loc, range , System.currentTimeMillis() + duration,h);
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
	private class ArrowTurret {
		Location loc;
		Hero creator;
		long expirationTime;
		double range;
		Block a;
		Block b;
		Block c;
		Block d;
		Block e;
		Block f;

		public ArrowTurret(Location loc, double range, long expirationTime, Hero creator) {
			this.expirationTime = expirationTime;
			this.loc = loc;
			this.creator = creator;
			this.range = range;
		}
		/**
		 * Creates a turret
		 **/
		public boolean createTurret() {
			World w = creator.getPlayer().getWorld();
			//We want to get whatever block is here first so that we can replace it later (if its not air)
			a = w.getBlockAt(loc);
			b = w.getBlockAt(new Location(w,loc.getX() + 1, loc.getY() , loc.getZ()));
			c = w.getBlockAt(new Location(w,loc.getX() - 1, loc.getY() , loc.getZ()));
			d = w.getBlockAt(new Location(w,loc.getX() , loc.getY() , loc.getZ() + 1));
			e = w.getBlockAt(new Location(w,loc.getX() , loc.getY() , loc.getZ() - 1));
			f = w.getBlockAt(new Location(w,loc.getX(), loc.getY() + 1, loc.getZ()));
			//Check to make sure that these blocks are clear
			if(!(a.getType().equals(Material.AIR) 
					&& b.getType().equals(Material.AIR) 
					&& c.getType().equals(Material.AIR) 
					&& d.getType().equals(Material.AIR) 
					&& e.getType().equals(Material.AIR) 
					&& f.getType().equals(Material.AIR))) {
				return false;
			}
			//Create the turret
			a.setType(Material.FENCE);
			b.setType(Material.FENCE);
			c.setType(Material.FENCE);
			d.setType(Material.FENCE);
			e.setType(Material.FENCE);
			f.setType(Material.DISPENSER);
			return true;
		}
		/**
		 * Destroys the turret
		 */
		public void destroyTurret() {
			a.setType(Material.AIR);
			b.setType(Material.AIR);
			c.setType(Material.AIR);
			d.setType(Material.AIR);
			e.setType(Material.AIR);
			f.setType(Material.AIR);
			return;
		}
		/**
		 * Fires the turret at any nearby entities
		 */
		public void fireTurret() {
			//First we want to get all entities near a location
			World w = loc.getWorld();
			Iterator<LivingEntity> livingEntities = w.getLivingEntities().iterator();
			ArrayList<LivingEntity> nearby = new ArrayList<LivingEntity>();
			//Cycle through all currently existing livingEntities in the world and figure out their distance
			while(livingEntities.hasNext()) {
				LivingEntity lE = livingEntities.next();
				double rangeSquared = Math.pow(range, 2);
				if(lE.getLocation().toVector().distanceSquared(loc.toVector()) <= rangeSquared ) {
					//If within the specified range we add it to a list of nearby entities
					nearby.add(lE);
				}
				continue;
			}
			Iterator<LivingEntity> nearbyEntities = nearby.iterator();
			//If there is nothing nearby (ergo nearbyEntities is empty) hasNext() will return false->thus skipping this entire loop
			while(nearbyEntities.hasNext()) {
				LivingEntity lE = nearbyEntities.next();
				//First we check IFF
				if(!Skill.damageCheck(creator.getPlayer(), lE)) {
					continue;
				}
				//Calculate arrow trajectory
				Location l = loc.getBlock().getRelative(BlockFace.UP,2).getLocation();
				Location lELoc = lE.getLocation();
				Vector trajectory = new Vector(lELoc.getX() - l.getX(), lELoc.getY() - l.getY(), lELoc.getZ() - l.getZ());
				//We create a new arrow, fired from the turret
				Arrow arrow = w.spawnArrow(loc, trajectory, 1F, 1);
				arrow.setVelocity(trajectory.multiply(1));
			}
		}
	}
}
