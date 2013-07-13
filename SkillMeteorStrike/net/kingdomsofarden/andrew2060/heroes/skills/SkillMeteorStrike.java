package net.kingdomsofarden.andrew2060.heroes.skills;

import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.AbstractProjectile;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityLargeFireball;
import net.minecraft.server.EntityTypes;
import net.minecraft.server.MovingObjectPosition;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillMeteorStrike extends ActiveSkill implements Listener {

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
        Bukkit.getPluginManager().registerEvents(this, plugin);
        setIdentifiers("skill meteorstrike");
        setUsage("/skill meteorstrike");
        setArgumentRange(0,0);
    }

    @Override
    public SkillResult use(Hero h, String[] args) {
        this.broadcast(h.getPlayer().getLocation(), ChatColor.GRAY + "[" + ChatColor.GREEN + "Skill" + ChatColor.GRAY + "] $1 used MeteorStrike!", new Object[] {h.getName()});
        List<Block> los;
        if(h.hasEffect("PowerLocusEffect")) {
            los = h.getPlayer().getLastTwoTargetBlocks(null, 16);
        } else {
            los = h.getPlayer().getLastTwoTargetBlocks(null, 100);
        }
        Location targetLoc = los.get(los.size()-1).getLocation();
        Location spawnLoc = h.getPlayer().getLocation();
        spawnLoc.setY(256);
        spawnMeteorAndTarget(targetLoc, spawnLoc).setCaster(h);
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
            Location hitLoc = event.getEntity().getLocation();
            Hero h = meteor.getCaster();
            Arrow a = hitLoc.getWorld().spawn(hitLoc, Arrow.class);
            for(Entity e : a.getNearbyEntities(16, 16, 16)) {
                if(!(e instanceof LivingEntity)) {
                    continue;
                }
                LivingEntity lE = (LivingEntity)e;
                if(Skill.damageCheck(h.getPlayer(), lE)) {
                    Skill.damageEntity(lE, h.getPlayer(), 150D, DamageCause.ENTITY_ATTACK);
                    continue;
                } else {
                    lE.damage(150D);
                }
            }
            a.remove();
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
            this.velMultiplier = 1.10F;
            this.explosionRadius = 70F;
            this.trailPower = 10F;
            this.caster = null;
        }

        @Override
        public void l_() {
            final LargeFireball meteor = (LargeFireball) this.getBukkitEntity();
            meteor.getWorld().createExplosion(meteor.getLocation(), trailPower);

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
                ExplosionPrimeEvent event = new ExplosionPrimeEvent((org.bukkit.entity.Explosive) org.bukkit.craftbukkit.entity.CraftEntity.getEntity(this.world.getServer(), this));
                this.world.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    this.world.createExplosion(this, this.locX, this.locY, this.locZ, explosionRadius, false, true);
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
        eMeteor.setVelocity(velocity.normalize().multiply(0.75)); //make a bit slower
        
        return eMeteor;
    }
}
