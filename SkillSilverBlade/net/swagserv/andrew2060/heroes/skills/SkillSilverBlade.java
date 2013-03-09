package net.swagserv.andrew2060.heroes.skills;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;

public class SkillSilverBlade extends PassiveSkill {

	public class SilverBladeListener implements Listener {
		private Skill skill;

		public SilverBladeListener(Skill skill) {
			this.skill = skill;
		}

		@EventHandler(ignoreCancelled = true,priority = EventPriority.MONITOR) 
		public void onWeaponDamage(WeaponDamageEvent event) {
			if(event.getDamager().hasEffect("SilverBlade")) {
				if(event.getEntity() instanceof LivingEntity) {
					Hero h = (Hero)event.getDamager();
					double percent = SkillConfigManager.getUseSetting(h, skill, "amountMax" , 1.00, false);
					percent += SkillConfigManager.getUseSetting(h,skill,"amountMaxPerLevel",0.05,false)*h.getLevel();
					LivingEntity target = (LivingEntity)event.getEntity();
					Skill.damageEntity(target, h.getEntity(), (int) (target.getMaxHealth() * percent * 0.01), DamageCause.MAGIC);
				}
			}
		}
	}

	public SkillSilverBlade(Heroes plugin) {
		super(plugin, "SilverBlade");
		setDescription("Passive: basic attacks deal bonus $1% true damage every hit");
		setIdentifiers("skill silverblade");
		setUsage("/skill silverblade");
		setArgumentRange(0,0);
		Bukkit.getPluginManager().registerEvents(new SilverBladeListener(this),this.plugin);
	}
	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection node = super.getDefaultConfig();
		node.set("amountMax", Double.valueOf(1.00));
		node.set("amountMaxPerLevel", Double.valueOf(0.05));
		return node;
	}
	@Override
	public String getDescription(Hero h) {
		return getDescription()
				.replace("$1",SkillConfigManager.getUseSetting(h, this, "amountMax" , 1.00, false) + SkillConfigManager.getUseSetting(h,this,"amountMaxPerLevel",0.05,false)*h.getLevel()+ "");
	}

}
