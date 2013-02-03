package net.swagserv.andrew2060.heroes.skills;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass.ExperienceType;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.util.Messaging;
import com.herocraftonline.heroes.util.Setting;

public class SkillEnchant extends PassiveSkill {

	public SkillEnchant(Heroes plugin) {
		super(plugin, "Enchant");
		setDescription("You can Enchant Stuff!");
		Bukkit.getPluginManager().registerEvents(new EnchantListener(this), plugin);
	}
	private class EnchantListener implements Listener {

		private Skill skill;

		public EnchantListener(Skill skill) {
			this.skill = skill;
		}
		@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
		public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
			Hero h = skill.plugin.getCharacterManager().getHero(event.getEnchanter());
			if(!h.hasEffect("Enchant")) {
				Messaging.send(h.getPlayer(), "You need to be an enchanter to use the enchanting table!", new Object[0]);
				event.setCancelled(true);
			}
			boolean hasEnchantDeductable = h.getHeroClass().hasExperiencetype(ExperienceType.ENCHANTING);
			if(!hasEnchantDeductable) {
				h.getPlayer().sendMessage("Fatal Bug Occured, please go bug Andrew2060 assuming he doesn't ignore you");
			}
			h.syncExperience(h.getHeroClass());
			return;
		}
		@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
		public void onEnchant(EnchantItemEvent event) {
			Player p = event.getEnchanter();
			Hero h = skill.plugin.getCharacterManager().getHero(p);
			Map<Enchantment, Integer> enchants = event.getEnchantsToAdd();
			Iterator<Entry<Enchantment, Integer>> iter = enchants.entrySet().iterator();
			double expCost = 0;
			int enchantsAdded = 0;
			while(iter.hasNext()) {
				Entry<Enchantment, Integer> next = iter.next();
				Enchantment enchant = next.getKey();
				int reqLevel = SkillConfigManager.getUseSetting(h, skill, ((Enchantment)next.getKey()).getName(), 1, true);
				int enchLevel = next.getValue();
				if (enchLevel > enchant.getMaxLevel())
					next.setValue(Integer.valueOf(enchant.getMaxLevel()));
				else if (enchLevel < enchant.getStartLevel()) {
					next.setValue(Integer.valueOf(enchant.getStartLevel()));
				}
				if ((h.getLevel(h.getHeroClass()) < reqLevel) || (!enchant.canEnchantItem(event.getItem()))) {
			          iter.remove();
			    } else {
			    	expCost += reqLevel;
			    	enchantsAdded++;
			    }
			}
			event.setExpLevelCost(0);
			double multiplier = SkillConfigManager.getUseSetting(h, skill, "vanillaexpcostmultiplier", 10, false);
			expCost *= multiplier;
			if(h.getExperience(h.getHeroClass()) < expCost) {
				event.setCancelled(true);
				h.getPlayer().sendMessage("Insufficient EXP to Enchant!");
				return;
			} else {
				h.gainExp(-expCost, ExperienceType.ENCHANTING, h.getPlayer().getLocation());
				h.addExp(enchantsAdded*200, h.getSecondClass(), h.getPlayer().getLocation());
			}
			
		}
	}
	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection section = super.getDefaultConfig();
		section.set("PROTECTION_ENVIRONMENTAL", Integer.valueOf(200));
		section.set("PROTECTION_FIRE", Integer.valueOf(1));
		section.set("PROTECTION_FALL", Integer.valueOf(1));
		section.set("PROTECTION_EXPLOSIONS", Integer.valueOf(200));
		section.set("PROTECTION_PROJECTILE", Integer.valueOf(200));
		section.set("OXYGEN", Integer.valueOf(1));
		section.set("WATER_WORKER", Integer.valueOf(1));
		section.set("DAMAGE_ALL", Integer.valueOf(200));
		section.set("DAMAGE_UNDEAD", Integer.valueOf(200));
		section.set("DAMAGE_ARTHROPODS", Integer.valueOf(200));
		section.set("KNOCKBACK", Integer.valueOf(1));
		section.set("FIRE_ASPECT", Integer.valueOf(1));
		section.set("LOOT_BONUS_MOBS", Integer.valueOf(1));
		section.set("DIG_SPEED", Integer.valueOf(1));
		section.set("SILK_TOUCH", Integer.valueOf(200));
		section.set("DURABILITY", Integer.valueOf(1));
		section.set("LOOT_BONUS_BLOCKS", Integer.valueOf(1));
		section.set("ARROW_DAMAGE", Integer.valueOf(200));
		section.set("ARROW_KNOCKBACK", Integer.valueOf(1));
		section.set("ARROW_FIRE", Integer.valueOf(1));
		section.set("ARROW_INFINITE", Integer.valueOf(1));
		section.set(Setting.APPLY_TEXT.node(), "");
		section.set(Setting.UNAPPLY_TEXT.node(), "");
		section.set("vanillaexpcostmultiplier", Integer.valueOf(10));
		//Ergo a lvl 30 enchant will cost 300 exp.
		return section;
	}
	@Override
	public String getDescription(Hero h) {
		return getDescription();
	}

}
