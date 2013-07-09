package net.kingdomsofarden.andrew2060.heroes.skills.turretModules;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.Skill;

public class Turret {
    private Location loc;
    private Location checkFireLoc;
    private Hero creator;
    private long expirationTime;
    double range;
    Block a;
    Block b;
    Block c;
    Block d;
    Block e;
    Block f;

    public Turret(Location loc, double range, long expirationTime, Hero creator) {
        this.setExpirationTime(expirationTime);
        this.setLoc(loc);
        this.setCreator(creator);
        this.range = range;
        this.checkFireLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()).add(0,2,0);
    }
    /**
     * Creates a turret
     **/
    public boolean createTurret() {
        World w = getCreator().getPlayer().getWorld();
        //We want to get whatever block is here first so that we can replace it later (if its not air)
        a = w.getBlockAt(loc);
        b = w.getBlockAt(new Location(w,getLoc().getX() + 1, getLoc().getY() , getLoc().getZ()));
        c = w.getBlockAt(new Location(w,getLoc().getX() - 1, getLoc().getY() , getLoc().getZ()));
        d = w.getBlockAt(new Location(w,getLoc().getX() , getLoc().getY() , getLoc().getZ() + 1));
        e = w.getBlockAt(new Location(w,getLoc().getX() , getLoc().getY() , getLoc().getZ() - 1));
        f = w.getBlockAt(new Location(w,getLoc().getX(), getLoc().getY() + 1, getLoc().getZ()));
        //Check to make sure that these blocks are clear
        if(!((a.getType().equals(Material.AIR) || a.getType().equals(Material.GRASS)) 
                && (b.getType().equals(Material.AIR) || b.getType().equals(Material.GRASS))
                && (c.getType().equals(Material.AIR) || c.getType().equals(Material.GRASS))
                && (d.getType().equals(Material.AIR) || d.getType().equals(Material.GRASS))
                && (e.getType().equals(Material.AIR) || e.getType().equals(Material.GRASS))
                && (f.getType().equals(Material.AIR) || f.getType().equals(Material.GRASS)))) {
            return false;
        }
        //Create the turret
        a.setType(Material.FENCE);
        b.setType(Material.FENCE);
        c.setType(Material.FENCE);
        d.setType(Material.FENCE);
        e.setType(Material.FENCE);
        f.setType(Material.DISPENSER);
        return true;
    }
    /**
     * Destroys the turret
     */
    public boolean destroyTurret() {
        TurretEffect tE = (TurretEffect)getCreator().getEffect("TurretEffect");
        if(tE != null) {
            TurretFireWrapper fW = tE.getFireFunctionWrapper();
            if(fW != null) {
                if(!fW.onDestroy(this)) {
                    return false;
                }
            }
            tE.removeTurret(this);
        }
        //We don't need to chunk check 'a'/'f' because its dead center anyways
        if(!b.getChunk().isLoaded()) {
            b.getChunk().load();
        }
        if(!c.getChunk().isLoaded()) {
            c.getChunk().load();
        }
        if(!d.getChunk().isLoaded()) {
            d.getChunk().load();
        }
        if(!e.getChunk().isLoaded()) {
            e.getChunk().load();
        }

        a.setType(Material.AIR);
        b.setType(Material.AIR);

        c.setType(Material.AIR);
        d.setType(Material.AIR);
        e.setType(Material.AIR);
        f.setType(Material.AIR);
        return true;
    }
    /**
     * Overrideable code that acquires turret targets
     * 
     * @return a list of valid targets
     */
    public List<LivingEntity> acquireTargets() {
        List<LivingEntity> validTargets = new LinkedList<LivingEntity>();
        Ocelot nearbySearch = (Ocelot) loc.getWorld().spawnEntity(checkFireLoc, EntityType.OCELOT);
        nearbySearch.setOwner(creator.getPlayer());
        List<Entity> debug = nearbySearch.getNearbyEntities(range, 5, range);
        Iterator<Entity> nearby = debug.iterator();
        nearbySearch.remove();
        while(nearby.hasNext()) {
            Entity next = nearby.next();
            if(!(next instanceof LivingEntity)) {
                continue;
            }
            LivingEntity lE = (LivingEntity) next;
            if(lE instanceof Tameable) {
                Player p = (Player)((Tameable)lE).getOwner();
                if(p == null) {
                    if(!Skill.damageCheck(creator.getPlayer(), lE)) {
                        continue;
                    }
                } else if(!p.isOnline()) {
                    continue;
                } else if(p == creator.getPlayer()) {
                    continue;
                } else {
                    if(!Skill.damageCheck(creator.getPlayer(), p)) {
                        continue;
                    }
                }
            }
            Ocelot o = (Ocelot) loc.getWorld().spawnEntity(checkFireLoc, EntityType.OCELOT);
            o.setTarget(lE);
            if(!o.hasLineOfSight(next)) {
                o.remove();
                continue;
            }
            o.remove();
            if(Skill.damageCheck(creator.getPlayer(), lE) && lE != creator.getEntity()) {
                validTargets.add(lE);
            }
        }
        return validTargets;
    }
    /**
     * Fires the turret at any nearby entities
     */
    public void fireTurret() {
        if(!creator.hasEffect("TurretEffect")) {
            return;
        }
        //Fire based on what is ordained within the hero's current TurretEffect
        TurretEffect tE = (TurretEffect)creator.getEffect("TurretEffect");
        TurretFireWrapper fW = tE.getFireFunctionWrapper();
        if(fW == null) {
            return; //No active mode selected, so we just exit out. Note that this means that turrets will always fire based on the last active effect
        }
        List<LivingEntity> validTargets = acquireTargets();
        fW.fire(creator, getLoc(),range, validTargets);
        return;
    }

    public long getExpirationTime() {
        return expirationTime;
    }
    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }
    public Hero getCreator() {
        return creator;
    }
    public void setCreator(Hero creator) {
        this.creator = creator;
    }
    public Location getLoc() {
        return loc;
    }
    public void setLoc(Location loc) {
        this.loc = loc;
    }
    public double getRange() {
        return range;
    }
    public void destroyTurretNonCancellable() {
        TurretEffect tE = (TurretEffect)getCreator().getEffect("TurretEffect");
        if(tE != null) {
            tE.removeTurret(this);
        }
        //We don't need to chunk check 'a'/'f' because its dead center anyways
        if(!b.getChunk().isLoaded()) {
            b.getChunk().load();
        }
        if(!c.getChunk().isLoaded()) {
            c.getChunk().load();
        }
        if(!d.getChunk().isLoaded()) {
            d.getChunk().load();
        }
        if(!e.getChunk().isLoaded()) {
            e.getChunk().load();
        }
        a.setType(Material.AIR);
        b.setType(Material.AIR);
        c.setType(Material.AIR);
        d.setType(Material.AIR);
        e.setType(Material.AIR);
        f.setType(Material.AIR);
        return;
    }
}