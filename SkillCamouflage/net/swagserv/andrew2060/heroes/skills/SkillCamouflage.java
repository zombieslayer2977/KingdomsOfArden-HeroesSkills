package net.swagserv.andrew2060.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;

public class SkillCamouflage extends PassiveSkill {

	public SkillCamouflage(Heroes plugin) {
		super(plugin, "Camouflage");
		setDescription("Passive: ");
	}

	@Override
	public String getDescription(Hero arg0) {
		return getDescription();
	}

}
