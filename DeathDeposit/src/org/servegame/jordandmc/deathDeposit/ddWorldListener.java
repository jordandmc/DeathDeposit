package org.servegame.jordandmc.deathDeposit;

import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldSaveEvent;

public class ddWorldListener extends WorldListener
{
	public static DeathDeposit plugin;

	public ddWorldListener(DeathDeposit instance)
	{
		plugin = instance;
	}

	public void onWorldSave(WorldSaveEvent event)
	{
		System.out.println("world listener");
		plugin.save();
	}
}