package net.swagserv.andrew2060.heroes.skills;


import java.text.DecimalFormat;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;

public class SkillRepair extends PassiveSkill {


	public SkillRepair(Heroes plugin) {
		super(plugin, "Repair");
		setDescription("Passive: Grants ability to use the anvil to repair weapons/tools/armor. Each repair has a $1% chance of breaking the item (decreases with blacksmith level)");
		setEffectTypes(new EffectType[] { EffectType.BENEFICIAL });
		Bukkit.getServer().getPluginManager().registerEvents(new BlockRightClickListener(), plugin);
	}

	@Override
	public String getDescription(Hero h) {
		double breakChance = Math.pow(h.getLevel(h.getSecondClass()), -1) * (1.0D/3.0D) * 100;
		DecimalFormat dF = new DecimalFormat("##.###");
		return getDescription().replace("$1",dF.format(breakChance)+"%");
	}
	
	public class BlockRightClickListener implements Listener {
		@SuppressWarnings("deprecation")
		@EventHandler(priority = EventPriority.MONITOR) 
		public void onRightClick(PlayerInteractEvent event) {
			if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				return;
			}
			if(!event.getClickedBlock().getType().equals(Material.ANVIL)) {
				return;
			}
			Material bMat = event.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType();
			if(bMat.equals(Material.FIRE)){
				return;
			}
			event.setCancelled(true);
			Player p = event.getPlayer();
			Hero h = SkillRepair.this.plugin.getCharacterManager().getHero(p);
			if (!h.hasEffect("Repair")) {
				p.sendMessage(ChatColor.GRAY + "You lack the training to repair items with an anvil (use /hero choose blacksmith to become a blacksmith)!");
				return;
			}
			int maxDurability = 0;
			Material requiredImprove = Material.DIAMOND;
			ItemStack handItem = h.getPlayer().getItemInHand();
			switch(handItem.getType()) {
			case DIAMOND_SWORD:
				maxDurability = 1562;
				break;
			case DIAMOND_PICKAXE:
			case DIAMOND_HOE:
			case DIAMOND_AXE:
			case DIAMOND_SPADE:
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
				maxDurability = 251;
				requiredImprove = Material.IRON_INGOT;
				break;
			case IRON_PICKAXE:
			case IRON_HOE:
			case IRON_AXE:
			case IRON_SPADE:
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
			case CHAINMAIL_HELMET:
				maxDurability = 166;
				requiredImprove = Material.LEATHER;
				break;
			case CHAINMAIL_CHESTPLATE:
				maxDurability = 242;
				requiredImprove = Material.IRON_INGOT;
				break;
			case CHAINMAIL_LEGGINGS:
				maxDurability = 226;
				requiredImprove = Material.IRON_INGOT;
				break;
			case CHAINMAIL_BOOTS:
				maxDurability = 196;
				requiredImprove = Material.LEATHER;
				break;
			case GOLD_SWORD:	
				maxDurability = 33;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case GOLD_PICKAXE:
			case GOLD_HOE:
			case GOLD_AXE:
			case GOLD_SPADE:
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
				maxDurability = 132;
				requiredImprove = Material.COBBLESTONE;
				break;
			case STONE_PICKAXE:				
			case STONE_HOE:
			case STONE_AXE:
			case STONE_SPADE:
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
				maxDurability = 60;
				requiredImprove = Material.WOOD;
				break;
			case WOOD_PICKAXE:
			case WOOD_HOE:
			case WOOD_AXE:
			case WOOD_SPADE:
				maxDurability = 60;
				requiredImprove = Material.WOOD;
				break;
			case BOW:
				maxDurability = 385;
				requiredImprove = Material.LEATHER;
				break;
			default:
				event.getPlayer().sendMessage(ChatColor.GRAY + "This is not a valid tool or armor type to repair!");
				return;

			}
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
				h.getPlayer().sendMessage(ChatColor.GRAY + "You need " + ChatColor.AQUA + commonName + ChatColor.GRAY + " to repair this item");
				return;
			}
			Inventory pInv = p.getInventory();
			ItemStack mat = pInv.getItem(pInv.first(requiredImprove));
			if(handItem.getDurability() == 0) {
				p.sendMessage(ChatColor.GRAY + "This Item is already at Max Durability!");
				return;
			}
			if (mat.getAmount() > 1) {
				mat.setAmount(mat.getAmount() - 1);
			}
			else pInv.clear(pInv.first(requiredImprove));
			p.updateInventory();
			double durabilityRestored = maxDurability*0.2;
			handItem.setDurability((short)(int)(handItem.getDurability() - durabilityRestored));
			Random randGen = new Random();
			double rand = randGen.nextInt(10000)*0.01;
			double breakChance = Math.pow(h.getLevel(h.getSecondClass()), -1) * (1.0D/3.0D) * 100;
			if(handItem.getDurability() < 0) {
				handItem.setDurability((short) 0);
			}
			/*Optional, add exp based on the item used to repair, remove the subsequent comment block if you want to use it*/
			if (h.getLevel(h.getSecondClass()) <= 50) {
				int exp = 0;
				switch (requiredImprove) {
				case DIAMOND:
					exp = 20;
					break;
				case IRON_INGOT:
					exp = 10;
					break;
				case GOLD_INGOT:
					exp = 6;
					break;
				case WOOD:
					exp = 2;
					break;
				case LEATHER:
					exp = 4;
					break;
				default:
					exp = 0;
				}

				h.addExp(exp, h.getSecondClass(), h.getPlayer().getLocation());
			}

			if(rand <= breakChance) {
				p.sendMessage(ChatColor.GRAY + "Repairing Failed! Your item broke");
				p.getInventory().remove(handItem);
				p.updateInventory();
				return;
			} else {
				p.sendMessage(ChatColor.GRAY + "Repair Successful");
			}
			
		}
	}
}
