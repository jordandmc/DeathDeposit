package org.servegame.jordandmc.deathDeposit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.server.InventoryLargeChest;
import net.minecraft.server.ItemStack;

public class ddFile
{
	public static DeathDeposit plugin;
	public static String baseDir = "plugins/Death Deposit/";
	private static File file = new File(baseDir + "chests.txt");

	public ddFile(DeathDeposit instance)
	{
		plugin = instance;
	}

	private String[] loadFile(File file)
	{
		String[] lines = { "6" };

		new File(baseDir).mkdir();
		try {
			if (!file.exists())
			{
				try {
					file.createNewFile();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
				System.out.println(DeathDeposit.messagePrefix + file.getName() + " not found, creating it.");

				return lines;
			}

			ArrayList<String> listLines = new ArrayList<String>();
			BufferedReader readBuffer = new BufferedReader(new FileReader(file));
			String line = "";
			while (line != null) {
				line = readBuffer.readLine();
				listLines.add(line);
			}
			lines = (String[])listLines.toArray(new String[0]);
			readBuffer.close();
		}
		catch (IOException ex)
		{
			System.out.println(DeathDeposit.messagePrefix + "Error while reading " + file.getName());
			ex.printStackTrace();
			return lines;
		}
		return lines;
	}

	@SuppressWarnings("unused")
	private void saveFile(File file, String[] lines){
		System.out.println("ddFile: saveFile");
		if(file.exists()){
			// Delete the old file.
			if(!file.delete()){
				System.out.println(DeathDeposit.messagePrefix + "Could not delete " + file.getName());
			}
			// Create an empty file.
			try	{
				file.createNewFile();
			}
			// If it fails, print why.
			catch (IOException e){
				e.printStackTrace();
			}
		}
		
		FileOutputStream outStream = null;
		PrintStream printStream = null;
		try	{
			outStream = new FileOutputStream(file, true);
			printStream = new PrintStream(outStream, true);
		}
		catch (IOException ex){
			if (printStream != null) printStream.close();
			try	{
				if (outStream != null) outStream.close();
			}
			catch (IOException ex2){}
			System.out.println(DeathDeposit.messagePrefix + "Could not write file " + file.getName());
			return;
		}

		for(String line : lines){
			if(line != null){
				printStream.println(line);
			}
		}

		try	{
			outStream.close();
		} catch (IOException ex) {}
		printStream.close();
	}

	public HashMap<String, InventoryLargeChest> load()
	{
		HashMap<String, InventoryLargeChest> chests = new HashMap<String, InventoryLargeChest>();

		String[] lines = loadFile(file);
		try
		{
			DeathDeposit.limit = Integer.parseInt(lines[0]);
		} catch (NumberFormatException e) {
			System.out.println("The first line of chest.txt isn't a number.");
		}

		if ((lines.length - 2) % 3 == 0) {
			for (int i = 1; i < lines.length - 1; i += 3) {
				if (chests.containsKey(lines[i])) {
					System.out.println(DeathDeposit.messagePrefix + "Duplicate chestname found: \"" + lines[i] + "\".");
				}
				else
				{
					InventoryLargeChest lrgchest = new InventoryLargeChest(lines[i], new ddChest(), new ddChest());
					chests.put(lines[i], lrgchest);
					String[] ids = lines[(i + 1)].split(" ");
					String[] amounts = lines[(i + 2)].split(" ");
					if ((ids.length != amounts.length) || (ids.length != 54))
						System.out.println(DeathDeposit.messagePrefix + "Found invalid amount of item ids/amounts in chests.txt");
					for (int j = 0; j < ids.length; j++) {
						int id = Integer.parseInt(ids[j]);
						int amount = Integer.parseInt(amounts[j]);
						if ((id == 0) || (amount == 0))
							continue;
						lrgchest.setItem(j, new ItemStack(id, amount, 0));
					}
				}
			}
			System.out.println(DeathDeposit.messagePrefix + "Loaded " + (lines.length - 2) / 3 + " chests.");
		}
		else {
			System.out.println(DeathDeposit.messagePrefix + "Found invalid amount of lines in chests.txt: " + lines.length + ", should be multiple of 3./nUsing default");
		}
		return chests;
	}

	public void save(HashMap<String, InventoryLargeChest> chests) {
		System.out.println("ddFile:save");

		ArrayList<String> lines = new ArrayList<String>();
		if (!chests.keySet().isEmpty()) {
			lines = getContents(chests);
		}
		lines.add(0, Integer.toString(DeathDeposit.limit));
		saveFile(file, (String[])lines.toArray(new String[0]));
	}

	private ArrayList<String> getContents(HashMap<String, InventoryLargeChest> chests)
	{
		ArrayList<String> lines = new ArrayList<String>();

		for (String chestName : chests.keySet()) {
			InventoryLargeChest lrgchest = (InventoryLargeChest)chests.get(chestName);
			ItemStack[] chestinv = lrgchest.getContents();
			String ids = "";
			String amounts = "";
			for (ItemStack stack : chestinv)
			{
				if (ids.equals("")) {
					if ((stack == null) || (stack.count == 0) || (stack.id == 0)) {
						ids = ids + "0";
						amounts = amounts + "0";
					}
					else {
						ids = ids + stack.id;
						amounts = amounts + stack.count;
					}

				}
				else if ((stack == null) || (stack.count == 0) || (stack.id == 0)) {
					ids = ids + " 0";
					amounts = amounts + " 0";
				}
				else {
					ids = ids + " " + stack.id;
					amounts = amounts + " " + stack.count;
				}
			}

			lines.add(chestName);
			lines.add(ids);
			lines.add(amounts);
		}
		return lines;
	}
}