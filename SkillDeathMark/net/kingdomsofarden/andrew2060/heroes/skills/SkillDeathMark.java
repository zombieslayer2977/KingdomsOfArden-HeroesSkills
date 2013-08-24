package net.kingdomsofarden.andrew2060.heroes.skills;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.Monster;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;

public class SkillDeathMark extends TargettedSkill {

	public SkillDeathMark(Heroes plugin) {
		super(plugin, "DeathMark");
		setDescription("Marks a target for death. While marked, every incoming attack generates one charge. After $1 seconds, the mark explodes, dealing physical damage equal to %charges max health. Marked targets gain immunity to further marks for $2 seconds..");
		setUsage("/skill deathmark");
		setIdentifiers("skill deathmark");
		setArgumentRange(0,1);
		Bukkit.getPluginManager().registerEvents(new DeathMarkListener(this), this.plugin);
		
	}

	@Override
	public SkillResult use(Hero h, LivingEntity lE, String[] args) {
		CharacterTemplate cT = this.plugin.getCharacterManager().getCharacter(lE);
		if(lE == h.getEntity()) {
			return SkillResult.INVALID_TARGET;
		}
		if(cT.hasEffect("DeathMarkExpiry")) {
			h.getPlayer().sendMessage("This target has been affected by a death mark too recently");
			return SkillResult.NORMAL;
		}
		cT.addEffect(new DeathMarkEffect(this,plugin,SkillConfigManager.getUseSetting(h, this, "explodeTime", 2000, false),h));
		broadcast(h.getPlayer().getLocation(), "§7[§2Skill§7]$1 marked $2 for death", new Object[] {h.getName(),cT.getName()});
		cT.addEffect(new ExpirableEffect(this, plugin, "DeathMarkExpiry", SkillConfigManager.getUseSetting(h, this, "reapplyTime", 30000, false)));
		return SkillResult.NORMAL;
	}
	private class DeathMarkListener implements Listener {
		private Skill skill; 	
		public DeathMarkListener(Skill skill) {
			this.skill = skill;
		}
		@EventHandler(ignoreCancelled = true)
		public void onWeaponDamage(WeaponDamageEvent event) {
			if(!(event.getEntity() instanceof LivingEntity)) {
				return;
			}
			CharacterTemplate target = skill.plugin.getCharacterManager().getCharacter((LivingEntity)event.getEntity());
			if(target.hasEffect("DeathMarkEffect")) {
				((DeathMarkEffect)target.getEffect("DeathMarkEffect")).incrementStack();
			}
			return;
		}
		@EventHandler(ignoreCancelled = true)
		public void onSkillDamage(SkillDamageEvent event) {
			if(!(event.getEntity() instanceof LivingEntity)) {
				return;
			}
			CharacterTemplate target = skill.plugin.getCharacterManager().getCharacter((LivingEntity)event.getEntity());
			if(target.hasEffect("DeathMarkEffect")) {
				((DeathMarkEffect)target.getEffect("DeathMarkEffect")).incrementStack();
			}
			return;
		}
	}
	private class DeathMarkEffect extends ExpirableEffect {
		int i;
		Hero attacker;
		public DeathMarkEffect(Skill skill, Heroes plugin, long duration, Hero h) {
			super(skill, plugin, "DeathMark", duration);
			this.i = 0;
			this.attacker = h;
			this.types.add(EffectType.DISPELLABLE);
			this.types.add(EffectType.DARK);
			this.types.add(EffectType.HARMFUL);
		}
		@Override
		public void removeFromHero(Hero h) {
			Player p = h.getPlayer();
			double damage = p.getMaxHealth()*i*0.01;
			addSpellTarget(h.getEntity(),attacker);
			Skill.damageEntity(h.getEntity(), attacker.getEntity(), damage, DamageCause.ENTITY_ATTACK);
			h.getPlayer().sendMessage(ChatColor.GRAY + "The death mark from " + attacker.getName() + " exploded, dealing " + ChatColor.RED + damage + ChatColor.GRAY + " damage.");
		}
		@Override
		public void removeFromMonster(Monster m) {
			LivingEntity lE = m.getEntity();
			double damage = lE.getMaxHealth()*i*0.01;
			addSpellTarget(lE,attacker);
			Skill.damageEntity(lE, attacker.getEntity(), damage, DamageCause.ENTITY_ATTACK);
		}
		public void incrementStack() {
			++i;
			return;
		}
	}
	@Override
	public String getDescription(Hero h) {
		return getDescription()
				.replace("$1",SkillConfigManager.getUseSetting(h, this, "explodeTime", 2000, false)*0.001 + "")
				.replace("$2",SkillConfigManager.getUseSetting(h, this, "reapplyTime", 30000, false)*0.001 + "");
	}
	@Override
	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection node = super.getDefaultConfig();
		node.set("explodeTime", Integer.valueOf(2000));
		node.set("reapplyTime", Integer.valueOf(30000));
		return node;
	}
}
