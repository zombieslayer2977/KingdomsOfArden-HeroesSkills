package net.swagserv.andrew2060.heroes.skills;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.HeroRegainHealthEvent;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.effects.common.SlowEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillType;

public class SkillSurge extends ActiveSkill{

	public SkillSurge(Heroes plugin) {
		super(plugin, "Surge");
        setDescription("After activation, movement speed is increased for 10 seconds. In addition, for the next 30 seconds, all arrow attacks deal 125% damage, and imbued effects gain additional stats. A kill reduces the cooldown of this skill by 60 seconds.");
        setUsage("/skill surge");
        setArgumentRange(0, 0);
        setIdentifiers("skill surge");
        setTypes(SkillType.SILENCABLE, SkillType.DAMAGING, SkillType.HARMFUL);
        Bukkit.getPluginManager().registerEvents(new SkillListener(this), this.plugin);
	}


	@Override
	public String getDescription(Hero arg0) {
		return getDescription();
	}

	@Override
	public SkillResult use(Hero h, String[] args) {
		h.getPlayer().addPotionEffect(PotionEffectType.SPEED.createEffect(200, 3));
		h.addEffect(new SurgeEffect(this, plugin, 30000));
		return SkillResult.NORMAL;
	}
	
	public class SurgeEffect extends ExpirableEffect {
		public SurgeEffect(Skill skill, Heroes plugin,
				long duration) {
			super(skill, plugin, "Surge", duration);
			this.types.add(EffectType.DISPELLABLE);
			this.types.add(EffectType.BENEFICIAL);
		}	
	}
	public class SurgeFrostEffect extends ExpirableEffect {
		public SurgeFrostEffect(Skill skill, Heroes plugin,
				long duration) {
			super(skill, plugin, "SurgeFrostEffect", duration);
			this.types.add(EffectType.DISABLE);
		}	
	}
	public class SurgePoisonEffect extends ExpirableEffect {
		public SurgePoisonEffect(Skill skill, Heroes plugin,
				long duration) {
			super(skill, plugin, "SurgePoisonEffect", duration);
			this.types.add(EffectType.DISABLE);
		}	
	}
	public class SkillListener implements Listener {
		Skill skill;
		SkillListener(Skill skill) {
			this.skill = skill;
		}
		@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled = true)
		public void onWeaponDamage(WeaponDamageEvent event) {
			if(!event.isProjectile()) {
				return;
			}
			if(!(event.getDamager() instanceof Hero)) {
				return;
			}
			Hero h = (Hero)event.getDamager();
			if(h.hasEffect("Surge")) {
				if(!(event.getEntity() instanceof LivingEntity)) {
					return;
				}
				event.setDamage(event.getDamage()*1.25);
				CharacterTemplate ct = SkillSurge.this.plugin.getCharacterManager().getCharacter((LivingEntity)event.getEntity());
				if(h.hasEffect("FrostShotEffect")) {
					ct.addEffect(new SurgeFrostEffect(this.skill, this.skill.plugin, 2000));
				}
				if(h.hasEffect("PoisonArrowBuff")) {
					ct.addEffect(new SurgePoisonEffect(this.skill, this.skill.plugin, 5000));
				}
				if(h.hasEffect("ExplodingArrowBuff")) {
					ct.addEffect(new SlowEffect(skill, "SurgeExplodingEffect", 2000, 1, false, "", "", h));
				}
				if(h.hasEffect("SapShotEffect")) {
					Player p = h.getPlayer();
					if((p.getHealth()+event.getDamage()*0.2)>p.getMaxHealth()) {
						p.setHealth(p.getMaxHealth());
					} else {
						p.setHealth((int) (p.getHealth()+event.getDamage()*0.2));
					}
					h.getPlayer().sendMessage(ChatColor.GRAY + "[Surge]: heal amount doubled!");
				}
				if(h.hasEffect("SilverArrows")) {
					Iterator<PotionEffect> activeEffects = ct.getEntity().getActivePotionEffects().iterator();
					while(activeEffects.hasNext()) {
						PotionEffect next = activeEffects.next();
						ct.getEntity().removePotionEffect(next.getType());
					}
					if(ct.getEntity() instanceof Player) {
						((Player)ct.getEntity()).sendMessage(ChatColor.GRAY + "Purified by Silver Arrows!"); 
					}
				}
			}
			return;
		}
		
		@EventHandler(priority=EventPriority.HIGH, ignoreCancelled = true)
		public void onOutgoingDamage(WeaponDamageEvent event) {

			if(!(event.getEntity() instanceof LivingEntity)) {
				return;
			}
			CharacterTemplate ct = event.getDamager();
			if(ct.hasEffect("SurgeFrostEffect")) {
				event.setDamage(event.getDamage()*0.7);
				if(ct instanceof Hero) {
					((Hero)ct).getPlayer().sendMessage(ChatColor.GRAY + "Damage Decreased by a Surged Frostshot!");
				}
			}
			return;
		}
		@EventHandler(priority=EventPriority.HIGH, ignoreCancelled = true)
		public void onOutgoingSkillDamage(SkillDamageEvent event) {
			if(!(event.getEntity() instanceof LivingEntity)) {
				return;
			}
			CharacterTemplate ct = event.getDamager();
			if(ct.hasEffect("SurgeFrostEffect")) {
				event.setDamage(event.getDamage()*0.7);
				if(ct instanceof Hero) {
					((Hero)ct).getPlayer().sendMessage(ChatColor.GRAY + "Damage Decreased by a Surged Frostshot!");
				}
			}
			return;
		}
		@EventHandler(priority=EventPriority.HIGH, ignoreCancelled = true)
		public void onHealthRegen(HeroRegainHealthEvent event) {
			if(event.getHero().hasEffect("SurgePoisonEffect")) {
				event.setAmount(event.getAmount()*0.5);
			}
			return;
		}
		@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
		public void onPlayerDeath(PlayerDeathEvent event) {
			 Player p = event.getEntity().getKiller();
			 if(p == null) {
				 return;
			 }
			 Hero h = SkillSurge.this.plugin.getCharacterManager().getHero(p);
			 if(!h.hasAccessToSkill("Surge")) {
				 return;
			 }
			 if(h.getCooldown("Surge") == null) {
				 return;
			 }
			 long cd = h.getCooldown("Surge")-60000;
			 if(cd <= System.currentTimeMillis()) {
				 h.setCooldown("Surge", System.currentTimeMillis());
			 } else {
				 h.setCooldown("Surge", cd);
			 }
		}
	}
	
}
