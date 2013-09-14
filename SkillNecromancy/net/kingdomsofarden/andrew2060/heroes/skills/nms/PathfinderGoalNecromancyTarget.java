package net.kingdomsofarden.andrew2060.heroes.skills.nms;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_6_R2.EntityCreature;
import net.minecraft.server.v1_6_R2.PathfinderGoalTarget;

public class PathfinderGoalNecromancyTarget extends PathfinderGoalTarget {
    
    EntityCreature entity;
    private Player summoner;
    
    public PathfinderGoalNecromancyTarget(EntityCreature entity, Player summoner) {
        super(entity,false);
        this.entity = entity;
        this.summoner = summoner;
    }

    @Override
    public boolean a() {
        boolean flag = false;
        if(entity.getGoalTarget() != null) {
            if(!entity.isAlive()) {
                return true;    //If target is dead skip subsequent checks
            }
            Location targetLoc = entity.getGoalTarget().getBukkitEntity().getLocation();
            if(entity.getBukkitEntity().getLocation().distanceSquared(targetLoc) > 1024) {
                flag = true;
            }
            if(summoner.getLocation().distanceSquared(targetLoc) > 1024) {
                flag = true;
            }
        } else {
            flag = true;
        }
        return flag;
    }

}
