package net.swagserv.andrew2060.heroes.skills;


import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.swagserv.andrew2060.heroes.skills.turretModules.TurretEffect;
import net.swagserv.andrew2060.heroes.skills.turretModules.TurretFireWrapper;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.effects.common.RootEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillCommandStaticField extends ActiveSkill {

	public SkillCommandStaticField(Heroes plugin) {
		super(plugin, "CommandStaticField");
		setDescription("Command: StaticField: Every ten firing cycles, the turret will fire out a static field dealing 40 physical damage (affected by armor) to everyone in its radius and paralyzing them for 2 seconds");
		setUsage("/skill commandstaticfield");
		setArgumentRange(0,0);
		setIdentifiers("skill commandstaticfield");
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
			tE = (TurretEffect)h.getEffect("TurretEffect");
		}
		StaticTurret fireFunc = new StaticTurret();
		tE.setFireFunctionWrapper(fireFunc);
		return SkillResult.NORMAL;
	}
	private class StaticTurret extends TurretFireWrapper {
		int charges;
		public StaticTurret() {
			this.charges = 10;
		}
		@Override
		public void fire(Hero h, Location loc, double range) {
			if(!(charges == 10)) {
				charges++;
				return;
			}
			Arrow a = loc.getWorld().spawnArrow(loc, new Vector(0,0,0), 0.6f, 1.6f);
			Iterator<Entity> nearby = a.getNearbyEntities(range*2, range*2, range*2).iterator();

			while(nearby.hasNext()) {
				Entity next = nearby.next();
				if(!(next instanceof LivingEntity)) {
					continue;
				}
				if(!((LivingEntity)next).hasLineOfSight(a)) {
					continue;
				}
				if(Skill.damageCheck(h.getPlayer(), (LivingEntity) next) && (LivingEntity)next != h.getEntity()) {
					Skill.damageEntity((LivingEntity)next, h.getEntity(), 40, DamageCause.ENTITY_ATTACK);
					CharacterTemplate cT = plugin.getCharacterManager().getCharacter((LivingEntity)next);
					cT.addEffect(new RootEffect(null, 2000));
				}
			}
			charges = 0;
			a.remove();
			return;
		}
	}
	@Override
	public String getDescription(Hero h) {
		return getDescription();
	}

}
