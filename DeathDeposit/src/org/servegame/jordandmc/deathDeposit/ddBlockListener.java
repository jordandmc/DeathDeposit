package org.servegame.jordandmc.deathDeposit;

import java.util.ArrayList;
import net.minecraft.server.InventoryLargeChest;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

public class ddBlockListener extends BlockListener
{
	public static DeathDeposit plugin;

	public ddBlockListener(DeathDeposit instance)
	{
		plugin = instance;
	}

	public void onSignChange(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		String chestOwner = plugin.getChestOwner(event.getLines());
		if (!chestOwner.equals(""))
			if (!chestOwner.equalsIgnoreCase(player.getName())) {
				event.setCancelled(true);
				player.sendMessage("You can only create a DeathChest for yourself");
			}
			else if (plugin.chests.containsKey(chestOwner)) {
				event.setCancelled(true);
				player.sendMessage("You already have a DeathChest");
			}
			else {
				plugin.chests.put(chestOwner, new InventoryLargeChest(chestOwner, new ddChest(), new ddChest()));
				player.sendMessage("DeathChest set");
			}
	}

	public void onBlockBreak(BlockBreakEvent event)
	{
		if ((event.getBlock().getState() instanceof Sign)) {
			Player player = event.getPlayer();
			Sign sign = (Sign)event.getBlock().getState();
			String[] lines = sign.getLines();
			String chestOwner = plugin.getChestOwner(lines);
			if (!chestOwner.equals(""))
				if (!plugin.chests.containsKey(chestOwner)) {
					player.sendMessage(DeathDeposit.messagePrefix + ChatColor.RED + "This Death Chest was broken");
				}
				else if (chestOwner.equals(player.getName())) {
					signDestroyed(sign, ((InventoryLargeChest)plugin.chests.get(chestOwner)).getContents());
					plugin.chests.remove(chestOwner);
				}
				else {
					event.setCancelled(true);
					sign.update();
					player.sendMessage(DeathDeposit.messagePrefix + ChatColor.RED + "This is not your Death Chest to remove");
				}
		}
	}

	private ArrayList<ItemStack> getStack(net.minecraft.server.ItemStack[] stack)
	{
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();

		for (int i = 0; i < stack.length; i++) {
			if (stack[i] != null) {
				items.add(new org.bukkit.inventory.ItemStack(stack[i].getItem().id, stack[i].count));
			}
		}
		return items;
	}

	private void signDestroyed(Sign sign, net.minecraft.server.ItemStack[] stack)
	{
		ArrayList<ItemStack> items = getStack(stack);
		Location pos = sign.getBlock().getLocation();
		World world = sign.getWorld();

		for (int i = 0; i < items.size(); i++)
			world.dropItemNaturally(pos, (org.bukkit.inventory.ItemStack)items.get(i));
	}
}