package org.servegame.jordandmc.deathDeposit;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class ddPlayerListener extends PlayerListener
{
	private static DeathDeposit plugin;

	public ddPlayerListener(DeathDeposit instance)
	{
		plugin = instance;
	}

	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		if ((event.getAction().toString() != "RIGHT_CLICK_BLOCK") || (block == null) || (!(block.getState() instanceof Chest)))
			return;
		block = plugin.getSign(block);
		if (block == null) return;
		if ((block.getState() instanceof Sign)) {
			String signName = plugin.getChestOwner(((Sign)block.getState()).getLines());
			if (signName.equalsIgnoreCase(player.getName())) {
				plugin.openChest(player);
				event.setCancelled(true);
			}
			else {
				player.sendMessage(DeathDeposit.messagePrefix + ChatColor.RED + "That's not your Death Chest");
				event.setCancelled(true);
			}
		}
	}

	public void onPlayerJoin(PlayerJoinEvent event)
	{
		event.getPlayer().sendMessage(DeathDeposit.messagePrefix + "Type /ddhelp to learn how to use this plugin.");
	}

	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		Player player = event.getPlayer();
		if (plugin.inventory.containsKey(player))
			((ddInv)plugin.inventory.get(player)).setIsRespawned(true);
	}

	public void onPlayerMove(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();

		if ((plugin.inventory.containsKey(player)) && (((ddInv)plugin.inventory.get(player)).isRespawned())) {
			((ddInv)plugin.inventory.get(player)).paste(player.getInventory());
			plugin.inventory.remove(player);
			plugin.openChest(player);
			plugin.deposit.put(player.getName(), Integer.valueOf(DeathDeposit.limit));
		}
	}
}