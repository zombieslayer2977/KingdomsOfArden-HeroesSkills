package net.kingdomsofarden.andrew2060.heroes.skills;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;
import net.kingdomsofarden.andrew2060.toolhandler.potions.PotionEffectManager;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.PeriodicEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillHeroicBeacon extends ActiveSkill implements Listener {

    PotionEffectManager pEMan;

    private class HeroicBeaconEffect extends PeriodicEffect {

        public HeroicBeaconEffect(Skill skill) {
            super(skill, "HeroicBeaconEffect",20);
            pEMan = ToolHandlerPlugin.instance.getPotionEffectHandler();
        }

        @Override
        public void applyToHero(Hero h) {
            h.getPlayer().sendMessage(ChatColor.GRAY + "Summoning Beacon Active!");
            broadcast(h.getPlayer().getLocation(), h.getPlayer().getName() + " has channeled a summoning beacon!");
        }
        
        @Override
        public void removeFromHero(Hero h) {
            h.getPlayer().sendMessage(ChatColor.GRAY + "Summoning Beacon Deactivated!");
        }
        
        @Override
        public void tickHero(Hero h) {
            pEMan.addPotionEffectStacking(PotionEffectType.SLOW.createEffect(100, 127), h.getEntity(), false);
            pEMan.addPotionEffectStacking(PotionEffectType.JUMP.createEffect(100, -127), h.getEntity(), false);
            int newMana = h.getMana() - 5;
            if(newMana < 0) {
                h.removeEffect(this);
                return;
            } else {
                h.setMana(newMana);
            }
        }

    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onWeaponDamage(WeaponDamageEvent event) {
        if(event.getDamager().hasEffect("HeroicBeaconEffect")) {
            event.setCancelled(true);
            return;
        }
        if(event.getEntity() instanceof LivingEntity) {
            LivingEntity lE = (LivingEntity) event.getEntity();
            CharacterTemplate cT = plugin.getCharacterManager().getCharacter(lE);
            if(cT.hasEffect("HeroicBeaconEffect")) {
                event.setDamage(event.getDamage() * 0.5);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onSkillDamage(SkillDamageEvent event) {
        if(event.getDamager().hasEffect("HeroicBeaconEffect")) {
            event.setCancelled(true);
            return;
        }
        if(event.getEntity() instanceof LivingEntity) {
            LivingEntity lE = (LivingEntity) event.getEntity();
            CharacterTemplate cT = plugin.getCharacterManager().getCharacter(lE);
            if(cT.hasEffect("HeroicBeaconEffect")) {
                event.setDamage(event.getDamage() * 0.5);
            }
        }
    }
    
    public SkillHeroicBeacon(Heroes plugin) {
        super(plugin, "HeroicBeacon");
        this.setDescription("Broadcasts a beacon to all party members, allowing them to teleport to the user's location regardless of factional status. While the beacon is active, the user is immobilized and gains a 50% defensive bonus but cannot attack. Consumes 5 Mana per Second");
        this.setArgumentRange(0, 0);
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public SkillResult use(Hero h, String[] arg1) {
        if(h.hasEffect("HeroicBeaconEffect")) {
            h.removeEffect(h.getEffect("HeroicBeaconEffect"));
        } else {
            h.addEffect(new HeroicBeaconEffect(this));
        }
        return SkillResult.NORMAL;
    }

    @Override
    public String getDescription(Hero arg0) {
        return getDescription();
    }

}
