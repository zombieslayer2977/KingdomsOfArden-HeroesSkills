package net.kingdomsofarden.andrew2060.heroes.skills;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;

public class SkillSalvage extends PassiveSkill implements Listener {

    public SkillSalvage(Heroes plugin) {
        super(plugin, "Salvage");
        for(Salvageable salvage : Salvageable.values()) {
            plugin.getServer().addRecipe(new FurnaceRecipe(new ItemStack(salvage.getFrom()),salvage.getTo()));
        }
        setDescription("Allows for the recovery of up to 80% of the materials used to create a tool/armor piece in a furnace. The amount recovered determines on the durability of the item in question");
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
    
    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event) {
        ItemStack source = event.getSource();
        Salvageable smelting = null;
        try {
            smelting = Salvageable.valueOf(source.getType().name());
        } catch (IllegalArgumentException e) {
            return;
        }
        int resultAmount = 0; 
        double calcDurability = source.getDurability() + smelting.getMaxDurability() * 0.2;
        
    }
    public enum Salvageable {
        DIAMOND_SPADE,
        DIAMOND_SWORD,
        DIAMOND_HOE,
        DIAMOND_PICKAXE,
        DIAMOND_AXE,
        DIAMOND_HELMET,
        DIAMOND_CHESTPLATE,
        DIAMOND_LEGGINGS,
        DIAMOND_BOOTS,
        IRON_SPADE,
        IRON_SWORD,
        IRON_HOE,
        IRON_PICKAXE,
        IRON_AXE,
        IRON_HELMET,
        IRON_CHESTPLATE,
        IRON_LEGGINGS,
        IRON_BOOTS,
        IRON_FENCE,
        CHAINMAIL_HELMET,
        CHAINMAIL_CHESTPLATE,
        CHAINMAIL_LEGGINGS,
        CHAINMAIL_BOOTS,
        GOLD_SPADE,
        GOLD_SWORD,
        GOLD_HOE,
        GOLD_PICKAXE,
        GOLD_AXE,
        GOLD_HELMET,
        GOLD_CHESTPLATE,
        GOLD_LEGGINGS,
        GOLD_BOOTS;
        
        private Material from;
        private Material to;
        private double resultAmountBase;
        private short maxDurability;
        Salvageable() {
            Material mat = Material.getMaterial(name());
            this.setFrom(mat);
            String[] name = name().split("_");
            if(name[0].equals("CHAINMAIL")) {
                this.setTo(Material.IRON_FENCE);
            } else {
                this.setTo(Material.getMaterial(name[0]+"_INGOT"));
            }
            
            setMaxDurability(mat.getMaxDurability());
            
            switch(name[1]) {
                
            case "SPADE": {
                this.setResultAmountBase(1);
                break;
            }
            case "SWORD":
            case "HOE": {
                this.setResultAmountBase(2);
                break;
            }
            case "PICKAXE":
            case "AXE": {
                this.setResultAmountBase(3);
                break;
            }
            
            case "HELMET": {
                this.setResultAmountBase(5);
                break;
            }
            
            case "CHESTPLATE": {
                this.setResultAmountBase(8);
                break;
            }
            
            case "LEGGINGS": {
                this.setResultAmountBase(7);
                break;
            }
            
            case "BOOTS": {
                this.setResultAmountBase(4);
                break;
            }
            
            default: {
                throw new IllegalArgumentException();
            }
            
            }
            
            
        }
        public Material getFrom() {
            return from;
        }
        private void setFrom(Material from) {
            this.from = from;
        }
        public Material getTo() {
            return to;
        }
        private void setTo(Material to) {
            this.to = to;
        }
        public double getResultAmountBase() {
            return resultAmountBase;
        }
        private void setResultAmountBase(double resultAmountBase) {
            this.resultAmountBase = resultAmountBase;
        }
        public short getMaxDurability() {
            return maxDurability;
        }
        private void setMaxDurability(short maxDurability) {
            this.maxDurability = maxDurability;
        }
        
        
    }
}
