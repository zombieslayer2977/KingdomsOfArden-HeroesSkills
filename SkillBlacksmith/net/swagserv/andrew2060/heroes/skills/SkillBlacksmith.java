package net.swagserv.andrew2060.heroes.skills;

/**
 * Not Coded for Swagserv->this skill is essentially a cleaner version of SkillForge minus the greater than 100% handling
 */
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SkillBlacksmith extends PassiveSkill {
	public SkillBlacksmith(Heroes plugin) {
		super(plugin, "Blacksmith");
		setDescription("Passive: Grants ability to use the forge to improve weapons/tools/armor: Current Repair Rate: $1% of Max Durability. (Right click Iron Block to use)");
		setEffectTypes(new EffectType[] { EffectType.BENEFICIAL });
		Bukkit.getServer().getPluginManager().registerEvents(new BlockRightClickListener(), plugin);
	}

	public String getDescription(Hero hero) {
		int level = hero.getLevel(hero.getSecondClass());
		int repair = 0;
		if (level < 20) {
			repair = 20;
		}
		if ((19 < level) && (level < 40)) {
			repair = 25;
		}
		if ((39 < level) && (level < 60)) {
			repair = 33;
		}
		if ((59 < level) && (level < 80)) {
			repair = 50;
		}
		if (79 < level) {
			repair = 100;
		}
		return getDescription().replace("$1", repair+"");
	}
	public class BlockRightClickListener implements Listener {
		@EventHandler(priority=EventPriority.MONITOR)
		public void onPlayerInteract(PlayerInteractEvent event) {
			/*Check to see if the person that triggered this event satisfies the conditions for triggering this check*/
			Action a = event.getAction();
			if (!a.equals(Action.RIGHT_CLICK_BLOCK)) {
				return;
			}
			Hero h = SkillBlacksmith.this.plugin.getCharacterManager().getHero(event.getPlayer());
			if (!h.hasEffect("Blacksmith")) {
				return;
			}			
			Block b = event.getClickedBlock();
			if (!b.getType().equals(Material.IRON_BLOCK)) {
				return;
			}
			/*End Checks*/
			/*Define Item Max Durabilities as well as the items required to repair them (referred to as improving them)*/
			int maxDurability = 0;
			Material requiredImprove = Material.DIAMOND;
			ItemStack handItem = h.getPlayer().getItemInHand();
			switch(handItem.getType()) {
			case DIAMOND_SWORD:				
			case DIAMOND_PICKAXE:
			case DIAMOND_HOE:
			case DIAMOND_AXE:
				maxDurability = 1562;
				break;
			case DIAMOND_HELMET:
				maxDurability = 364;
				break;
			case DIAMOND_CHESTPLATE:
				maxDurability = 529;
				break;
			case DIAMOND_LEGGINGS:
				maxDurability = 496;
				break;
			case DIAMOND_BOOTS:
				maxDurability = 430;
				break;
			case IRON_SWORD:
			case IRON_PICKAXE:
			case IRON_HOE:
			case IRON_AXE:
				maxDurability = 251;
				requiredImprove = Material.IRON_INGOT;
				break;
			case IRON_HELMET:
				maxDurability = 166;
				requiredImprove = Material.IRON_INGOT;
				break;
			case IRON_CHESTPLATE:
				maxDurability = 242;
				requiredImprove = Material.IRON_INGOT;
				break;
			case IRON_LEGGINGS:
				maxDurability = 226;
				requiredImprove = Material.IRON_INGOT;
				break;
			case IRON_BOOTS:
				maxDurability = 196;
				requiredImprove = Material.IRON_INGOT;
				break;
			case GOLD_SWORD:	
			case GOLD_PICKAXE:
			case GOLD_HOE:
			case GOLD_AXE:
				maxDurability = 33;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case GOLD_HELMET:
				maxDurability = 78;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case GOLD_CHESTPLATE:
				maxDurability = 114;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case GOLD_LEGGINGS:
				maxDurability = 106;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case GOLD_BOOTS:
				maxDurability = 92;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case STONE_SWORD:
			case STONE_PICKAXE:				
			case STONE_HOE:
			case STONE_AXE:
				maxDurability = 132;
				requiredImprove = Material.COBBLESTONE;
				break;
			case LEATHER_HELMET:	
				maxDurability = 56;
				requiredImprove = Material.LEATHER;
				break;
			case LEATHER_CHESTPLATE:
				maxDurability = 82;
				requiredImprove = Material.LEATHER;
				break;
			case LEATHER_LEGGINGS:
				maxDurability = 76;
				requiredImprove = Material.LEATHER;
				break;
			case LEATHER_BOOTS:
				maxDurability = 66;
				requiredImprove = Material.LEATHER;
				break;
			case WOOD_SWORD:
			case WOOD_PICKAXE:
			case WOOD_HOE:
			case WOOD_AXE:
				maxDurability = 60;
				requiredImprove = Material.WOOD;
				break;
			case BOW:
				maxDurability = 385;
				requiredImprove = Material.LEATHER;
				break;
			default:
				event.getPlayer().sendMessage(ChatColor.GRAY + "This is not a valid tool or armor type to improve!");
				return;
			}
			/*End max durability definitions*/
			/*Check to see if player actually has the items required to repair*/
			if (!h.getPlayer().getInventory().contains(requiredImprove)) {
				String commonName;
				switch(requiredImprove) {
				case DIAMOND:
					commonName = "diamond ore";
					break;
				case GOLD_INGOT:
					commonName = "gold ingot";
					break;
				case IRON_INGOT:
					commonName = "iron ingot";
					break;
				case LEATHER:
					commonName = "leather";
					break;
				case WOOD:
					commonName = "wood plank";
					break;
				case COBBLESTONE:
					commonName = "cobblestone";
					break;
				default: 
					commonName = "something broke here, go get andrew2060";
					break;
				}
				h.getPlayer().sendMessage(ChatColor.GRAY + "You need " + ChatColor.AQUA + commonName + ChatColor.GRAY + " to improve this item");
				return;
			}
			/*End check*/
			/*Actually do the repairing now*/
			Player p = event.getPlayer();
			Inventory pInv = p.getInventory();
			ItemStack mat = pInv.getItem(pInv.first(requiredImprove));
			/*Check if at max durability*/
			if(handItem.getDurability() == 0) {
				p.sendMessage(ChatColor.GRAY + "This Item is already at Max Durability!");
				return;
			}
			/*End Check*/
			/*Remove the required item to improve*/
			if (mat.getAmount() > 1) {
				mat.setAmount(mat.getAmount() - 1);
			}
			else pInv.clear(pInv.first(requiredImprove));
			/*End Removal*/
			/*Define durability to restore*/
			double durabilityRestored = maxDurability; 						//Initialize the variable to 100% durability, then modify by multiplying based on level
			int level = h.getLevel();
			if (level < 10) {
				durabilityRestored *= 0.2D;
			}
			if ((9 < level) && (level < 20)) {
				durabilityRestored *= 0.25D;
			}
			if ((19 < level) && (level < 30)) {
				durabilityRestored *= 0.33D;								//Not perfect, but 1/3 doesn't work
			}
			if ((29 < level) && (level < 40)) {
				durabilityRestored *= 0.5D;
			}
			if (39 < level) {
				durabilityRestored *= 1.0D;
			}
			/*Actually set durability (0 = new, if getDurability = maxDurability(), item has 0 uses left)*/
			handItem.setDurability((short)(int)(handItem.getDurability() - durabilityRestored));
			/*Check if the repair went above 100% durability, if so, set to 100% durability*/
			if(handItem.getDurability() < 0) {
				handItem.setDurability((short) 0);
			}
			/*End Check*/
			p.sendMessage(ChatColor.AQUA + "Item Improvement Successful!");
			
			/*Optional, add exp based on the item used to repair, remove the subsequent comment block if you want to use it*/
			/*
			if (h.getLevel(h.getSecondClass()) <= 50) {
				int exp = 0;
				switch (requiredImprove.getType()) {
				case DIAMOND:
					exp = 10;
					break;
				case IRON_INGOT:
					exp = 5;
					break;
				case GOLD_INGOT:
					exp = 3;
					break;
				case WOOD:
					exp = 1;
					break;
				case LEATHER:
					exp = 2;
					break;
				default:
					exp = 0;
				}

				h.addExp(exp, h.getSecondClass(), h.getPlayer().getLocation());
			}
			*/
			/*Enchantment handling, in this case chances of losing enchants is 100-level%*/
			Random rand = new Random();
			if(rand.nextInt(100) < 100-h.getLevel(h.getSecondClass())) {
				Set<Enchantment> enchants = handItem.getEnchantments().keySet();
				Iterator<Enchantment> enchantIterator = enchants.iterator();
				while(enchantIterator.hasNext()) {
					handItem.removeEnchantment(enchantIterator.next());
				}
				p.sendMessage(ChatColor.GRAY + "Enchantments were lost during repair, level up your blacksmithing skill to reduce the chance of this happening!");
			}
			return;
		}
	}
}