package barbosatk.minigames.Player;

import barbosatk.minigames.Enums.GameStatus;
import barbosatk.minigames.Enums.MGItems;
import barbosatk.minigames.Helper.MGSupport;
import barbosatk.minigames.Menus.MGMenu;
import barbosatk.minigames.Menus.MiniGameMenus.LobbyMenu;
import barbosatk.minigames.MiniGames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;

import static org.bukkit.Bukkit.getServer;

public class PlayerMGMenuEvents implements Listener {

    public PlayerMGMenuEvents(Plugin plugin) {
        MiniGames.mgMenu = new MGMenu(plugin);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent pje) {
        MiniGames.totalPlayersOnServer.insertPlayer(pje.getPlayer());
        this.checkPlayerWorld(pje.getPlayer());
        this.onPlayerJoinCancelRemoveData(pje);

        // Set default ScoreBoards
        pje.getPlayer().setScoreboard(MGSupport.worldSideBarSB());
    }

    private void checkPlayerWorld(Player player) {
        if (!MiniGames.totalPlayersOnServer.getPlayerData(player).getChatChannelToken().isEmpty())
            return;
        if (!MiniGames.playerInfoManager.hasContent(player))
            return;

        PlayerWorldInfo playerWorldInfo = MiniGames.playerInfoManager.getContent(player);
        player.teleport(playerWorldInfo.getLocation());
        playerWorldInfo.givePlayerContents(player);
    }

    private void onPlayerJoinCancelRemoveData(PlayerJoinEvent pje) {
        PlayerData playerData = MiniGames.totalPlayersOnServer.getPlayerData(pje.getPlayer());
        int task = playerData.getDeleteDataTask();

        if (!Bukkit.getScheduler().isQueued(task))
            return;

        Bukkit.getScheduler().cancelTask(task);
        playerData.setDeleteDataTask(-1);
        System.out.println("[Task] The action to delete '" + playerData.getPlayer().getName() + "' data was canceled.");
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent pqe) {
        this.onPlayerLeaveRemoveData(pqe);
    }

    private void onPlayerLeaveRemoveData(PlayerQuitEvent pqe) {
        PlayerData playerData = MiniGames.totalPlayersOnServer.getPlayerData(pqe.getPlayer());
        int leaveEvent = Bukkit.getScheduler().scheduleSyncDelayedTask(MiniGames.getPlugin(MiniGames.class), () -> {
            MiniGames.totalPlayersOnServer.removePlayer(pqe.getPlayer());
            System.out.println("[Task] Data for player '" + pqe.getPlayer().getName() + "' was deleted!");
        }, 1800);
        playerData.setDeleteDataTask(leaveEvent);
        System.out.println("[i] Task to delete '" + pqe.getPlayer().getName() + "' data was initialized!");
        System.out.println(playerData.getDeleteDataTask());
    }

    @EventHandler
    private void onPlayerDeathMenuItem(PlayerDeathEvent pde) {
        PlayerData playerData = MiniGames.totalPlayersOnServer.getPlayerData(pde.getEntity());
        if (!playerData.isInGame()) {
            playerData.saveItemSlot();
        }
        pde.getDrops().removeIf(item -> item.isSimilar(MGItems.MGItem.MG_MENU.getItemStack()));
    }

    @EventHandler
    private void onRespawnMenuItem(PlayerRespawnEvent pre) {
        System.out.println("Slot: " + MiniGames.totalPlayersOnServer.getPlayerData(pre.getPlayer()).getMgItemSlot());
        pre.getPlayer().getInventory().setItem(MiniGames.totalPlayersOnServer.getPlayerData(pre.getPlayer()).getMgItemSlot(), MGItems.MGItem.MG_MENU.getItemStack());
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent pie) {
        if (pie.getAction() == Action.RIGHT_CLICK_BLOCK || pie.getAction() == Action.RIGHT_CLICK_AIR)
            if (pie.getItem() != null && pie.getItem().isSimilar(MGItems.MGItem.MG_MENU.getItemStack()))
                openMGMenu(pie.getPlayer());
    }

    private void openMGMenu(Player player) {
        LobbyMenu lobbyMenu = MGSupport.getPlayerLobby(player);
        if (lobbyMenu == null) {
            MiniGames.mgMenu.buildMGMenu(player);
            return;
        }

        GameStatus gameStatus = lobbyMenu.getMiniGame().getMgSettings().getGameStatus();
        switch (gameStatus) {
            case AWAITING_FOR_PLAYERS:
            case COUNTDOWN_TO_START:
                lobbyMenu.buildMenu(player);
                break;
            case GAME_INITIALIZATION:
            case ENDING_GAME:
                return;
            case GAME_RUNNING:
                player.openInventory(lobbyMenu.getMiniGame().getInGameInventory());
                break;
        }
    }

    private final DecimalFormat df2 = new DecimalFormat("#.##");

    @EventHandler
    private void onDamageTest(EntityDamageEvent evt) {
        if (evt.getEntityType().toString().equals("PLAYER")) {
            org.bukkit.entity.Entity e = evt.getEntity();
            double damage = evt.getFinalDamage() / 2;
            if (damage == 1) {
                getServer().broadcastMessage(ChatColor.GOLD + evt.getEntity().getName() + ChatColor.GREEN + " perdeu " + df2.format(damage) + " coração.");
            } else {
                getServer().broadcastMessage(ChatColor.GOLD + evt.getEntity().getName() + ChatColor.GREEN + " perdeu " + df2.format(damage) + " corações.");
            }
        }
    }

    @EventHandler
    private void onPlayerClickMiniGameMenu(InventoryClickEvent ice) {
       /*
         I Need to implement a command executioner instance that lets user set a key.
         The key is saved to a yaml file to not get lost.
         Get the key from the yaml file? and
         Check for KEY-CODE (XXXX-XXXX-XXXX) Validation through API
         If valid, let player open the MiniGameMenu
       */
        Player player = (Player) ice.getWhoClicked();
        if (ice.getClickedInventory() == player.getInventory()) {
            if (ice.getCurrentItem() != null && ice.getCurrentItem().isSimilar(MGItems.MGItem.MG_MENU.getItemStack())) {
                if (ice.getClick() == ClickType.RIGHT) {
                    openMGMenu(player);
                }
            }
        }
    }

    @EventHandler
    private void onPlayerDrop(PlayerDropItemEvent pdi) {
        if (pdi.getItemDrop().getItemStack().isSimilar(MGItems.MGItem.MG_MENU.getItemStack()))
            pdi.setCancelled(true);
    }

}
