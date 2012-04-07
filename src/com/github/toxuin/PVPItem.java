package com.github.toxuin;

import org.bukkit.inventory.ItemStack;

public class PVPItem {
	public Integer id;
	public double damage;
	public String name;
	public ItemStack getItemStack () {
		return new ItemStack(this.id);
	}
}
