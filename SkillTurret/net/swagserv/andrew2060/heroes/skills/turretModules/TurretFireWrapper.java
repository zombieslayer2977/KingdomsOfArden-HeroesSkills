package net.swagserv.andrew2060.heroes.skills.turretModules;

import org.bukkit.Location;

import com.herocraftonline.heroes.characters.Hero;

public abstract class TurretFireWrapper {
	public abstract void fire(Hero h, Location loc, double range);
}
