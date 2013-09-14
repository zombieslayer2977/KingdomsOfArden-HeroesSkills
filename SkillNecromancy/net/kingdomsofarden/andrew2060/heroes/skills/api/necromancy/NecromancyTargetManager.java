package net.kingdomsofarden.andrew2060.heroes.skills.api.necromancy;

import org.bukkit.entity.LivingEntity;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.skill.Skill;

public class NecromancyTargetManager extends Effect {

    private LivingEntity target;
    private NecromancyMobManager mobMan;
    public NecromancyTargetManager(Skill skill, NecromancyMobManager mobMan) {
        super(skill, "NecromancyTargetManager");
        this.setPersistent(true);
        this.target = null;
        this.mobMan = mobMan;
    }
    
    public boolean setTarget(LivingEntity lE) {
        this.target = lE;
        this.mobMan.updateTargets(lE);
        return true;
        
    }

    public LivingEntity getTarget() {
        return this.target;
    }
    
}
