package net.swagserv.andrew2060.heroes.skills;


import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
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
		broadcast(h.getPlayer().getLocation(), "§7[§2Skill§7]$1 activated flamethrower turret", new Object[] {h.getName()});
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
		public void fire(Hero h, Location loc, double range, List<LivingEntity> validTargets) {
			Iterator<LivingEntity> valid = validTargets.iterator();
			while(valid.hasNext()) {
				LivingEntity next = valid.next();
				addSpellTarget(next, h);
				Skill.damageEntity(next, h.getEntity(), 2, DamageCause.CUSTOM, false);
				next.setFireTicks(100);
			}
		}	
	}
	@Override
	public String getDescription(Hero h) {
		return getDescription();
	}

}
