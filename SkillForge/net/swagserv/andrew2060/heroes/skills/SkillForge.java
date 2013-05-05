package net.swagserv.andrew2060.heroes.skills;

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
import org.bukkit.inventory.ShapedRecipe;

public class SkillForge extends PassiveSkill {
	public SkillForge(Heroes plugin) {
		super(plugin, "Forge");
		setDescription("Passive: Grants ability to use the anvil to improve weapons/tools/armor The maximum amount improved depends on the blacksmith's level.");
		setEffectTypes(new EffectType[] { EffectType.BENEFICIAL });
		Bukkit.getServer().getPluginManager().registerEvents(new BlockRightClickListener(), plugin);
	}

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
			event.setCancelled(true);
			Hero h = SkillForge.this.plugin.getCharacterManager().getHero(event.getPlayer());
			if (!h.hasEffect("Forge")) {
				h.getPlayer().sendMessage(ChatColor.GRAY + "You lack the training to improve items with an anvil (use /hero choose blacksmith to become a blacksmith)!");
				return;
			}
			int maxDurability = 0;
			Material requiredImprove = Material.IRON_INGOT;
			String commonName = null;
			ItemStack handItem = h.getPlayer().getItemInHand();
			int t = 20;
			switch(handItem.getType()) {
			case DIAMOND_SWORD:
				t = 1;
				maxDurability = 1562;
				break;
			case DIAMOND_PICKAXE:
			case DIAMOND_HOE:
			case DIAMOND_AXE:
			case DIAMOND_SPADE:
				t = 4;
				maxDurability = 1562;
				break;
			case DIAMOND_HELMET:
				t = 3;
				maxDurability = 364;
				break;
			case DIAMOND_CHESTPLATE:
				t = 3;
				maxDurability = 529;
				break;
			case DIAMOND_LEGGINGS:
				t = 3;
				maxDurability = 496;
				break;
			case DIAMOND_BOOTS:
				t = 3;
				maxDurability = 430;
				break;
			case IRON_SWORD:
				t = 1;
				maxDurability = 251;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case IRON_PICKAXE:
			case IRON_HOE:
			case IRON_AXE:
			case IRON_SPADE:
				t = 4;
				maxDurability = 251;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case IRON_HELMET:
				t = 3;
				maxDurability = 166;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case IRON_CHESTPLATE:
				t = 3;
				maxDurability = 242;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case IRON_LEGGINGS:
				t = 3;
				maxDurability = 226;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case IRON_BOOTS:
				t = 3;
				maxDurability = 196;
				requiredImprove = Material.GOLD_INGOT;
				break;
			case CHAINMAIL_HELMET:
				t = 3;
				maxDurability = 166;
				requiredImprove = Material.LEATHER;
				break;
			case CHAINMAIL_CHESTPLATE:
				t = 3;
				maxDurability = 242;
				requiredImprove = Material.IRON_INGOT;
				break;
			case CHAINMAIL_LEGGINGS:
				t = 3;
				maxDurability = 226;
				requiredImprove = Material.IRON_INGOT;
				break;
			case CHAINMAIL_BOOTS:
				t = 3;
				maxDurability = 196;
				requiredImprove = Material.LEATHER;
				break;
			case GOLD_SWORD:	
				t = 1;
				maxDurability = 33;
				requiredImprove = Material.FLINT;
				break;
			case GOLD_PICKAXE:
			case GOLD_HOE:
			case GOLD_AXE:
			case GOLD_SPADE:
				t = 4;
				maxDurability = 33;
				requiredImprove = Material.FLINT;
				break;
			case GOLD_HELMET:
				t = 3;
				maxDurability = 78;
				requiredImprove = Material.FLINT;
				break;
			case GOLD_CHESTPLATE:
				t = 3;
				maxDurability = 114;
				requiredImprove = Material.FLINT;
				break;
			case GOLD_LEGGINGS:
				t = 3;
				maxDurability = 106;
				requiredImprove = Material.FLINT;
				break;
			case GOLD_BOOTS:
				t = 3;
				maxDurability = 92;
				requiredImprove = Material.FLINT;
				break;
			case STONE_SWORD:
				t = 1;
				maxDurability = 132;
				requiredImprove = Material.LEATHER;
				break;
			case STONE_PICKAXE:				
			case STONE_HOE:
			case STONE_AXE:
			case STONE_SPADE:
				t = 4;
				maxDurability = 132;
				requiredImprove = Material.LEATHER;
				break;
			case LEATHER_HELMET:
				t = 3;
				maxDurability = 56;
				requiredImprove = Material.WOOD;
				break;
			case LEATHER_CHESTPLATE:
				t = 3;
				maxDurability = 82;
				requiredImprove = Material.WOOD;
				break;
			case LEATHER_LEGGINGS:
				t = 3;
				maxDurability = 76;
				requiredImprove = Material.WOOD;
				break;
			case LEATHER_BOOTS:
				t = 3;
				maxDurability = 66;
				requiredImprove = Material.WOOD;
				break;
			case WOOD_SWORD:
				t = 1;
				maxDurability = 60;
				requiredImprove = Material.LEATHER;
				break;
			case WOOD_PICKAXE:
			case WOOD_HOE:
			case WOOD_AXE:
			case WOOD_SPADE:
				t = 4;
				maxDurability = 60;
				requiredImprove = Material.WOOD;
				break;
			case BOW:
				t = 2;
				maxDurability = 385;
				requiredImprove = Material.FLINT;
				break;
			default:
				event.getPlayer().sendMessage(ChatColor.GRAY + "This is not a valid tool or armor type to improve!");
				return;
			}
			int level = h.getLevel(h.getSecondClass());
			double durabilityRestored = maxDurability * 0.17D*0.2D;
			if (!h.getPlayer().getInventory().contains(requiredImprove)) {
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

				h.getPlayer().sendMessage(ChatColor.GRAY + "You need " + ChatColor.AQUA + commonName + ChatColor.GRAY + " to improve this item");
				return;
			}
			Player p = h.getPlayer();
			double breakoff = 0.00D;
			if(level <=10) {
				breakoff = 0.37;
			} else if(level <=20) {
				breakoff = 0.53;
			} else if(level <= 30) {
				breakoff = 0.68;
			} else if(level <= 40) {
				breakoff = 0.84;
			} else if(level <= 50) {
				breakoff = 1.00;
			}
			
			Inventory pInv = p.getInventory();
			ItemStack mat = pInv.getItem(pInv.first(requiredImprove));
			short finalDura = (short) (handItem.getDurability() - durabilityRestored);
			if(breakoff == 1.00) {
				if(handItem.getDurability() > 0 && finalDura < 0) {
					finalDura = 0;
				} else {
					p.sendMessage(ChatColor.GRAY + "You cannot improve this item any further!");
					return;
				}
			} else if (maxDurability - finalDura > maxDurability * breakoff) {
				p.sendMessage(ChatColor.GRAY + "You cannot improve this item any further ! You need more experience with blacksmithing!");
				return;
			}
			handItem.setDurability(finalDura);
			if (mat.getAmount() > 1) {
				mat.setAmount(mat.getAmount() - 1);
			} else pInv.remove(mat);
			p.updateInventory();
			ItemStack i = handItem;
			int durability = maxDurability - i.getDurability();
			if (t == 1) {
				if( durability > maxDurability * 0.84) {
					i.removeEnchantment(Enchantment.DAMAGE_ALL);
					i.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
				}
				if(maxDurability * 0.84 >= durability && durability > maxDurability * 0.68) {
					i.removeEnchantment(Enchantment.DAMAGE_ALL);
					i.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 4);
				}
				if(maxDurability * 0.68 >= durability && durability > maxDurability * 0.53) {
					i.removeEnchantment(Enchantment.DAMAGE_ALL);
					i.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
				}
				if(maxDurability * 0.53 >= durability && durability > maxDurability * 0.37) {
					i.removeEnchantment(Enchantment.DAMAGE_ALL);
					i.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
				}
				if(maxDurability * 0.37 >= durability && durability > maxDurability*0.20) {
					i.removeEnchantment(Enchantment.DAMAGE_ALL);
					i.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
				}
				if(durability < maxDurability*0.20) {
					i.removeEnchantment(Enchantment.DAMAGE_ALL);
				}   
			} else if (t == 2) {
				if( durability > maxDurability * 0.84) {
					i.removeEnchantment(Enchantment.ARROW_DAMAGE);
					i.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 5);
				}
				if(maxDurability * 0.84 >= durability && durability > maxDurability * 0.68) {
					i.removeEnchantment(Enchantment.ARROW_DAMAGE);
					i.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 4);
				}
				if(maxDurability * 0.68 >= durability && durability > maxDurability * 0.53) {
					i.removeEnchantment(Enchantment.ARROW_DAMAGE);
					i.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);
				}
				if(maxDurability * 0.53 >= durability && durability > maxDurability * 0.37) {
					i.removeEnchantment(Enchantment.ARROW_DAMAGE);
					i.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);
				}
				if(maxDurability * 0.37 >= durability && durability > maxDurability*0.20) {
					i.removeEnchantment(Enchantment.ARROW_DAMAGE);
					i.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
				}
				if(durability < maxDurability*0.20) {
					i.removeEnchantment(Enchantment.ARROW_DAMAGE);
					return;
				}   
			} else if (t == 3) {
				if( durability > maxDurability * 0.84) {
					i.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
					i.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
				}
				if(maxDurability * 0.84 >= durability && durability > maxDurability * 0.68) {
					i.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
					i.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
				}
				if(maxDurability * 0.68 >= durability && durability > maxDurability * 0.53) {
					i.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
					i.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
				}
				if(maxDurability * 0.53 >= durability && durability > maxDurability * 0.37) {
					i.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
					i.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				}
				if(maxDurability * 0.37 >= durability && durability > maxDurability * 0.20) {
					i.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
					i.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				}
	            if(maxDurability * 0.20 >= durability) {
					i.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
	            }
			} else if (t == 4) {
				if( durability > maxDurability * 0.84) {
					i.removeEnchantment(Enchantment.DIG_SPEED);
					i.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);
					return;
				}
				if(maxDurability * 0.84 >= durability && durability > maxDurability * 0.68) {
					i.removeEnchantment(Enchantment.DIG_SPEED);
					i.addUnsafeEnchantment(Enchantment.DIG_SPEED, 4);
					return;
				}
				if(maxDurability * 0.68 >= durability && durability > maxDurability * 0.53) {
					i.removeEnchantment(Enchantment.DIG_SPEED);
					i.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3);
					return;
				}
				if(maxDurability * 0.53 >= durability && durability > maxDurability * 0.37) {
					i.removeEnchantment(Enchantment.DIG_SPEED);
					i.addUnsafeEnchantment(Enchantment.DIG_SPEED, 2);
					return;
				}
				if(maxDurability * 0.37 >= durability && durability > maxDurability*0.20) {
					i.removeEnchantment(Enchantment.DIG_SPEED);
					i.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
					return;
				}
		        if(durability < maxDurability*0.20) {
					i.removeEnchantment(Enchantment.DIG_SPEED);
					return;
		        }
			}

			p.sendMessage(ChatColor.AQUA + "Item Improvement Successful!");
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