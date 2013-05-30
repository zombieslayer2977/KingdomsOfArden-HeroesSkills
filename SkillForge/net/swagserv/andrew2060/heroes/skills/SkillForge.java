package net.swagserv.andrew2060.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.swagserv.andrew2060.toolhandler.util.ImprovementUtil;
public class SkillForge extends PassiveSkill {
	public SkillForge(Heroes plugin) {
		super(plugin, "Forge");
		setDescription("Passive: Grants ability to use the anvil with a fire 2 blocks underneath to improve weapons/tools/armor's sharpness/efficiency/protection respectively.");
		setEffectTypes(new EffectType[] { EffectType.BENEFICIAL });
		Bukkit.getServer().getPluginManager().registerEvents(new BlockRightClickListener(), plugin);
	}

	@Override 
	public String getDescription(Hero hero) {
		return getDescription();
	}
	public class BlockRightClickListener implements Listener {
		@SuppressWarnings("deprecation")
		@EventHandler(priority=EventPriority.MONITOR)
		public void onPlayerInteract(PlayerInteractEvent event) {
			Action a = event.getAction();
			if (!a.equals(Action.RIGHT_CLICK_BLOCK)) {
				return;
			}
			Block b = event.getClickedBlock();
			if (!b.getType().equals(Material.ANVIL)) {
				return;
			}
			Material mat = b.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType();
			if(!(mat.equals(Material.FIRE))) {
				return;
			}
			event.setCancelled(true);
			Hero h = SkillForge.this.plugin.getCharacterManager().getHero(event.getPlayer());
			Player p = event.getPlayer();
			if (!h.hasEffect("Forge")) {
				p.sendMessage(ChatColor.GRAY + "You lack the training to improve items with an anvil (use /hero choose blacksmith to become a blacksmith)!");
				return;
			}
			Material requiredImprove = Material.IRON_INGOT;
			String commonName = null;
			ItemStack handItem = p.getItemInHand();
			int t = 20;
			switch(handItem.getType()) {
			case DIAMOND_SWORD:
				t = 1;
				break;
			case DIAMOND_PICKAXE:
			case DIAMOND_HOE:
			case DIAMOND_AXE:
			case DIAMOND_SPADE:
				t = 4;
				break;
			case DIAMOND_HELMET:
			case DIAMOND_CHESTPLATE:
			case DIAMOND_LEGGINGS:
			case DIAMOND_BOOTS:
				t = 3;
				break;
			case IRON_SWORD:
				t = 1;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case IRON_PICKAXE:
			case IRON_HOE:
			case IRON_AXE:
			case IRON_SPADE:
				t = 4;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case IRON_HELMET:
				t = 3;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case IRON_CHESTPLATE:
				t = 3;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case IRON_LEGGINGS:
				t = 3;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case IRON_BOOTS:
				t = 3;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case CHAINMAIL_HELMET:
				t = 3;
				requiredImprove = Material.LEATHER;
				break;
			case CHAINMAIL_CHESTPLATE:
				t = 3;
				requiredImprove = Material.IRON_INGOT;
				break;
			case CHAINMAIL_LEGGINGS:
				t = 3;
				requiredImprove = Material.IRON_INGOT;
				break;
			case CHAINMAIL_BOOTS:
				t = 3;
				requiredImprove = Material.LEATHER;
				break;
			case GOLD_SWORD:	
				t = 1;
				requiredImprove = Material.FLINT;
				break;
			case GOLD_PICKAXE:
			case GOLD_HOE:
			case GOLD_AXE:
			case GOLD_SPADE:
				t = 4;
				requiredImprove = Material.FLINT;
				break;
			case GOLD_HELMET:
				t = 3;
				requiredImprove = Material.FLINT;
				break;
			case GOLD_CHESTPLATE:
				t = 3;
				requiredImprove = Material.FLINT;
				break;
			case GOLD_LEGGINGS:
				t = 3;
				requiredImprove = Material.FLINT;
				break;
			case GOLD_BOOTS:
				t = 3;
				requiredImprove = Material.FLINT;
				break;
			case STONE_SWORD:
				t = 1;
				requiredImprove = Material.LEATHER;
				break;
			case STONE_PICKAXE:				
			case STONE_HOE:
			case STONE_AXE:
			case STONE_SPADE:
				t = 4;
				requiredImprove = Material.LEATHER;
				break;
			case LEATHER_HELMET:
				t = 3;
				requiredImprove = Material.WOOD;
				break;
			case LEATHER_CHESTPLATE:
				t = 3;
				requiredImprove = Material.WOOD;
				break;
			case LEATHER_LEGGINGS:
				t = 3;
				requiredImprove = Material.WOOD;
				break;
			case LEATHER_BOOTS:
				t = 3;
				requiredImprove = Material.WOOD;
				break;
			case WOOD_SWORD:
				t = 1;
				requiredImprove = Material.LEATHER;
				break;
			case WOOD_PICKAXE:
			case WOOD_HOE:
			case WOOD_AXE:
			case WOOD_SPADE:
				t = 4;
				requiredImprove = Material.WOOD;
				break;
			case BOW:
				t = 2;
				requiredImprove = Material.FLINT;
				break;
			default:
				event.getPlayer().sendMessage(ChatColor.GRAY + "This is not a valid tool or armor type to improve!");
				return;
			}
			if (!p.getInventory().contains(requiredImprove)) {
				switch (requiredImprove.getId()) {
				case 265:
					commonName = "iron ingots";
					break;
				case 266:
					commonName = "gold ingots";
					break;
				case 318:
					commonName = "flint";
					break;
				case 5:
					commonName = "wood planks";
					break;
				case 334:
					commonName = "leather";
					break;
				default:
					commonName = "something broke here, go get andrew2060";
					break;
				}
				p.sendMessage(ChatColor.GRAY + "You need " + ChatColor.AQUA + commonName + ChatColor.GRAY + " to improve this item");
				return;
			}
			int level = h.getLevel(h.getSecondClass());
			int threshold = 0;
			if(level <= 10) {
				threshold = 20;
			} else if (level <= 20) {
				threshold = 40;
			} else if (level <= 30) {
				threshold = 60;
			} else if (level <= 40) {
				threshold = 80;
			} else if (level > 40) {
				threshold = 100;
			}
			double quality = ImprovementUtil.getQuality(handItem);
			if(quality >= threshold && threshold != 100) {
				p.sendMessage(ChatColor.GRAY + "You lack sufficient blacksmithing experience to improve this item further!");
				return;
			} else if (quality >= 100) {
				p.sendMessage(ChatColor.GRAY + "This item cannot be improved to a higher quality.");
				return;
			} else {
				if(ImprovementUtil.improveQuality(handItem) == -1) {
					p.sendMessage(ChatColor.GRAY + "This item cannot be improved to a higher quality.");
					return;
				} else {
					p.sendMessage("Item Improvement Successful!");
					switch(t) {
					case 1:
						ImprovementUtil.applyEnchantmentLevel(handItem, Enchantment.DAMAGE_ALL);
						break;
					case 2:
						ImprovementUtil.applyEnchantmentLevel(handItem, Enchantment.ARROW_DAMAGE);
						break;
					case 3:
						ImprovementUtil.applyEnchantmentLevel(handItem, Enchantment.PROTECTION_ENVIRONMENTAL);
						break;
					case 4:
						ImprovementUtil.applyEnchantmentLevel(handItem, Enchantment.DIG_SPEED);
					}
					PlayerInventory pI = p.getInventory();
					int firstId = pI.first(requiredImprove);
					if (pI.getItem(firstId).getAmount() > 1) {
						pI.getItem(firstId).setAmount(pI.getItem(firstId).getAmount() - 1);
					}
					else pI.clear(pI.first(requiredImprove));
					p.updateInventory();
					int exp = 0;
					switch (requiredImprove) {
					case IRON_INGOT:
						exp = 30;
						break; 
					case GOLD_INGOT:
						exp = 10;
						break;
					case LEATHER:
						exp = 6;
						break;
					case WOOD:
						exp = 2;
						break;
					case FLINT:
						exp = 4;
						break;
					default:
						exp = 0;
					}	
					h.addExp(exp, h.getSecondClass(), h.getPlayer().getLocation());			
				}
			}
			
			
		}
	}
}