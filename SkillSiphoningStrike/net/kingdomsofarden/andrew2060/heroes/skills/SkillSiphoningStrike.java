package net.kingdomsofarden.andrew2060.heroes.skills;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillSiphoningStrike extends ActiveSkill{

	public SkillSiphoningStrike(Heroes plugin) {
		super(plugin, "SiphoningStrike");
		setArgumentRange(0, 0);
		setIdentifiers(new String[] { "skill siphoningstrike" });
		setDescription("On use, the next attack will gain $1% life steal.");
		Bukkit.getServer().getPluginManager().registerEvents(new SkillListener(this), plugin);
	}

	@Override
	public SkillResult use(Hero h, String[] arg1) {
		this.broadcast(h.getPlayer().getLocation(), h.getName() + " used " + ChatColor.WHITE + "Siphoning Strike!", new Object[0]);
		h.addEffect(new SiphoningEffect(this));
		return SkillResult.NORMAL;
	}

	@Override
	public String getDescription(Hero h) {
	    DecimalFormat dF = new DecimalFormat("##.##");
		return getDescription().replace("$1", dF.format(h.getLevel()/5.0 + 25) +"");
	}
	public class SiphoningEffect extends Effect {

		public SiphoningEffect(Skill skill) {
			super(skill, "SiphoningEffect");
			this.types.add(EffectType.BENEFICIAL);
			this.types.add(EffectType.DISPELLABLE);
		}
		
	}
	public class SkillListener implements Listener {
		
		Skill skill;
		public SkillListener(Skill skill) {
			this.skill = skill;
		}
		@EventHandler(priority=EventPriority.MONITOR)
		public void onWeaponDamage(WeaponDamageEvent event) {
			if(event.isCancelled()) {
				return;
			}
			if(!(event.getDamager() instanceof Hero)) {
				return;
			}
			Hero h = (Hero) event.getDamager();
			if(!h.hasEffect("SiphoningEffect")) {
				return;
			}
			h.removeEffect(h.getEffect("SiphoningEffect"));
			Player p = h.getPlayer();
			double max = p.getMaxHealth();
			double cur = p.getHealth();
			double dmg = event.getDamage();
			int level = h.getLevel();
			double healthregain = (dmg*(level/5.0D + 25)*0.01);
			
			//TODO: Change to use HeroRegainHealthEvent

			h.getPlayer().sendMessage(ChatColor.GRAY + "Siphoned " + ChatColor.GREEN + healthregain + ChatColor.GRAY + " health!");
			
			if(cur+healthregain > max) {
				p.setHealth(p.getMaxHealth());
				return;
			}
			p.setHealth(cur+healthregain);
			return;
		}
		
		
	}
}
