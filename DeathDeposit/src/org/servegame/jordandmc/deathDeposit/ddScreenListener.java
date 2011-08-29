package org.servegame.jordandmc.deathDeposit;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ddScreenListener extends ScreenListener
{
	private DeathDeposit plugin;

	public ddScreenListener(DeathDeposit instance)
	{
		plugin = instance;
	}

	public void onButtonClick(ButtonClickEvent event) {
		SpoutPlayer player = event.getPlayer();
		String name = player.getName();

		if (event.getButton().getText().equals("Cancel")) {
			event.getPlayer().getMainScreen().closePopup();
			plugin.openChest(player);
		}
		else if (event.getButton().getText().equals("O.K")) {
			World world = plugin.death.getWorld(name);
			Location loc = plugin.death.getLoc(name);

			plugin.deposit.remove(player.getName());
			event.getPlayer().getMainScreen().closePopup();

			for (ItemStack item : player.getInventory().getContents()) {
				if (item != null)
					world.dropItem(loc, item);
			}
			for (int i = 0; i < 36; i++)
				player.getInventory().clear(i);
		}
	}
}