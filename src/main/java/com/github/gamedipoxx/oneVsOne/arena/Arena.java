package com.github.gamedipoxx.oneVsOne.arena;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.github.gamedipoxx.oneVsOne.Messages;
import com.github.gamedipoxx.oneVsOne.OneVsOne;
import com.github.gamedipoxx.oneVsOne.events.GameStateChangeEvent;
import com.github.gamedipoxx.oneVsOne.events.PlayerJoinArenaEvent;
import com.github.gamedipoxx.oneVsOne.events.PlayerLeaveArenaEvent;
import com.onarandombox.MultiverseCore.api.MVWorldManager;

public class Arena {
	private String arenaUuid;
	private final static Location lobby = new Location(Bukkit.getWorld(OneVsOne.getPlugin().getConfig().getString("Lobby.World")), OneVsOne.getPlugin().getConfig().getInt("Lobby.X"), OneVsOne.getPlugin().getConfig().getInt("Lobby.Y"), OneVsOne.getPlugin().getConfig().getInt("Lobby.Z"), OneVsOne.getPlugin().getConfig().getLong("Lobby.Pitch"), OneVsOne.getPlugin().getConfig().getLong("Lobby.Yaw"));
	private static MVWorldManager worldmanager;
	private String worldname;
	private int playercount;
	private Location spawn1;
	private Location spawn2;
	private GameState gameState;
	private Collection<Player> players = new ArrayList<Player>();
	private World world;
	
	//TODO Gamerule /gamerule doImmediateRespawm to true
	public Arena(@NotNull String arenaname) {
		arenaUuid = "" + Instant.now().getEpochSecond() + RandomUtils.nextInt();
		if(Bukkit.getWorld(arenaname) == null) {
			OneVsOne.getPlugin().getLogger().warning("Cant find world " + arenaname);
			Bukkit.getServer().getPluginManager().disablePlugin(OneVsOne.getPlugin());
		}
		worldname = arenaUuid;
		worldmanager = OneVsOne.getMultiversecore().getMVWorldManager(); //set Multiverse wolrdmanager
		worldmanager.cloneWorld(arenaname, worldname); //clone world
		
		playercount = 0;
		gameState = GameState.WAITING;
		
		spawn1 = initSpawn(1);
		spawn2 = initSpawn(2);
		
		
	}
	
	//Init the Spawn from Config
	private Location initSpawn(int spawn) {
		double x = OneVsOne.getPlugin().getConfig().getDouble("Spawn" + spawn + ".X");
		double y = OneVsOne.getPlugin().getConfig().getDouble("Spawn" + spawn + ".Y");
		double z = OneVsOne.getPlugin().getConfig().getDouble("Spawn" + spawn + ".Z");
		float pitch = OneVsOne.getPlugin().getConfig().getLong("Spawn" + spawn + ".Pitch");
		float yaw = OneVsOne.getPlugin().getConfig().getLong("Spawn" + spawn + ".Yaw");
		
		return new Location(Bukkit.getWorld(worldname), x, y, z, pitch, yaw);
		
	}
	
	public void joinPlayer(Player player) { //adds a player to the arena and fires event
		if(playercount == 0) {
			player.teleport(spawn1);
		}
		if(playercount == 1) {
			player.teleport(spawn2);
		}
		
		players.add(player);
		playercount = players.size();
		
		PlayerJoinArenaEvent event = new PlayerJoinArenaEvent(this, player);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
	}
	
	public void removePlayer(Player player) {
		player.getInventory().clear();
		if(player.isOnline()) {
			player.teleport(lobby);
		}
		players.remove(player);
		playercount = players.size();
		
		PlayerLeaveArenaEvent event = new PlayerLeaveArenaEvent(this, player);
		Bukkit.getServer().getPluginManager().callEvent(event);
	
	}
	
	public void broadcastMessage(String message) {
		for(Player player : players) {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
			player.sendMessage(message);
		}
	}
	
	public static void deleteAndUnregisterArena(Arena arena) {	 //teleports players to Lobby and destroy the arena and unregister it and fire a Event
		World arenaworld = Bukkit.getWorld(arena.getArenaName());
		if(arenaworld == null) {
			return;
		}
		for(Player player : arenaworld.getPlayers()) {
			arena.removePlayer(player);
			player.sendMessage(Messages.PREFIX.getString() + Messages.TELEPORTTOLOBBY.getString());
			Bukkit.getServer().getPluginManager().callEvent(new PlayerLeaveArenaEvent(arena, player));
		}
		worldmanager.deleteWorld(arenaworld.getName());
		
		ArrayList<Arena> arenalist = (ArrayList<Arena>) OneVsOne.getArena();
		arenalist.remove(arena);
		OneVsOne.setArena(arenalist);
		
	}
	
	public static Arena createAndRegisterArena() { //create a arena (Name based on the amout of arenas) and register it in the OneVsOne Class
		Arena arena = new Arena(OneVsOne.getPlugin().getConfig().getString("Arenaworld"));
		OneVsOne.getArena().add(arena);
		return arena;
	}
	
	
	public static Arena getArena(@NotNull String arenaname) {
		for(Arena forlooparena : OneVsOne.getArena()) {
			if(forlooparena.getArenaName().equalsIgnoreCase(arenaname)) {
				return forlooparena;
			}
			else {
				continue;
			}
		}
		return null;
	}
	
	public int getPlayerCount() {
		return playercount;
	}
	
	public String getArenaUuid() {
		return arenaUuid;
	}
	
	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		GameStateChangeEvent event = new GameStateChangeEvent(this, this.gameState, gameState);
		Bukkit.getPluginManager().callEvent(event);
		this.gameState = gameState;
	}
	
	public String getArenaName() {
		return worldname;
	}

	public Collection<Player> getPlayers() {
		return players;
	}

	
}
