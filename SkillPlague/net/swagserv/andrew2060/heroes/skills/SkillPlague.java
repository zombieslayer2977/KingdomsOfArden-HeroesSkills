package net.swagserv.andrew2060.heroes.skills;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.Monster;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.PeriodicExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillPlague extends ActiveSkill {

	public SkillPlague(Heroes plugin) {
		super(plugin, "Plague");
		setUsage("/skill plague");
		setArgumentRange(0, 0);
		setIdentifiers(new String[] { "skill plague" });
		setDescription("On use, user infects all surrounding enemies with the plague, dealing 4% max health true damage every 2 seconds for 30 seconds to all surrounding enemies");
		}

	@Override
	public SkillResult use(Hero h, String[] args) {
		Iterator<Entity> near = h.getPlayer().getNearbyEntities(10, 10, 10).iterator();
		while(near.hasNext()) {
			Entity next = near.next();
			if(!(next instanceof LivingEntity)) {
				continue;
			}
			if(!Skill.damageCheck(h.getPlayer(), (LivingEntity)next)) {
				continue;
			}
			CharacterTemplate cT = this.plugin.getCharacterManager().getCharacter((LivingEntity)next);
			if(cT.hasEffect("PlagueEffect")) {
				continue;
			}
			cT.addEffect(new PlagueEffect(this, this.plugin, 2000L, 30000L, h));
		}
		this.broadcast(h.getPlayer().getLocation(), ChatColor.DARK_GRAY + h.getName() + " used Plague!");
		return SkillResult.NORMAL;
	}

	@Override
	public String getDescription(Hero arg0) {
		return getDescription();
	}
	public class PlagueEffect extends PeriodicExpirableEffect {
		Hero attacker;
		public PlagueEffect(Skill skill, Heroes plugin, long period, long duration, Hero attacker) {
			super(skill, "PlagueEffect", period, duration);
			this.types.add(EffectType.DISPELLABLE);
			this.types.add(EffectType.BENEFICIAL);
			this.attacker = attacker;
		}

		@Override
		public void tickHero(Hero h) {
			damageEntity(h.getEntity(), attacker.getEntity(), (int) (h.getMaxHealth()*0.02), DamageCause.MAGIC);
			Iterator<Entity> near = h.getPlayer().getNearbyEntities(10, 10, 10).iterator();
			while(near.hasNext()) {
				Entity e = near.next();
				if(!(e instanceof LivingEntity)) {
					continue;
				}
				LivingEntity le = (LivingEntity)e;
				if(!Skill.damageCheck(h.getPlayer(), le)) {
					continue;
				}
				CharacterTemplate charTemp = this.plugin.getCharacterManager().getCharacter(le);
				addSpellTarget(le, h);
				Skill.damageEntity(le, h.getEntity(), (int) (charTemp.getMaxHealth()*0.02), DamageCause.CUSTOM);
			}
		}

		@Override
		public void tickMonster(Monster m) {
			damageEntity(m.getEntity(), attacker.getEntity(), (int) (m.getMaxHealth()*0.02), DamageCause.MAGIC);
		}


		 	
	}

}
