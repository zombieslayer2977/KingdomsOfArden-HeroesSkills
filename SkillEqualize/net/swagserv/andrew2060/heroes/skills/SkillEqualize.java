package net.swagserv.andrew2060.heroes.skills;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.party.HeroParty;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.SkillSetting;

public class SkillEqualize extends ActiveSkill {

	public SkillEqualize(Heroes plugin) {
		super(plugin, "Equalize");
		setDescription("On use, equalizes the percent max health of everyone in the party within a $1 block radius.");
		setUsage("/skill equalize");
		setIdentifiers("skill equalize");
		setArgumentRange(0,0);
		setTypes(SkillType.SILENCABLE, SkillType.HEAL);
	}
	@Override
	public SkillResult use(Hero h, String[] arg1) {
		HeroParty heroParty = h.getParty();
		Player p = h.getPlayer();
		if(heroParty == null) {
			h.getPlayer().sendMessage(ChatColor.GRAY + "You are not in a party!");
			return SkillResult.INVALID_TARGET_NO_MSG;
		}
		int maxHealthTotal = 0;
		int currentHealthTotal = 0;
		Iterator<Hero> partyMembers = heroParty.getMembers().iterator();
		Vector v = h.getPlayer().getLocation().toVector();
		int range = SkillConfigManager.getUseSetting(h, this, "maxrange", 0, false);
		boolean skipRangeCheck = (range == 0);						//0 for no maximum range
		while(partyMembers.hasNext()) {
			Hero h2 = partyMembers.next();
			Player p2 = h2.getPlayer();
			if(skipRangeCheck || p2.getLocation().toVector().distanceSquared(v) < range) {
				maxHealthTotal += p2.getMaxHealth();
				currentHealthTotal += p2.getHealth();
			}
			continue;
		}
		if(maxHealthTotal == p.getMaxHealth()) {
			h.getPlayer().sendMessage("There is noone in range to equalize with!");
			return SkillResult.INVALID_TARGET_NO_MSG;
		}
		double healthMultiplier = currentHealthTotal*Math.pow(maxHealthTotal, -1);
		Iterator<Hero> applyHealthIterator = heroParty.getMembers().iterator();
		while(applyHealthIterator.hasNext()) {
			Hero applyHero = applyHealthIterator.next();
			Player applyP = applyHero.getPlayer();
			if(skipRangeCheck || applyHero.getPlayer().getLocation().toVector().distanceSquared(v) < range) {
				applyP.setHealth((int) (applyP.getMaxHealth()*healthMultiplier));
				if(applyHero.getName() == h.getName()) {
					h.getPlayer().sendMessage(ChatColor.GRAY + "You used Equalize!");
				} else {
					applyHero.getPlayer().sendMessage(ChatColor.GRAY + h.getName() + " equalized your health with that of your party!");
				}
			}
		}
		return SkillResult.NORMAL;
	}

	@Override
	public String getDescription(Hero h) {
		int range = SkillConfigManager.getUseSetting(h, this, "maxrange", 10, false);
		return getDescription().replace("$1", range + "");
	}
	@Override
	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection node = super.getDefaultConfig();
		node.set("maxrange", Integer.valueOf(0));
		node.set(SkillSetting.COOLDOWN.node(), Integer.valueOf(180000));
		return node;
		
	}
}
