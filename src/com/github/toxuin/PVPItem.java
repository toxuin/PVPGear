package com.github.toxuin;

import org.bukkit.inventory.ItemStack;

public class PVPItem {
	public Integer id = 7; // 7 = bedrock so no harm could be made with error in config.
	public double pvpDamage = 1; // X * 1 = X
	public double pveDamage = 1;
	public String name = "!!!UNKNOWN ITEM!!!"; // you should see this only in case of error in config
	public ItemStack getItemStack () {
		return new ItemStack(this.id);
	}
}
