package com.github.gamedipoxx.oneVsOne.listener;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.github.gamedipoxx.oneVsOne.Messages;
import com.github.gamedipoxx.oneVsOne.OneVsOne;
import com.github.gamedipoxx.oneVsOne.arena.Arena;
import com.github.gamedipoxx.oneVsOne.arena.GameState;
import com.github.gamedipoxx.oneVsOne.arena.ScheduledArenaDelete;
import com.github.gamedipoxx.oneVsOne.events.GameStateChangeEvent;
import com.github.gamedipoxx.oneVsOne.events.PlayerJoinArenaEvent;
import com.github.gamedipoxx.oneVsOne.events.PlayerLeaveArenaEvent;

public class ArenaManager implements Listener{
	
	@EventHandler
	public void playerJoinArenaEvent(PlayerJoinArenaEvent event) {
		Arena arena = event.getArena();
		//first Player join
		if(arena.getPlayerCount() == 1 && arena.getGameState() == GameState.WAITING) {
			arena.setGameState(GameState.STARTING);
		}
		//second player join
		if(arena.getPlayerCount() == 2 && arena.getGameState() == GameState.STARTING) {
			arena.setGameState(GameState.INGAME);
		}
	}
	@EventHandler
	public void playerLeftArenaEvent(PlayerLeaveArenaEvent event) {
		Arena arena = event.getArena();
		//Dont wanna wait anymore
		if(arena.getPlayerCount() == 1 && arena.getGameState() == GameState.STARTING) {
			arena.setGameState(GameState.WAITING);
			return;
		}
		else if(arena.getPlayerCount() == 0 && arena.getGameState() == GameState.STARTING) {
			arena.setGameState(GameState.WAITING);
		}
		//RageQuit
		if(arena.getGameState() == GameState.INGAME) {
			arena.setGameState(GameState.ENDING);
		}
		
	}
	@EventHandler
	public void onGameStateChangeEvent(GameStateChangeEvent event) {
		Arena arena = event.getArena();
		GameState before = event.getBefore();
		GameState after = event.getAfter();
		
		if(before == GameState.WAITING && after == GameState.STARTING) {
			arena.broadcastMessage(Messages.PREFIX.getString() + Messages.WAITINGFORMOREPLAYER.getString());
		}
		if(before == GameState.STARTING && after == GameState.INGAME) {
			arena.broadcastMessage(Messages.PREFIX.getString() + Messages.STARTINGGAME.getString());
			giveInv(arena);
			for(Player player : arena.getPlayers()) {
				player.sendTitle(Messages.STARTTITLE.getString(), Messages.STARTSUBTITLE.getString(), 10, 40, 10);
				player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 0.5F, 1F);
			}
		}
		if(before == GameState.INGAME && after == GameState.ENDING) {
			for(Player player : arena.getPlayers()) {
				player.getInventory().clear();
				player.setHealth(20);
				player.setFoodLevel(20);
			}
			new ScheduledArenaDelete(arena);
		}
		
		
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = (Player) event.getEntity();
		for(Arena arena : OneVsOne.getArena()) {
			for(Player arenaplayer : arena.getPlayers()) {
				if(arenaplayer == player) {
					event.setDeathMessage(" ");
					event.setKeepInventory(true);
					event.setDroppedExp(0);
					arena.setGameState(GameState.ENDING);
					arena.broadcastMessage(Messages.PREFIX.getString() + player.getDisplayName() + " " + Messages.PLAYERDIED.getString());
					for(Player winPlayer : arena.getPlayers()) {
						if(winPlayer != player) {
							arena.broadcastMessage(Messages.PREFIX.getString() + winPlayer.getDisplayName() + " " + Messages.PLAYERWIN.getString());
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player quitPlayer = event.getPlayer();
		event.setQuitMessage(" ");
		for(Arena arena : OneVsOne.getArena()) {
			for(Player player : arena.getPlayers()) {
				if(quitPlayer == player) {
					arena.broadcastMessage(Messages.PREFIX.getString() + quitPlayer.getDisplayName() + " " + Messages.PLAYERLEAVEARENA.getString());
					arena.setGameState(GameState.ENDING);
				}
			}
		}
	}
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
	@EventHandler
	public void onToolDamageEvent(PlayerItemDamageEvent event) {
		event.setCancelled(true);
	}
	@EventHandler
	public void onPlayerPickUpItem(EntityPickupItemEvent event) {
		if(event.getEntity() instanceof Player) {
			event.setCancelled(true);
		}
	}
	
	//TODO Multiple Kits
	private void giveInv(Arena arena) {
		for(Player player : arena.getPlayers()) {
			player.getInventory().clear();
			player.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
			player.getInventory().setItem(1, new ItemStack(Material.BOW));
			player.getInventory().setItem(2, new ItemStack(Material.FISHING_ROD));
			player.getInventory().setItem(8, new ItemStack(Material.COOKED_BEEF, 32));
			player.getInventory().setItem(7, new ItemStack(Material.ARROW, 10));
			player.getInventory().setItem(100, new ItemStack(Material.LEATHER_BOOTS));
			player.getInventory().setItem(101, new ItemStack(Material.LEATHER_LEGGINGS));
			player.getInventory().setItem(102, new ItemStack(Material.LEATHER_CHESTPLATE));
			player.getInventory().setItem(103, new ItemStack(Material.LEATHER_HELMET));
		}
	}
}



//cases:
//Kein Bock auf warten -> Starting -> Strting|Waiting
//RageQuit -> Ingame -> Ending
//Game zu ende -> Ending -> Ending
