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
	
	public static int getNewDamage (Player bully, Player victim, int oldDamage) {
		// INIT THE VARS
        int newDamage = oldDamage;
        
        // ------- ATTACKER!
        
    	for (PVPItem item : pvpWeapons) {
    		if (Material.getMaterial(item.id) == bully.getItemInHand().getType()) {
    			newDamage = (int) Math.round(newDamage * item.damage);
    			if (PVPGear.debug) Logger.getLogger("Minecraft").info(PVPGear.prefix+"DEBUG: ATTACK * "+item.damage+" ~= "+newDamage);
    		}
    	}
        
        // ------- DEFENDER!

        ItemStack[] armor = victim.getInventory().getArmorContents();
        
    	for (PVPItem item : pvpArmor) {
    		for (ItemStack equiped : armor) {
	    		if (Material.getMaterial(item.id) == equiped.getType()) {
	    			newDamage = (int) Math.round(newDamage * item.damage);
	    			if (PVPGear.debug) Logger.getLogger("Minecraft").info(PVPGear.prefix+"DEBUG: DEFENCE * "+item.damage+" ~= "+newDamage);
	    		}
    		}
    	}
		
		return newDamage;
	}
}
