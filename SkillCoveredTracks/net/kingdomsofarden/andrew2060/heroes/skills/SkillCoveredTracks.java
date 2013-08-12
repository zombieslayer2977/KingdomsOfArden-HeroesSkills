package net.kingdomsofarden.andrew2060.heroes.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;

public class SkillCoveredTracks extends PassiveSkill implements Listener {

    public SkillCoveredTracks(Heroes plugin) {
        super(plugin, "CoveredTracks");
        setDescription("Hostile Monsters won't aggress you unless you attack them first!");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
    
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if(!(event.getTarget() instanceof Player)) {
            return;
        }
        Hero h = plugin.getCharacterManager().getHero((Player) event.getTarget());
        if(h.hasEffect("CoveredTracks")) {
            if(!(event.getEntity() instanceof LivingEntity)) {
                return;
            }
            if(!h.isInCombatWith((LivingEntity)event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

}
