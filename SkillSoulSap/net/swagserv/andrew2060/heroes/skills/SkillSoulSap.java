package net.swagserv.andrew2060.heroes.skills;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillSoulSap extends PassiveSkill {

	public SkillSoulSap(Heroes plugin) {
		super(plugin, "SoulSap");
		setDescription("Passive: any player kills will automatically charge soul gems (emeralds) based on level if available in inventory");
		Bukkit.getServer().getPluginManager().registerEvents(new SoulSapListener(this), this.plugin);
	}

	@Override
	public String getDescription(Hero hero) {
		return getDescription();
	}
	public class SoulSapListener implements Listener {
		private Skill skill;

		public SoulSapListener(Skill skill) {
			this.skill = skill;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPlayerDeath(PlayerDeathEvent event) {
			Player p = event.getEntity();
			Player k = p.getKiller();
			if(k==null) {
				return;
			}
			Hero h = skill.plugin.getCharacterManager().getHero(k);
			if(!h.hasEffect("SoulSap")) {
				return;
			}
			k.sendMessage("Attempting Creation of New SoulGem");
			PlayerInventory pI = k.getInventory();
			if(!pI.contains(Material.EMERALD)) {
				return;
			}
			Iterator<Integer> emeralds = pI.all(Material.EMERALD).keySet().iterator();
			while(emeralds.hasNext()) {
				int next = emeralds.next();
				ItemStack emerald = pI.getItem(next);
				if(emerald.getAmount() > 1) {
					if(!createSoulGem(pI,p,skill.plugin)) {
						k.sendMessage("Not enough space in your inventory for a new soul gem!");
						return;
					}
					emerald.setAmount(emerald.getAmount()-1);
					break;
				}
				if(emerald.getItemMeta().getDisplayName().toUpperCase().contains("GEM")) {
					continue;
				}
				if(!createSoulGem(pI,p,skill.plugin)) {
					k.sendMessage("Not enough space in your inventory for a new soul gem!");
					return;
				}
				break;
			}
			k.sendMessage("If nothing was successfully sent to you earlier, then soul gem creation failed");
		}

		private boolean createSoulGem(PlayerInventory pI, Player deadPlayer, Heroes heroes) {
			int empty = pI.firstEmpty();
			if(empty == -1) {
				return false;
			}
			ItemStack emerald = new ItemStack(Material.EMERALD,1);
			Hero h = heroes.getCharacterManager().getHero(deadPlayer);
			int level = h.getLevel();
			String rank = "";
			if(level >= 0 && level <= 20) {
				rank = "§8Newbie";
			}
			if(level > 20 && level <= 30) {
				rank = "§9Apprentice";
			}
			if(level > 30 && level <= 40) {
				rank = "§3Seasoned";
			}
			if(level > 40 && level <= 50) {
				rank = "§2Veteran";
			}
			if(level > 50 && level < 65) {
				rank = "§6Elite";
			}
			if(level >= 65 && level <75) {
				rank = "§5Legendary";
			}
			if(level == 75) {
				rank = "§4Master";
			}
			if(!h.getHeroClass().hasNoParents()) {
				rank = "§4Master";
			}
			emerald.getItemMeta().setDisplayName(rank + " Soul Gem");
			pI.setItem(empty, emerald);
			((Player)((LivingEntity)pI.getHolder())).sendMessage(rank + " Soul Gem" + ChatColor.GRAY + " Successfully Created!");
			return true;
		}
	}

}
