package net.kingdomsofarden.andrew2060.heroes.skills;

import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;
import net.kingdomsofarden.andrew2060.toolhandler.potions.PotionEffectManager;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.SkillUseEvent;
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
		
		@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
		public void onSkillUse(SkillUseEvent event) {
		    final Hero h = event.getHero();
		    if(!h.hasEffect("PowerLocusEffect")) {
		        return;
		    }
		    final String skillName = event.getSkill().getName();
		    final double multiplier = 0.5 - (event.getHero().getLevel(event.getHero().getHeroClass())*0.005);
		    event.setManaCost((int) (event.getManaCost()*0.5));
		    //Must be scheduled due to cooldown being added after this
		    Bukkit.getScheduler().runTaskLater(skill.plugin,new Runnable() {

                @Override
                public void run() {
                    h.setCooldown(skillName, (long) (((h.getCooldown(skillName)-System.currentTimeMillis())*multiplier) + System.currentTimeMillis()));
                }
		        
		    },1L);
		}
		
	}


	public SkillPowerLocus(Heroes plugin) {
		super(plugin, "PowerLocus");
		setArgumentRange(0,0);
		setIdentifiers("skill powerlocus");
		setUsage("/skill powerlocus");
		setDescription("Roots User in Place. While power locus is active spells gain massively increased range, cooldown reduction, and cost no mana, but also deal less damage. Exiting power locus grants a 5 second speed boost");
		Bukkit.getPluginManager().registerEvents(new PowerLocusListener(this), this.plugin);
	}
	@Override
	public SkillResult use(Hero h, String[] arg1) {
		if(h.hasEffect("PowerLocusEffect")) {
			broadcast(h.getEntity().getLocation(),"§7[§2Skill§7] $1 has stopped drawing from a locus of power!", new Object[] {h.getPlayer().getName()});
			h.removeEffect(h.getEffect("PowerLocusEffect"));
			h.setCooldown(this.getName(), System.currentTimeMillis() + 60000);
			return SkillResult.NORMAL;
		} else {
			broadcast(h.getEntity().getLocation(),"§7[§2Skill§7] $1 has begun drawing from a locus of power!", new Object[] {h.getPlayer().getName()});
			h.addEffect(new PowerLocusEffect(this));
			return SkillResult.SKIP_POST_USAGE;
		}
	}

	@Override
	public String getDescription(Hero arg0) {
		return getDescription();
	}
	
	
	public class PowerLocusEffect extends PeriodicEffect {
		
	    PotionEffectManager pEMan;

		public PowerLocusEffect(Skill skill) {
			super(skill, "PowerLocusEffect",1000);
	        pEMan = ToolHandlerPlugin.instance.getPotionEffectHandler();
		}
		@Override
		public void applyToHero(Hero h) {
		    super.applyToHero(h);
		    pEMan.addPotionEffectStacking(PotionEffectType.SLOW.createEffect(72000, 127), h.getEntity(), false);
            pEMan.addPotionEffectStacking(PotionEffectType.JUMP.createEffect(72000, -127), h.getEntity(), false);
		}
		@Override
		public void tickHero(Hero h) {
		}
		
		@Override
		public void removeFromHero(Hero h) {
			super.removeFromHero(h);
			pEMan.removePotionEffect(PotionEffectType.SLOW,h.getEntity());
			pEMan.removePotionEffect(PotionEffectType.JUMP,h.getEntity());
			if(!h.getEntity().isDead()) {
			    pEMan.addPotionEffectStacking(PotionEffectType.SPEED.createEffect(100, 2), h.getEntity(), false);
			}
		}
		
		
	}
}
