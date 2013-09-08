
package net.kingdomsofarden.andrew2060.heroes.skills.aura;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.effects.PeriodicEffect;

public class AuraEffect extends PeriodicEffect {
	public AuraWrapper fWrapper;
	public AuraEffect(Heroes plugin, AuraWrapper funcWrapper) {
		super(plugin, "AuraEffect", 40L);
		fWrapper = funcWrapper;
	}
	
	@Override
	public void applyToHero(Hero h) {
	    h.addEffect(new ExpirableEffect(null, plugin, "AuraChangeCooldown",10000));
		fWrapper.onApply(h);
		if(fWrapper != null && !fWrapper.auraName.equalsIgnoreCase("none")) {
		    broadcast(h.getPlayer().getLocation(), "§7[§2Skill§7]$1 has activated the aura $2", new Object[] {h.getName(),fWrapper.auraName});
		}
	}
	
	@Override
	public void tickHero(Hero h) {
		fWrapper.onTick(h);
	}
	@Override
	public void removeFromHero(Hero h) {
		fWrapper.onEnd(h);
		if(fWrapper != null && !fWrapper.auraName.equalsIgnoreCase("none")) {
            broadcast(h.getPlayer().getLocation(), "§7[§2Skill§7]$1 has stopped using the aura $2", new Object[] {h.getName(),fWrapper.auraName});
        }
	}

    public SkillResult setFWrapper(AuraWrapper wrapper, Hero h) {
        if(!h.hasEffect("AuraChangeCooldown")) {
            broadcast(h.getPlayer().getLocation(), "§7[§2Skill§7]$1 has stopped using the aura $2", new Object[] {h.getName(),fWrapper.auraName});
            this.fWrapper = wrapper;
            broadcast(h.getPlayer().getLocation(), "§7[§2Skill§7]$1 has activated the aura $2", new Object[] {h.getName(),fWrapper.auraName});
            return SkillResult.NORMAL;
        } else {
            h.getPlayer().sendMessage("§7[§2Skill§7]$1 Too soon to change auras again!");
            return SkillResult.INVALID_TARGET_NO_MSG;
        }
    }
}
