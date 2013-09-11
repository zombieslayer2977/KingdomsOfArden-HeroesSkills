package net.kingdomsofarden.andrew2060.heroes.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.HeroEnterCombatEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
public class SkillCamouflage extends PassiveSkill {
	//An HashMap is essentially a list linking two variables, in this case a Player along with a Boolean value (whether they are currently vanished), which will be initialized in SkillCamouflage's Constructor.
	public HashMap<Player,Boolean> camouflaged;
	/*
	 * A hashmap of players that can TEMPORARILY see each other due to being within the 3 block range
	 * HashMap<Player movingPlayer, List<Player> camouflagedPlayers>
	 * We then schedule a timed task to check this every 5 seconds and rehide if necessary
	 */
	public HashMap<Player, Set<Player>> tempSee;
	private final SkillCamouflage skill;
	//SkillCamouflage's Constructor: Note the lack of a return type! a constructor is called when a new instance of an object is created using the new keyword
	public SkillCamouflage(Heroes plugin) {
		super(plugin, "Camouflage");
		setDescription("Passive: When within $1 blocks of a vine or leaf block, becomes invisible to everyone greater than 4 blocks away. $2");
		//Note the new className(this): this means I am calling upon the CONSTRUCTOR of an object (a special type of function with no return type)
		Bukkit.getPluginManager().registerEvents(new CamouflageListener(this), this.plugin);
		this.camouflaged = new HashMap<Player,Boolean>();
		this.tempSee = new HashMap<Player,Set<Player>>();
		//Schedule a repeating task every 3 seconds to check if a player is camouflaged again in the tempSee list
		this.skill = this;
		Bukkit.getScheduler().runTaskTimer(this.plugin, new Runnable() {

			@Override
			public void run() {
				Iterator<Player> entrySet = skill.tempSee.keySet().iterator();
				while(entrySet.hasNext()) {
					Player next = entrySet.next();
					Set<Player> toShow = skill.tempSee.get(next);
					Iterator<Player> showIt = toShow.iterator();
					while(showIt.hasNext()) {
						Player check = showIt.next();
						Location loc = next.getLocation();
						Location loc2 = check.getLocation();
						if(loc.getWorld() != loc2.getWorld() || next.getLocation().distanceSquared(check.getLocation()) > 16) {
							toShow.remove(check);
							Boolean stillCamoed = skill.camouflaged.get(check);
							if(stillCamoed != null && stillCamoed) {
								next.hidePlayer(check);
							}
						}
					}
					skill.tempSee.put(next, toShow);
				}
			}
			
		}, 0, 60L);
	}
	@Override
	public void apply(Hero hero) {
	    final CamouflageEffect effect = new CamouflageEffect(this.plugin, this, getName(), new EffectType[] {});
        hero.addEffect(effect);
	}
	
	@Override
	public void unapply(Hero hero) {
	    if(hero.hasEffect(getName())) {
	        hero.removeEffect(hero.getEffect(getName()));
	    } else {
	        System.out.println("Someone lost camouflage without having camouflage passive! Showing just in case");
	        for(Player p : plugin.getServer().getOnlinePlayers()) {
                p.showPlayer(hero.getPlayer());
            }
	    }
	}
	
	private class CamouflageEffect extends Effect {

        private SkillCamouflage camoskill;
        public CamouflageEffect(Heroes plugin, SkillCamouflage skill, String name, EffectType[] types) {
            super(plugin, skill, name, types);
            this.camoskill = skill;
            this.setPersistent(true);
        }
        
        @Override
        public void applyToHero(Hero h) {
            camoskill.camouflaged.put(h.getPlayer(), false);
            super.applyToHero(h);
        }
        @Override
        public void removeFromHero(Hero h) {
            if(camoskill.camouflaged.containsKey(h.getPlayer())) {
                camoskill.camouflaged.remove(h.getPlayer());
            }
            for(Player p : plugin.getServer().getOnlinePlayers()) {
                p.showPlayer(h.getPlayer());
            }
            super.removeFromHero(h);
        }
	    
	}
	@Override
	public String getDescription(Hero h) {
		int radius = SkillConfigManager.getUseSetting(h, this, "radius", 2, false);
		int bonusdmg = SkillConfigManager.getUseSetting(h, this, "bonuspercent", 50, false);
		boolean enhanced = h.getHeroClass().hasNoParents();
		String enhancedtext;
		if(!enhanced) {
			enhancedtext = "Enhanced: Attacking while camouflaged or up to 2 seconds " +
					"after leaving camouflage will cause that attack to" +
					" deal a bonus $2% damage and uncamouflage the attacker" +
					"for the duration of combat.";
			enhancedtext = enhancedtext.replace("$2",bonusdmg + "");
		} else {
			enhancedtext = "";
		}
		return getDescription()
				.replace("$1", radius + "")
				.replace("$2", enhancedtext + "");
	}
	// So now let us create an "Effect" that we can apply to the player: we apply this while they are camouflaged, remove after the first attack.
	private class CamouflageAttackBonusEffect extends Effect {
		public CamouflageAttackBonusEffect(Skill skill) {
			super(skill, "CamouflageDmg");
			types.add(EffectType.BENEFICIAL);
		}		
	}
	//Now lets tell heroes to populate skills.yml with our default values if they do not exist:
	@Override
	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection node = super.getDefaultConfig();
		node.set("radius", Integer.valueOf(2));
		node.set("bonuspercent", Integer.valueOf(50));
		node.set("enhanced", Integer.valueOf(0));
		return node;
	}
	//"Listener" is an interface that was provided by the Bukkit API: We use this interface to register event listeners in a format that 
	//The bukkit server will understand
	private class CamouflageListener implements Listener {
		private Skill skill;
		public CamouflageListener(Skill skill) { 
			/*I can do this because SkillCamouflage extends passiveskill: so even though earlier we're passing "this" to the constructor
			 *Which is technically SkillCamouflage.class, SkillCamouflage EXTENDS passiveskill which in turn EXTENDS skill, ergo SkillCamouflage
			 *is an instanceof Skill -> if you know object inheritance, this is important when you're verifying that a parameter you are passed can be used in your implementation!
			 */
			this.skill = skill;
			//The this keyword refers to the current class: i.e. when I used it earlier, it referred to its containing class, SkillCamouflage, because this is inside another class, 
			//this would refer to CamouflageListener.class in this specific application. As such, it becomes difficult to refer to the original SkillCamouflage, that is why 
			//I had it passed to this class via the use of new CamouflageListener(this): the SkillCamouflage.java instance was PASSED to our listener via the constructor,
			//so now I can refer to it as "skill" while inside this class.
		}
		//Signal to Bukkit event listener Registrar (known as the EventHandler) that an event listener is beginning on the next line
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		//Implement our listener, in this case I want to listen for player logins so that I can detect if/when they have access to camouflage: if they do add them to 
		//our HashMap of camouflaged players.
		public void onPlayerLogin(PlayerLoginEvent event) {
			final Player p = event.getPlayer();
			//Run on a delay due to problems w/ getting a hero prior to login
			plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
			    @Override
			    public void run() {
			        Hero h = skill.plugin.getCharacterManager().getHero(p);
		            //Check to see if the player logging in has access to this skill.
		            if(!h.hasEffect("Camouflage")) {
		                //Ok he does not have this effect-> we want to continue/don't want to do anything
		                return;
		                //This will return; our function, and will make anything below it not run.
		            }
		            //The return; won't get called if the h.hasEffect("Camouflage") returns true
		            //As such anything under this WILL be run if the if statement returns true

		            //Add the player to our HashMap
		            camouflaged.put(p, false);
		            //We've done all we want to do->return.
			    }
			});
			
			return;
		}
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) 
		public void onHeroEnterCombat(HeroEnterCombatEvent event) {
			Player p = event.getHero().getPlayer();
			if(!camouflaged.containsKey(p)) {
				return;
			}
			if(camouflaged.get(p) == true) {
				camouflaged.put(p, false);
				for(Player online : Bukkit.getServer().getOnlinePlayers()) {
					online.showPlayer(p);
				}
			}
		}
		@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
		//Handle camouflaging (listen on highest because we want the distance check to run after
		public void onPlayerMove(PlayerMoveEvent event) {
			//PlayerMoveEvent is the LAGGIEST event to listen to, we want to avoid doing major stuff as often as possible.
			Player p = event.getPlayer();
			if(!camouflaged.containsKey(p)) {
				return;
			}
			final Hero h = skill.plugin.getCharacterManager().getHero(p);
			if(h.isInCombat()) {
				return;
			}
			boolean currentlyVanished = camouflaged.get(p);
			//Cut it down some more: if no leaves nearby cancel.
			if(!checkForValidBlocks(p.getLocation(),SkillConfigManager.getUseSetting(h, skill, "radius", 2, false))) {
				if(currentlyVanished) {
					//Means player just left camouflage
					//I'll do the damage boost later
					//For now lets just set him to non-vanished mode.
					//And set him to be non-vanished in our hashmap
					camouflaged.put(p, false);
					for(Player online : Bukkit.getServer().getOnlinePlayers()) {
						online.showPlayer(p);
					}
					//Send a message saying that he has left camo
					if(!h.isSuppressing(this.skill)) {
						p.sendMessage(ChatColor.GRAY + "You have Exited Camouflage");
					}
					//Remove the bonus damage effect 2 seconds after leaving stealth.
					Runnable task = new Runnable() {
						@Override
						public void run() {
							h.removeEffect(h.getEffect("CamouflageDmg"));
						}
						
					};
					Bukkit.getScheduler().runTaskLater(skill.plugin, task, 40L);	//1 Second = 20 Ticks->2 Seconds = 40 Ticks
					return;
				} else {
					return;
				}
			} else {
				//He is within range of leaves
				if(currentlyVanished) {
					//Already vanished,we don't need to do anything
					return;
				} else {
					//Hide and set vanished state to true in hashmap.
					if(!h.isSuppressing(this.skill)) {
						p.sendMessage(ChatColor.GRAY + "You have Entered Camouflage");
					}
					camouflaged.put(p, true);
					for(Player online : Bukkit.getServer().getOnlinePlayers()) {
						online.hidePlayer(p);
					} 
					//Add the bonus damage effect to our player.
					if(!h.getHeroClass().hasNoParents()) {
						h.addEffect(new CamouflageAttackBonusEffect(skill));
					}
					return;
				}
			}
		}
		//Move event listener: check if someone is camouflaged within 3 blocks, if so show them
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		//Once again we want to call on PlayerMoveEvent as little as possible
		public void onNonCamouflagedPlayerMove(PlayerMoveEvent event) {
			Player p = event.getPlayer();
			Iterator<Entity> nearIt = p.getNearbyEntities(4, 3, 4).iterator();
			List<Player> toCheck = new ArrayList<Player>();
			while(nearIt.hasNext()) {
				Entity next = nearIt.next();
				if(next instanceof Player) {
					toCheck.add((Player)next);
					continue;
				}
				continue;
			}
			//Check our players see if they are currently camouflaged
			Iterator<Player> checkIt = toCheck.iterator();
			while(checkIt.hasNext()) {
				Player check = checkIt.next();
				if(camouflaged.containsKey(check) && camouflaged.get(check)) {
					if(tempSee.containsKey(p)) {
						Set<Player> players = tempSee.get(p);
						if(players.contains(check)) {
							continue;
						} else {
							if(!check.hasPermission("essentials.vanish")) {
								Hero h = skill.plugin.getCharacterManager().getHero(check);
								if(h.hasEffect("ShadowAssaultEffect")) {
									continue;
								}
								players.add(check);
								p.showPlayer(check);
								tempSee.put(p, players);
							}
						}
					} else {
						tempSee.put(p, new HashSet<Player>());
						Set<Player> players = tempSee.get(p);
						if(!check.hasPermission("essentials.vanish")) {
							Hero h = skill.plugin.getCharacterManager().getHero(check);
							if(h.hasEffect("ShadowAssaultEffect")) {
								continue;
							}
							players.add(check);
							p.showPlayer(check);
							tempSee.put(p, players);
							
						}
					}
				}
			}
		}
		//Implement our bonus damage effect
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		public void onWeaponDamage(WeaponDamageEvent event) {
			//CharacterTemplate is a containing class for Hero and Monster->that is to say a CharacterTemplate is either an instanceof Hero or an instanceof Monster.
			CharacterTemplate cT = event.getDamager();
			if(!cT.hasEffect("CamouflageDmg")) {
				return;
			}
			//Check to see if the entity being damaged is actually living (an entity could also be a dropped item, an arrow, for instance)
			//Note that inheritance is not necessarily bi-directional: a LivingEntity will ALWAYS be an instanceof Entity, but the converse is not necessarily true.
			//Think of it as a tree:
			//An Entity can be a LivingEntity, a projectile, an experience orb, or a variety of other things
			//http://jd.bukkit.org/doxygen/d6/dde/interfaceorg_1_1bukkit_1_1entity_1_1Entity__inherit__graph_org.svg
			if(!(event.getEntity() instanceof LivingEntity)) {
				return;
			}
			cT.removeEffect(cT.getEffect("CamouflageDmg"));
			int bonusdmg = SkillConfigManager.getUseSetting((Hero)cT, skill, "bonuspercent", 50, false);
			addSpellTarget(event.getEntity(),(Hero)cT);
			Skill.damageEntity((LivingEntity)event.getEntity(), cT.getEntity(), bonusdmg*0.01*event.getDamage(), DamageCause.CUSTOM);
			//And we're done!
		}
		//Prevent entity targetting while camo'd
		@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true) 
		public void onEntityTarget(EntityTargetEvent event) {
		    if(!(event.getTarget() instanceof Player)) {
		        return;
		    }
		    Player p = (Player) event.getTarget();
		    if(camouflaged.containsKey(p)) {
		        if(camouflaged.get(p)) {
		            if(!event.getEntity().getNearbyEntities(4, 3, 4).contains(p)) {
		                event.setCancelled(true);
		            }
		        }
		    }
		}
	}
	//Function I wrote a while ago to check area around a person for a block, not going to go into detail about how it works but if you can't figure it out I'll be happy to help
	private boolean checkForValidBlocks(Location center, int radius) {
		for (int x = center.getBlockX() - radius; x <= center.getBlockX() + radius; x++) {
			for (int y = center.getBlockY() - radius; y <= center.getBlockY() + radius; y++) {
				for (int z = center.getBlockZ() - radius; z <= center.getBlockZ() + radius; z++) {
					Material mat = center.getWorld().getBlockAt(x, y, z).getType();
					if (mat.equals(Material.LEAVES) || mat.equals(Material.VINE)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
