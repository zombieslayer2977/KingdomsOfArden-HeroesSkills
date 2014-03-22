package net.kingdomsofarden.andrew2060.heroes.skills;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillVolley extends ActiveSkill implements Listener {

    public SkillVolley(Heroes plugin) {
        super(plugin, "Volley");
        setIdentifiers("skill volley");
        setUsage("/skill volley");
        setArgumentRange(0,0);
        setDescription("On use, next shot within 5 seconds will fire 6 arrows in an arc.");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        hero.addEffect(new VolleyEffect(this));
        return SkillResult.NORMAL;
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
    
    private class VolleyEffect extends ExpirableEffect {

        public VolleyEffect(Skill skill) {
            super(skill, "Volley" , 5000);
        }
        
        @Override
        public void applyToHero(Hero h) {
            super.applyToHero(h);
            h.getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "Skill" + ChatColor.GRAY + "] Volley "
                    + "has been activated for your next shot! You must shoot within 5 seconds or it will self-cancel!");
        }
        
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent event) {
        if(event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            Hero h = plugin.getCharacterManager().getHero(p);
            if(h.hasEffect("Volley")) {
                h.removeEffect(h.getEffect("Volley"));
                Vector v = event.getProjectile().getVelocity();
                double dX = v.getX();
                double dZ = v.getZ();
                double hypotenuse = Math.sqrt(Math.pow(dX,2) + Math.pow(dZ,2));
                double angle = Math.toDegrees(Math.atan(dZ/dX));
                for(int i = 1; i <= 3; i++) {
                    
                    Vector vPositiveAngle = v.clone();
                    Vector vNegativeAngle = v.clone();
                    double adjustedAnglePositive = angle + 10*i;
                    double adjustedAngleNegative = angle - 10*i;
                    
                    double zPositiveMultiplier = Math.sin(Math.toRadians(adjustedAnglePositive));
                    double zNegativeMultiplier = Math.sin(Math.toRadians(adjustedAngleNegative));
                    double xPositiveMultiplier = Math.cos(Math.toRadians(adjustedAnglePositive));
                    double xNegativeMultiplier = Math.cos(Math.toRadians(adjustedAngleNegative));
                    
                    vPositiveAngle.setX(hypotenuse * xPositiveMultiplier);
                    vPositiveAngle.setZ(hypotenuse * zPositiveMultiplier);
                    
                    vNegativeAngle.setX(hypotenuse * xNegativeMultiplier);
                    vNegativeAngle.setZ(hypotenuse * zNegativeMultiplier);
                    
                    Arrow positiveAngle = h.getPlayer().launchProjectile(Arrow.class);
                    positiveAngle.setVelocity(vPositiveAngle);
                    Arrow negativeAngle = h.getPlayer().launchProjectile(Arrow.class);
                    negativeAngle.setVelocity(vNegativeAngle);

                }
            }
        }
    }

}
