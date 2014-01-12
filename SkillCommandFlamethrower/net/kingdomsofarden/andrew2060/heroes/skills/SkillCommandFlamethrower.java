package net.kingdomsofarden.andrew2060.heroes.skills;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import net.kingdomsofarden.andrew2060.heroes.skills.turretModules.TurretEffect;
import net.kingdomsofarden.andrew2060.heroes.skills.turretModules.TurretFireWrapper;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillCommandFlamethrower extends ActiveSkill implements Listener {

	public List<LivingEntity> exemptFire;
    public SkillCommandFlamethrower(Heroes plugin) {
		super(plugin, "CommandFlamethrower");
		setDescription("Command: Flamethrower: Puts all active turrets in flamethrower state - Turrets will set all enemies in range on fire");
		setUsage("/skill commandflamethrower");
		setArgumentRange(0,0);
		setIdentifiers("skill commandflamethrower");
		this.exemptFire = new ArrayList<LivingEntity>();
	}

	@Override
	public SkillResult use(Hero h, String[] arg1) {
		if(h.hasEffect("TurretEffectCooldown")) {
			h.getPlayer().sendMessage("You must wait 10 seconds between using different command skills!");
			return SkillResult.NORMAL;
		}
		broadcast(h.getPlayer().getLocation(), "§7[§2Skill§7] $1 activated flamethrower turret", new Object[] {h.getName()});
		h.addEffect(new ExpirableEffect(this,this.plugin,"TurretEffectCooldown",10000));
		TurretEffect tE;
		if(!h.hasEffect("TurretEffect")) {
			tE = new TurretEffect(plugin, this);
			h.addEffect(tE);
		} else {
			tE = (TurretEffect) h.getEffect("TurretEffect");
		}
		FlameThrowerTurret fireFunc = new FlameThrowerTurret(this);
		tE.setFireFunctionWrapper(fireFunc);
		return SkillResult.NORMAL;
	}
	private class FlameThrowerTurret extends TurretFireWrapper {
		
	    private SkillCommandFlamethrower skill;

        public FlameThrowerTurret(SkillCommandFlamethrower skill) {
		    this.skill = skill;
		}

        @Override
		public void fire(Hero h, Location loc, double range, List<LivingEntity> validTargets) {
			Iterator<LivingEntity> valid = validTargets.iterator();
			while(valid.hasNext()) {
				LivingEntity next = valid.next();
				addSpellTarget(next, h);
				Skill.damageEntity(next, h.getEntity(), 3D, DamageCause.CUSTOM, false);
				if(next.getFireTicks() > 19) {
				    return;
				} else {
				    next.setFireTicks(19);
				    skill.exemptFire.add(next);
				}
			}
		}	
	}
	@Override
	public String getDescription(Hero h) {
		return getDescription();
	}
	
	@EventHandler(ignoreCancelled = true) 
	public void onFireDamage(EntityDamageEvent event) {
	    if(event.getEntity() instanceof LivingEntity) {
	        LivingEntity lE = (LivingEntity) event.getEntity();
	        if(this.exemptFire.contains(lE)) {
	            if(event.getCause() == DamageCause.FIRE_TICK) {
	                event.setCancelled(true);
	                this.exemptFire.remove(lE);
	                return;
	            }
	        }
	    }
	}

}
