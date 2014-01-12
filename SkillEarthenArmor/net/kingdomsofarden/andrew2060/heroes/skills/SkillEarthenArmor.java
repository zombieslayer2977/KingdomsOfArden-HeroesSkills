package net.kingdomsofarden.andrew2060.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.SkillSetting;

import java.text.DecimalFormat;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SkillEarthenArmor extends ActiveSkill {
    private String applyText;
    private String expireText;

    public SkillEarthenArmor(Heroes plugin) {
        super(plugin, "EarthenArmor");
        setDescription("Passive: Reduces all incoming damage by $1% Active: Shatters the armor, dealing $2% of current health damage to all targets in an area. The armor takes $3 seconds to reaccumulate.");
        setUsage("/skill earthenarmor");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill earthenarmor" });
        setTypes(new SkillType[] { SkillType.COUNTER, SkillType.EARTH, SkillType.PHYSICAL });
        Bukkit.getServer().getPluginManager().registerEvents(new EAListener(), plugin);
    }
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection section = super.getDefaultConfig();
        section.set(SkillSetting.DURATION.node(), Integer.valueOf(300000));
        section.set(SkillSetting.APPLY_TEXT.node(), "%hero% shattered their armor!");
        section.set(SkillSetting.EXPIRE_TEXT.node(), "%hero% has reaccumulated their earthen armor!");
        return section;
    }

    @Override
    public SkillResult use(Hero h, String[] args) {
        if (h.hasEffect("EAEffect")) {
            h.getPlayer().sendMessage("Your Earthen Armor hasn't re-accumulated yet!");
            return SkillResult.NORMAL;
        }
        int duration = SkillConfigManager.getUseSetting(h, this, SkillSetting.DURATION, 300000, false);
        List<Entity> entitylist = h.getEntity().getNearbyEntities(10.0D, 10.0D, 10.0D);
        Player p = h.getPlayer();
        int level = h.getLevel() + h.getHeroClass().getTier() * 75;
        double dmgMult = 150/(150 + level);
        double damage = h.getPlayer().getHealth() * dmgMult;
        for (Entity entity : entitylist) {
            if (entity instanceof LivingEntity) {
                CharacterTemplate cT = this.plugin.getCharacterManager().getCharacter((LivingEntity)entity);
                Skill.damageCheck(p, cT.getEntity());
                addSpellTarget(cT.getEntity(),h);
                damageEntity(cT.getEntity(), p, damage);
            }
        }

        h.addEffect(new EAEffect(this, duration));
        return SkillResult.NORMAL;
    }

    public String getDescription(Hero hero) {
        int duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 300000, false);
        double reduct = hero.getLevel() * 0.5D;
        int level = hero.getLevel() + hero.getHeroClass().getTier() * 75;
        double dmg = 150/(150 + level);
        DecimalFormat dF = new DecimalFormat("##.##");
        return getDescription().replace("$1", reduct+"").replace("$2", dF.format(dmg)).replace("$3", duration*0.001+"");
    }
    public class EAEffect extends ExpirableEffect {
        public EAEffect(Skill skill, long duration) {
            super(skill, "EAEffect", duration);
            this.types.add(EffectType.BENEFICIAL);
        }
        public void applyToHero(Hero hero) {
            super.applyToHero(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), SkillEarthenArmor.this.applyText, new Object[] { player.getDisplayName() });
        }
        public void removeFromHero(Hero hero) {
            super.removeFromHero(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), SkillEarthenArmor.this.expireText, new Object[] { player.getDisplayName() });
        }
    }
    public class EAListener implements Listener {
        public EAListener() {}

        @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
        public void onWeaponDamage(WeaponDamageEvent event) { 
            if (!(event.getDamager() instanceof Player)) {
                return;
            }
            Player p = (Player)event.getDamager();
            Hero h = SkillEarthenArmor.this.plugin.getCharacterManager().getHero(p);
            if (!h.hasAccessToSkill("EarthenArmor")) {
                return;
            }
            if (h.hasEffect("EAEffect")) {
                return;
            }
            int level = h.getLevel() + h.getHeroClass().getTier() * 75;
            double levelMultiplier = 150/(150 + level);
            event.setDamage(event.getDamage() * levelMultiplier);
        }
    }
}