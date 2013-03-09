package net.swagserv.andrew2060.heroes.skills;


import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import net.swagserv.andrew2060.heroes.skills.turretModules.TurretEffect;
import net.swagserv.andrew2060.heroes.skills.turretModules.TurretFireWrapper;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillCommandFlamethrower extends ActiveSkill {

	public SkillCommandFlamethrower(Heroes plugin) {
		super(plugin, "CommandFlamethrower");
		setDescription("Command: Flamethrower: Puts all active turrets in flamethrower state - Turrets will set all enemies in range on fire");
		setUsage("/skill commandflamethrower");
		setArgumentRange(0,0);
		setIdentifiers("skill commandflamethrower");
	}

	@Override
	public SkillResult use(Hero h, String[] arg1) {
		if(h.hasEffect("TurretEffectCooldown")) {
			h.getPlayer().sendMessage("You must wait 10 seconds between using different command skills!");
			return SkillResult.NORMAL;
		}
		h.addEffect(new ExpirableEffect(this,this.plugin,"TurretEffectCooldown",10000));
		TurretEffect tE;
		if(!h.hasEffect("TurretEffect")) {
			tE = new TurretEffect(plugin, this);
			h.addEffect(tE);
		} else {
			tE = (TurretEffect) h.getEffect("TurretEffect");
		}
		FlameThrowerTurret fireFunc = new FlameThrowerTurret();
		tE.setFireFunctionWrapper(fireFunc);
		return SkillResult.NORMAL;
	}
	private class FlameThrowerTurret extends TurretFireWrapper {

		@Override
		public void fire(Hero h, Location loc, double range) {
			Arrow a = loc.getWorld().spawnArrow(loc, new Vector(0,0,0), 0.6f, 1.6f);
			Iterator<Entity> nearby = a.getNearbyEntities(range*2, range*2, range*2).iterator();
			while(nearby.hasNext()) {
				Entity next = nearby.next();
				if(!(next instanceof LivingEntity)) {
					continue;
				}
				if(Skill.damageCheck(h.getPlayer(), (LivingEntity) next)  && (LivingEntity)next != h.getEntity()) {
					next.setFireTicks(100);
				}
			}
			a.remove();
			return;
		}
		
	}
	@Override
	public String getDescription(Hero h) {
		return getDescription();
	}

}
