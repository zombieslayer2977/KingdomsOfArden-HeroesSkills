package net.swagserv.andrew2060.heroes.skills.turretModules;

import java.util.LinkedList;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.skill.Skill;

public class TurretEffect extends Effect {
	private int numberOfTurrets;
	private TurretFireWrapper fireFunctionWrapper;
	private LinkedList<Turret> queue = new LinkedList<Turret>();
	public TurretEffect(Heroes plugin, Skill skill) {
		super(plugin,skill,"TurretEffect");
		this.setFireFunctionWrapper(null);
		this.numberOfTurrets = 0;
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
		Turret first = queue.getFirst();
		return first;		
	}
	public LinkedList<Turret> getCreatedTurrets() {
		return queue;
	}
	public void removeOldestTurret() {
		queue.removeFirst();
		numberOfTurrets--;
		if(numberOfTurrets < 0) {
			numberOfTurrets = 0;
		}
	}
	public void addNewTurret(Turret newTurret) {
		queue.addLast(newTurret);
		numberOfTurrets++;
	}

}
