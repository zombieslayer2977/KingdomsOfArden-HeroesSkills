package net.swagserv.andrew2060.heroes.skills;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.PeriodicEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillPowerLocus extends ActiveSkill {

	public class PowerLocusListener implements Listener {

		private SkillPowerLocus skill;

		public PowerLocusListener(SkillPowerLocus skill) {
			this.skill = skill;
		}
		
		@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true) 
		public void onInteract(PlayerInteractEvent event) {
			Player p = event.getPlayer();
			Hero h = skill.plugin.getCharacterManager().getHero(p);
			if(!h.hasEffect("PowerLocusEffect")) {
				return; 
			}
			if(!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
			    return;
			}
			if(!p.getItemInHand().getType().equals(Material.BLAZE_ROD)){
				return;
			}
			PowerLocusEffect locusEffect = (PowerLocusEffect)h.getEffect("PowerLocusEffect");
			locusEffect.toggleZoom();
			event.setCancelled(true);
		}
		
	}


	public SkillPowerLocus(Heroes plugin) {
		super(plugin, "PowerLocus");
		setArgumentRange(0,0);
		setIdentifiers("skill powerlocus");
		setUsage("/skill powerlocus");
		setDescription("Roots User in Place. While power locus is active spells gain massively increased range, cooldown reduction, and cost less mana, but also deal less damage. Exiting power locus grants a 5 second speed boost");
		Bukkit.getPluginManager().registerEvents(new PowerLocusListener(this), this.plugin);
	}
	@Override
	public SkillResult use(Hero h, String[] arg1) {
		broadcastExecuteText(h);
		if(h.hasEffect("PowerLocusEffect")) {
			broadcast(h.getEntity().getLocation(),"§7[§2Skill§7] $1 has stopped drawing from a locus of power!", new Object[] {h.getPlayer().getName()});
			h.removeEffect(h.getEffect("PowerLocusEffect"));
		} else {
			broadcast(h.getEntity().getLocation(),"§7[§2Skill§7] $1 has begun drawing from a locus of power!", new Object[] {h.getPlayer().getName()});
			h.addEffect(new PowerLocusEffect(this));
		}
		return SkillResult.NORMAL;
	}

	@Override
	public String getDescription(Hero arg0) {
		return getDescription();
	}
	
	
	public class PowerLocusEffect extends PeriodicEffect {
		
		private boolean zoomed;

		public PowerLocusEffect(Skill skill) {
			super(skill, "PowerLocusEffect",1000);
			this.zoomed = false;
		}
		
		@Override
		public void tickHero(Hero h) {
			if(!zoomed) {
				h.getEntity().addPotionEffect(PotionEffectType.SLOW.createEffect(30, 1), true);
			} else {
				h.getEntity().addPotionEffect(PotionEffectType.SLOW.createEffect(30,99999), true);
			}
			h.getEntity().addPotionEffect(PotionEffectType.JUMP.createEffect(30, -5), true);
		}
		
		@Override
		public void removeFromHero(Hero h) {
			super.removeFromHero(h);
			h.getEntity().addPotionEffect(PotionEffectType.SPEED.createEffect(100, 2),true);
		}
		
		public boolean isZoomed() {
			return this.zoomed;
		}
		public void toggleZoom() {
			this.zoomed = !this.zoomed;
		}
	}
}
