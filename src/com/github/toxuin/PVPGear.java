package com.github.toxuin;

import java.io.File;

import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.entity.CraftMonster;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class PVPGear extends JavaPlugin implements Listener {
	
	public static String prefix = null;
	public static File directory;
	public static boolean debug = false;
	
	private static FileConfiguration config = null;
	private static File configFile = null;
	private Logger log = Logger.getLogger("Minecraft");
	
	public void onEnable(){
		directory = this.getDataFolder();
		PluginDescriptionFile pdfFile = this.getDescription();
		prefix = "[" + pdfFile.getName()+ "]: ";
		
		readConfig();
		
		this.getServer().getPluginManager().registerEvents(this, this);
		
		log.info( prefix + "Enabled! Version: " + pdfFile.getVersion());
	}
	 
	public void onDisable(){ 
		log.info( prefix + "Disabled.");
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event instanceof EntityDamageByEntityEvent)) return;  // OKAY THIS IS NOT AN ATTACK

        EntityDamageByEntityEvent attackEvent = (EntityDamageByEntityEvent) event;
		
        Entity attacked = event.getEntity(); // WHO GETS THE DAMAGE
        Entity attacker = attackEvent.getDamager(); // WHO DEALS DAMAGE
        
        if (attacker instanceof Player && attacked instanceof Player) {
        	// PVP MELEE ATTACK
            Player bully = (Player) attacker;
            Player victim = (Player) attacked;
            
            if(!bully.hasPermission("pvpgear.pvp")) return;

            int oldDamage = attackEvent.getDamage();
            int newDamage = PVPGearReferee.getPvpDamage(bully, victim, attackEvent.getDamage());
            if (newDamage < 0) newDamage = 0;
            if(debug) {
            	log.info( prefix + "DEBUG: "+bully.getDisplayName()+" is hitting "+victim.getDisplayName()+" with "+bully.getItemInHand().getType().name());
    			log.info( prefix + "DEBUG: was damage: "+oldDamage+" new damage: "+newDamage);
            }
            attackEvent.setDamage(newDamage);
           
            
        } else if (attacker instanceof Projectile && attacked instanceof Player) {
            // PLAYER IS SHOOYING AT PLAYER
            Projectile bullet = (Projectile) attacker;
            LivingEntity shooter = bullet.getShooter();
            
            if (shooter instanceof Player) {
                if (bullet instanceof Snowball) return;
                if (bullet instanceof EnderPearl) return;

                Player bully = (Player) shooter;
                Player victim = (Player) attacked;

                if(!bully.hasPermission("pvpgear.pvp")) return;
                
                int oldDamage = attackEvent.getDamage();
                int newDamage = PVPGearReferee.getPvpDamage(bully, victim, attackEvent.getDamage());
                if (newDamage < 0) newDamage = 0;
                if(debug) {
                	log.info( prefix + "DEBUG: "+bully.getDisplayName()+" is shooting at "+victim.getDisplayName()+" with "+bullet.toString());
        			log.info( prefix + "DEBUG: was damage: "+oldDamage+" new damage: "+newDamage);
                }
                attackEvent.setDamage(newDamage);
            }
        } else if (attacker instanceof Player && attacked instanceof CraftMonster) {
        	// PVE MELEE ATACK
        	Player bully = (Player) attacker;
        	if(!bully.hasPermission("pvpgear.pve")) return;
            
            int oldDamage = attackEvent.getDamage();
            int newDamage = PVPGearReferee.getPveDamage(bully, attacked, attackEvent.getDamage());
            if (newDamage < 0) newDamage = 0;
            if(debug) {
            	log.info( prefix + "DEBUG: "+bully.getDisplayName()+" is hitting "+attacked.toString()+" with "+bully.getItemInHand().getType().name());
    			log.info( prefix + "DEBUG: was damage: "+oldDamage+" new damage: "+newDamage);
            }
            if(debug) log.info(prefix+"DEBUG: "+bully.getDisplayName()+" is attacking "+attacked.toString());
            
            attackEvent.setDamage(newDamage);

        } else if (attacker instanceof Projectile && attacked instanceof CraftMonster) {
        	// PVE SHOOTING
        	Projectile bullet = (Projectile) attacker;
        	LivingEntity shooter = bullet.getShooter();
        	
        	if (shooter instanceof Player) {
        		Player bully = (Player) shooter;
        		if(!bully.hasPermission("pvpgear.pve")) return;
        		
        		int oldDamage = attackEvent.getDamage();
        		int newDamage = PVPGearReferee.getPveDamage(bully, attacked, attackEvent.getDamage());
        		if (newDamage < 0) newDamage = 0;
        		if(debug) {
                	log.info( prefix + "DEBUG: "+bully.getDisplayName()+" is shooting at "+attacked.getType().getName()+" with "+bullet.toString());
        			log.info( prefix + "DEBUG: was damage: "+oldDamage+" new damage: "+newDamage);
                }
        		attackEvent.setDamage(newDamage);
        	}
        }
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("pvpgear")) {
			if (args[0].equalsIgnoreCase("reload")) {
				if (sender instanceof ConsoleCommandSender) {
					PVPGearReferee.pvpWeapons.clear();
					PVPGearReferee.pvpArmor.clear();
					PVPGearReferee.pvpEnchants.clear();
					readConfig();
				}
			}
			return true;
		}
		return false; 
	}
	
	private void readConfig () {
    	configFile = new File(directory,"config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        
        if (configFile.exists()) {
        	Set<String> weapons = config.getConfigurationSection("weapons").getKeys(false);
        	Set<String> armor = config.getConfigurationSection("armor").getKeys(false);
        	Set<String> auras = config.getConfigurationSection("enchantments").getKeys(false);
        	debug = config.getBoolean("Debug");
        	
        	for (String weapon : weapons) {
        		PVPItem item = new PVPItem();
        		item.id = Integer.parseInt(weapon);
        		
        		item.pvpEffects.damage = config.getDouble("weapons."+weapon+".pvp-effects.damage");
        		item.pvpEffects.ignite = config.getInt("weapons."+weapon+".pvp-effects.ignite");
        		
        		item.pveEffects.damage = config.getDouble("weapons."+weapon+".pve-effects.damage");
        		item.pveEffects.ignite = config.getInt("weapons."+weapon+".pve-effects.ignite");

        		item.name = config.getString("weapons."+weapon+".name");
        		PVPGearReferee.pvpWeapons.add(item);
        	}
        	
        	for (String gear : armor) {
        		PVPItem item = new PVPItem();
        		item.id = Integer.parseInt(gear);
        		item.pvpEffects.damage = config.getDouble("armor."+gear+".pvp-effects.damage");
        		item.pvpEffects.ignite = config.getInt("armor."+gear+".pvp-effects.ignite");
        		
        		item.pveEffects.damage = config.getDouble("armor."+gear+".pvp-effects.damage");
        		item.pveEffects.ignite = config.getInt("armor."+gear+".pvp-effects.ignite");
        		
        		item.name = config.getString("armor."+gear+".name");
        		PVPGearReferee.pvpArmor.add(item);
        	}
        	
        	for (String ench : auras) {
        		PVPItem enchant = new PVPItem();
        		enchant.id = Integer.parseInt(ench);
        		enchant.pvpEffects.damage = config.getDouble("enchantments."+ench+".pvp-effects.damage");
        		enchant.pvpEffects.ignite = config.getInt("enchantments."+ench+".pvp-effects.ignite");
        		
        		enchant.name = config.getString("armor."+ench+".name");
        		PVPGearReferee.pvpEnchants.add(enchant);
        	}
        	
        	if(debug) {
        		log.info(prefix+"DEBUG: loaded weapons: "+PVPGearReferee.pvpWeapons.size());
        		log.info(prefix+"DEBUG: loaded armor: "+PVPGearReferee.pvpArmor.size());
        	}
        	
        	log.info(prefix+"Config loaded!");
        } else {
        	config.set("weapons.283.name", "golden sword");
        	config.set("weapons.283.pvp-effects.damage", 1.0);
        	config.set("weapons.283.pvp-effects.ignite", 0);
        	config.set("weapons.283.pve-effects.damage", 1.0);
        	config.set("weapons.283.pve-effects.ignite", 0);
        	
        	config.set("weapons.267.name", "iron sword");
        	config.set("weapons.267.pvp-effects.damage", 1.0);
        	config.set("weapons.267.pvp-effects.ignite", 0);
        	config.set("weapons.267.pve-effects.damage", 1.0);
        	config.set("weapons.267.pve-effects.ignite", 0);
        	
        	
        	config.set("armor.314.name", "golden helmet");
        	config.set("armor.314.pvp-effects.damage", 1.0);
        	config.set("armor.314.pvp-effects.ignite", 0);
        	config.set("armor.314.pve-effects.damage", 1.0);
        	config.set("armor.314.pve-effects.ignite", 0);
        	
        	config.set("armor.315.name", "golden chestplate");
        	config.set("armor.315.pvp-effects.damage", 1.0);
        	config.set("armor.315.pvp-effects.ignite", 0);
        	config.set("armor.315.pve-effects.damage", 1.0);
        	config.set("armor.315.pve-effects.ignite", 0);
        	
        	
        	config.set("armor.316.name", "golden pants");
        	config.set("armor.316.pvp-effects.damage", 1.0);
        	config.set("armor.316.pvp-effects.ignite", 0);
        	config.set("armor.316.pve-effects.damage", 1.0);
        	config.set("armor.316.pve-effects.ignite", 0);
        	
        	config.set("armor.317.name", "golden boots");
        	config.set("armor.317.pvp-effects.damage", 1.0);
        	config.set("armor.317.pvp-effects.ignite", 0);
        	config.set("armor.317.pve-effects.damage", 1.0);
        	config.set("armor.317.pve-effects.ignite", 0);
        	
        	
        	config.set("enchantments.3.name", "protection from explosions");
        	config.set("enchantments.3.pvp-effects.damage", 1.0);
        	config.set("enchantments.3.pvp-effects.ignite", 0);
        	config.set("enchantments.3.pve-effects.damage", 1.0);
        	config.set("enchantments.3.pve-effects.ignite", 0);
        	
        	config.set("enchantments.17.name", "damage to undead");
        	config.set("enchantments.17.pvp-effects.damage", 1);
        	config.set("enchantments.17.pvp-effects.ignite", 0);
        	config.set("enchantments.17.pve-effects.damage", 1.0);
        	config.set("enchantments.17.pve-effects.ignite", 0);
        	
        	config.set("Version", this.getDescription().getVersion());
        	config.set("Debug", false);
        	
        	try {
        		config.save(configFile);
        		log.info(prefix+"CREATED DEFAULT CONFIG");
        	} catch (Exception e) {
        		log.info(prefix+"ERROR when creating config.yml");
        		e.printStackTrace();
        	}
        }
	}
}
