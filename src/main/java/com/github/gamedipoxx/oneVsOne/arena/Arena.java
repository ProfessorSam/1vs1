package com.github.gamedipoxx.oneVsOne.arena;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.github.gamedipoxx.oneVsOne.Messages;
import com.github.gamedipoxx.oneVsOne.OneVsOne;
import com.github.gamedipoxx.oneVsOne.arena.GameState.GameStates;
import com.github.gamedipoxx.oneVsOne.events.PlayerJoinArenaEvent;
import com.onarandombox.MultiverseCore.api.MVWorldManager;

public class Arena {
	private UUID arenaUuid;
	private static Location lobby = new Location(Bukkit.getWorld(OneVsOne.getPlugin().getConfig().getString("Lobby.World")), OneVsOne.getPlugin().getConfig().getInt("Lobby.X"), OneVsOne.getPlugin().getConfig().getInt("Lobby.Y"), OneVsOne.getPlugin().getConfig().getInt("Lobby.Z"), OneVsOne.getPlugin().getConfig().getLong("Lobby.Pitch"), OneVsOne.getPlugin().getConfig().getLong("Lobby.Yaw"));
	private static MVWorldManager worldmanager;
	private String worldname;
	int playercount;
	private Location spawn1;
	private Location spawn2;
	private GameStates gameState;
	
	public Arena(@NotNull String arenaname) {
		UUID.randomUUID();
		if(Bukkit.getWorld(arenaname) == null) {
			OneVsOne.getPlugin().getLogger().warning("Cant find world " + arenaname);
			Bukkit.getServer().getPluginManager().disablePlugin(OneVsOne.getPlugin());
		}
		worldname = "Arena" + OneVsOne.getArena().size() + 1; //Name of the world and Arenaname
		worldmanager = OneVsOne.getMultiversecore().getMVWorldManager(); //set Multiverse wolrdmanager
		worldmanager.cloneWorld(arenaname, worldname); //clone world
		playercount = 0;
		
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
			return;
		}
		player.teleport(spawn2);
		
		PlayerJoinArenaEvent event = new PlayerJoinArenaEvent(this, player);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		
	}
	
	public static void deleteAndUnregisterArena(Arena arena) { //teleports players to Lobby and destroy the arena and unregister it
		World arenaworld = Bukkit.getWorld(arena.getArenaName());
		if(arenaworld == null) {
			return;
		}
		for(Player player : arenaworld.getPlayers()) {
			player.teleport(lobby);
			player.sendMessage(Messages.PREFIX.getString() + Messages.TELEPORTTOLOBBY.getString());
		}
		worldmanager.deleteWorld(arenaworld.getName());
		
	}
	
	public static Arena createAndRegisterArena() { //create a arena (Name based on the amout of arenas) and register it in the OneVsOne Class
		Arena arena = new Arena(OneVsOne.getPlugin().getConfig().getString("Arenaworld"));
		OneVsOne.getArena().add(arena);
		arena.setGameState(GameStates.WAITING);
		return arena;
	}
	
	public static Arena getArena(String arenaname) {
		Arena arena = null;
		for(Arena i : OneVsOne.getArena()) {
			if(i.getArenaName() == arenaname) {
				arena = i;
			}
			else {
				continue;
			}
		}
		return arena;
	}
	
	public int getPlayerCount() {
		return playercount;
	}
	
	public UUID getArenaUuid() {
		return arenaUuid;
	}
	
	public GameStates getGameState() {
		return gameState;
	}

	public void setGameState(GameStates gameState) {
		this.gameState = gameState;
	}
	
	public String getArenaName() {
		return worldname;
	}

	
}
