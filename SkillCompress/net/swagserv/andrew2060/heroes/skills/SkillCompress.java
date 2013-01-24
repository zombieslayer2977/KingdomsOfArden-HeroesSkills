package net.swagserv.andrew2060.heroes.skills;

import java.util.Map;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.Monster;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.effects.common.SlowEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.util.Messaging;
import com.herocraftonline.heroes.util.Properties;
import com.herocraftonline.heroes.util.Setting;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class SkillCompress extends ActiveSkill {
	private String applyText;
	private String expireText;
	private String pvpapplyText;

	public SkillCompress(Heroes plugin) {
		super(plugin, "Compress");
		setDescription("Your tools compress materials as you gather them for $1 seconds (Requires Diamond Tools). PvP: Bashes target player into the ground every hit, applying a slow effect (stuns mobs)");
		setUsage("/skill compress");
		setArgumentRange(0, 0);
		setIdentifiers(new String[] { "skill compress" });
		setTypes(new SkillType[] { SkillType.FIRE, SkillType.EARTH, SkillType.BUFF, SkillType.SILENCABLE });
		Bukkit.getServer().getPluginManager().registerEvents(new SkillPlayerListener(), plugin);
		Bukkit.getServer().getPluginManager().registerEvents(new SkillPlayerListenerPvP(this), plugin);
	}

	public ConfigurationSection getDefaultConfig() {
		ConfigurationSection section = super.getDefaultConfig();
		section.set(Setting.DURATION.node(), Integer.valueOf(10000));
		section.set(Setting.APPLY_TEXT.node(), "%hero%'s tools now exert extreme pressure on anything they contact!");
		section.set(Setting.EXPIRE_TEXT.node(), "%hero%'s tools are no longer exerting pressure!");
		return section;
	}

	public void init() {
		super.init();
		this.applyText = SkillConfigManager.getRaw(this, Setting.APPLY_TEXT, "%hero%'s tools now exert extreme pressure on anything they contact!").replace("%hero%", "$1");
		this.expireText = SkillConfigManager.getRaw(this, Setting.EXPIRE_TEXT, "%hero%'s tools are no longer exerting pressure!").replace("%hero%", "$1");
		this.pvpapplyText = SkillConfigManager.getRaw(this, Setting.APPLY_TEXT.node(), "%hero% bashed %target% into the Ground!").replace("%target%", "$1").replace("%hero%", "$2");
	}

	public SkillResult use(Hero hero, String[] args) {
		broadcastExecuteText(hero);

		int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 10000, false);
		hero.addEffect(new CompressEffect(this, duration));

		return SkillResult.NORMAL;
	}

	public String getDescription(Hero hero) {
		int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 10000, false);
		return getDescription().replace("$1", duration / 1000 +"");
	}

	public class CompressEffect extends ExpirableEffect {
		public CompressEffect(Skill skill, long duration) {
			super(skill, "Compress", duration);
			this.types.add(EffectType.DISPELLABLE);
			this.types.add(EffectType.BENEFICIAL);
			this.types.add(EffectType.FIRE);
		}

		public void applyToHero(Hero hero) {
			super.applyToHero(hero);
			Player player = hero.getPlayer();
			broadcast(player.getLocation(), SkillCompress.this.applyText, new Object[] { player.getDisplayName() });
		}

		public void removeFromHero(Hero hero) {
			super.removeFromHero(hero);
			Player player = hero.getPlayer();
			broadcast(player.getLocation(), SkillCompress.this.expireText, new Object[] { player.getDisplayName() });
		}
	}

	public class SkillPlayerListener implements Listener {
		public SkillPlayerListener() {}

		@EventHandler(priority=EventPriority.HIGHEST)
		public void onBlockBreak(BlockBreakEvent event) {
			if (event.isCancelled()) {
				return;
			}

			Hero hero = SkillCompress.this.plugin.getCharacterManager().getHero(event.getPlayer());
			if (hero.hasEffect("Compress")) {
				Block block = event.getBlock();
				Material tool = event.getPlayer().getItemInHand().getType();
				Material type = block.getType();
				switch (tool) {
				case DIAMOND_SPADE:
					switch (type) {
					case GRASS:
						event.setCancelled(true);
						block.setType(Material.AIR);
						block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.COAL, 2));
						break;
					case DIRT:
						event.setCancelled(true);
						block.setType(Material.AIR);
						block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.COAL, 2));
						break;
					case SAND:
						event.setCancelled(true);
						block.setType(Material.AIR);
						block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.SANDSTONE, 1));
						break;
					case CLAY:
						event.setCancelled(true);
						block.setType(Material.AIR);
						block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.CLAY, 1));
					default:
						event.setCancelled(false);
					}

					break;
				case DIAMOND_PICKAXE:
					switch (type) {
					case STONE:
						event.setCancelled(true);
						block.setType(Material.AIR);
						block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.SMOOTH_BRICK, 1));
						break;
					case NETHERRACK:
						event.setCancelled(true);
						block.setType(Material.AIR);
						block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.NETHER_BRICK, 1));
						break;
					case GLOWSTONE:
						event.setCancelled(true);
						block.setType(Material.AIR);
						block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.GLOWSTONE_DUST, 8));
						break;
					default:
						break;
					}

					break;
				case DIAMOND_AXE:
					switch (type) {
					case LOG:
						event.setCancelled(true);
						block.setType(Material.AIR);
						block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.COAL, 2));
						break;
					default:
						break;
					}
					break;
				default:
					Player player = Bukkit.getPlayer(hero.getName());
					player.sendMessage("Your tool is not strong enough to exert the pressure needed to compress materials");
					break;
				}
				Map<Material, Double> expValues = Heroes.properties.miningExp;
				hero.addExp(expValues.get(type), hero.getHeroClass(), hero.getPlayer().getLocation());
			}
			
		}
	}

	public class SkillPlayerListenerPvP implements Listener {
		private final Skill skill;

		public SkillPlayerListenerPvP(Skill skill) { this.skill = skill; }

		@EventHandler(priority=EventPriority.MONITOR)
		public void onWeaponDamage(WeaponDamageEvent event) {
			if (event.isCancelled()) {
				return;
			}
			if (event.getCause() != DamageCause.ENTITY_ATTACK) {
				return;
			}
			if ((event.getDamager() instanceof Hero)) {
				Hero hero = (Hero) event.getDamager();
				if (hero.hasEffect("Compress")) {
					LivingEntity targetentity = (LivingEntity)event.getEntity();
					Player player = hero.getPlayer();
					SlowEffect effect = new SlowEffect(this.skill, 5000L, 3, false, SkillCompress.this.pvpapplyText, "$1 has regained their senses", hero);
					SkillCompress.this.plugin.getCharacterManager().getCharacter(targetentity).addEffect(effect);
					SkillCompress.this.broadcast(targetentity.getLocation(), SkillCompress.this.pvpapplyText, new Object[] { Messaging.getLivingEntityName(targetentity), player.getDisplayName() });
				}
				return;
			}
		}
	}
}