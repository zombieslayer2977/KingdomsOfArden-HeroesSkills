package net.swagserv.andrew2060.heroes.skills.turretModules;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.skill.Skill;

public class TurretEffect extends Effect {
	private TurretFireWrapper fireFunctionWrapper;
	public TurretEffect(Heroes plugin, Skill skill) {
		super(plugin,skill,"TurretEffect");
		this.setFireFunctionWrapper(null);
	}
	public TurretFireWrapper getFireFunctionWrapper() {
		return fireFunctionWrapper;
	}
	public void setFireFunctionWrapper(TurretFireWrapper fireFunctionWrapper) {
		this.fireFunctionWrapper = fireFunctionWrapper;
	}

}
