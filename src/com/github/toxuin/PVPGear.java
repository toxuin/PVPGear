package com.github.toxuin;

import java.io.File;

import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
		if (!(event instanceof EntityDamageByEntityEvent)) return; // OKAY THIS IS NOT PVP

        EntityDamageByEntityEvent pvpevent = (EntityDamageByEntityEvent) event;
		
        Entity attacked = event.getEntity(); // WHO GETS THE DAMAGE
        Entity attacker = pvpevent.getDamager(); // WHO DEALS DAMAGE
        
        if (attacker instanceof Player && attacked instanceof Player) {
            Player bully = (Player) attacker;
            Player victim = (Player) attacked;

            int oldDamage = pvpevent.getDamage();
            int newDamage = PVPGearReferee.getNewDamage(bully, victim, pvpevent.getDamage());
            if (newDamage < 0) newDamage = 0;
            pvpevent.setDamage(newDamage);
            if(debug) {
            	log.info( prefix + "DEBUG: "+bully.getDisplayName()+" is hitting "+victim.getDisplayName()+" with "+bully.getItemInHand().getType().name());
    			log.info( prefix + "DEBUG: was damage: "+oldDamage+" new damage: "+newDamage);
            }
            pvpevent.setDamage(newDamage);
           
            
        } else if (attacker instanceof Projectile && attacked instanceof Player) {
            // BANG BANG SHOOT SHOOT
            Projectile bullet = (Projectile) attacker;
            LivingEntity shooter = bullet.getShooter();
            
            if (shooter instanceof Player) {
                if (bullet instanceof Snowball) return;
                if (bullet instanceof EnderPearl) return;

                Player bully = (Player) shooter;
                Player victim = (Player) attacked;

                int oldDamage = pvpevent.getDamage();
                int newDamage = PVPGearReferee.getNewDamage(bully, victim, pvpevent.getDamage());
                if (newDamage < 0) newDamage = 0;
                if(debug) {
                	log.info( prefix + "DEBUG: "+bully.getDisplayName()+" is shooting at "+victim.getDisplayName()+" with "+bullet.toString());
        			log.info( prefix + "DEBUG: was damage: "+oldDamage+" new damage: "+newDamage);
                }
                pvpevent.setDamage(newDamage);
            }
        }
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("pvpgear")) {
			if (args[0].equalsIgnoreCase("reload")) {
				if (sender instanceof ConsoleCommandSender) {
					PVPGearReferee.pvpWeapons.clear();
					PVPGearReferee.pvpArmor.clear();
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
        	debug = config.getBoolean("Debug");
        	
        	for (String weapon : weapons) {
        		PVPItem item = new PVPItem();
        		item.id = Integer.parseInt(weapon);
        		item.damage = config.getDouble("weapons."+weapon+".damage");
        		item.name = config.getString("weapons."+weapon+".name");
        		PVPGearReferee.pvpWeapons.add(item);
        	}
        	
        	for (String gear : armor) {
        		PVPItem item = new PVPItem();
        		item.id = Integer.parseInt(gear);
        		item.damage = config.getDouble("armor."+gear+".damage");
        		item.name = config.getString("armor."+gear+".name");
        		PVPGearReferee.pvpArmor.add(item);
        	}
        	
        	if(debug) {
        		log.info(prefix+"DEBUG: loaded weapons: "+PVPGearReferee.pvpWeapons.size());
        		log.info(prefix+"DEBUG: loaded armor: "+PVPGearReferee.pvpArmor.size());
        	}
        	
        	log.info(prefix+"Config loaded!");
        } else {
        	config.set("weapons.283.name", "golden sword");
        	config.set("weapons.283.damage", 4);
        	config.set("weapons.267.name", "iron sword");
        	config.set("weapons.267.damage", 1);
        	
        	config.set("armor.314.name", "golden helmet");
        	config.set("armor.314.damage", 1);
        	config.set("armor.315.name", "golden chestplate");
        	config.set("armor.315.damage", 1.0);
        	config.set("armor.316.name", "golden pants");
        	config.set("armor.316.damage", 1);
        	config.set("armor.317.name", "golden boots");
        	config.set("armor.317.damage", 1);
        	
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
