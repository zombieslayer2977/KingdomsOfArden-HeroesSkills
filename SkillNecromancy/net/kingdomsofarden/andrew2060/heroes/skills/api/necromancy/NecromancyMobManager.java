package net.kingdomsofarden.andrew2060.heroes.skills.api.necromancy;

import java.util.ArrayList;
import java.util.UUID;

import net.kingdomsofarden.andrew2060.heroes.skills.api.necromancy.events.SummonedDeathEvent;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.Effect;

public class NecromancyMobManager extends Effect implements Listener {

    private ArrayList<UUID> activeEntities;
    private Hero summoner;
    
    public NecromancyMobManager(Heroes plugin, Hero summoner) {
        super(plugin, null, "NecromancyMobManager");
        this.activeEntities = new ArrayList<UUID>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.summoner = summoner;
        this.setPersistent(true);
        return;
    }

    public boolean isTrackedEntity(LivingEntity entity) {
        return activeEntities.contains(entity.getUniqueId());
    }
    
    public void addTrackedEntity(LivingEntity entity) {
        activeEntities.add(entity.getUniqueId());
        return;
    }
    
    @EventHandler(ignoreCancelled = true, priority=EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if(isTrackedEntity(event.getEntity())) {
            this.activeEntities.remove(event.getEntity().getUniqueId());
            SummonedDeathEvent dEvent = new SummonedDeathEvent(event.getEntity(),summoner);
            plugin.getServer().getPluginManager().callEvent(dEvent);
            return;
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST) 
    public void onEntityTarget(EntityTargetEvent event) {
        if(!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        if(isTrackedEntity((LivingEntity) event.getEntity())) {
            event.setCancelled(true);   //Requires some further refinement
            return;
        }
        
    }
    
}
