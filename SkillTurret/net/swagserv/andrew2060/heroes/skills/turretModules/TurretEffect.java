package net.swagserv.andrew2060.heroes.skills.turretModules;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.skill.Skill;

public class TurretEffect extends Effect {
	private int numberOfTurrets;
	private TurretFireWrapper fireFunctionWrapper;
	private LinkedList<Turret> queue;
	public TurretEffect(Heroes plugin, Skill skill) {
		super(plugin,skill,"TurretEffect");
		this.setFireFunctionWrapper(null);
		this.numberOfTurrets = 0;
		this.queue = new LinkedList<Turret>();
	}
	public TurretFireWrapper getFireFunctionWrapper() {
		return fireFunctionWrapper;
	}
	public void setFireFunctionWrapper(TurretFireWrapper fireFunctionWrapper) {
		this.fireFunctionWrapper = fireFunctionWrapper;
	}
	public int getTurretNumber() {
		return numberOfTurrets;
	}
	public Turret getOldest() {
		if(queue.isEmpty()) {
			return null;
		} else {
			Turret first = queue.getFirst();
			return first;
		}
	}
	public boolean removeTurret(Turret turret) {
		if(queue.isEmpty()) {
			return false;
		} else {
			try {
				queue.remove(turret);
				numberOfTurrets--;
				if(numberOfTurrets < 0) {
					numberOfTurrets = 0;
				}
				return true;
			} catch (NoSuchElementException e) {
				System.out.println("Attempted to remove a turret from queue where it does not exist");
				System.out.println("=====Turret Info=====");
				System.out.println("Owner: " + turret.getCreator().getName());
				System.out.println("Location: " + turret.getLoc().toString());
				return false;
			}
		}
	}
	public LinkedList<Turret> getCreatedTurrets() {
		return queue;
	}
	public Turret removeOldestTurret() {
		Turret turret = null;
		try {
			turret = queue.removeFirst();
			numberOfTurrets--;
			if(numberOfTurrets < 0) {
				numberOfTurrets = 0;
			}
		} catch(NoSuchElementException e) {
			System.out.println("Attempted to Remove Oldest when there is no Oldest");
			e.getCause().printStackTrace();
			return null;
		}
		
		return turret;
	}
	public void addNewTurret(Turret newTurret) {
		queue.addLast(newTurret);
		numberOfTurrets++;
	}

}
