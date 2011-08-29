package org.servegame.jordandmc.deathDeposit;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class ddEntityListener extends EntityListener
{
	private static DeathDeposit plugin;

	public ddEntityListener(DeathDeposit instance)
	{
		plugin = instance;
	}

	public void onEntityDeath(EntityDeathEvent e) {
		if ((e.getEntity() instanceof Player)) {
			Player player = (Player)e.getEntity();
			String name = player.getName();

			if (plugin.chests.containsKey(name)) {
				plugin.death.register(name, player.getWorld(), player.getLocation());
				plugin.inventory.put(player, new ddInv(player.getInventory()));
				for (int i = e.getDrops().size(); i > 0; i--)
					e.getDrops().remove(i - 1);
			}
			else {
				player.sendMessage("You don't have a DeathChest to deposit your items in");
			}
		}
	}
}