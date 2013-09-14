package net.kingdomsofarden.andrew2060.heroes.skills.api.necromancy;

import java.util.ArrayList;
import java.util.UUID;

import net.kingdomsofarden.andrew2060.heroes.skills.api.necromancy.events.SummonedDeathEvent;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
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
    private NecromancyTargetManager targetMan;
    
    public NecromancyMobManager(Heroes plugin, Hero summoner) {
        super(plugin, null, "NecromancyMobManager");
        this.activeEntities = new ArrayList<UUID>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.summoner = summoner;
        this.setPersistent(true);
        this.targetMan = null;
        return;
    }

    public void setTargetManager(NecromancyTargetManager targetMan) {
        this.targetMan = targetMan;
    }
    
    public boolean isTrackedEntity(Creature entity) {
        return activeEntities.contains(entity.getUniqueId());
    }
    
    public void addTrackedEntity(Creature entity) {
        activeEntities.add(entity.getUniqueId());
        return;
    }
    public void updateTargets(LivingEntity lE) {
        for(Entity e : summoner.getPlayer().getWorld().getEntities()) {
            if(activeEntities.contains(e.getUniqueId())) {
                ((Creature)e).setTarget(lE);
            }
        }
    }
    @EventHandler(ignoreCancelled = true, priority=EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if(!(event.getEntity() instanceof Creature)) {
            return;
        }
        if(isTrackedEntity((Creature) event.getEntity())) {
            this.activeEntities.remove(event.getEntity().getUniqueId());
            SummonedDeathEvent dEvent = new SummonedDeathEvent(event.getEntity(),summoner);
            plugin.getServer().getPluginManager().callEvent(dEvent);
            return;
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST) 
    public void onEntityTarget(EntityTargetEvent event) {
        if(!(event.getEntity() instanceof Creature)) {
            return;
        }
        if(isTrackedEntity((Creature) event.getEntity())) {
            event.setTarget(targetMan.getTarget());
            event.setCancelled(true);   //Requires some further refinement
            return;
        }
        
    }
    
}
