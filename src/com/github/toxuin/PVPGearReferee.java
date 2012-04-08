package com.github.toxuin;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PVPGearReferee {
	
	public static Set<PVPItem> pvpWeapons = new HashSet<PVPItem>();
	public static Set<PVPItem> pvpArmor = new HashSet<PVPItem>();
	
	public static int getPvpDamage (Player bully, Player victim, int newDamage) {        
        // ------- ATTACKER!
        
    	for (PVPItem item : pvpWeapons) {
    		if (item.pvpDamage !=1 || item.pvpDamage != 0) {
	    		if (Material.getMaterial(item.id) == bully.getItemInHand().getType()) {
	    			newDamage = (int) Math.round(newDamage * item.pvpDamage);
	    			if (PVPGear.debug) Logger.getLogger("Minecraft").info(PVPGear.prefix+"DEBUG: PVP ATTACK * "+item.pvpDamage+" ~= "+newDamage);
	    		}
    		}
    	}
        
        // ------- DEFENDER!

        ItemStack[] armor = victim.getInventory().getArmorContents();
        
    	for (PVPItem item : pvpArmor) {
    		if (item.pvpDamage !=1 || item.pvpDamage != 0) {
	    		for (ItemStack equiped : armor) {
		    		if (Material.getMaterial(item.id) == equiped.getType()) {
		    			newDamage = (int) Math.round(newDamage * item.pvpDamage);
		    			if (PVPGear.debug) Logger.getLogger("Minecraft").info(PVPGear.prefix+"DEBUG: PVP DEFENCE x"+item.pvpDamage+" ~= "+newDamage);
		    		}
	    		}
    		}
    	}
		return newDamage;
	}
	
	public static int getPveDamage(Player bully, int newDamage) {
		
		// SCAN FOR PVE BOOSTER ITEMS
		
		for (PVPItem item : pvpWeapons) {
			if (item.pveDamage !=1 || item.pveDamage != 0) {
				if (Material.getMaterial(item.id) == bully.getItemInHand().getType()) {
	    			newDamage = (int) Math.round(newDamage * item.pveDamage);
	    			if (PVPGear.debug) Logger.getLogger("Minecraft").info(PVPGear.prefix+"DEBUG: PVE ATTACK x"+item.pveDamage+" ~= "+newDamage);
	    		}
			}
		}
		
		
		// SCAN FOR PVE BOOSTER ARMOR
		
		ItemStack[] armor = bully.getInventory().getArmorContents();
        
    	for (PVPItem item : pvpArmor) {
    		if (item.pveDamage !=1 || item.pveDamage != 0) {
	    		for (ItemStack equiped : armor) {
		    		if (Material.getMaterial(item.id) == equiped.getType()) {
		    			newDamage = (int) Math.round(newDamage * item.pveDamage);
		    			if (PVPGear.debug) Logger.getLogger("Minecraft").info(PVPGear.prefix+"DEBUG: PVE ARMOR DAMAGE BOOST x"+item.pveDamage+" ~= "+newDamage);
		    		}
	    		}
    		}
    	}
		
		return newDamage;
	}
}
