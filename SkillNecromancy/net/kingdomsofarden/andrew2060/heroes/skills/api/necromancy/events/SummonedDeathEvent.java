package net.kingdomsofarden.andrew2060.heroes.skills.api.necromancy.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.herocraftonline.heroes.characters.Hero;

public class SummonedDeathEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private LivingEntity lE;
    private Hero summoner;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public SummonedDeathEvent(LivingEntity lE, Hero summoner) {
        this.lE = lE;
        this.summoner = summoner;
    }
    
    public LivingEntity getEntity() {
        return lE;
    }
    
    public Hero getSummoner() {
        return summoner;
    }    

}
