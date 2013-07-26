package net.kingdomsofarden.andrew2060.heroes.skills;

import com.herocraftonline.heroes.characters.Hero;

public abstract class AuraWrapper {
	public String auraName;
	public AuraWrapper(String auraName) {
		this.auraName = auraName;
	}
	public abstract void onApply(Hero h);
	public abstract void onTick(Hero h);
	public abstract void onEnd(Hero h);
}
