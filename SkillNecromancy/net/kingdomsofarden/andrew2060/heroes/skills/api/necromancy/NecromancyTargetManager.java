package net.kingdomsofarden.andrew2060.heroes.skills.api.necromancy;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.skill.Skill;

public class NecromancyTargetManager extends Effect {

    private LivingEntity target;
    private NecromancyMobManager mobMan;
    private Hero hero;
    public NecromancyTargetManager(Skill skill, NecromancyMobManager mobMan, Hero hero) {
        super(skill, "NecromancyTargetManager");
        this.setPersistent(true);
        this.target = null;
        this.mobMan = mobMan;
        this.hero = hero;
    }
    
    public boolean setTarget(LivingEntity lE) {
        this.target = lE;
        this.mobMan.updateTargets(lE);
        return true;
        
    }

    public LivingEntity getTarget(Creature creature) {
        if(this.target.getLocation().distanceSquared(creature.getLocation()) > 1024) {
            this.target = null;
            return null;
        }
        if(this.target.getLocation().distanceSquared(hero.getPlayer().getLocation()) > 1024) {
            this.target = null;
            return null;
        }
        return this.target;
    }
    
}
