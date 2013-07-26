
package net.kingdomsofarden.andrew2060.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.PeriodicEffect;

public class AuraEffect extends PeriodicEffect {
	AuraWrapper fWrapper;
	public AuraEffect(Heroes plugin, AuraWrapper funcWrapper) {
		super(plugin, "AuraEffect", 20L);
		fWrapper = funcWrapper;
	}
	
	@Override
	public void applyToHero(Hero h) {
		fWrapper.onApply(h);
	}
	
	@Override
	public void tickHero(Hero h) {
		fWrapper.onTick(h);
	}
	@Override
	public void removeFromHero(Hero h) {
		fWrapper.onEnd(h);
	}
}
