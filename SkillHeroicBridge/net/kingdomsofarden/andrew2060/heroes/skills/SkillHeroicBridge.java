package net.kingdomsofarden.andrew2060.heroes.skills;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;

public class SkillHeroicBridge extends ActiveSkill {

    public SkillHeroicBridge(Heroes plugin) {
        super(plugin, "HeroicBridge");
        setDescription("Teleports user to an active HeroicBeacon. Must not be in combat and in the same party as the beacon's creator.");
        setArgumentRange(0,0);
        setIdentifiers("skill heroicbridge","bridge");
        setUsage("/skill heroicbridge");
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        if(hero.isInCombat()) {
            hero.getPlayer().sendMessage(ChatColor.GRAY + "Cannot be used in combat!");
            return SkillResult.INVALID_TARGET_NO_MSG;
        }
        if(!hero.hasParty()) {
            hero.getPlayer().sendMessage(ChatColor.GRAY + "You must be in a Party to use this skill!");
            return SkillResult.INVALID_TARGET_NO_MSG;
        }
        for(Hero h : hero.getParty().getMembers()) {
            if(h.hasEffect("HeroicBeaconEffect")) {
                Random rand = new Random(2);
                Location to = h.getPlayer().getLocation().add(rand.nextInt() - 1, 0 , rand.nextInt() - 1);
                hero.getPlayer().teleport(to, TeleportCause.PLUGIN);
                to.getWorld().playSound(to, Sound.ENDERMAN_TELEPORT, 10, 1);
                to.getWorld().spigot().strikeLightningEffect(to,true);
                return SkillResult.NORMAL;
            }
        }
        hero.getPlayer().sendMessage(ChatColor.GRAY + "There are no active beacons in your party!");
        return SkillResult.NORMAL;
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }

}
