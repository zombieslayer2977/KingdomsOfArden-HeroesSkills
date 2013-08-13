package net.kingdomsofarden.andrew2060.heroes.skills;


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
                p.sendMessage(ChatColor.GRAY + "You lack the training to repair items with an anvil (use /hero prof blacksmith to become a blacksmith)!");
                return;
            }
            int maxDurability = 0;
            Material requiredImprove = Material.DIAMOND;
            ItemStack handItem = h.getPlayer().getItemInHand();
            int fullRepair = 8;

            switch(handItem.getType()) {
            case DIAMOND_SWORD:
            case DIAMOND_SPADE:
            case DIAMOND_HOE:
                maxDurability = 1562;
                fullRepair = 2;
                break;
            case DIAMOND_PICKAXE:
            case DIAMOND_AXE:
                maxDurability = 1562;
                fullRepair = 3;
                break;
            case DIAMOND_HELMET:
                maxDurability = 364;
                fullRepair = 5;
                break;
            case DIAMOND_CHESTPLATE:
                maxDurability = 529;
                fullRepair = 8;
                break;
            case DIAMOND_LEGGINGS:
                maxDurability = 496;
                fullRepair = 7;
                break;
            case DIAMOND_BOOTS:
                maxDurability = 430;
                fullRepair = 4;
                break;
            case IRON_SWORD:
            case IRON_SPADE:
            case IRON_HOE:
                maxDurability = 251;
                requiredImprove = Material.IRON_INGOT;
                fullRepair = 2;
                break;
            case IRON_PICKAXE:
            case IRON_AXE:
                maxDurability = 251;
                requiredImprove = Material.IRON_INGOT;
                fullRepair = 3;
                break;
            case IRON_HELMET:
                maxDurability = 166;
                requiredImprove = Material.IRON_INGOT;
                fullRepair = 5;
                break;
            case IRON_CHESTPLATE:
                maxDurability = 242;
                requiredImprove = Material.IRON_INGOT;
                fullRepair = 8;
                break;
            case IRON_LEGGINGS:
                maxDurability = 226;
                requiredImprove = Material.IRON_INGOT;
                fullRepair = 7;
                break;
            case IRON_BOOTS:
                maxDurability = 196;
                requiredImprove = Material.IRON_INGOT;
                fullRepair = 4;
                break;
            case CHAINMAIL_HELMET:
                maxDurability = 166;
                requiredImprove = Material.LEATHER;
                fullRepair = 5;
                break;
            case CHAINMAIL_CHESTPLATE:
                maxDurability = 242;
                requiredImprove = Material.IRON_INGOT;
                fullRepair = 8;
                break;
            case CHAINMAIL_LEGGINGS:
                maxDurability = 226;
                requiredImprove = Material.IRON_INGOT;
                fullRepair = 7;
                break;
            case CHAINMAIL_BOOTS:
                maxDurability = 196;
                requiredImprove = Material.LEATHER;
                fullRepair = 4;
                break;
            case GOLD_SWORD:	
            case GOLD_SPADE:
            case GOLD_HOE:
                maxDurability = 33;
                requiredImprove = Material.GOLD_INGOT;
                fullRepair = 2;
                break;
            case GOLD_PICKAXE:
            case GOLD_AXE:
                maxDurability = 33;
                requiredImprove = Material.GOLD_INGOT;
                fullRepair = 3;
                break;
            case GOLD_HELMET:
                maxDurability = 78;
                requiredImprove = Material.GOLD_INGOT;
                fullRepair = 5;
                break;
            case GOLD_CHESTPLATE:
                maxDurability = 114;
                requiredImprove = Material.GOLD_INGOT;
                fullRepair = 8;
                break;
            case GOLD_LEGGINGS:
                maxDurability = 106;
                requiredImprove = Material.GOLD_INGOT;
                fullRepair = 7;
                break;
            case GOLD_BOOTS:
                maxDurability = 92;
                requiredImprove = Material.GOLD_INGOT;
                fullRepair = 4;
                break;
            case STONE_SWORD:
            case STONE_HOE:
            case STONE_SPADE:
                maxDurability = 132;
                requiredImprove = Material.COBBLESTONE;
                fullRepair = 2;
                break;
            case STONE_PICKAXE:				
            case STONE_AXE:
                maxDurability = 132;
                requiredImprove = Material.COBBLESTONE;
                fullRepair = 3;
                break;
            case LEATHER_HELMET:
                maxDurability = 56;
                requiredImprove = Material.LEATHER;
                fullRepair = 5;
                break;
            case LEATHER_CHESTPLATE:
                maxDurability = 82;
                requiredImprove = Material.LEATHER;
                fullRepair = 8;
                break;
            case LEATHER_LEGGINGS:
                maxDurability = 76;
                requiredImprove = Material.LEATHER;
                fullRepair = 7;
                break;
            case LEATHER_BOOTS:
                maxDurability = 66;
                requiredImprove = Material.LEATHER;
                fullRepair = 4;
                break;
            case WOOD_SWORD:
            case WOOD_HOE:
            case WOOD_SPADE:
                maxDurability = 60;
                requiredImprove = Material.WOOD;
                fullRepair = 2;
                break;
            case WOOD_PICKAXE:
            case WOOD_AXE:
                maxDurability = 60;
                requiredImprove = Material.WOOD;
                fullRepair = 3;
                break;
            case BOW:
                maxDurability = 385;
                requiredImprove = Material.STRING;
                fullRepair = 3;
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
            double multiplier = 100/fullRepair;
            double durabilityRestored = maxDurability*multiplier;
            short finalDurability = (short)(handItem.getDurability() - durabilityRestored);
            handItem.setDurability(finalDurability >= 0 ? finalDurability : 0);
            Random randGen = new Random();
            double rand = randGen.nextInt(10000)*0.01;
            double breakChance = Math.pow(h.getLevel(h.getSecondClass()), -1) * (1.0D/3.0D) * 100;
            /*Optional, add exp based on the item used to repair, remove the subsequent comment block if you want to use it*/
            if (h.getLevel(h.getSecondClass()) <= 75) {
                int exp = 0;
                switch (requiredImprove) {
                case DIAMOND:
                    exp = 40;
                    break;
                case IRON_INGOT:
                    exp = 20;
                    break;
                case GOLD_INGOT:
                    exp = 12;
                    break;
                case WOOD:
                    exp = 4;
                    break;
                case COBBLESTONE:
                    exp = 8;
                    break;
                case LEATHER:
                    exp = 8;
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
