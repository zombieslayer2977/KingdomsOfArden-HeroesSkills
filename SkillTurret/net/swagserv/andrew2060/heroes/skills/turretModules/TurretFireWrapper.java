package net.swagserv.andrew2060.heroes.skills.turretModules;

import org.bukkit.Location;

import com.herocraftonline.heroes.characters.Hero;

public abstract class TurretFireWrapper {
	public abstract void fire(Hero h, Location loc, double range);

	public boolean onDestroy(Turret turret) {
		//By default we don't want to do anything. Other effects can cancel the destruction however.
		return true;
	}
}
