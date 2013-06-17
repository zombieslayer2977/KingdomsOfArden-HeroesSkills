package net.swagserv.andrew2060.heroes.skills;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.util.Util;

public class SkillDisenchant extends ActiveSkill {

	public SkillDisenchant(Heroes plugin) {
		super(plugin, "Disenchant");
		setDescription("Disenchants an item to store its enchantment energy inside a bottle! Has a $1% chance of destroying the item for each enchantment removed.");
		setIdentifiers(new String[] { "skill disenchant" });
		setUsage("/skill disenchant");
		setArgumentRange(0,0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public SkillResult use(Hero h, String[] args) {
		ItemStack hand = h.getPlayer().getItemInHand();
		if(!(Util.isArmor(hand.getType()) || Util.isWeapon(hand.getType()))) {
			h.getPlayer().sendMessage(ChatColor.GRAY + "This is not a disenchantable item!");
			return SkillResult.INVALID_TARGET_NO_MSG;
		}
		Random randgen = new Random();
		//Get all enchants currently on an item
		Iterator<Entry<Enchantment, Integer>> enchants = hand.getEnchantments().entrySet().iterator();
		//Go through each of these enchants 1 by 1
		while(enchants.hasNext()) {
			Entry<Enchantment, Integer> next = enchants.next();
			Enchantment ench = next.getKey();
			//Skip over sharpness/prot/punch because  that is durability dependent, not enchantment dependent
			if(ench.equals(Enchantment.ARROW_DAMAGE) || ench.equals(Enchantment.DAMAGE_ALL) || ench.equals(Enchantment.PROTECTION_ENVIRONMENTAL)) {
				continue;
			}
			int level = next.getValue();
			hand.removeEnchantment(ench);
			//1 exp bottle/level of enchant removed
			ItemStack expbottle = new ItemStack(Material.EXP_BOTTLE, level);
			ItemMeta meta = expbottle.getItemMeta();
			meta.setDisplayName("Essence of enchantment");
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GRAY + "Contains a fraction of extracted enchantment energy.");
			lore.add(ChatColor.GRAY + "Can be used to power the combination of soul gems");
			lore.add(ChatColor.GRAY + "Store enchantments within scrolls");
			lore.add(ChatColor.GRAY + "Or empower a tool with additional mod slots");
			meta.setLore(lore);
			expbottle.setItemMeta(meta);
			h.getPlayer().getWorld().dropItem(h.getPlayer().getLocation(), expbottle);
			//Handle breaking
			if(randgen.nextInt(100) < Math.pow(h.getLevel(h.getSecondClass()),-1)*100) {
				h.getPlayer().sendMessage(ChatColor.GRAY + "Oh no your item broke :("); //Like we actually give a fuck
				hand.setType(Material.AIR);
				h.getPlayer().updateInventory();
				break;
			}
		}
		return SkillResult.NORMAL;
	}

	@Override
	public String getDescription(Hero h) {
		DecimalFormat dF = new DecimalFormat("##.##");
		return getDescription().replace("$1", dF.format(Math.pow(h.getLevel(h.getSecondClass()),-1)*100) + "");
	}
	

}
