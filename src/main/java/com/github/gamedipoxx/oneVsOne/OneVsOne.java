package com.github.gamedipoxx.oneVsOne;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.gamedipoxx.oneVsOne.arena.Arena;
import com.github.gamedipoxx.oneVsOne.commands.OneVsOneCommand;
import com.github.gamedipoxx.oneVsOne.listener.ArenaManager;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class OneVsOne extends JavaPlugin{
	private static ArrayList<Arena> arena = new ArrayList<Arena>();
	private static OneVsOne plugin;
	private static MultiverseCore multiversecore;
	@Override
	public void onEnable() {
		
		saveDefaultConfig();
		saveConfig();
		plugin = this;
		multiversecore = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		
		this.getCommand("OneVsOne").setExecutor(new OneVsOneCommand());
		getServer().getPluginManager().registerEvents(new ArenaManager(), this);
		//getServer().getPluginManager().registerEvents(new EventDebugger(), this); //USE THIS JUST FOR DEBUG PURPOSE!
	}
	
	@Override
	public void onDisable() {
		for(Arena forarena : arena) {
			Arena.deleteAndUnregisterArena(forarena);
		}
	}
	public static Collection<Arena> getArena() {
		return arena;
	}
	
	public static void setArena(ArrayList<Arena> arena) {
		OneVsOne.arena = arena;
	}
	public static OneVsOne getPlugin() {
		return plugin;
	}
	public static MultiverseCore getMultiversecore() {
		return multiversecore;
	}
}
