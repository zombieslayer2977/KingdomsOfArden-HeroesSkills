package net.kingdomsofarden.andrew2060.heroes.skills.nms;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

import com.herocraftonline.heroes.characters.Hero;

import net.kingdomsofarden.andrew2060.heroes.skills.api.necromancy.NecromancyTargetManager;
import net.minecraft.server.v1_7_R1.DamageSource;
import net.minecraft.server.v1_7_R1.EntityCreature;
import net.minecraft.server.v1_7_R1.PathfinderGoalTarget;

public class PathfinderGoalNecromancyTarget extends PathfinderGoalTarget {
    
    private Hero summoner;
    private LivingEntity target;
    
    public PathfinderGoalNecromancyTarget(EntityCreature entity, Hero summoner) {
        super(entity,false);
        this.summoner = summoner;
        this.target = null;
    }

    @Override
    public boolean a() {
        boolean flag = false;
        if(this.c.getGoalTarget() != null) {
            if(!c.isAlive()) {
                return false;    //If target is dead skip subsequent checks
            }
            Location targetLoc = c.getGoalTarget().getBukkitEntity().getLocation();
            if(this.c.getBukkitEntity().getLocation().distanceSquared(targetLoc) > 1024) {
                flag = true;
            }
            if(summoner.getPlayer().getLocation().distanceSquared(targetLoc) > 1024) {
                flag = true;
            }
            if(!summoner.hasEffect("NecromancyTargetManager")) {
                flag = false;
                this.c.damageEntity(DamageSource.ANVIL, 10000); //Kill the entity
            } else {
                NecromancyTargetManager targetMan = (NecromancyTargetManager) summoner.getEffect("NecromancyTargetManager");
                target = targetMan.getTarget((Creature) c.getBukkitEntity());
                if(this.target == null) {
                    flag = false;
                } else {
                    flag = true;
                }
            }
        } else {
            flag = true;
        }
        return flag;
    }

    @Override
    public void c() {
        ((Creature)this.c.getBukkitEntity()).setTarget(target);
        super.c();
    }
    
}
