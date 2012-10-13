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

public class SkillForge extends PassiveSkill {
	public SkillForge(Heroes plugin) {
		super(plugin, "Forge");
		setDescription("Passive: Grants ability to use the forge to improve weapons/tools/armor: Current Cost: $1 of material per tier. (Right click Iron Block to use)");
		setEffectTypes(new EffectType[] { EffectType.BENEFICIAL });
		Bukkit.getServer().getPluginManager().registerEvents(new BlockRightClickListener(), plugin);
	}

	public String getDescription(Hero hero) {
		int level = hero.getLevel(hero.getSecondClass());
		int cost = 0;
		if (level < 20) {
			cost = 5;
		}
		if ((19 < level) && (level < 40)) {
			cost = 4;
		}
		if ((39 < level) && (level < 60)) {
			cost = 3;
		}
		if ((59 < level) && (level < 80)) {
			cost = 2;
		}
		if (79 < level) {
			cost = 1;
		}
		return getDescription().replace("$1", cost+"");
	}
	public class BlockRightClickListener implements Listener {
		public BlockRightClickListener() {}
		@EventHandler(priority=EventPriority.MONITOR)
		public void onPlayerInteract(PlayerInteractEvent event) {
			Action a = event.getAction();
			if (!a.equals(Action.RIGHT_CLICK_BLOCK)) {
				return;
			}
			Hero h = SkillForge.this.plugin.getCharacterManager().getHero(event.getPlayer());
			if (!h.hasEffect("Forge")) {
				return;
			}
			Block b = event.getClickedBlock();
			if (!b.getType().equals(Material.IRON_BLOCK)) {
				return;
			}
			int maxDurability = 0;
			Material requiredImprove = Material.DIAMOND;
			String commonName = null;
			ItemStack handItem = h.getPlayer().getItemInHand();
			int t = 20;
			switch (handItem.getTypeId()) {
			case 146:
				maxDurability = 1562;
				requiredImprove = Material.IRON_INGOT;
				t = 1;
				break;
			case 137:
				maxDurability = 251;
				requiredImprove = Material.GOLD_INGOT;
				t = 1;
				break;
			case 153:
				maxDurability = 33;
				requiredImprove = Material.FLINT;
				t = 1;
				break;
			case 142:
				maxDurability = 130;
				requiredImprove = Material.LEATHER;
				t = 1;
				break;
			case 138:
				maxDurability = 60;
				requiredImprove = Material.FLINT;
				t = 1;
				break;
			case 131:
				maxDurability = 385;
				requiredImprove = Material.LEATHER;
				t = 2;
				break;
			case 183:
				maxDurability = 430;
				requiredImprove = Material.IRON_INGOT;
				t = 3;
				break;
			case 179:
				maxDurability = 196;
				requiredImprove = Material.GOLD_INGOT;
				t = 3;
				break;
			case 187:
				maxDurability = 92;
				requiredImprove = Material.LEATHER;
				t = 3;
				break;
			case 175:
				maxDurability = 196;
				requiredImprove = Material.LEATHER;
				t = 3;
				break;
			case 171:
				maxDurability = 66;
				requiredImprove = Material.WOOD;
				t = 3;
				break;
			case 182:
				maxDurability = 496;
				requiredImprove = Material.IRON_INGOT;
				t = 3;
				break;
			case 178:
				maxDurability = 226;
				requiredImprove = Material.GOLD_INGOT;
				t = 3;
				break;
			case 186:
				maxDurability = 106;
				requiredImprove = Material.LEATHER;
				t = 3;
				break;
			case 174:
				maxDurability = 226;
				requiredImprove = Material.LEATHER;
				t = 3;
				break;
			case 170:
				maxDurability = 76;
				requiredImprove = Material.WOOD;
				t = 3;
				break;
			case 181:
				maxDurability = 529;
				requiredImprove = Material.IRON_INGOT;
				t = 3;
				break;
			case 177:
				maxDurability = 242;
				requiredImprove = Material.GOLD_INGOT;
				t = 3;
				break;
			case 185:
				maxDurability = 114;
				requiredImprove = Material.LEATHER;
				t = 3;
				break;
			case 173:
				maxDurability = 242;
				requiredImprove = Material.LEATHER;
				t = 3;
				break;
			case 169:
				maxDurability = 82;
				requiredImprove = Material.WOOD;
				t = 3;
				break;
			case 180:
				maxDurability = 364;
				requiredImprove = Material.IRON_INGOT;
				t = 3;
				break;
			case 176:
				maxDurability = 166;
				requiredImprove = Material.GOLD_INGOT;
				t = 3;
				break;
			case 184:
				maxDurability = 78;
				requiredImprove = Material.LEATHER;
				t = 3;
				break;
			case 172:
				maxDurability = 166;
				requiredImprove = Material.LEATHER;
				t = 3;
				break;
			case 168:
				maxDurability = 56;
				requiredImprove = Material.WOOD;
				t = 3;
				break;
			case 148:
				maxDurability = 1562;
				requiredImprove = Material.IRON_INGOT;
				t = 4;
				break;
			case 127:
				maxDurability = 251;
				requiredImprove = Material.GOLD_INGOT;
				t = 4;
				break;
			case 155:
				maxDurability = 33;
				requiredImprove = Material.FLINT;
				t = 4;
				break;
			case 144:
				maxDurability = 132;
				requiredImprove = Material.LEATHER;
				t = 4;
				break;
			case 140:
				maxDurability = 60;
				requiredImprove = Material.FLINT;
				t = 4;
				break;
			default:
				event.getPlayer().sendMessage(ChatColor.GRAY + "This is not a valid weapon or armor type to improve!");
				return;
			}
			int level = h.getLevel(h.getSecondClass());
			double durabilityRestored = maxDurability * 0.17D;
			if (level < 10) {
				durabilityRestored *= 0.2D;
			}
			if ((9 < level) && (level < 20)) {
				durabilityRestored *= 0.25D;
			}
			if ((19 < level) && (level < 30)) {
				durabilityRestored *= 0.4D;
			}
			if ((29 < level) && (level < 40)) {
				durabilityRestored *= 0.5D;
			}
			if (39 < level) {
				durabilityRestored *= 1.0D;
			}
			if (!h.getPlayer().getInventory().contains(requiredImprove)) {
				switch (requiredImprove.getId()) {
				case 135:
					commonName = "iron ingots";
					break;
				case 136:
					commonName = "gold ingots";
					break;
				case 188:
					commonName = "flint";
					break;
				case 6:
					commonName = "wood planks";
					break;
				case 204:
					commonName = "leather";
					break;
				default:
					commonName = "something broke here, go get andrew2060";
				}

				h.getPlayer().sendMessage(ChatColor.GRAY + "You need " + ChatColor.AQUA + commonName + ChatColor.GRAY + " to improve this item");
			}
			Player p = event.getPlayer();
			Inventory pInv = p.getInventory();
			ItemStack mat = pInv.getItem(pInv.first(requiredImprove));
			if (maxDurability - handItem.getDurability() > maxDurability * 2.0D) {
				p.sendMessage(ChatColor.GRAY + "This Item cannot be Improved Further!");
				return;
			}
			handItem.setDurability((short)(int)(handItem.getDurability() - durabilityRestored));
			if (mat.getAmount() > 1) {
				mat.setAmount(mat.getAmount() - 1);
			}
			else pInv.clear(pInv.first(requiredImprove));
			ItemStack i = handItem;
			int durability = maxDurability - i.getDurability();
			if (t == 1) {
				if (durability > maxDurability * 1.68D) {
					i.removeEnchantment(Enchantment.DAMAGE_ALL);
					i.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
				}
				if ((maxDurability * 1.68D > durability) && (durability > maxDurability * 1.51D)) {
					i.removeEnchantment(Enchantment.DAMAGE_ALL);
					i.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 4);
				}
				if ((maxDurability * 1.51D > durability) && (durability > maxDurability * 1.34D)) {
					i.removeEnchantment(Enchantment.DAMAGE_ALL);
					i.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
				}
				if ((maxDurability * 1.34D > durability) && (durability > maxDurability * 1.17D)) {
					i.removeEnchantment(Enchantment.DAMAGE_ALL);
					i.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
				}
				if ((maxDurability * 1.17D > durability) && (durability > maxDurability * 1.0D)) {
					i.removeEnchantment(Enchantment.DAMAGE_ALL);
					i.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
				}
				if (durability < maxDurability * 1.0D) {
					i.removeEnchantment(Enchantment.DAMAGE_ALL);
				}
			} else if (t == 2) {
				if (durability > maxDurability * 1.68D) {
					i.removeEnchantment(Enchantment.ARROW_DAMAGE);
					i.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 5);
				}
				if ((maxDurability * 1.68D > durability) && (durability > maxDurability * 1.51D)) {
					i.removeEnchantment(Enchantment.ARROW_DAMAGE);
					i.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 4);
				}
				if ((maxDurability * 1.51D > durability) && (durability > maxDurability * 1.34D)) {
					i.removeEnchantment(Enchantment.ARROW_DAMAGE);
					i.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);
				}
				if ((maxDurability * 1.34D > durability) && (durability > maxDurability * 1.17D)) {
					i.removeEnchantment(Enchantment.ARROW_DAMAGE);
					i.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);
				}
				if ((maxDurability * 1.17D > durability) && (durability > maxDurability * 1.0D)) {
					i.removeEnchantment(Enchantment.ARROW_DAMAGE);
					i.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
				}
				if (durability < maxDurability * 1.0D) {
					i.removeEnchantment(Enchantment.ARROW_DAMAGE);
				}
			} else if (t == 3) {
				if (durability > maxDurability * 1.68D) {
					i.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
					i.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
				}
				if ((maxDurability * 1.68D > durability) && (durability > maxDurability * 1.51D)) {
					i.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
					i.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
				}
				if ((maxDurability * 1.51D > durability) && (durability > maxDurability * 1.34D)) {
					i.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
					i.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
				}
				if ((maxDurability * 1.34D > durability) && (durability > maxDurability * 1.17D)) {
					i.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
					i.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				}
				if ((maxDurability * 1.17D > durability) && (durability > maxDurability * 1.0D)) {
					i.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
					i.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				}
				if (durability < maxDurability * 1.0D) {
					i.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
				}
			} else if (t == 4) {
				if (durability > maxDurability * 1.68D) {
					i.removeEnchantment(Enchantment.DIG_SPEED);
					i.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);
				}
				if ((maxDurability * 1.68D > durability) && (durability > maxDurability * 1.51D)) {
					i.removeEnchantment(Enchantment.DIG_SPEED);
					i.addUnsafeEnchantment(Enchantment.DIG_SPEED, 4);
				}
				if ((maxDurability * 1.51D > durability) && (durability > maxDurability * 1.34D)) {
					i.removeEnchantment(Enchantment.DIG_SPEED);
					i.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3);
				}
				if ((maxDurability * 1.34D > durability) && (durability > maxDurability * 1.17D)) {
					i.removeEnchantment(Enchantment.DIG_SPEED);
					i.addUnsafeEnchantment(Enchantment.DIG_SPEED, 2);
				}
				if ((maxDurability * 1.17D > durability) && (durability > maxDurability * 1.0D)) {
					i.removeEnchantment(Enchantment.DIG_SPEED);
					i.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
				}
				if (durability < maxDurability * 1.0D) {
					i.removeEnchantment(Enchantment.DIG_SPEED);
				}
			}

			p.sendMessage(ChatColor.AQUA + "Item Improvement Successful!");
			if (h.getLevel(h.getSecondClass()) <= 50) {
				int exp = 0;
				switch (requiredImprove.getId()) {
				case 135:
					exp = 10;
					break;
				case 136:
					exp = 5;
					break;
				case 188:
					exp = 3;
					break;
				case 6:
					exp = 1;
					break;
				case 204:
					exp = 2;
					break;
				default:
					exp = 0;
				}

				h.addExp(exp, h.getSecondClass(), h.getPlayer().getLocation());
			}
		}
	}
}