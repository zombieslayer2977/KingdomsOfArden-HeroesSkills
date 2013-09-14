package net.kingdomsofarden.andrew2060.heroes.skills.api.necromancy;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.skill.Skill;

public class NecromancyTargetManager extends Effect {

    private LivingEntity target;
    private Player owner;
    public NecromancyTargetManager(Skill skill, Player p) {
        super(skill, "NecromancyTargetManager");
        this.setPersistent(true);
        this.owner = p;
        this.target = null;
    }
    
    public boolean setTarget(LivingEntity lE) {
        if(Skill.damageCheck(owner, lE)) {
            this.target = lE;
            return true;
        } else {
            return false;
        }
    }

    public LivingEntity getTarget() {
        return this.target;
    }
    
}
