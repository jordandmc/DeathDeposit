package org.servegame.jordandmc.deathDeposit;

import net.minecraft.server.InventoryLargeChest;
import net.minecraft.server.Packet101CloseWindow;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ddInventoryListener extends InventoryListener
{
	private static DeathDeposit plugin;

	public ddInventoryListener(DeathDeposit instance)
	{
		plugin = instance;
	}

	public void onInventoryClose(InventoryCloseEvent event) {
		SpoutPlayer player = (SpoutPlayer)event.getPlayer();
		String name = player.getName();

		if (plugin.deposit.containsKey(name))
			player.getMainScreen().attachPopupScreen(new cBox(player.getMainScreen()));
	}

	public void onInventoryClick(InventoryClickEvent event)
	{
		Player player = event.getPlayer();
		String name = player.getName();

		if (plugin.deposit.containsKey(name)) {
			if (event.getInventory().getSize() == 54) {
				event.setCancelled(true);
				return;
			}
			if (event.getItem() != null) {
				int num = ((Integer)plugin.deposit.get(name)).intValue();

				if (num == 1) {
					addItem(event);
					plugin.deposit.remove(name);
					((CraftPlayer)player).getHandle().netServerHandler.sendPacket(new Packet101CloseWindow());
					drop(player);
				}
				else {
					num--;
					plugin.deposit.put(name, Integer.valueOf(num));
					addItem(event);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void addItem(InventoryClickEvent event) {
		int empty = -1;
		Player player = event.getPlayer();
		String name = player.getName();
		InventoryLargeChest chest = (InventoryLargeChest)plugin.chests.get(name);

		for (int i = 0; i < 54; i++) {
			if (chest.getItem(i) == null) {
				empty = i;
				break;
			}
		}

		if ((empty < 53) && (empty != -1)) {
			int num = ((Integer)plugin.deposit.get(name)).intValue();

			if (event.getRawSlot() == 36) {
				player.getInventory().clear(0);
				player.updateInventory();
			}
			else {
				player.getInventory().clear(event.getRawSlot());
			}chest.setItem(empty, getItem(event.getItem()));
			player.sendMessage(num + " items left");
		}
		else if (empty == 53) {
			if (event.getRawSlot() == 36) {
				player.getInventory().clear(0);
				player.updateInventory();
			}
			else {
				player.getInventory().clear(event.getRawSlot());
			}
			chest.setItem(empty, getItem(event.getItem()));
			close(player);
		}
		else {
			close(player);
		}
	}

	private void close(Player player) {
		plugin.deposit.remove(player.getName());
		((CraftPlayer)player).getHandle().netServerHandler.sendPacket(new Packet101CloseWindow());
		player.sendMessage("Your chest is full. Please go pickup your items");
		drop(player);
	}

	private void drop(Player player) {
		String name = player.getName();
		World world = plugin.death.getWorld(name);
		Location loc = plugin.death.getLoc(name);
		PlayerInventory inv = player.getInventory();
		org.bukkit.inventory.ItemStack[] items = inv.getContents();

		for (int i = 0; i < items.length; i++) {
			if (items[i] != null)
				world.dropItem(loc, items[i]);
			inv.remove(items[i]);
		}
	}

	private net.minecraft.server.ItemStack getItem(org.bukkit.inventory.ItemStack item) {
		net.minecraft.server.ItemStack clone = new net.minecraft.server.ItemStack(0, 0, 0);

		clone.id = item.getTypeId();
		clone.count = item.getAmount();
		clone.damage = item.getDurability();

		return clone;
	}
}