package net.kingdomsofarden.andrew2060.heroes.skills;

import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;

public class SkillMortarStrike extends ActiveSkill {

	public SkillMortarStrike(Heroes plugin) {
		super(plugin, "MortarStrike");
		setDescription("Fires $1 mortars into the air that impact anywhere inside a $2 by $2 area at the area under the shooter's cursor up to a distance of $3 blocks after $4 seconds");
		setArgumentRange(0,0);
		setIdentifiers("skill mortarstrike");
		setUsage("/skill mortarstrike");
	}

	@Override
	public SkillResult use(Hero h, String[] arg1) {
		Bukkit.getServer().getLogger().log(Level.INFO, "Skill Used");
		int mortars = SkillConfigManager.getUseSetting(h, this, "base-mortar-count", 0, false) + SkillConfigManager.getUseSetting(h, this, "mortars-per-level", 1, false)*h.getLevel();
		if(SkillConfigManager.getUseSetting(h, this, "max-mortars", 50, false) > mortars) {
			mortars = SkillConfigManager.getUseSetting(h, this, "max-mortars", 50, false);
		}
		final int dispersion = SkillConfigManager.getUseSetting(h, this, "impactdispersion", 10, false);
		int distance = SkillConfigManager.getUseSetting(h, this, "impactdistance", 30, false);
		long delay = (long) (SkillConfigManager.getUseSetting(h, this, "impactdelay", 5000, false)*0.001);
		long time = (long) (SkillConfigManager.getUseSetting(h, this, "time-between-mortars", 500, false)*0.001*20);
		final Location fireloc = h.getPlayer().getLocation();
		final Location loc = fireloc.getDirection().setY(0).normalize().multiply(distance).toLocation(fireloc.getWorld()).add(fireloc);
		final long impacttime = delay*20;
		final Hero hero = h;
		for(int i = 0;i<mortars;i++) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {

				@Override
				public void run() {
					final Firework firework = (Firework) hero.getPlayer().getWorld().spawnEntity(fireloc, EntityType.FIREWORK);
					FireworkMeta meta = firework.getFireworkMeta();
					meta.setPower(64);
					firework.setFireworkMeta(meta);
					//Prevent Lag by removing firework after a second and a half of it flying
					Bukkit.getScheduler().scheduleSyncDelayedTask(SkillMortarStrike.this.plugin, new Runnable() {

						@Override
						public void run() {
							firework.remove();
						}
						
					}, 40L);
					Bukkit.getScheduler().scheduleSyncDelayedTask(SkillMortarStrike.this.plugin, new Runnable() {

						@Override
						public void run() {
							Random randGen = new Random();
							int disp1 = randGen.nextInt(dispersion*2)-dispersion;
				 			int disp2 = randGen.nextInt(dispersion*2)-dispersion;
							Location impactLoc = hero.getPlayer().getWorld().getHighestBlockAt(loc.getBlockX() + disp1, loc.getBlockZ() + disp2).getLocation();
							hero.getPlayer().getWorld().createExplosion(impactLoc, 0F);
							Bukkit.getServer().getLogger().log(Level.INFO, "Created explosion at " + impactLoc.toString());
						}
						
					},impacttime);
				}
				
			}, i*time);
		}
		return SkillResult.NORMAL;
	}

	@Override
	public String getDescription(Hero h) {
		int mortars = SkillConfigManager.getUseSetting(h, this, "base-mortar-count", 0, false) + SkillConfigManager.getUseSetting(h, this, "mortars-per-level", 1, false)*h.getLevel();
		if(SkillConfigManager.getUseSetting(h, this, "max-mortars", 50, false) > mortars) {
			mortars = SkillConfigManager.getUseSetting(h, this, "max-mortars", 50, false);
		}
		int dispersion = SkillConfigManager.getUseSetting(h, this, "impactdispersion", 10, false);
		int distance = SkillConfigManager.getUseSetting(h, this, "impactdistance", 30, false);
		double delay = SkillConfigManager.getUseSetting(h, this, "impactdelay", 5000, false)*0.001;
		return getDescription()
				.replace("$1", mortars + "")
				.replace("$2", dispersion*2 + "")
				.replace("$3", distance + "")
				.replace("$4", delay + "");
	}
	
	@Override
	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection node = super.getDefaultConfig();
		node.set("base-mortar-count", 0);
		node.set("mortars-per-level", 1);
		node.set("max-mortars", 50);
		node.set("impactdispersion", 10);
		node.set("impactdistance", 30);
		node.set("impactdelay", 5000);
		return node;
	}
}
