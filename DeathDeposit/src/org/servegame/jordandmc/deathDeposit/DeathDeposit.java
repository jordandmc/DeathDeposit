package org.servegame.jordandmc.deathDeposit;

import java.util.HashMap;
import java.util.logging.Logger;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.InventoryLargeChest;
import net.minecraft.server.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathDeposit extends JavaPlugin
{
	public static final Logger log = Logger.getLogger("Minecraft");
	public static String messagePrefix = "[DD] ";
	public HashMap<String, InventoryLargeChest> chests = new HashMap<String, InventoryLargeChest>();
	public HashMap<Player, ddInv> inventory = new HashMap<Player, ddInv>();
	public HashMap<String, Integer> deposit = new HashMap<String, Integer>();
	public ddInfo death = new ddInfo();
	public static int limit;
	private static DeathDeposit instance;
	private final ddPlayerListener playerListener = new ddPlayerListener(this);
	private final ddBlockListener blockListener = new ddBlockListener(this);
	private final ddEntityListener entityListener = new ddEntityListener(this);
	private final ddWorldListener worldListener = new ddWorldListener(this);
	private final ddInventoryListener invListener = new ddInventoryListener(this);
	private final ddScreenListener screenListener = new ddScreenListener(this);
	private final ddFile fileIO = new ddFile(this);

	public DeathDeposit() {
		instance = this;
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);

		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);

		pm.registerEvent(Event.Type.CUSTOM_EVENT, invListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT, screenListener, Event.Priority.Normal, this);

		pm.registerEvent(Event.Type.WORLD_SAVE, worldListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Event.Priority.Normal, this);

		chests = fileIO.load();

		PluginDescriptionFile pdfFile = getDescription();
		log.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " was successfully enabled!");
	}

	public void onDisable() {
		save();

		PluginDescriptionFile pdfFile = getDescription();
		System.out.println(messagePrefix + pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!");
	}

	public void save() {
		System.out.println("DeathDeposit");
		fileIO.save(chests);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(messagePrefix + ChatColor.RED + "Only players can use this command!");
			return true;
		}
		Player player = (Player)sender;
		String cmdName = cmd.getName().toLowerCase();
		if (cmdName.substring(0, 2).equals("dd")) {
			String command = cmdName.substring(2);
			if (command.equals("open")) {
				if (player.isOp()) {
					if (args.length == 1)
						openChest(player, args);
					else
						openChest(player);
				}
				else {
					player.sendMessage(messagePrefix + ChatColor.RED + "You don't have permission to use that command");
				}
			}
			else if (command.equals("sort")) {
				stackChest(player);
				sortChest(player);
			}
			else if (command.equals("help")) {
				showHelp(player);
			}
			else {
				return false;
			}

			return true;
		}
		return false;
	}

	public void showHelp(Player player) {
		player.sendMessage(ChatColor.YELLOW + messagePrefix + "---------------------------------------");

		player.sendMessage(ChatColor.GREEN + "Possible commands:");
		player.sendMessage("ddopen/ddopen <player> - Opens the specified player's DeathChest");
		player.sendMessage("ddsort/ddsort - sort and stack your DeathChest");
		player.sendMessage("ddserver/ddserver - Shows the server id");

		player.sendMessage(ChatColor.YELLOW + messagePrefix + "---------------------------------------");
	}

	public void openChest(Player player) {
		openChest(player, new String[] { player.getName() });
	}

	public void openChest(Player player, String[] args) {
		if (args[0] == null) args[0] = player.getName();
		if (!chests.containsKey(args[0].toLowerCase())) {
			player.sendMessage(messagePrefix + ChatColor.RED + "You have not yet placed a DeathChest");
			return;
		}
		InventoryLargeChest chest = (InventoryLargeChest)chests.get(args[0]);
		EntityPlayer cPlayer = ((CraftPlayer)player).getHandle();

		stackChest(player);
		sortChest(player);

		cPlayer.a(chest);
	}

	public boolean stackChest(Player player) {
		String name = player.getName();
		if (!chests.containsKey(name)) {
			player.sendMessage(messagePrefix + ChatColor.RED + "You haven't designated a DeathChest yet.");
			return false;
		}
		InventoryLargeChest chest = (InventoryLargeChest)chests.get(name);
		for (int index = 0; index < 54; index++) {
			ItemStack stack = chest.getItem(index);
			if ((stack != null) && (stack.count != 0) && (stack.id != 0)) {
				int i = 0;
				for (ItemStack stack2 : chest.getContents())
				{
					if ((stack2 != null) && (i != index) && (stack2.count != 0) && (stack2.count < stack2.b) && (stack2.id == stack.id))
					{
						stack.count = Math.min(stack2.b, stack.count + stack2.count);
						chest.setItem(index, stack);
						stack2.count = Math.max(0, stack.count + stack2.count - stack2.b);

						if (stack2.count > 0) {
							chest.setItem(i, stack2);
							break;
						}

						chest.setItem(i, null);
					}
					i++;
				}
			}
			index++;
		}

		return true;
	}

	public boolean sortChest(Player player) {
		String name = player.getName();
		InventoryLargeChest chest = (InventoryLargeChest)chests.get(name);

		int swapStackI = 0;

		@SuppressWarnings("unused")
		int numItems = 0;
		for (int index = 0; index < 54; index++) {
			ItemStack stack = chest.getItem(index);
			ItemStack swapStack = stack;
			swapStackI = index;
			for (int i = index + 1; i < 54; i++) {
				ItemStack stack2 = chest.getItem(i);
				if ((stack2 == null) || (stack2.count == 0) || (stack2.id == 0) || (
						(swapStack != null) && (stack2.id >= swapStack.id))) continue;
				swapStackI = i;
				swapStack = stack2;
			}

			if (swapStack != null) {
				if (swapStack != stack) {
					chest.setItem(index, swapStack);
					chest.setItem(swapStackI, stack);
				}
			}
			else {
				numItems = index;
				break;
			}
		}
		return true;
	}

	public boolean isChestOwner(Player player, Block block) {
		Block cube = getSign(block);
		if (!(cube instanceof Sign)) {
			return false;
		}

		Sign sign = (Sign)cube;

		return sign.getLine(1).equalsIgnoreCase(player.getName());
	}

	public Block getSign(Block block) {
		Block check = block.getRelative(BlockFace.NORTH);
		if (check.getType().equals(Material.WALL_SIGN)) return check;

		check = block.getRelative(BlockFace.EAST);
		if (check.getType().equals(Material.WALL_SIGN)) return check;

		check = block.getRelative(BlockFace.SOUTH);
		if (check.getType().equals(Material.WALL_SIGN)) return check;

		check = block.getRelative(BlockFace.WEST);
		if (check.getType().equals(Material.WALL_SIGN)) return check;

		return null;
	}

	public String getChestOwner(String[] lines) {
		String name = "";
		if (lines[2].equalsIgnoreCase("[DD]")) {
			name = lines[1].toLowerCase();
		}
		return name;
	}

	public static DeathDeposit getInstance() {
		return instance;
	}
}