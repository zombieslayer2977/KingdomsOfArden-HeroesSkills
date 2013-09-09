package net.kingdomsofarden.andrew2060.heroes.skills;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.HeroKillCharacterEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;

public class SkillPvPSpecialization extends PassiveSkill {
    
    private HashMap<String,HashMap<String,KillData>> killData;
    
	public SkillPvPSpecialization(Heroes plugin) {
		super(plugin, "PvPSpecialization");
		setDescription("This Specialization is a PvP based specialization and gains EXP from PvPing");
		Bukkit.getServer().getPluginManager().registerEvents(new SkillEXPListener(), this.plugin);
		this.killData = new HashMap<String,HashMap<String,KillData>>();
	}
	
	public class SkillEXPListener implements Listener {
		@EventHandler(priority = EventPriority.LOWEST)
		public void onCharacterKill(HeroKillCharacterEvent event) {
			Hero h = event.getAttacker();
			if(!h.hasEffect("PvPSpecialization")) {
				return;
			}
			if(!(event.getDefender() instanceof Hero)) {
				return;
			}
			
			Hero h2 = (Hero)event.getDefender();
			
			int level = h2.getLevel(h2.getHeroClass());
			if(h2.getName().equals(h.getName())) {
				return;
			}
			
			boolean spec = true;
			if(h2.getHeroClass().hasNoParents()) {
				spec = false;
			}
			int exp = 0;
			String rank = "§8newbie§7";
			if(level > 0 && level <= 20) {
				exp = 100;
				rank = "§8newbie§7";
			}
			if(level > 20 && level <= 30) {
				exp = 200;
				rank = "§9apprentice§7";
			}
			if(level > 30 && level <= 40) {
				exp = 400;
				rank = "§3seasoned§7";
			}
			if(level > 40 && level <= 50) {
				exp = 800;
				rank = "§2veteran§7";
			}
			if(level > 50 && level < 65) {
				exp = 1600;
				rank = "§6elite§7";
			}
			if(level >= 65 && level <75) {
				exp = 3200;
				rank = "§5legendary§7";
			}
			if(level == 75) {
				exp = 6400;
				rank = "§4master§7";
			}
			if(spec == true) {
				exp = exp + 6400;
			}
			if(h.getPlayer().getLocation().getWorld().getName().toLowerCase().contains("arena")) {
				exp = exp-3200;
				if(h2.getHeroClass().hasNoParents()) {
		            h.getPlayer().sendMessage("§7You gain no exp for killing a " + h2.getHeroClass().getName() + " in the arena: specializations only gain EXP in arena from other specializations!");
		            return;
				}
			}
			KillData data = null;
            if(killData.containsKey(h.getName())) {
                data = killData.get(h.getName()).get(h2.getName());
                if(data != null) {
                    data.add();
                    if(!data.checkSize()) {
                        h.getPlayer().sendMessage("§7You can only get exp for killing the same person a maximum of 5 times per hour!");
                        return;
                    }
                } else {
                    KillData kData = new KillData();
                    kData.add();
                    killData.get(h.getName()).put(h2.getName(), kData);
                }
            } else {
                killData.put(h.getName(), new HashMap<String,KillData>());
                KillData kData = new KillData();
                kData.add();
                killData.get(h.getName()).put(h2.getName(), kData);
            }
			h.getPlayer().sendMessage("§7You were awarded " + exp + " exp for killing a " + rank + " " + h2.getHeroClass().getName() + "!");
			h.addExp(exp, h.getHeroClass(), h.getPlayer().getLocation());
			
		}
	}

	@Override
	public String getDescription(Hero arg0) {
		return getDescription();
	}

}
