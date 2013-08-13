package net.kingdomsofarden.andrew2060.heroes.skills;

import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffectType;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.SkillUseEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;

public class SkillShadowStrike extends TargettedSkill {

	public class SilenceListener implements Listener {
		@EventHandler(ignoreCancelled = true)
		public void onSkillUse(SkillUseEvent event) {
			Hero h = event.getHero();
			if(h.hasEffect("ShadowStrikeSilence")) {
				h.getPlayer().sendMessage(ChatColor.GRAY + "You are silenced and cannot use skills");
				event.setCancelled(true);
			}
		}
	}

	public SkillShadowStrike(Heroes plugin) {
		super(plugin, "ShadowStrike");
		setDescription("Blinks to a target, silencing them, dealing 5% max health damage, and applying a 3 second 30% slow.");
		setUsage("/skill shadowstrike");
		setIdentifiers("skill shadowstrike");
		setArgumentRange(0,1);
		Bukkit.getPluginManager().registerEvents(new SilenceListener(), this.plugin);
	}

	@Override
	public String getDescription(Hero arg0) {
		return getDescription();
	}

	@Override
	public SkillResult use(Hero h, LivingEntity lE, String[] args) {
		if(lE == h.getEntity()) {
			return SkillResult.INVALID_TARGET;
		}
		CharacterTemplate cT = this.plugin.getCharacterManager().getCharacter(lE);
		h.getPlayer().teleport(lE.getLocation(), TeleportCause.UNKNOWN);
		cT.addEffect(new ExpirableEffect(this, plugin, "ShadowStrikeSilence", 4000));
		ToolHandlerPlugin.instance.getPotionEffectHandler().addPotionEffectStacking(PotionEffectType.SLOW.createEffect(80, 3),lE,false);
		Skill.damageEntity(lE, h.getEntity(), lE.getMaxHealth()*5*0.01, DamageCause.ENTITY_ATTACK);
		if(!(lE instanceof Player)) {
			broadcast(h.getPlayer().getLocation(), "§7[§2Skill§7]$1 struck $2 from the shadows.", new Object[] {h.getName(),lE.getType().getName()});
		} else {
			broadcast(h.getPlayer().getLocation(), "§7[§2Skill§7]$1 struck $2 from the shadows.", new Object[] {h.getName(),((Player)lE).getName()});
		}
		return SkillResult.NORMAL;
	}

}
