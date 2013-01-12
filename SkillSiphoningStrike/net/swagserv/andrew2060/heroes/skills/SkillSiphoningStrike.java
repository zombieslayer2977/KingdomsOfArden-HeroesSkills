package net.swagserv.andrew2060.heroes.skills;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftOcelot;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftPlayer;
import org.bukkit.entity.Ocelot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.Effect;
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
		return getDescription().replace("$1", h.getLevel()+"");
	}
	public class SiphoningEffect extends Effect {

		public SiphoningEffect(Skill skill) {
			super(skill, "SiphoningEffect");
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
			int max = h.getMaxHealth();
			int cur = h.getHealth();
			int dmg = event.getDamage();
			int level = h.getLevel();
			int healthregain = (int) (dmg*level*0.01);
			
			CraftPlayer p = (CraftPlayer)h.getPlayer();
			CraftOcelot o = (CraftOcelot)p.getWorld().spawn(h.getPlayer().getLocation(), Ocelot.class);
			p.getHandle().world.broadcastEntityEffect(o.getHandle(), (byte)7);
			o.remove();

			h.getPlayer().sendMessage(ChatColor.GRAY + "Siphoned " + ChatColor.GREEN + healthregain + ChatColor.GRAY + " health!");
			
			if(cur+healthregain > max) {
				h.setHealth(h.getMaxHealth());
				return;
			}
			h.setHealth(cur+healthregain);
			return;
		}
		
		
	}
}
