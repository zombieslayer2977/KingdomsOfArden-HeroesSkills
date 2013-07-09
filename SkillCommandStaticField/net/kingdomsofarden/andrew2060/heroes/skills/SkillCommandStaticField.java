package net.kingdomsofarden.andrew2060.heroes.skills;


import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import net.kingdomsofarden.andrew2060.heroes.skills.turretModules.TurretEffect;
import net.kingdomsofarden.andrew2060.heroes.skills.turretModules.TurretFireWrapper;

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
		broadcast(h.getPlayer().getLocation(), "§7[§2Skill§7]$1 activated static field on his turrets", new Object[] {h.getName()});
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
		public void fire(Hero h, Location loc, double range, List<LivingEntity> validTargets) {
			if(!(charges == 10)) {
				charges++;
				return;
			}
			Iterator<LivingEntity> nearby = validTargets.iterator();
			while(nearby.hasNext()) {
				LivingEntity next = nearby.next();
				Skill.damageEntity((LivingEntity)next, h.getEntity(), 40D, DamageCause.ENTITY_ATTACK);
				CharacterTemplate cT = plugin.getCharacterManager().getCharacter((LivingEntity)next);
				cT.addEffect(new RootEffect(SkillCommandStaticField.this, 2000));
			}
			charges = 0;
			return;
		}
	}
	@Override
	public String getDescription(Hero h) {
		return getDescription();
	}

}
