package com.github.toxuin;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PVPGearReferee {
	
	public static Set<PVPItem> pvpWeapons = new HashSet<PVPItem>();
	public static Set<PVPItem> pvpArmor = new HashSet<PVPItem>();
	public static Set<PVPItem> pvpEnchants = new HashSet<PVPItem>();
	
	private static Logger log = Logger.getLogger("Minecraft");
	
	public static int getPvpDamage (Player bully, Player victim, int newDamage) { 
		
		// THIS IS pvP! REDUCE DAMAGE FOR ARMOR, BOOST DAMAGE FOR ITEM IN HAND
		
        // ------- ATTACKER!
        
    	newDamage = newDamage * (int) Math.round(checkWeapon(bully, true));
    	newDamage = newDamage * (int) Math.round(checkEnchantments(bully, true));
        
        // ------- DEFENDER!
    	int defense = (int) Math.round(checkArmor(victim, true));
    	if (defense != 0) newDamage = newDamage / defense;
    	
    	int magicDefense = (int) Math.round(checkEnchantments(victim, true));
    	if (magicDefense != 0) newDamage = newDamage / magicDefense;
    	
    	
    	// ------- IGNITE EACH OTHER!!!
    	
    	int victimFireTicks = checkFireTicks(victim, true);
    	int bullyFireTicks = checkFireTicks(bully, true);
    	
    	if (victimFireTicks != 0) bully.setFireTicks(victimFireTicks);
    	if (victimFireTicks != 0) victim.setFireTicks(bullyFireTicks);
    	
		return newDamage;
	}
	
	public static int getPveDamage(Player bully, Entity victim, int newDamage) {
		
		// THIS IS pvE - BOOST DAMAGE FOR ARMOR, BOOST DAMAGE FOR ITEM IN HAND
		
		// SCAN FOR ITEM IN HAND
		
		newDamage = newDamage * (int) Math.round(checkWeapon(bully, false));
		
		// SCAN FOR ENCHANTMENTS
		
		newDamage = newDamage * (int) Math.round(checkEnchantments(bully, false));
		
		// SCAN FOR PVE BOOSTER ARMOR
		
		newDamage = (int) Math.round(newDamage * checkArmor(bully, false));
		
		// IGNITE MONSTER!!!
		int fireTicks = checkFireTicks(bully, false);
		if (fireTicks != 0) victim.setFireTicks(fireTicks);
		
		return newDamage;
	}
	
	///// ------------------- IGNITING CHECKS BELOW ------------------------
	
	public static int checkFireTicks(Player player, boolean pvp) {
		int result = 0;
		
		ItemStack[] armor = player.getInventory().getArmorContents();
		
		for (PVPItem aura : pvpEnchants) {
			PVPItem.Effects effectSet = pvp?aura.pvpEffects:aura.pveEffects;
			
    		if (effectSet.ignite != 0) {

    			Map<Enchantment, Integer> enchantments = player.getItemInHand().getEnchantments();
    			for (int i = 0; i<enchantments.size(); i++) {
    				Object[] enchantsAll = enchantments.keySet().toArray();
    				Object[] enchantsLevels = enchantments.values().toArray();
    				Enchantment ench = (Enchantment) enchantsAll[i];
    				if (aura.id == ench.getId()) {
    					log.info(PVPGear.prefix+ "DEBUG: " + player.getDisplayName() + " GOT AN IGNITING ENCHANT ON HIS WEAPON " + ench.getId() + " ("+ench.getName()+") Level : " + enchantsLevels[i].toString());
    					result += effectSet.ignite * Integer.parseInt(enchantsLevels[i].toString());
    				}
    			}
    		}
    		
    		
    		for (ItemStack gear : armor) {
    			Map<Enchantment, Integer> enchantments = gear.getEnchantments();
    			for (int i = 0; i<enchantments.size(); i++) {
    				Object[] enchantsAll = enchantments.keySet().toArray();
    				Object[] enchantsLevels = enchantments.values().toArray();
    				Enchantment ench = (Enchantment) enchantsAll[i];
    				if (effectSet.ignite != 0 && aura.id == ench.getId()) {
    					if (PVPGear.debug) log.info(PVPGear.prefix+ "DEBUG: ENCHANTMENTS ON PLAYER " + player.getDisplayName() + ": " + ench.getId() + " ("+ench.getName()+") Level : " + enchantsLevels[i].toString());
	    				result += effectSet.ignite * Integer.parseInt(enchantsLevels[i].toString());
    				}
    			}
    		}
    		
    	}
		
		for (PVPItem weapon : pvpWeapons) {
			PVPItem.Effects effectSet = pvp?weapon.pvpEffects:weapon.pveEffects;
			if (Material.getMaterial(weapon.id) == player.getItemInHand().getType() && effectSet.ignite != 0) {
    			result += effectSet.ignite;
    			if (PVPGear.debug) log.info(PVPGear.prefix+"DEBUG: CHECK "+player.getDisplayName()+" FOR IGNITING WEAPON: "+effectSet.ignite);
    		}
		}
		
		for (PVPItem gear : pvpArmor) {
			PVPItem.Effects effectSet = pvp?gear.pvpEffects:gear.pveEffects;
			for (ItemStack piece : armor) {
				if (Material.getMaterial(gear.id) == piece.getType() && effectSet.ignite != 0) {
	    			result += effectSet.ignite;
	    			if (PVPGear.debug) log.info(PVPGear.prefix+"DEBUG: CHECK "+player.getDisplayName()+" FOR IGNITING ARMOR: "+effectSet.ignite);
	    		}
			}
		}
		
		return result;
	}
	
	///// --------------- INTERNAL STUFF BELOW THIS LINE -------------------
	

	private static double checkWeapon(Player player, boolean pvp) {
		double result = 1;
		
		for (PVPItem item : pvpWeapons) {
			PVPItem.Effects effectSet = pvp?item.pvpEffects:item.pveEffects;
    		
    		if (effectSet.damage != 1 || effectSet.damage != 0) {
	    		if (Material.getMaterial(item.id) == player.getItemInHand().getType()) {
	    			result = effectSet.damage;
	    			if (PVPGear.debug) log.info(PVPGear.prefix+"DEBUG: CHECK "+player.getDisplayName()+"'S WEAPON: x"+effectSet.damage);
	    		}
    		}
    	}
		
		return result;
	}
	
	private static double checkArmor(Player player, boolean pvp) {
		double armorPoints = 0;
		
		// these are % of total armor for each piece
		double helm  = 15; // SLOT 3
		double chest = 40; // SLOT 2
		double pants = 30; // SLOT 1
		double boots = 15; // SLOT 0
		
		ItemStack[] armor = player.getInventory().getArmorContents();
		
		for (PVPItem item : pvpArmor) {
			PVPItem.Effects effectSet = pvp?item.pvpEffects:item.pveEffects;
    		if (effectSet.damage != 1 || effectSet.damage != 0) {

				if (armor[0].getType() == Material.getMaterial(item.id)) {
					if(effectSet.damage != 0) {
						helm = helm * effectSet.damage;
					}
				}
				if (armor[1].getType() == Material.getMaterial(item.id)) {
					if(effectSet.damage != 0) {
						pants = pants * effectSet.damage;
					}
				}
				if (armor[2].getType() == Material.getMaterial(item.id)) {
					if(effectSet.damage != 0) {
						chest = chest * effectSet.damage;
					}
				}
				if (armor[3].getType() == Material.getMaterial(item.id)) {
					if(effectSet.damage != 0) {
						boots = boots * effectSet.damage;
					}
				}
    		}
    	}
		
		armorPoints = (helm + chest + pants + boots) / 100;
		if (PVPGear.debug) {
			log.info(PVPGear.prefix+"DEBUG: CHECK "+player.getDisplayName()+"'S ARMOR: "+armorPoints);
			log.info(PVPGear.prefix+"DEBUG: BOOTS: "+boots+", PANTS: "+pants+", CHEST: "+chest+", HELM: "+helm);
		}
		
		return armorPoints;
	}
	
	private static double checkEnchantments(Player player, boolean pvp) {
		double result = 1;

		ItemStack[] armor = player.getInventory().getArmorContents();
		
		for (PVPItem aura : pvpEnchants) {
			PVPItem.Effects effectSet = pvp?aura.pvpEffects:aura.pveEffects;
			
    		if (effectSet.damage != 1 && effectSet.damage != 0) {
    			Map<Enchantment, Integer> enchantments = player.getItemInHand().getEnchantments();
    			for (int i = 0; i<enchantments.size(); i++) {
    				Object[] enchantsAll = enchantments.keySet().toArray();
    				Object[] enchantsLevels = enchantments.values().toArray();
    				Enchantment ench = (Enchantment) enchantsAll[i];
    				if (aura.id == ench.getId()) {
    					log.info(PVPGear.prefix+ "DEBUG: " + player.getDisplayName() + " GOT AN ENCHANT ON HIS WEAPON " + ench.getId() + " ("+ench.getName()+") Level : " + enchantsLevels[i].toString());
    					result = result * effectSet.damage * Integer.parseInt(enchantsLevels[i].toString());
    				}
    			}
    		}
    		
    		
    		for (ItemStack gear : armor) {
    			Map<Enchantment, Integer> enchantments = gear.getEnchantments();
    			for (int i = 0; i<enchantments.size(); i++) {
    				Object[] enchantsAll = enchantments.keySet().toArray();
    				Object[] enchantsLevels = enchantments.values().toArray();
    				Enchantment ench = (Enchantment) enchantsAll[i];
    				if (effectSet.damage != 1 && effectSet.damage != 0 && aura.id == ench.getId()) {
	    				log.info(PVPGear.prefix+ "DEBUG: ENCHANTMENTS ON PLAYER " + player.getDisplayName() + ": " + ench.getId() + " ("+ench.getName()+") Level : " + enchantsLevels[i].toString());
	    				result = result * effectSet.damage * Integer.parseInt(enchantsLevels[i].toString());
    				}
    			}
    		}
    		
    	}
		
		return result;
	}
}
