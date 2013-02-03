package net.swagserv.andrew2060.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.Monster;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.util.Setting;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

public class SkillRiftWalk extends ActiveSkill {
	public SkillRiftWalk(Heroes plugin) {
		super(plugin, "RiftWalk");
		setUsage("/skill riftwalk");
		setArgumentRange(0, 0);
		setIdentifiers(new String[] { "skill riftwalk" });
		setDescription("On use, user teleports $1 blocks in the direction of the cursor, ignoring terrain and dealing $2 damage to anyone in an area around the apparition point. If anyone is hit by the damage, the user riftwalks again automatically after 3 seconds dealing $3 damage.");
	}

	public String getDescription(Hero hero) {
		int level = hero.getLevel();
		int range = SkillConfigManager.getUseSetting(hero, this, "range", 16, false);
	   return getDescription().replace("$1", range +"").replace("$2", level / 2 +"").replace("$3", level / 4 +"");
	}

	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection node = super.getDefaultConfig();
		node.set("range", Integer.valueOf(16));
		node.set(Setting.COOLDOWN.node(), Integer.valueOf(120000));
		node.set(Setting.MANA.node(), Integer.valueOf(10));
		return node;
	}

	public SkillResult use(Hero hero, String[] arg1) {
		Player p = hero.getPlayer();
		int range = SkillConfigManager.getUseSetting(hero, this, "range", 16, false);
		Vector v = p.getLocation().getDirection().normalize().multiply(range);
		Location finalLocation = p.getLocation().add(v);
		finalLocation.setPitch(p.getLocation().getPitch());
		finalLocation.setYaw(p.getLocation().getYaw());
		finalLocation = finalLocation.add(0.0D, 1.0D, 0.0D);
		Block b = finalLocation.getBlock();
		Block hb = b.getRelative(BlockFace.UP);
		if ((b.getType() != Material.AIR) || (hb.getType() != Material.AIR)) {
			for (int i = 0; i < 256; i++) {
				b = finalLocation.add(0.0D, i, 0.0D).getBlock();
				hb = b.getRelative(BlockFace.UP);
				if ((b.getType() == Material.AIR) && (hb.getType() == Material.AIR)) {
					finalLocation = finalLocation.add(0.0D, i, 0.0D);
					break;
				}
			}
		}

		p.playEffect(p.getLocation(), Effect.GHAST_SHRIEK, 1);
		p.teleport(finalLocation);
		p.getWorld().strikeLightningEffect(finalLocation);
		List<Entity> nearby = p.getNearbyEntities(5.0D, 5.0D, 5.0D);
		boolean repeat = false;
		for (int x = 0; x < nearby.size(); x++) {
			if ((nearby.get(x) instanceof LivingEntity)) {
				if (damageCheck(p, (LivingEntity)nearby.get(x))) {
					damageEntity((LivingEntity)nearby.get(x), p, (int)(hero.getLevel() * 0.5D), DamageCause.MAGIC);
					if ((nearby.get(x) instanceof Player)) {
						Hero hDmg = this.plugin.getCharacterManager().getHero((Player)nearby.get(x));
						p.sendMessage(hDmg.getName() + " took " + hero.getLevel() * 0.5D + " damage. " + hDmg.getHealth() + "/" + hDmg.getMaxHealth() + " health.");
					} else {
						Monster mDmgDel = this.plugin.getCharacterManager().getMonster((LivingEntity)nearby.get(x));
						p.sendMessage(mDmgDel.getName() + " took " + hero.getLevel() * 0.5D + " damage. " + mDmgDel.getHealth() + "/" + mDmgDel.getMaxHealth() + " health.");
					}
					repeat = true;
				}
			}
		}
		if (repeat) {
			final Player pDelayed = p;
			final Hero hDelayed = hero;
			Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
				public void run() {
					int rangeDelayed = SkillConfigManager.getUseSetting(hDelayed, SkillRiftWalk.this, "range", 16, false);
					Vector vDel = pDelayed.getLocation().getDirection().normalize().multiply(rangeDelayed);
					Location delayedLoc = pDelayed.getLocation().add(vDel);
					delayedLoc.setPitch(pDelayed.getLocation().getPitch());
					delayedLoc.setYaw(pDelayed.getLocation().getYaw());
					delayedLoc = delayedLoc.add(0.0D, 1.0D, 0.0D);
					Block b1 = delayedLoc.getBlock();
					Block hb1 = b1.getRelative(BlockFace.UP);
					if ((b1.getType() != Material.AIR) || (hb1.getType() != Material.AIR)) {
						for (int i = 0; i < 256; i++) {
							b1 = delayedLoc.add(0.0D, i, 0.0D).getBlock();
							hb1 = b1.getRelative(BlockFace.UP);
							if ((b1.getType() == Material.AIR) && (hb1.getType() == Material.AIR)) {
								delayedLoc = delayedLoc.add(0.0D, i, 0.0D);
								break;
							}
						}

					}

					pDelayed.playEffect(pDelayed.getLocation(), Effect.GHAST_SHRIEK, 1);
					pDelayed.teleport(delayedLoc, TeleportCause.UNKNOWN);
					pDelayed.getWorld().strikeLightningEffect(delayedLoc);
					List<Entity> nearbyDel = pDelayed.getNearbyEntities(5.0D, 5.0D, 5.0D);
					for (int x = 0; x < nearbyDel.size(); x++) {
						if ((nearbyDel.get(x) instanceof LivingEntity)) {
							if (SkillRiftWalk.damageCheck(pDelayed, (LivingEntity)nearbyDel.get(x))) {
								SkillRiftWalk.damageEntity((LivingEntity)nearbyDel.get(x), pDelayed, (int)(hDelayed.getLevel() * 0.25D), DamageCause.MAGIC);
								if ((nearbyDel.get(x) instanceof Player)) {
									Hero hDmgDel = SkillRiftWalk.this.plugin.getCharacterManager().getHero((Player)nearbyDel.get(x));
									pDelayed.sendMessage(hDmgDel.getName() + " took " + hDelayed.getLevel() * 0.25D + " damage. " + hDmgDel.getHealth() + "/" + hDmgDel.getMaxHealth() + " health.");
								} else {
									Monster mDmgDel = SkillRiftWalk.this.plugin.getCharacterManager().getMonster((LivingEntity)nearbyDel.get(x));
									pDelayed.sendMessage(mDmgDel.getName() + " took " + hDelayed.getLevel() * 0.25D + " damage. " + mDmgDel.getHealth() + "/" + mDmgDel.getMaxHealth() + " health.");
								}
							}
						}
					}
				}
			}
			, 60L);
		}
		return SkillResult.NORMAL;
	}
}