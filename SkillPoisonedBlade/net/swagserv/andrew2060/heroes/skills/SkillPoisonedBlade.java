/*
 * So for the benefit of people that are interested, this will be a fairly simple skill
 * that I'm coding from scratch: I'll annotate as best as possible so that
 * you can understand what I am doing
 */
package net.swagserv.andrew2060.heroes.skills;
//This is a package declaration: you can think of packages as declaring the folder that this class
//Is contained in. You want your package name to be unique: convention is com.yoursite.yourname.projectname
//Reason for a package is if multiple people were to use the same class name, you want them in
//Different packages so that they don't conflict.

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.CharacterTemplate;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.util.Setting;

public class SkillPoisonedBlade extends ActiveSkill {

	/*
	 * The extends keyword means that I am essentially setting my class as a subtype of
	 * ActiveSkill: what this means is that I inherit all of the methods that are defined
	 * by default in the ActiveSkill class, and in the future I can use the instanceof check: 
	 * this skill will always be an instanceof ActiveSkill.
	 */
	
	public SkillPoisonedBlade(Heroes plugin) {
		/*
		 * This is a constructor: a constructor is a special type of function that is called
		 * whenever a new instance of a class (referred to as an object) is created:
		 * note that it has no return type and that the function name matches the name of our class.
		 */
		
		//-The following portion between these two comments is only important int his specific application
		//Begin Heroes Specific Constructor Arguments
		super(plugin, "PoisonedBlade");
		setDescription("Poisons the blade, which applies a $1 second poison on next hit");
		setUsage("/skill poisonedblade");
		setIdentifiers("skill poisonedblade");
		setArgumentRange(0,0);
		//End Heroes Specific Constructor Arguments
		//Register an event listener with bukkit (see further on down for an explanation of the event system)
		Bukkit.getServer().getPluginManager().registerEvents(new PoisonedBladeListener(this), plugin);
	}
	
	/*
	 * This function overrides the default ActiveSkill Behaviour: which is nothing.
	 * Because we extend ActiveSkill which explicitly states that we have to 
	 * Code our own implementation of the use() and getDescription() function, 
	 * we do so here (otherwise our skill would do nothing)
	 */
	@Override					
	/*
	 * The Override notation lets our compiler (in this case eclipse)
	 * know that we're overriding the default behaviour in the class
	 * we're inheriting from (ActiveSkill)
	 */
	
	/*
	 * This here (if you didn't know) is a function.
	 * If you do not know this, you have no business coding skills.
	 * Go back and review basic java coding.
	 * 
	 * This function gets called when the player uses a skill,
	 * and we see that we get passed the following information:
	 * 
	 * A Hero object named "hero", 
	 * and an array of Strings named "args"
	 * 
	 * A string is essentially a group of characters.
	 * 
	 * 'A' <- This is a character
	 * "Andrew" <- This is a string
	 * 
	 * Note that strings are contained within doublequotes: otherwise your compiler
	 * will think that you're declaring a variable name!
	 * 
	 * i.e.
	 * String name = "Andrew"; will work
	 * String name = Andrew; will not work
	 * unless I had String Andrew = "Andrew";
	 * somewhere before that.
	 * 
	 * We also see that this function must return a SkillResult object, which is defined by heroes
	 * and not by us.
	 */
	public SkillResult use(Hero h, String[] args) {
		//Add the poison blade effect/attach it to the player (See the comment after the use() function)
		h.addEffect(new PoisonedBladeEffect(this));
		/*
		 * The "new" keyword creates a new instance of this object, the "this" keyword refers to the
		 * containing class, in this case SkillPoisonedBlade.
		 * 
		 * Because SkillPoisonedBlade extends ActiveSkill which in turn extends Skill
		 * we can pass it to our constructor, even though the constructor states that it accepts
		 * "Skill skill" as parameters.
		 */
		return SkillResult.NORMAL;
	}
	/*
	 * This is an effect: the effect doesn't actually do anything, but we can attach it to 
	 * a player and then check for its existence later using Hero.hasEffect("PoisonBladeEffect");
	 */
	private class PoisonedBladeEffect extends Effect {
		public PoisonedBladeEffect(Skill skill) {
			super(skill, "PoisonBladeEffect");
			types.add(EffectType.POISON);
		}
		
	}
	@Override
	public String getDescription(Hero h) {
		int duration = SkillConfigManager.getUseSetting(h, this, Setting.DURATION.node(), 2000, false);
		return getDescription()
				.replace("$1", duration + "");
		//To typecast to a string, simply add +"" to the end: for an explanation of typecasting, please read further down.
	}
	/*
	 * This isn't really java programming but I'll explain as best I can:
	 * Bukkit/all of its implementations runs on an "event" system:
	 * every time an action is performed, said event is fired.
	 * For instance, every time a block is broken, the event BlockBreakEvent is fired.
	 * bukkit then passes this event to all of the plugins that have registered a listener for that
	 * event. The event can then be manipulated by the different plugins, such as cancel them, modify
	 * the exp dropped from the block, etc etc.
	 * 
	 * Plugins can also make their own events and call them from bukkit, which allows other people's
	 * plugins to listen for their custom events.
	 * 
	 * In this case, we will use a custom event provided by heroes: "WeaponDamageEvent"
	 */
	private class PoisonedBladeListener implements Listener {

		private Skill skill;
		public PoisonedBladeListener(Skill skill) {
			this.skill = skill;
		}
		//So first off, we have to indicate to bukkit that a listener is on the next line.
		//We do this by informing the EventHandler in code.
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		/*
		 * The EventPriority tag is interesting:
		 * In essence, plugins manipulate each event one at a time, in series.
		 * The priority at which we register our listener affects when we get passed this event
		 * 
		 * LOWEST priority gets first say, HIGHEST priority gets final say.
		 * MONITOR priority gets called after HIGHEST, but you should NOT modify anything about 
		 * the events with this priority: it is for MONITORING only.
		 * 
		 * Priorities are especially important for damage calculations in damage events.
		 * 
		 * For instance, say I have a plugin 1 that adds 10 damage to every attack.
		 * and another plugin 2 that adds 50% damage to every attack.
		 * 
		 * Now say we have a base attack of 100.
		 * If plugin 1 is registered at lowest priority for its listener, and plugin 2 at highest
		 * (100+10)*1.5=155 Damage
		 * 
		 * Now on the other hand if plugin 2 is registered at a lower priority than plugin 1,
		 * plugin 2 gets first say:
		 * 100*1.5+10 = 160 Damage.
		 * 
		 * There is also a question of efficiency:
		 * If for instance, you are a block protection plugin, you should listen on as low of a 
		 * priority as possible: why? You get first say, i.e. you can setCancelled(true) the event
		 * ASAP: what this means is that other listeners who usually have ignoreCancelled set to true
		 * in their EventHandler annotation WONT RUN: therefore you save a lot of cpu cycles as 
		 * opposed to forcing other plugins to run all their checks, just to cancel in the end.

		 */
		public void onWeaponDamage(WeaponDamageEvent event) {
			//Note that I can name the function name whatever I want, as long as it takes
			//The Event I want as a parameter.
			//So what do I want to do when I get passed this event? Well first I want to get the damager:
			CharacterTemplate cT = event.getDamager();
			//Pop Quiz: if I am using the above statement, then what type of object does the
			//getDamager() function return?
			
			//So now I get passed a CharacterTemplate. 
			//If you look at the javadoc for heroes
			//http://ci.herocraftonline.com/job/Heroes/javadoc/com/herocraftonline/heroes/characters/CharacterTemplate.html
			//You will see that it has two subclasses (ergo two classes that extend it): Hero, and Monster.
			//Since only Heros can use skills, we want to check and see if our charactertemplate is a hero.
			if(!(cT instanceof Hero)) {
				return;
			}
			/*
			 * Now what I am doing next is referred to as "type-casting":
			 * if an object is a subclass/a superclass and you have confirmed that
			 * object 1 is an instanceof whatever you want to cast to, then you can
			 * convert its data type as follows:
			 * 
			 * Object1 var1;
			 * 
			 * if(var1 instanceof Object2) {
			 * 	Object2 var2 = (Object2)var1;
			 * }
			 */
			Hero h = (Hero)cT;
			//Check to see if the hero has an effect, if not we are done here, exit.
			if(!h.hasEffect("PoisonedBladeEffect")) {
				return;
			}
			//Check and make sure that the thing being damaged is actually a LivingEntity:
			//Entities have several subclasses, including LivingEntities, Projectiles (i.e. Arrows,Snowballs)
			//And dropped items
			if(!(event.getEntity() instanceof LivingEntity)) {
				return;
			}
			//So now we typecast event.getEntity() to a livingentity so that we can use the functions
			//That are defined for it: namely bukkit allows you to add potion effects to a living entity
			LivingEntity lE = (LivingEntity)event.getEntity();
			//Get the configuration setting for the duration and convert from milliseconds to ticks (because ticks is what bukkit uses)
			long duration = (long) (SkillConfigManager.getUseSetting(h, skill, Setting.DURATION.node(), 2000, false)*0.001*20);
			
			//Add the potion effect
			lE.addPotionEffect(PotionEffectType.POISON.createEffect((int) duration, 3), true);
			
			//Remove the effect from the hero (otherwise the effect would never be removed even after the first hit
			h.removeEffect(h.getEffect("PoisonedBladeEffect"));
			
			//We're done. Return
			return;
		}
		
	}
	/*
	 * So now we have to tell the skill to populate default settings
	 */
	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection node = super.getDefaultConfig();
		node.set(Setting.DURATION.node(), Integer.valueOf(2000));
		return node;
	}

}
