package net.swagserv.andrew2060.heroes.skills;


import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wither;
import org.bukkit.util.Vector;

import net.swagserv.andrew2060.heroes.skills.turretModules.Turret;
import net.swagserv.andrew2060.heroes.skills.turretModules.TurretEffect;
import net.swagserv.andrew2060.heroes.skills.turretModules.TurretFireWrapper;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Rel;

public class SkillCommandSentry extends ActiveSkill {

	public SkillCommandSentry(Heroes plugin) {
		super(plugin, "CommandSentry");
		setDescription("Command: Sentry: Range of turret is increased to 50 blocks horizontally and 10 blocks vertically. While in sentry mode, " +
				"turrets will not fire but will scan for the presence of enemies once every five seconds. " +
				"Any found enemies will be reported to the turret's owner as well as their distance from the turret. " +
				"In addition, the location of any stealthed players will be marked by a signal every scan cycle.");
		setUsage("/skill commandsentry");
		setArgumentRange(0,0);
		setIdentifiers("skill commandsentry");
	}

	@Override
	public SkillResult use(Hero h, String[] arg1) {
		if(h.hasEffect("TurretEffectCooldown")) {
			h.getPlayer().sendMessage("You must wait 10 seconds between using different command skills!");
			return SkillResult.NORMAL;
		}
		broadcast(h.getPlayer().getLocation(), "§7[§2Skill§7]$1 activated sentry turret", new Object[] {h.getName()});
		h.addEffect(new ExpirableEffect(this,this.plugin,"TurretEffectCooldown",10000));
		TurretEffect tE;
		if(!h.hasEffect("TurretEffect")) {
			tE = new TurretEffect(plugin, this);
			h.addEffect(tE);
		} else {
			tE = (TurretEffect)h.getEffect("TurretEffect");
		}
		SentryTurret fireFunc = new SentryTurret();
		tE.setFireFunctionWrapper(fireFunc);
		return SkillResult.NORMAL;
	}
	private class SentryTurret extends TurretFireWrapper {
		int cycles;
		public SentryTurret() {
			this.cycles = 0;
		}
		@Override
		public void fire(Hero h, Location loc, double range, List<LivingEntity> validTargets) {
			if(cycles < 5) {
				cycles++;
				return;
			}
			cycles = 0;
			Arrow a = loc.getWorld().spawnArrow(loc, new Vector(0,0,0), 0.6f, 1.6f);
			Player p = h.getPlayer();
			Iterator<Entity> nearby = a.getNearbyEntities(50, 10 , 50).iterator();
			String head = ChatColor.YELLOW + "=======Sentry Report (" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ")=======";
			p.sendMessage(head);
			int bosses = 0;
			int monsters = 0;
			FPlayer fP = FPlayers.i.get(p);
			while(nearby.hasNext()) {
				Entity next = nearby.next();
				if(!(next instanceof LivingEntity)) {
					continue;
				}
				if(!(next instanceof  HumanEntity)) {
					if(next instanceof Wither) {
						bosses++;
						continue;
					}
					if(next instanceof Monster) {
						monsters++;
						continue;
					}
					if(next instanceof Ghast) {
						monsters++;
						continue;
					}
					if(next instanceof Slime) {
						monsters++;
						continue;
					}
					if(next instanceof EnderDragon || next instanceof EnderDragonPart) {
						bosses++;
						continue;
					}
					if(next instanceof EnderDragon || next instanceof EnderDragonPart) {
						bosses++;
						continue;
					}
					if(next instanceof Creature) {
						continue;
					}
				}
				final Player nextP = (Player)next;
				FPlayer nextfP = FPlayers.i.get(nextP);
				if((fP.getRelationTo(nextfP).equals(Rel.ENEMY) || fP.getRelationTo(nextfP).equals(Rel.ALLY)) && (LivingEntity)next != h.getEntity()) {
					Hero nextH = SkillCommandSentry.this.plugin.getCharacterManager().getHero(nextP);
					if(nextP.hasPermission("essentials.vanish")) {
						continue;
					}
					if(p.canSee(nextP)) {
						p.sendMessage(ChatColor.RED + "Hostile Player " + ChatColor.GRAY + "- Class: " + nextH.getHeroClass().getName() + " - Level: " + nextH.getLevel() + " - Health: " + nextP.getHealth() + "/" + nextP.getMaxHealth() + " - Range:" + nextP.getLocation().distance(loc) + " blocks.");
					} else {
						p.sendMessage(ChatColor.RED + "Stealthed hostile player detected at range " +
								nextP.getLocation().distance(loc) + " blocks: location marked!");
						nextP.getWorld().playEffect(nextP.getLocation(), Effect.SMOKE, new Object[0]);
						Runnable displaySmoke = new Runnable() {

							@Override
							public void run() {
								nextP.getWorld().playEffect(nextP.getLocation(), Effect.SMOKE, new Object[0]);
							}
							
						};
						Bukkit.getScheduler().runTaskLater(SkillCommandSentry.this.plugin, displaySmoke, 10);
						Bukkit.getScheduler().runTaskLater(SkillCommandSentry.this.plugin, displaySmoke, 20);
						//Play it 3 times for better visibility
					}
				} else {
					Hero nextH = SkillCommandSentry.this.plugin.getCharacterManager().getHero(nextP);
					p.sendMessage(ChatColor.GREEN + "Friendly player " + nextP.getName() +ChatColor.GRAY + "- Class: " + nextH.getHeroClass().getName() + " - Level: " + nextH.getLevel() + " - Health: " + nextP.getHealth() + "/" + nextP.getMaxHealth() + " - Range:" + nextP.getLocation().distance(loc) + " blocks.");
				}
			}
			if(monsters > 0) {
				p.sendMessage(ChatColor.GRAY + "There are " + monsters + " hostile monsters within the scan radius.");
				p.sendMessage(ChatColor.RED + "There are " + bosses + " hostile boss mobs within the scan radius.");
			}
			a.remove();
			return;
		}
		@Override
		//Prevent sentried turrets expiry
		public boolean onDestroy(Turret turret) {
			return false;
		}
		
	}
	@Override
	public String getDescription(Hero h) {
		return getDescription();
	}

}
