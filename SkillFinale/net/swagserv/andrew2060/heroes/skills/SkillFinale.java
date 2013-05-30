package net.swagserv.andrew2060.heroes.skills;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.common.RootEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;

public class SkillFinale extends ActiveSkill  {

	public SkillFinale(Heroes plugin) {
		super(plugin, "Finale");
		setIdentifiers("skill finale");
		setUsage("/skill finale");
		setArgumentRange(0,0);
		setDescription("On use, user is rooted into place for 5 seconds. " +
				"After the 5 seconds, the user unleashes a hail of devastating magical artillery in the surrounding area");
	}

	@Override
	public SkillResult use(final Hero h, String[] arg1) {
		h.addEffect(new RootEffect(this, 5000L) {
			@Override
			public void applyToHero(Hero h) {
				super.applyToHero(h);
			    broadcast(h.getEntity().getLocation(), "§7[§2Skill§7] $1 has begun charging up for a finale!", new Object[] {h.getPlayer().getName()});
			}
			@Override
			public void removeFromHero(Hero hero) {
			    super.removeFromHero(hero);
			    Player player = hero.getPlayer();
			    broadcast(player.getLocation(), "§7[§2Skill§7] Finale Unleashed!", new Object[] {});
			}
		});
		Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
			@Override
			public void run() {
				
			}
			
		}, 100L);
		return null;
	}

	@Override
	public String getDescription(Hero arg0) {
		// TODO Auto-generated method stub
		return null;
	}


}
