package net.swagserv.andrew2060.heroes.skills;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.EntityHorse;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntityTypes;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.World;

/**
 * A stand-in API for Horses using NMS - only use this up the the point where bukkit introduces an actual API for Horses
 * <br>
 * <br>
 * <b>IMPORTANT:</b> This will VERY LIKELY break every minecraft update.
 * @author Andrew
 *
 */
public class NMSHorse {
    private EntityHorse nmsHorse;
    private NBTTagCompound nbtTagCompound;
    
    /**
     * Creates a new horse instance using the default net.minecraft.server.EntityHorse
     * 
     * @param loc Location to spawn the horse at
     */
    public NMSHorse(Location loc) {
        this(EntityHorse.class, loc);

    }
    /**
     * Creates a new horse instance using the provided horse class at the provided location
     * 
     * @param clazz A class that extends net.minecraft.server.EntityHorse
     * @param loc Location to spawn the horse at
     */
    public NMSHorse(Class<? extends EntityHorse> clazz, Location loc) {
        this(clazz, loc, SpawnReason.CUSTOM);
    }
    /**
     * Creates a new horse instance using the provided horse class at the provided location with the provided spawn reason
     * 
     * @param clazz A class that extends net.minecraft.server.EntityHorse
     * @param loc Location to spawn the horse at
     * @param spawnReason Reason for the horse spawning
     */
    public NMSHorse(Class<? extends EntityHorse> clazz, Location loc, SpawnReason spawnReason) {
        this.nmsHorse = null;
        this.nbtTagCompound = null;
        if(!clazz.getName().equals(EntityHorse.class.getName())) {   //Same as nms, don't need to add to valid EntityTypes
            try {
                Method a = EntityTypes.class.getDeclaredMethod("a", new Class<?>[] {Class.class, String.class, int.class});
                a.setAccessible(true);
                a.invoke(a, clazz, "EntityHorse", 100);
            } catch (Exception e) {
                System.out.println("There was an error creating a horse with custom entity class " + clazz.getName() + ": It could not be registered with the NMS EntityType handler!");
                e.printStackTrace();
                return;
            }
        }
        World nmsWorld = null;
        try {
            nmsWorld = (World)loc.getClass().getMethod("getHandle", new Class[0]).invoke(loc, new Object[0]);
            this.nmsHorse = clazz.getConstructor(World.class).newInstance(nmsWorld);
        } catch (Exception e) {
            System.out.println("There was an error creating a horse");
            e.printStackTrace();
            return;
        }
        this.nbtTagCompound = new NBTTagCompound();
        nmsHorse.a(nbtTagCompound);
        if(!verifyNBTIntegrity(nbtTagCompound)) {
            throw new IllegalArgumentException("This NBT Tag Compound is not valid for a Horse!");
        }
        nmsHorse.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
        nmsWorld.addEntity(nmsHorse, spawnReason);
        
    }
    
    private boolean hasKey(String key) {
        try {
            return nbtTagCompound.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean hasKeys(String[] keys) {
        for (String key : keys) {
            if (!hasKey(key)) {
                return false;
            }
        }
        return true;
    }
    private void setValue(String key, Object value) {
        try {
            if (value instanceof Integer) {
                nbtTagCompound.setInt(key, (Integer)value);
                return;
            } else if (value instanceof Boolean) {
                nbtTagCompound.setBoolean(key, (Boolean)value);
                return;
            } else {
                nbtTagCompound.set(key, (NBTBase) value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Object getValue(String key, Class<?> type) {
        if(type == Integer.class) {
            return nbtTagCompound.getInt(key);
        } else if(type == Boolean.class) {
            return nbtTagCompound.getBoolean(key);
        } else {
            return nbtTagCompound.getCompound(key);
        }        
    }
    private Method getMethod(String name, Class<?> c, int params) {
        for (Method m : c.getMethods()) {
            if (m.getName().equals(name) && m.getParameterTypes().length == params) {
                return m;
            }
        }
        return null;
    }
    private boolean verifyNBTIntegrity(NBTTagCompound nbtTagCompound) {
        return hasKeys(new String[] { "EatingHaystack", "ChestedHorse", "HasReproduced", "Bred", "Type", "Variant", "Temper", "Tame" });
    }

    /**
     * Changes whether the horse is carrying a chest (only for donkeys and mules)
     */
    public void setChested(boolean chested) {
        writeToNBT("ChestedHorse", chested);
    }

    /**
     * Changes whether the horse is eating or not
     */
    public void setEating(boolean eating) {
        writeToNBT("EatingHaystack", eating);
    }

    /**
     * Changes whether the horse was bred or not
     */
    public void setBred(boolean bred) {
        writeToNBT("Bred", bred);
    }
    
    /**
     * Changes the temper of the horse
     */
    public void setTemper(int temper) {
        writeToNBT("Temper", temper);
    }

    /**
     * Changes whether the horse is tamed or not
     */
    public void setTamed(boolean tamed) {
        writeToNBT("Tame", tamed);
    }
    /**
     * Sets the type of horse
     * @param type The horse type to set
     */
    public void setType(HorseType type) {
        writeToNBT("Type", type.getId());
    }
    /**
     * Set whether a horse is saddled
     * 
     * @param saddled Whether the horse is saddled;
     */
    public void setSaddled(boolean saddled) {
        writeToNBT("Saddle", saddled);
    }

    /**
     * Sets the armor item of the horse (only for normal horses)
     * 
     * @param i the ItemStack to set
     */
    public void setArmorItem(ItemStack i) {
        if (i != null) {
            try {
                Object itemTag = new NBTTagCompound("ArmorItem");
                Object itemStack = getMethod("asNMSCopy", Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".inventory.CraftItemStack"), 1).invoke(this, i);
                getMethod("save", itemStack.getClass(), 1).invoke(itemStack, itemTag);
                writeToNBT("ArmorItem", itemTag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            writeToNBT("ArmorItem", null);
        }
    }
    /**
     * Sets the variant of this horse (i.e. its coat)<br>
     * Will only apply if horse is not of zombie or skeleton type
     * 
     * @param variant HorseVariant to set
     */
    public void setVariant(HorseVariant variant) {
        writeToNBT("Variant", variant);
    }
    /**
     * @return whether the horse/mule is carrying a chest
     */
    public boolean isChested() {
        return (Boolean) getValue("ChestedHorse", Boolean.class);
    }

    /**
     * @return whether the horse is eating or not
     */
    public boolean isEating() {
        return (Boolean) getValue("EatingHaystack", Boolean.class);
    }

    /**
     * Returns whether the horse was bred or not
     */
    public boolean isBred() {
        return (Boolean) getValue("Bred", Boolean.class);
    }

    

    /**
     * Returns whether the horse is tamed or not
     */
    public boolean isTamed() {
        return (Boolean) getValue("Tame", Boolean.class);
    }

    /**
     * Returns whether the horse is saddled or not
     */
    public boolean isSaddled() {
        return (Boolean) getValue("Saddle", Boolean.class);
    }

    /**
     * Returns the armor item of the horse
     */
    public ItemStack getArmorItem() {
        try {
            Object itemTag = getValue("ArmorItem",NBTTagCompound.class);
            Object itemStack = getMethod("createStack", ItemStack.class, 1).invoke(this, itemTag);
            return (ItemStack) getMethod("asCraftMirror", Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".inventory.CraftItemStack"), 1).invoke(this, itemStack);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Gets the variant of this horse
     * @return The variant of this horse
     */
    public HorseVariant getVariant() {
        int variantId = (Integer) getValue("Variant", Integer.class);
        return HorseVariant.fromId(variantId);
    }
    /**
     * Returns the temper of the horse
     */
    public int getTemper() {
        return (Integer) getValue("Temper", Integer.class);
    }
    /**
     * Gets the type of horse
     * 
     * @return the type of the horse, or null if none is found.
     */
    public HorseType getType() {
        Integer typeId = (Integer) getValue("Type", Integer.class);
        for(HorseType type : HorseType.values()) {
            if(type.getId() == typeId) {
                return type;
            }
        }
        return null;
    }

    /**
     * Opens the inventory of the horse
     * 
     * @param player Player to open the inventory for
     */
    public void openInventory(Player player) {
        try {
            EntityPlayer entityPlayer = (EntityPlayer) player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
            nmsHorse.f(entityPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the horse entity
     */
    public LivingEntity getHorse() {
        return (LivingEntity)nmsHorse.getBukkitEntity();
    }

    /**
     * Changes a value in the NBTTagCompound and updates it to the horse
     */
    private void writeToNBT(String key, Object value) {
        setValue(key, value);
        nmsHorse.a(nbtTagCompound);
    }
    /**
     * Gets the Bukkit LivingEntity associated with this horse
     * @return The Living Entity associated with this horse
     */
    public LivingEntity getBukkitEntity() {
        return (CraftLivingEntity) nmsHorse.getBukkitEntity();
    }
    //Subsequent portions of code adapted from code created by DarkBlade12 (forums.bukkit.org)
    public enum HorseType {
        NORMAL("normal", 0), DONKEY("donkey", 1), MULE("mule", 2), UNDEAD("undead", 3), SKELETAL("skeletal", 4);
        private int id;
        private String name;

        HorseType(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

    }
    public enum HorseVariant {
        WHITE("white", 0), CREAMY("creamy", 1), CHESTNUT("chestnut", 2), BROWN("brown", 3), BLACK("black", 4), GRAY("gray", 5), DARK_BROWN("dark brown", 6), INVISIBLE("invisible", 7), WHITE_WHITE(
                "white-white", 256), CREAMY_WHITE("creamy-white", 257), CHESTNUT_WHITE("chestnut-white", 258), BROWN_WHITE("brown-white", 259), BLACK_WHITE("black-white", 260), GRAY_WHITE("gray-white", 261), DARK_BROWN_WHITE(
                "dark brown-white", 262), WHITE_WHITE_FIELD("white-white field", 512), CREAMY_WHITE_FIELD("creamy-white field", 513), CHESTNUT_WHITE_FIELD("chestnut-white field", 514), BROWN_WHITE_FIELD(
                "brown-white field", 515), BLACK_WHITE_FIELD("black-white field", 516), GRAY_WHITE_FIELD("gray-white field", 517), DARK_BROWN_WHITE_FIELD("dark brown-white field", 518), WHITE_WHITE_DOTS(
                "white-white dots", 768), CREAMY_WHITE_DOTS("creamy-white dots", 769), CHESTNUT_WHITE_DOTS("chestnut-white dots", 770), BROWN_WHITE_DOTS("brown-white dots", 771), BLACK_WHITE_DOTS(
                "black-white dots", 772), GRAY_WHITE_DOTS("gray-white dots", 773), DARK_BROWN_WHITE_DOTS("dark brown-white dots", 774), WHITE_BLACK_DOTS("white-black dots", 1024), CREAMY_BLACK_DOTS(
                "creamy-black dots", 1025), CHESTNUT_BLACK_DOTS("chestnut-black dots", 1026), BROWN_BLACK_DOTS("brown-black dots", 1027), BLACK_BLACK_DOTS("black-black dots", 1028), GRAY_BLACK_DOTS(
                "gray-black dots", 1029), DARK_BROWN_BLACK_DOTS("dark brown-black dots", 1030);
 
        private String name;
        private int id;
 
        HorseVariant(String name, int id) {
            this.name = name;
            this.id = id;
        }
 
        public String getName() {
            return name;
        }
 
        public int getId() {
            return id;
        }
 
        private static final Map<String, HorseVariant> NAME_MAP = new HashMap<String, HorseVariant>();
        private static final Map<Integer, HorseVariant> ID_MAP = new HashMap<Integer, HorseVariant>();
        static {
            for (HorseVariant effect : values()) {
                NAME_MAP.put(effect.name, effect);
                ID_MAP.put(effect.id, effect);
            }
        }
 
        public static HorseVariant fromName(String name) {
            if (name == null) {
                return null;
            }
            for (Entry<String, HorseVariant> e : NAME_MAP.entrySet()) {
                if (e.getKey().equalsIgnoreCase(name)) {
                    return e.getValue();
                }
            }
            return null;
        }
 
        public static HorseVariant fromId(int id) {
            return ID_MAP.get(id);
        }
    }
}
