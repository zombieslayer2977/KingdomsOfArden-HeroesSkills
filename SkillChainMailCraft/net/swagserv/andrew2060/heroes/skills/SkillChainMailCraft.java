package net.swagserv.andrew2060.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class SkillChainMailCraft extends PassiveSkill {
	public SkillChainMailCraft(Heroes plugin) {
		super(plugin, "ChainmailCraft");
		ShapedRecipe chainHelm = new ShapedRecipe(new ItemStack(Material.CHAINMAIL_HELMET, 1));
        chainHelm.shape("III", "L L", "   ")
        .setIngredient('I', Material.IRON_INGOT)
        .setIngredient('L', Material.LEATHER);
		ShapedRecipe chainChest = new ShapedRecipe(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
        chainChest.shape("L L", "III", "III")
    	.setIngredient('I', Material.IRON_INGOT)
    	.setIngredient('L', Material.LEATHER);
		ShapedRecipe chainLegs = new ShapedRecipe(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
        chainLegs.shape("LIL", "I I", "I I")
    	.setIngredient('I', Material.IRON_INGOT)
    	.setIngredient('L', Material.LEATHER);
		ShapedRecipe chainBoots = new ShapedRecipe(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
        chainBoots.shape("   ", "L L", "I I")
    	.setIngredient('I', Material.IRON_INGOT)
    	.setIngredient('L', Material.LEATHER);
        Bukkit.addRecipe(chainHelm);
        Bukkit.addRecipe(chainChest);
        Bukkit.addRecipe(chainLegs);
        Bukkit.addRecipe(chainBoots);
		setDescription("Passive: Grants ability to forge chainmail armor");
		setEffectTypes(new EffectType[] { EffectType.BENEFICIAL });
		Bukkit.getServer().getPluginManager().registerEvents(new CraftListener(), plugin);
	}

	public String getDescription(Hero hero) {
		return getDescription();
	}
	public class CraftListener implements Listener {
		public CraftListener() { }
		@EventHandler(priority=EventPriority.LOWEST)
		public void onPlayerCraft(CraftItemEvent event) {
			if (event.isCancelled()) {
				return;
			}

			if (((event.getInventory() instanceof CraftingInventory)) && (event.getSlotType().equals(InventoryType.SlotType.RESULT))) {
				if (event.getCurrentItem() == null) {
					return;
				}
				if ((event.getCurrentItem().getType() == Material.CHAINMAIL_BOOTS) || (event.getCurrentItem().getType() == Material.CHAINMAIL_LEGGINGS) || (event.getCurrentItem().getType() == Material.CHAINMAIL_CHESTPLATE) || (event.getCurrentItem().getType() == Material.CHAINMAIL_HELMET)) {
					Hero h = SkillChainMailCraft.this.plugin.getCharacterManager().getHero((Player)event.getWhoClicked());
					if (!h.hasEffect("ChainMailCraft")) {
						event.setCancelled(true);
						h.getPlayer().sendMessage(ChatColor.GRAY + "You lack the blacksmithing expertise required to craft chainmail!");
					}
					h.addExp(50.0D, h.getSecondClass(), h.getPlayer().getLocation());
					return;
				}
			}
		}
	}
}