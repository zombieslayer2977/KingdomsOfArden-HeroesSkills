package net.kingdomsofarden.andrew2060.heroes.skills;

import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillArcaneBarrage extends ActiveSkill{

    public SkillArcaneBarrage(Heroes plugin) {
        super(plugin, "ArcaneBarrage");
        setIdentifiers("skill arcanebarrage");
        setUsage("/skill arcanebarrage");
    }
    private boolean fire(Location origin, Location vector, Hero hero) {
        if(!origin.getWorld().equals(vector.getWorld())) {
            return false;
        }
        World hitWorld = origin.getWorld();
        Vector multiplier = vector.toVector().subtract(origin.toVector()).normalize();
        
        for(int i = 0; i < 16; i++) {
            origin.add(multiplier);
            new StrikeTask(origin, hero).runTaskLater(plugin, i*5);
        }
        return true;
    }
    private class StrikeTask extends BukkitRunnable {
        private Location hitLoc;
        private Hero hero;

        public StrikeTask(Location loc, Hero h) {
            this.hitLoc = loc;
            this.hero = h;
        }

        @Override
        public void run() {
            hitLoc.getWorld().strikeLightningEffect(hitLoc);
            applyDamage(hitLoc, hero);
        }
    }
    private void applyDamage(Location origin, Hero hero) {
        Arrow a = origin.getWorld().spawn(origin, Arrow.class);
        for(Entity e : a.getNearbyEntities(3, 3, 3)) {
            if(!(e instanceof LivingEntity)) {
                continue;
            }
            LivingEntity lE = (LivingEntity)e;
            if(!Skill.damageCheck(hero.getPlayer(), lE)) {
                continue;
            } else {
                Skill.damageEntity(lE, hero.getPlayer(), 160.00, DamageCause.MAGIC);
                ToolHandlerPlugin.instance.getPotionEffectHandler().addPotionEffectStacking(PotionEffectType.SLOW.createEffect(100, 2), lE);
            }
            
            
        }
    }
    @Override
    public SkillResult use(Hero hero, String[] args) {
        return SkillResult.NORMAL;
    }
    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
}
