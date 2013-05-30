package net.swagserv.andrew2060.heroes.skills;


import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

import net.swagserv.andrew2060.heroes.skills.turretModules.Turret;
import net.swagserv.andrew2060.heroes.skills.turretModules.TurretEffect;
import net.swagserv.andrew2060.heroes.skills.turretModules.TurretFireWrapper;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillCommandSelfDestruct extends ActiveSkill {

	public SkillCommandSelfDestruct(Heroes plugin) {
		super(plugin, "CommandSelfDestruct");
		setDescription("Command: SelfDestruct: Self-destructs all your turrets, dealing $1 damage per turret to all units within range");
		setUsage("/skill commandselfdestruct");
		setArgumentRange(0,0);
		setIdentifiers("skill commandselfdestruct");
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
		tE.setFireFunctionWrapper(new NullTurret());
		ArrayList<Turret> turrets = ((SkillTurret)this.plugin.getSkillManager().getSkill("Turret")).getTurrets();
		Iterator<Turret> turretIt = turrets.iterator();
		while(turretIt.hasNext()) {
			Turret next = turretIt.next();
			if(next.getCreator() == h) {
				next.setExpirationTime(System.currentTimeMillis());
				Location loc = next.getLoc();
				loc.getWorld().createExplosion(loc,1.0F);
				next.destroyTurret();
				Arrow a = loc.getWorld().spawnArrow(loc, new Vector(0,0,0), 0.6f, 1.6f);
				Iterator<Entity> nearby = a.getNearbyEntities(next.getRange()*2, next.getRange()*2, next.getRange()*2).iterator();
				while(nearby.hasNext()) {
					Entity nextEnt = nearby.next();
					if(!(nextEnt instanceof LivingEntity)) {
						continue;
					}
					if(Skill.damageCheck(h.getPlayer(), (LivingEntity) nextEnt) && (LivingEntity)nextEnt != h.getEntity()) {
						this.addSpellTarget(nextEnt, h);
						Skill.damageEntity((LivingEntity) nextEnt, h.getEntity(), 20, DamageCause.MAGIC);
					}
				}
				a.remove();
			} else {
				continue;
			}
		}
		
		return SkillResult.NORMAL;
	}
	@Override
	public String getDescription(Hero h) {
		return getDescription()
				.replace("$1", h.getLevel() + "");
	}
	private class NullTurret extends TurretFireWrapper {

		@Override
		public void fire(Hero h, Location loc, double range) {
			return;
		}
	}
}
