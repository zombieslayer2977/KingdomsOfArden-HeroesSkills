package net.kingdomsofarden.andrew2060.heroes.skills;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R2.entity.AbstractProjectile;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_6_R2.DamageSource;
import net.minecraft.server.v1_6_R2.EntityLargeFireball;
import net.minecraft.server.v1_6_R2.EntityTypes;
import net.minecraft.server.v1_6_R2.MovingObjectPosition;
import net.minecraft.server.v1_6_R2.World;
import net.minecraft.server.v1_6_R2.WorldServer;
import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;
import net.kingdomsofarden.andrew2060.toolhandler.potions.PotionEffectManager;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillMeteorStrike extends ActiveSkill implements Listener {

    PotionEffectManager pEMan;

    public SkillMeteorStrike(Heroes plugin) {
        super(plugin, "MeteorStrike");
        try {
            Method a = EntityTypes.class.getDeclaredMethod("a", new Class<?>[] {Class.class, String.class, int.class});
            a.setAccessible(true);
            a.invoke(a, EntityMeteor.class, "Fireball", 12);
        } catch (Exception e) {
            System.out.println("There was an error creating a meteor with custom entity class version 1_6 : It could not be registered with the NMS EntityType handler!");
            e.printStackTrace();
            plugin.getSkillManager().removeSkill(this);
            return;
        }
        setDescription("Summons a devastating meteor to strike at the target location, harming friend and foe alike.");
        setIdentifiers("skill meteorstrike");
        setUsage("/skill meteorstrike");
        setArgumentRange(0,0);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                pEMan = ((ToolHandlerPlugin)Bukkit.getServer().getPluginManager().getPlugin("KingdomsOfArden-ToolHandler")).getPotionEffectHandler();
            }

        }, 200L);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public SkillResult use(final Hero h, String[] args) {
        this.broadcast(h.getPlayer().getLocation(), ChatColor.GRAY + "[" + ChatColor.GREEN + "Skill" + ChatColor.GRAY + "] $1 used MeteorStrike!", new Object[] {h.getName()});
        List<Block> los;
        if(!h.hasEffect("PowerLocusEffect")) {
            los = h.getPlayer().getLastTwoTargetBlocks(null, 32);
        } else {
            los = h.getPlayer().getLastTwoTargetBlocks(null, 100);
        }
        final Location targetLoc = los.get(los.size()-1).getLocation();
        final Location spawnLoc = h.getPlayer().getLocation().add(0,75,0);
        plugin.getServer().getScheduler().runTaskLater(plugin, new BukkitRunnable() {
            @Override
            public void run() {
                spawnMeteorAndTarget(targetLoc, spawnLoc).setCaster(h);
            }
        }, 40);
        return SkillResult.NORMAL;
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if(!(event.getEntity() instanceof AbstractProjectile)) {
            return;
        }

        if(((AbstractProjectile)event.getEntity()).getHandle() instanceof EntityMeteor) {
            EntityMeteor meteor = (EntityMeteor) ((AbstractProjectile)event.getEntity()).getHandle();
            final Location hitLoc = event.getEntity().getLocation();
            Hero h = meteor.getCaster();
            Arrow a = hitLoc.getWorld().spawn(hitLoc.clone().add(0,16,0), Arrow.class);
            for(Entity e : a.getNearbyEntities(32, 32, 32)) {
                if(!(e instanceof LivingEntity)) {
                    continue;
                }
                LivingEntity lE = (LivingEntity)e;
                double distanceSquared = lE.getLocation().distanceSquared(hitLoc);
                double multiplier = 1 - (distanceSquared / 1024D);  //Scale damage based on distance to epicenter down to a minimum of 50% damage
                if(multiplier <= 0) {
                    continue;
                }
                if(Skill.damageCheck(h.getPlayer(), lE)) {
                    addSpellTarget(lE,h);
                    Skill.damageEntity(lE, h.getPlayer(), lE.getMaxHealth() * 1.5 * multiplier, DamageCause.ENTITY_ATTACK);
                    lE.setVelocity(lE.getLocation().toVector().subtract(hitLoc.toVector()).normalize().multiply(16D).multiply(multiplier));
                    pEMan.addPotionEffectStacking(PotionEffectType.BLINDNESS.createEffect(800, 1), lE, false);
                    pEMan.addPotionEffectStacking(PotionEffectType.CONFUSION.createEffect(400, 1), lE, false);
                    continue;
                } else {
                    lE.damage(lE.getMaxHealth() * 1.5 * multiplier,event.getEntity());
                    lE.setVelocity(lE.getLocation().toVector().subtract(hitLoc.toVector()).normalize().multiply(16D).multiply(multiplier));
                    pEMan.addPotionEffectStacking(PotionEffectType.BLINDNESS.createEffect(800, 1), lE, false);
                    pEMan.addPotionEffectStacking(PotionEffectType.CONFUSION.createEffect(400, 1), lE, false);
                    continue;
                }
            }
            a.remove();

//            for(int i = 0; i < 32 ; i++) {
//                final int radius = i;
//                (new BukkitRunnable() {
//
//                    @Override
//                    public void run() {
//
//                        for(Location loc : circle(hitLoc, radius, 5, true, false, -1)) {
//                            loc.getWorld().createExplosion(loc, 0F, false);
//                        }
//
//                        return;
//
//                    }
//
//                }).runTaskLater(plugin, Math.round(0.25*radius));
//            }
//
//            for(int i = 0; i < 32; i++) {
//                final int height = i;
//                (new BukkitRunnable() {
//
//                    @Override
//                    public void run() {
//                        
//                        for(Location loc : circle(hitLoc, height > 20 ? (20-(height-20)) : 5, 5, true, false, height)) {
//                            loc.getWorld().createExplosion(loc, 0F, false);
//                        }
//                        return;
//
//                    }
//
//                }).runTaskLater(plugin, Math.round(0.25*height));
//            }
        }
    }
    public class EntityMeteor extends EntityLargeFireball {
        private float velMultiplier;
        private float explosionRadius;
        private float trailPower;
        private Hero caster;

        public EntityMeteor(World world) {
            super(world);
            a(1.0F,1.0F);
            this.velMultiplier = 0.95F;
            this.explosionRadius = 70F;
            this.trailPower = 0F;
            this.caster = null;
        }

        @Override
        public void l_() {

            this.world.createExplosion(this, this.locX, this.locY, this.locZ, trailPower, false, false);

            motX *= velMultiplier;
            motY *= velMultiplier;
            motZ *= velMultiplier;

            super.l_();
        }

        @Override
        public void a(MovingObjectPosition movingObjectPosition) {
            if (!this.world.isStatic) {
                if (movingObjectPosition.entity != null) {
                    movingObjectPosition.entity.damageEntity(DamageSource.fireball(this, this.shooter), 150);
                }

                // CraftBukkit start
                ExplosionPrimeEvent event = new ExplosionPrimeEvent((org.bukkit.entity.Explosive) org.bukkit.craftbukkit.v1_6_R2.entity.CraftEntity.getEntity(this.world.getServer(), this));
                this.world.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    this.world.createExplosion(this, this.locX, this.locY, this.locZ, explosionRadius, false, false);
                }
                this.die();
            }
        }

        public Vector getDirection() {
            return new Vector(this.dirX, this.dirY, this.dirZ);
        }

        public void setDirection(Vector direction) {
            this.setDirection(direction.getX(), direction.getY(), direction.getZ());
        }

        public Vector getVelocity() {
            return new Vector(this.motX, this.motY, this.motZ);
        }

        public void setVelocity(Vector vel) {
            this.motX = vel.getX();
            this.motY = vel.getY();
            this.motZ = vel.getZ();
            this.velocityChanged = true;
        }

        public CraftWorld getWorld() {
            return ((WorldServer) this.world).getWorld();
        }

        public boolean teleport(Location location) {
            return teleport(location, TeleportCause.PLUGIN);
        }

        public boolean teleport(Location location, TeleportCause cause) {
            this.world = ((CraftWorld) location.getWorld()).getHandle();
            this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            return true;
        }

        public void setCaster(Hero hero) {
            this.caster = hero;
        }

        public Hero getCaster() {
            return this.caster;
        }
    }
    public EntityMeteor spawnMeteorAndTarget(Location targetLoc, Location spawnLoc) {
        // Target coords
        final double x1 = targetLoc.getX();
        final double y1 = targetLoc.getY();
        final double z1 = targetLoc.getZ();

        // Spawn coords
        final double x0 = spawnLoc.getX();
        final double y0 = spawnLoc.getY();
        final double z0 = spawnLoc.getZ();

        final double vx = (x1 - x0) / 10;
        final double vy = (y1 - y0) / 10;
        final double vz = (z1 - z0) / 10;

        CraftWorld cWorld = (CraftWorld) targetLoc.getWorld();

        EntityMeteor eMeteor = new EntityMeteor(cWorld.getHandle());

        cWorld.getHandle().addEntity(eMeteor, SpawnReason.NATURAL);

        eMeteor.setPosition(x0, y0, z0);

        Vector translation = new Vector(x1 - x0, y1 - y0, z1 - z0);
        eMeteor.setDirection(translation);

        Vector velocity = new Vector(vx, vy, vz);
        eMeteor.setVelocity(velocity.normalize().multiply(0.5)); //make a bit slower

        return eMeteor;
    }
    protected List<Location> circle(Location loc, Integer r, Integer h, boolean hollow, boolean sphere, int plus_y) {
        List<Location> circleblocks = new ArrayList<Location>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        for (int x = cx - r; x <= cx +r; x++)
            for (int z = cz - r; z <= cz +r; z++)
                for (int y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r*r && !(hollow && dist < (r-1)*(r-1))) {
                        Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }

        return circleblocks;
    }
}
