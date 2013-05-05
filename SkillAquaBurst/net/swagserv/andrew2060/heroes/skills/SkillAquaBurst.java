package net.swagserv.andrew2060.heroes.skills;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffectType;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.HeroRegainHealthEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.common.SlowEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillSetting;

public class SkillAquaBurst extends ActiveSkill{

	private Map<Snowball, Long> waterballs = new LinkedHashMap<Snowball, Long>(100) {

		private static final long serialVersionUID = 1L;

		protected boolean removeEldestEntry(Map.Entry<Snowball, Long> eldest) {
            return (eldest.getValue() + 5000 <= System.currentTimeMillis());
        }
    };
	public SkillAquaBurst(Heroes plugin) {
		super(plugin, "AquaBurst");
		setUsage("/skill aquaburst");
		setArgumentRange(0, 0);
		setIdentifiers(new String[] { "skill aquaburst" });
		setDescription("On use, fires a ball of water at a target. If the target hit is an enemy, $1 damage is dealt and a $2 slow/attack speed debuff is applied for 3 seconds. If the target hit is an ally, $3 of the target's maximum health is returned and their movement/attack speed is increased by $4% for 2 seconds.");	}

	@Override
	public SkillResult use(Hero hero, String[] args) {
		Snowball waterball = hero.getPlayer().launchProjectile(Snowball.class);
		waterball.setShooter(hero.getEntity());
		waterballs.put(waterball, System.currentTimeMillis());
		return SkillResult.NORMAL;
	}

	@Override
	public String getDescription(Hero hero) {
		int damage = (int) (SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE, 30, false) + SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE, 0.5, false)*hero.getLevel());
		return getDescription()
				.replace("$1", damage+"")
				.replace("$2", SkillConfigManager.getUseSetting(hero, this, "SlowAmountBase", 1, false) +SkillConfigManager.getUseSetting(hero, this, "SlowAmountperLevel", 0.025, false)*hero.getLevel() + "")
				.replace("$3", SkillConfigManager.getUseSetting(hero, this, "HealAmountBase", 10, false) + SkillConfigManager.getUseSetting(hero, this, "HealAmountperLevel", 0.5, false)*hero.getLevel() +"")
				.replace("$4", SkillConfigManager.getUseSetting(hero, this, "SpeedAmountBase", 1, false) + SkillConfigManager.getUseSetting(hero, this, "SpeedAmountperLevel", 0.05, false)*hero.getLevel() + "");
	}
    public class SkillPvPListener implements Listener {

        private final Skill skill;

        public SkillPvPListener(Skill skill) {
            this.skill = skill;
        }

        @EventHandler()
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || !(event instanceof EntityDamageByEntityEvent) || !(event.getEntity() instanceof LivingEntity)) {
                return;
            }

            EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
            Entity waterball = subEvent.getDamager();
            if (!(waterball instanceof Snowball) || !waterballs.containsKey(waterball)) {
                return;
            }
            Player dmger = (Player) ((Snowball)waterball).getShooter();
            waterballs.remove(waterball);
            LivingEntity entity = (LivingEntity) subEvent.getEntity();
            Hero hero = plugin.getCharacterManager().getHero(dmger);
            event.setCancelled(true);
        	CharacterTemplate ct = this.skill.plugin.getCharacterManager().getCharacter(entity);
            if(!Skill.damageCheck(dmger, entity)) {
            	dmger.sendMessage("Debug: Friendly");
            	int heal = (int) (SkillConfigManager.getUseSetting(hero, this.skill, "HealAmountBase", 10, false) + SkillConfigManager.getUseSetting(hero, this.skill, "HealAmountperLevel", 0.5, false)*hero.getLevel());
            	int speed = (int) (SkillConfigManager.getUseSetting(hero, this.skill, "SpeedAmountBase", 1, false) + SkillConfigManager.getUseSetting(hero, this.skill, "SpeedAmountperLevel", 0.05, false)*hero.getLevel());
            	entity.addPotionEffect(PotionEffectType.SPEED.createEffect(400, speed));
            	entity.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(400, speed));
            	if(entity instanceof Player) {
            		HeroRegainHealthEvent healEvent = new HeroRegainHealthEvent(hero, heal, skill, hero);
            		Bukkit.getServer().getPluginManager().callEvent(healEvent);
            		heal = healEvent.getAmount();
            	}
            	if(entity.getMaxHealth() > entity.getHealth()+heal) {
            		entity.setHealth(entity.getMaxHealth());
            	} else {
            		entity.setHealth(entity.getHealth()+heal);
            	}
            	return;
            }
            dmger.sendMessage("Debug: Hostile");
            addSpellTarget(entity, hero);
            int damage = SkillConfigManager.getUseSetting(hero, skill, SkillSetting.DAMAGE, 30, false);
            damage += (int) (SkillConfigManager.getUseSetting(hero, skill, SkillSetting.DAMAGE_INCREASE, 0.5, false) * hero.getSkillLevel(skill));
            damageEntity(entity, hero.getPlayer(), damage, EntityDamageEvent.DamageCause.MAGIC);
            int slow = (int) (SkillConfigManager.getUseSetting(hero, skill, "SlowAmountBase", 1, false) +SkillConfigManager.getUseSetting(hero, skill, "SlowAmountperLevel", 0.025, false)*hero.getLevel());
            SlowEffect slowEffect = new SlowEffect(skill, "AquaBurstSlow", 600, slow, true, "", "", hero);
            ct.addEffect(slowEffect);
            
        }
        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onProjectileHit(ProjectileHitEvent event) {
        	if(!(event.getEntity() instanceof Snowball)) {
        		return;
        	}
        	Snowball waterball = (Snowball)event.getEntity();
        	if(!waterballs.containsKey(waterball)) {
        		return;
        	}
        	waterballs.remove(waterball);
        }
    }
}
