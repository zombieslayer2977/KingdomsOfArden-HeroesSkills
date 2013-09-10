package net.kingdomsofarden.andrew2060.heroes.skills;

import java.util.Iterator;
import java.util.Set;

import net.kingdomsofarden.andrew2060.heroes.skills.aura.AuraEffect;
import net.kingdomsofarden.andrew2060.heroes.skills.aura.AuraWrapper;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.HeroRegainHealthEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.party.HeroParty;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;

public class SkillAuraOfFortitude extends ActiveSkill {

    public class AuraFortitudeListener implements Listener {
        private CharacterManager charMan;

        public AuraFortitudeListener(SkillAuraOfFortitude skill) {
            this.charMan = skill.plugin.getCharacterManager();
        }

        @EventHandler(ignoreCancelled = true)
        public void onWeaponDamage(WeaponDamageEvent event) {
            if(!(event.getEntity() instanceof LivingEntity)) {
                return;
            }
            CharacterTemplate cT = charMan.getCharacter((LivingEntity) event.getEntity());
            if(cT.hasEffect("FortitudeEffect")) {
                event.setDamage(event.getDamage()*0.9D);
            }
        }
    }
    public SkillAuraOfFortitude(Heroes plugin) {
        super(plugin, "AuraOfFortitude");
        setDescription("Aura: Reduces damage taken by all party members within 10 blocks by 10%. Activation: Upon switching to this aura, the caster and the closest party member within 10 blocks is healed for 40 health.");
        setIdentifiers("skill auraoffortitude");
        setUsage("/skill auraoffortitude");
        setArgumentRange(0,0);
        Bukkit.getPluginManager().registerEvents(new AuraFortitudeListener(this), this.plugin);
    }

    @Override
    public SkillResult use(Hero hero, String[] arg1) {
        AuraEffect eff = (AuraEffect) hero.getEffect("AuraEffect");
        if(eff == null) {
            hero.addEffect(new AuraEffect(plugin, new FortitudeAuraWrapper()));
            return SkillResult.NORMAL;
        } else {
            return eff.setFWrapper(new FortitudeAuraWrapper(),hero);
        }
    }

    @Override
    public String getDescription(Hero arg0) {
        return getDescription();
    }
    private class FortitudeAuraWrapper extends AuraWrapper {

        public FortitudeAuraWrapper() {
            super("Fortitude");
        }

        @Override
        public void onApply(Hero h) {
            HeroRegainHealthEvent selfHealEvent = new HeroRegainHealthEvent(h, 40D, SkillAuraOfFortitude.this, h);
            Bukkit.getPluginManager().callEvent(selfHealEvent);
            h.heal(selfHealEvent.getAmount());
            HeroParty hP = h.getParty();
            Set<Hero> members = hP.getMembers();
            members.remove(h);
            Iterator<Hero> mIt = members.iterator();
            Hero closest = null;
            while(mIt.hasNext()) {
                Hero next = mIt.next();
                double distanceNext = next.getPlayer().getLocation().distanceSquared(h.getPlayer().getLocation());
                if(distanceNext <= 100) {
                    continue;
                }
                if(closest == null) {
                    closest = next;
                    continue;
                }
                double distanceClosest = closest.getPlayer().getLocation().distanceSquared(h.getPlayer().getLocation());
                if(distanceNext < distanceClosest) {
                    closest = next;
                    continue;
                }
            }
            if(closest == null) {
                return;
            } else {
                HeroRegainHealthEvent allyHealEvent = new HeroRegainHealthEvent(closest, 40D, SkillAuraOfFortitude.this, h);
                Bukkit.getPluginManager().callEvent(allyHealEvent);
                Player p = closest.getPlayer();
                p.setHealth(p.getHealth() + allyHealEvent.getAmount() > p.getMaxHealth() ? p.getMaxHealth() : p.getHealth() + allyHealEvent.getAmount());
            }
        }

        @Override
        public void onTick(Hero h) {
            Iterator<Hero> hPIt = h.getParty().getMembers().iterator();
            while(hPIt.hasNext()) {
                Hero next = hPIt.next();
                if(next == h) {
                    next.addEffect(new ExpirableEffect(null, "FortitudeEffect", 2000));
                    continue;
                }
                if(next.getPlayer().getLocation().distanceSquared(h.getPlayer().getLocation()) <= 100) {
                    next.addEffect(new ExpirableEffect(null, "FortitudeEffect", 2000));
                    continue;
                }
                continue;
            }
            return;
        }

        @Override
        public void onEnd(Hero h) {
            return;
        }
    }
}
