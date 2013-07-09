package net.kingdomsofarden.andrew2060.heroes.skills;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;

public class SkillNetherSteel extends PassiveSkill implements Listener {

    public SkillNetherSteel(Heroes plugin) {
        super(plugin,"NetherSteel");
        setDescription("Basic attacks with scythes apply 2 seconds of wither III");
        Bukkit.getPluginManager().registerEvents(this,this.plugin);
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }

}
