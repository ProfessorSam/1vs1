package com.github.gamedipoxx.oneVsOne;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.gamedipoxx.oneVsOne.arena.Arena;
import com.github.gamedipoxx.oneVsOne.arena.ArenaMap;
import com.github.gamedipoxx.oneVsOne.commands.OneVsOneCommand;
import com.github.gamedipoxx.oneVsOne.commands.OneVsOneLeaveCommand;
import com.github.gamedipoxx.oneVsOne.commands.OneVsOneSetupCommand;
import com.github.gamedipoxx.oneVsOne.listener.ArenaManager;
import com.github.gamedipoxx.oneVsOne.listener.BlockBreakOnStartingListener;
import com.github.gamedipoxx.oneVsOne.listener.LeaveItem;
import com.github.gamedipoxx.oneVsOne.listener.OnTntPlaceListener;
import com.github.gamedipoxx.oneVsOne.listener.PlayerChatListener;
import com.github.gamedipoxx.oneVsOne.listener.PlayerJoinListener;
import com.github.gamedipoxx.oneVsOne.listener.PlayerMoveEventCancel;
import com.github.gamedipoxx.oneVsOne.listener.TabListRemover;
import com.github.gamedipoxx.oneVsOne.scoreboard.ScoreboardManager;
import com.github.gamedipoxx.oneVsOne.utils.MessagesFile;
import com.github.gamedipoxx.oneVsOne.utils.MySQLManager;
import com.github.gamedipoxx.oneVsOne.utils.UpdateChecker;
import com.github.gamedipoxx.oneVsOne.utils.stats.GlobalStatsGUI;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.bstats.bukkit.Metrics;

public class OneVsOne extends JavaPlugin{
	private static ArrayList<Arena> arena = new ArrayList<Arena>();
	private static OneVsOne plugin;
	private static MultiverseCore multiversecore;
	private static String servername;
	@Override
	public void onEnable() {
		//Config stuff
		saveDefaultConfig();
		saveConfig();
		servername = getConfig().getString("ServerName");
		MessagesFile.setPlugin(this);
		MessagesFile.init();
		GlobalStatsGUI.setPlugin(plugin);
		GlobalStatsGUI.setGamesLost(Messages.GAMESLOST.getString());
		GlobalStatsGUI.setGamesPlayed(Messages.GAMESPLAYED.getString());
		GlobalStatsGUI.setGamesWon(Messages.GAMESWON.getString());
		
		//init plugins and Apis
		plugin = this;
		multiversecore = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		
		//Check Version
		UpdateChecker.setCurrentVersion(getDescription().getVersion());
		UpdateChecker.setPlugin(this);
		UpdateChecker.check();
		
		//register Commands and Bungeecord
		this.getCommand("OneVsOne").setExecutor(new OneVsOneCommand());
		this.getCommand("OneVsOneSetup").setExecutor(new OneVsOneSetupCommand());
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		
		//check for setupmode and cancel the rest of onEnable()
		if(getConfig().getBoolean("setupmode") == true) {
			OneVsOneSetupCommand.resetSetupObject();
			return;
		}
		
		if(getConfig().getBoolean("leaveCommand")) {
			this.getCommand("leave").setExecutor(new OneVsOneLeaveCommand());
			ArrayList<String> list = new ArrayList<>();
			list.add("l");
			this.getCommand("leave").setAliases((List<String>)list);
		}
		
		//init Database
		MySQLManager.setConfig(getConfig());
		MySQLManager.setPlugin(this);
		MySQLManager.setSetupFile(getResource("dbsetup.sql"));
		if (!MySQLManager.init()) {
			getServer().getPluginManager().disablePlugin(this);
		}
		//register all Events
		getServer().getPluginManager().registerEvents(new ArenaManager(), this);
		getServer().getPluginManager().registerEvents(new PlayerMoveEventCancel(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
		getServer().getPluginManager().registerEvents(new TabListRemover(), this);
		getServer().getPluginManager().registerEvents(new LeaveItem(), this);
		getServer().getPluginManager().registerEvents(new BlockBreakOnStartingListener(), this);
		getServer().getPluginManager().registerEvents(new UpdateChecker(), this);
		//getServer().getPluginManager().registerEvents(new EventDebugger(), this); //USE THIS JUST FOR DEBUG PURPOSE!
		
		//Check if the scoreboard is enables and then register the listener
		if(getConfig().getBoolean("scoreboard")) {
			getServer().getPluginManager().registerEvents(new ScoreboardManager(), this);
		}
		
		//checks if the tnt is enabled
		if(getConfig().getBoolean("tnt")) {
			getServer().getPluginManager().registerEvents(new OnTntPlaceListener(), this);
		}
		
		//Create a Kit list
		ArenaMap.setMaps(getConfig().getStringList("Maps"));
		
		//clear Database
		MySQLManager.purgeDatabase();
		
		//create all Arenas as definded in the config.yml
		ArenaManager.createMaxArenas();
		
		//Integrate bstats
		int pluginId = 14364;
		new Metrics(this, pluginId);
	}
	
	@Override
	public void onDisable() {
		if(getConfig().getBoolean("setupmode")) {
			return;
		}
		
		MySQLManager.purgeDatabase();
		@SuppressWarnings("unchecked")
		ArrayList<Arena> templist = (ArrayList<Arena>) arena.clone();
		for(Arena arena : templist) {
			Arena.deleteAndUnregisterArenaForOnDisable(arena);
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

	public static String getServername() {
		return servername;
	}
}
