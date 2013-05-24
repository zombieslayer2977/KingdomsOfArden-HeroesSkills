package net.swagserv.andrew2060.heroes.skills;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillSilverArrows extends ActiveSkill{


	public SkillSilverArrows(Heroes plugin) {
		super(plugin, "SilverArrows");
		setUsage("/skill silverarrows");
		setArgumentRange(0, 0);
		setIdentifiers(new String[] { "skill silverarrows" });
		setDescription("Toggle: While active, arrows fired will consume 1 iron ingot per shot, and deal an additional 5% max health true damage on hitting an enemy. Note: Boss monsters are immune to silver arrows!");
		Bukkit.getServer().getPluginManager().registerEvents(new SkillListener(), plugin);
	}

	@Override
	public SkillResult use(Hero hero, String[] args) {
		if(hero.hasEffect("SilverArrows")) {
			hero.removeEffect(hero.getEffect("SilverArrows"));
			broadcast(hero.getPlayer().getLocation(), ChatColor.GRAY + hero.getName() + "'s arrows are no longer tipped with silver!");
			return SkillResult.NORMAL;
		} else {
			hero.addEffect(new Effect(this, "SilverArrows"));
			broadcast(hero.getPlayer().getLocation(), ChatColor.GRAY + hero.getName() + "'s arrows are now tipped with silver!");
			return SkillResult.NORMAL;
		}
	}

	@Override
	public String getDescription(Hero arg0) {
		return getDescription();
	}
	
	public class SkillListener implements Listener {
		@SuppressWarnings("deprecation")
		@EventHandler(priority=EventPriority.MONITOR)
		public void handleInventoryDeduction(ProjectileLaunchEvent event) {
			if(event.isCancelled()) {
				return;
			}
			if(!event.getEntityType().equals(EntityType.ARROW)) {
				return;
			}
			if(!(event.getEntity().getShooter() instanceof Player)) {
				return;
			}
			Hero h = SkillSilverArrows.this.plugin.getCharacterManager().getHero((Player)event.getEntity().getShooter());
			if(!h.hasEffect("SilverArrows")) {
				return;	
			}
			PlayerInventory pInv = ((Player)event.getEntity().getShooter()).getInventory();
			if(!pInv.contains(Material.IRON_INGOT)) {
				h.getPlayer().sendMessage(ChatColor.GRAY + "You have run out of iron ingots to create silver arrows with!");
				SkillSilverArrows.this.broadcast(h.getPlayer().getLocation(), ChatColor.GRAY + h.getName() + "'s arrows are no longer tipped with silver!");
				h.removeEffect(h.getEffect("SilverArrows"));
				return;
			}
			ItemStack stack = pInv.getItem(pInv.first(Material.IRON_INGOT));
			if(stack.getAmount() > 1) {
				stack.setAmount(stack.getAmount() - 1);
			} else {
				pInv.remove(stack);
			}
			h.getPlayer().updateInventory();
			return;
		}
		@EventHandler(priority=EventPriority.MONITOR)
		public void onProjectileDamage(WeaponDamageEvent event) {
			if(event.isCancelled()) {
				return;
			}
			if(!event.isProjectile()) {
				return;
			}
			if(!(event.getDamager() instanceof Hero)) {
				return;
			}
			Hero h = (Hero)event.getDamager();
			if(!h.hasEffect("SilverArrows")) {
				return;
			}
			if(!(event.getEntity() instanceof LivingEntity)) {
				return;
			}
			LivingEntity le = (LivingEntity)event.getEntity();
			if(Skill.damageCheck(h.getPlayer(), le)) {
				int extradmg = (int) (le.getMaxHealth()*0.05);
				h.getPlayer().sendMessage(ChatColor.GRAY + "Silver Arrows dealt an additional " + extradmg + " damage!");
				Skill.damageEntity(le, h.getEntity(), extradmg, DamageCause.MAGIC);
				return;
			}
			
		}
	}
	
}
