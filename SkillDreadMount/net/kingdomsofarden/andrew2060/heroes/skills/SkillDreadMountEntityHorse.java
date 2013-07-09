package net.kingdomsofarden.andrew2060.heroes.skills;

import net.minecraft.server.EntityHorse;
import net.minecraft.server.World;

public class SkillDreadMountEntityHorse extends EntityHorse {

    public SkillDreadMountEntityHorse(World world) {
        super(world);
        this.fireProof = true;
        this.fireTicks = 300000;
    }
}