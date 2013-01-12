package net.swagserv.andrew2060.heroes.skills;

import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffectType;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.util.Setting;

public class SkillGeyser extends ActiveSkill {

	public SkillGeyser(Heroes plugin) {
		super(plugin, "Geyser");
	    setDescription("After $1 seconds, summons a geyser that erupts from the ground, dealing $2 damage to anyone within a radius of the caster and slowing them by $3% for $4 seconds.");
	    setUsage("/skill geyser");
	    setArgumentRange(0, 0);
	    setIdentifiers(new String[] { "skill geyser" });	}
	@Override
	public String getDescription(Hero h) {
		return getDescription()
				.replace("$1", SkillConfigManager.getUseSetting(h, this, "TimeBurst", 1000, false)*0.001 +"")
				.replace("$2", SkillConfigManager.getUseSetting(h, this, Setting.DAMAGE.node(), 30, false) +h.getLevel()*SkillConfigManager.getUseSetting(h, this, Setting.DAMAGE_INCREASE.node(), 0.5, false) + "")
				.replace("$3", SkillConfigManager.getUseSetting(h, this, "SlowAmount", 2, false)*15 +"")
				.replace("$4", SkillConfigManager.getUseSetting(h, this, "SlowDuration", 6000, false)*0.001 +"");
	}
    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set("TimeBurst", 1000);
        node.set(Setting.DAMAGE.node(), 30);
        node.set("SlowAmount", 2);
        node.set("SlowDuration", 6000);
        node.set(Setting.COOLDOWN.node(), 30000);
        node.set(Setting.DAMAGE.node(), 30);
        node.set(Setting.DAMAGE_INCREASE.node(), 0.5);
        node.set(Setting.MANA.node(), 5);
        return node;
    }
	@Override
	public SkillResult use(Hero h, String[] args) {
		final Hero casterHero = h;
		long timeBurst = (long) (SkillConfigManager.getUseSetting(h, this, "TimeBurst", 1000, false)* 0.001*20);
		final double damage = SkillConfigManager.getUseSetting(h, this, Setting.DAMAGE.node(), 30, false) +  +h.getLevel()*SkillConfigManager.getUseSetting(h, this, Setting.DAMAGE_INCREASE.node(), 0.5, false);
		final int slowStrength = SkillConfigManager.getUseSetting(h, this, "SlowAmount", 1, false);
		final int slowDuration = (int) (SkillConfigManager.getUseSetting(h, this, "SlowDuration", 6000, false)*0.001*20);
		this.broadcast(h.getPlayer().getLocation(), h.getName() + ChatColor.GRAY + " is channeling a geyser!");
		h.getPlayer().getWorld().playEffect(h.getPlayer().getLocation(), Effect.ENDER_SIGNAL, 3);
		h.getPlayer().getWorld().playSound(casterHero.getEntity().getLocation(), Sound.CREEPER_HISS, 100, 1);
		Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
			@Override
			public void run() {
				SkillGeyser.this.broadcast(casterHero.getPlayer().getLocation(), casterHero.getName() + " erupted a geyser!");
				Iterator<Entity> nearby = casterHero.getEntity().getNearbyEntities(5, 5, 5).iterator();
				while(nearby.hasNext()) {
					Entity e = nearby.next();
					if(!(e instanceof LivingEntity)) {
						continue;
					}
					LivingEntity le = (LivingEntity)e;
					if(!Skill.damageCheck(casterHero.getPlayer(), le)) {
						continue;
					}
					addSpellTarget(le,casterHero);
					Skill.damageEntity(le, casterHero.getEntity(), (int) damage, DamageCause.MAGIC);
					le.addPotionEffect(PotionEffectType.SLOW.createEffect(slowDuration, slowStrength));
				}
			}
		}, timeBurst);
		return SkillResult.NORMAL;
	}


	public static Entity[]  getNearbyEntities(Location l, int radius){
        int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16))/16;
        HashSet<Entity> radiusEntities = new HashSet<Entity>();
            for (int chX = 0 -chunkRadius; chX <= chunkRadius; chX ++){
                for (int chZ = 0 -chunkRadius; chZ <= chunkRadius; chZ++){
                    int x=(int) l.getX(),y=(int) l.getY(),z=(int) l.getZ();
                    for (Entity e : new Location(l.getWorld(),x+(chX*16),y,z+(chZ*16)).getChunk().getEntities()){
                        if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock()) radiusEntities.add(e);
                    }
                }
            }
        return radiusEntities.toArray(new Entity[radiusEntities.size()]);
    }

}
