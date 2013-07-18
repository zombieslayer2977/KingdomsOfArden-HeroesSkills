package net.kingdomsofarden.andrew2060.heroes.skills;

import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;
import net.kingdomsofarden.andrew2060.toolhandler.potions.PotionEffectManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;

public class SkillNetherSteel extends PassiveSkill implements Listener {

    private PotionEffectManager pEMan;

    public SkillNetherSteel(Heroes plugin) {
        super(plugin,"NetherSteel");
        setDescription("Basic attacks with scythes apply 2 seconds of wither III");
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                pEMan = ((ToolHandlerPlugin)Bukkit.getServer().getPluginManager().getPlugin("KingdomsOfArden-ToolHandler")).getPotionEffectHandler();
            }
            
        }, 200L);
        Bukkit.getPluginManager().registerEvents(this,this.plugin);
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) 
    public void onWeaponDamage(WeaponDamageEvent event) {
        if(!(event.getDamager() instanceof Hero)) {
            return;
        }
        if(!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        Hero h = (Hero) event.getDamager(); 
        if(h.hasEffect("NetherSteel")) {
            switch(h.getPlayer().getItemInHand().getType()) {
            
            case DIAMOND_HOE:
            case IRON_HOE:
            case GOLD_HOE:
            case STONE_HOE:
            case WOOD_HOE: {
                pEMan.addPotionEffectStacking(PotionEffectType.WITHER.createEffect(40, 3), (LivingEntity) event.getEntity());
                return;
            }
            default: {
                return;
            }
        
            }
        }  
    }
}
