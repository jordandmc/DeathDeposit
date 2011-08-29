package org.servegame.jordandmc.deathDeposit;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.World;

public class ddInfo
{
	private HashMap<String, World> world;
	private HashMap<String, Location> loc;

	public ddInfo()
	{
		world = new HashMap<String, World>();
		loc = new HashMap<String, Location>();
	}
	public void register(String name, World world, Location loc) {
		this.world.put(name, world);
		this.loc.put(name, loc);
	}

	public World getWorld(String name) {
		return (World)world.get(name);
	}

	public Location getLoc(String name) {
		return (Location)loc.get(name);
	}

	public void clear(String name) {
		if (world.containsKey(name)) {
			world.remove(name);
			loc.remove(name);
		}
	}
}