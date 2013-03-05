package net.swagserv.andrew2060.heroes.skills;


import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.swagserv.andrew2060.heroes.skills.turretModules.TurretEffect;
import net.swagserv.andrew2060.heroes.skills.turretModules.TurretFireWrapper;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillCommandSlow extends ActiveSkill {

	public SkillCommandSlow(Heroes plugin) {
		super(plugin, "CommandSlow");
		setDescription("Command: Slow: Turrets will slow all enemies in range by 15/30/45/60% at levels 1/25/50/75");
		setUsage("/skill commandslow");
		setArgumentRange(0,0);
		setIdentifiers("skill commandslow");
	}

	@Override
	public SkillResult use(Hero h, String[] arg1) {
		TurretEffect tE;
		if(!h.hasEffect("TurretEffect")) {
			tE = new TurretEffect(plugin, this);
			h.addEffect(tE);
		} else {
			tE = (TurretEffect)h.getEffect("TurretEffect");
		}
		int slowLevel = 0;
		int heroLevel = h.getLevel();
		if(heroLevel < 25) {
			slowLevel = 1;
		}
		if(heroLevel >= 25 && heroLevel < 50) {
			slowLevel = 2;
		}
		if(heroLevel >= 50 && heroLevel < 75) {
			slowLevel = 3;
		}
		if(heroLevel >= 75) {
			slowLevel = 4;
		}
		SlowTurret fireFunc = new SlowTurret(slowLevel);
		tE.setFireFunctionWrapper(fireFunc);
		return SkillResult.NORMAL;
	}
	private class SlowTurret extends TurretFireWrapper {
		int slowLevel;
		public SlowTurret(int slowLevel) {
			this.slowLevel = slowLevel;
		}
		@Override
		public void fire(Hero h, Location loc, double range) {
			Arrow a = loc.getWorld().spawnArrow(loc, new Vector(0,0,0), 0.6f, 1.6f);
			Iterator<Entity> nearby = a.getNearbyEntities(range*2, range*2, range*2).iterator();

			while(nearby.hasNext()) {
				Entity next = nearby.next();
				if(!(next instanceof LivingEntity)) {
					continue;
				}
				if(Skill.damageCheck(h.getPlayer(), (LivingEntity) next) && (LivingEntity)next != h.getEntity()) {
					((LivingEntity)next).addPotionEffect(PotionEffectType.SLOW.createEffect(40, slowLevel));
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
