package net.swagserv.andrew2060.heroes.skills.turretModules;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.herocraftonline.heroes.characters.Hero;

public class Turret {
	private Location loc;
	private Hero creator;
	private long expirationTime;
	double range;
	Block a;
	Block b;
	Block c;
	Block d;
	Block e;
	Block f;

	public Turret(Location loc, double range, long expirationTime, Hero creator) {
		this.setExpirationTime(expirationTime);
		this.setLoc(loc);
		this.setCreator(creator);
		this.range = range;
	}
	/**
	 * Creates a turret
	 **/
	public boolean createTurret() {
		World w = getCreator().getPlayer().getWorld();
		//We want to get whatever block is here first so that we can replace it later (if its not air)
		a = w.getBlockAt(getLoc());
		b = w.getBlockAt(new Location(w,getLoc().getX() + 1, getLoc().getY() , getLoc().getZ()));
		c = w.getBlockAt(new Location(w,getLoc().getX() - 1, getLoc().getY() , getLoc().getZ()));
		d = w.getBlockAt(new Location(w,getLoc().getX() , getLoc().getY() , getLoc().getZ() + 1));
		e = w.getBlockAt(new Location(w,getLoc().getX() , getLoc().getY() , getLoc().getZ() - 1));
		f = w.getBlockAt(new Location(w,getLoc().getX(), getLoc().getY() + 1, getLoc().getZ()));
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
	public boolean destroyTurret() {
		TurretEffect tE = (TurretEffect)getCreator().getEffect("TurretEffect");
		if(tE != null) {
			TurretFireWrapper fW = tE.getFireFunctionWrapper();
			if(fW != null) {
				if(!fW.onDestroy(this)) {
					return false;
				}
			}
			tE.getCreatedTurrets().remove(this);
		}
		a.setType(Material.AIR);
		b.setType(Material.AIR);
		c.setType(Material.AIR);
		d.setType(Material.AIR);
		e.setType(Material.AIR);
		f.setType(Material.AIR);
		return true;
	}
	/**
	 * Fires the turret at any nearby entities
	 */
	public void fireTurret() {
		if(!getCreator().hasEffect("TurretEffect")) {
			return;
		}
		//Fire based on what is ordained within the hero's current TurretEffect
		TurretEffect tE = (TurretEffect)getCreator().getEffect("TurretEffect");
		TurretFireWrapper fW = tE.getFireFunctionWrapper();
		if(fW == null) {
			return; //No active mode selected, so we just exit out. Note that this means that turrets will always fire based on the last active effect
		}
		fW.fire(getCreator(), getLoc(),range);
		return;
	}

	public long getExpirationTime() {
		return expirationTime;
	}
	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}
	public Hero getCreator() {
		return creator;
	}
	public void setCreator(Hero creator) {
		this.creator = creator;
	}
	public Location getLoc() {
		return loc;
	}
	public void setLoc(Location loc) {
		this.loc = loc;
	}
	public double getRange() {
		return range;
	}
	public void destroyTurretNonCancellable() {
		TurretEffect tE = (TurretEffect)getCreator().getEffect("TurretEffect");
		if(tE != null) {
			tE.getCreatedTurrets().remove(this);
		}
		a.setType(Material.AIR);
		b.setType(Material.AIR);
		c.setType(Material.AIR);
		d.setType(Material.AIR);
		e.setType(Material.AIR);
		f.setType(Material.AIR);
		return;
	}
}