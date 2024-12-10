package barbosatk.minigames.Menus.MiniGameMenus;

import barbosatk.minigames.Config.Contants.MGConstants;
import barbosatk.minigames.Enums.GameStatus;
import barbosatk.minigames.Enums.GameType;
import barbosatk.minigames.Enums.LobbyActionType;
import barbosatk.minigames.Enums.MGItems;
import barbosatk.minigames.Games.MiniGame;
import barbosatk.minigames.Helper.MGSupport;
import barbosatk.minigames.Menus.MiniGameMenus.InOutMenus.OutGameMenu;
import barbosatk.minigames.MiniGames;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LobbyMenu implements LobbyHandler, Listener {
    private MiniGame miniGame;
    private final LobbyItem lobbyItem;
    private final int inventorySize = MGConstants.LOBBY_MENU_INVENTORY_SIZE;

    private HashMap<Player, Inventory> playerTemporaryOutGameInventory = new HashMap<>();

    // --------------- CONSTRUCTOR -------------
    public LobbyMenu(Player owner, GameType gameType) {
        this.setNewMiniGame(owner, gameType);
        this.lobbyItem = new LobbyItem(gameType, this.miniGame);
    }

    private void setNewMiniGame(Player owner, GameType gameType) {
        try {
            this.miniGame = gameType.getMiniGame().getClass().getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("[ERROR] Something happened while trying to create a new instance of MiniGame!\n" + e);
        }
        this.miniGame.getMgSettings().setCapacity(MGConstants.LOBBY_QUANTITY_LIMIT);
        this.miniGame.getMgSettings().setOwner(owner);
        this.miniGame.getMgSettings().setGameType(gameType);
        this.miniGame.insertPlayerNotUse(owner, this);
        this.miniGame.loadMGSettingsInventory();
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.getPlugin(MiniGames.class));
    }

    // Getters & Setters
    public MiniGame getMiniGame() {
        return miniGame;
    }

    public void setMiniGame(MiniGame miniGame) {
        this.miniGame = miniGame;
    }

    public LobbyItem getLobbyItem() {
        return lobbyItem;
    }

    public int getInventorySize() {
        return inventorySize;
    }

    public HashMap<Player, Inventory> getPlayerTemporaryOutGameInventory() {
        return playerTemporaryOutGameInventory;
    }

    public void setPlayerTemporaryOutGameInventory(HashMap<Player, Inventory> playerTemporaryOutGameInventory) {
        this.playerTemporaryOutGameInventory = playerTemporaryOutGameInventory;
    }

    // -------------------------------------------------

    // LobbyHandler Methods
    @Override
    public void joinLobby(Player player) {
        this.miniGame.insertPlayerNotUse(player, this);
        this.lobbyItem.setPlayerCapacity(this.miniGame.getLobbyPlayersSize());
        if (!this.getMiniGame().getMgSettings().getGameStatus().equals(GameStatus.GAME_RUNNING))
            this.miniGame.joinLeaveLobbyMessage(LobbyActionType.JOIN, player.getName());
        this.loadLobbyPlayers();
    }

    @Override
    public void leaveLobby(Player player) {
        if (!this.getMiniGame().getMgSettings().getGameStatus().equals(GameStatus.GAME_RUNNING))
            this.miniGame.joinLeaveLobbyMessage(LobbyActionType.LEAVE, player.getName());
        this.miniGame.removePlayerNotUse(player);
        this.lobbyItem.setPlayerCapacity(this.miniGame.getLobbyPlayersSize());
        this.loadLobbyPlayers();
    }

    // -------------------------------------------------
    public void loadLobbyPlayers() {
        for (Player player : this.playerTemporaryOutGameInventory.keySet()) {
            int currentSlot = 18;
            Inventory inventory = this.playerTemporaryOutGameInventory.get(player);
            this.clearAllSkulls(inventory, this.miniGame.getLobbyPlayers().size());
            for (Player playerToMakeSkull : this.miniGame.getLobbyPlayers()) {
                if (currentSlot > inventorySize)
                    break;
                inventory.setItem(currentSlot, MiniGames.totalPlayersOnServer.getPlayerData(playerToMakeSkull).getSkull());
                currentSlot++;
            }
        }
    }

    private void clearAllSkulls(Inventory inventory, int size) {
        for (int i = 18; i <= (18 + size); i++) {
            inventory.clear(i);
        }
    }

    public void buildMenu(Player player) {
        if (this.miniGame.getLobbyPlayers().contains(player) && this.playerTemporaryOutGameInventory.containsKey(player)) {
            player.openInventory(this.playerTemporaryOutGameInventory.get(player));
            return;
        }
        new OutGameMenu().buildMenu(this, player);
    }

    public void redirectAllPlayers() {
        Inventory inventory = MiniGames.preLobbyManager.findPreLobbyMenu(this.miniGame.getMgSettings().getGameType()).getInventory();
        List<Player> playersTemp = new ArrayList<>(this.playerTemporaryOutGameInventory.keySet());
        for (Player player : playersTemp) {
            this.getMiniGame().removePlayerNotUse(player);
            player.openInventory(inventory);
        }
    }

    public void notifyPlayersRemoveLobby(Player player) {
        this.miniGame.sendMessageToLobby(MGConstants.gameLobbyDeletedMessage(player.getName(), this.miniGame.getMgSettings().getGameType().getGameName()));
        this.removeLobby();
    }

    public void removeLobby() {
        if (this.miniGame.getMgSettings().getGameStatus().equals(GameStatus.GAME_INITIALIZATION) && startCounter > 0)
            this.enableCounter(true);
        MiniGames.preLobbyManager.findPreLobbyMenu(this.miniGame.getMgSettings().getGameType()).deleteLobby(this);
        HandlerList.unregisterAll(this);
    }

    /* START COUNTER */
    private int startCounter, startTimer;

    public void setStartCounter(int startCounter) {
        this.startCounter = startCounter;
    }

    public void enableCounter(boolean cancel) {
        if (!cancel) {
            startTimer = 10;
            this.miniGame.sendMessageToLobby(MGConstants.gameCounterInitializedMessage(this.miniGame.getMgSettings().getOwner().getName(), this.miniGame.getMgSettings().getGameType().getGameName()));
            MGSupport.playSoundForPlayers(miniGame.getLobbyPlayers(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 1f);
            this.updateLobbyItemGameStatus(GameStatus.COUNTDOWN_TO_START);
            startCounter = Bukkit.getScheduler().scheduleSyncRepeatingTask(MiniGames.getPlugin(MiniGames.class), new Runnable() {
                @Override
                public void run() {
                    if (startTimer >= 1) {
                        if (startTimer <= 5) {
                            getMiniGame().sendMessageToLobby(MGConstants.chatTagSuccess(miniGame.getMgSettings().getGameType().getGameName()) + "O jogo começa dentro de §a" + startTimer + "§6 segundos");
                            MGSupport.playSoundForPlayers(miniGame.getLobbyPlayers(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 1f);
                        }
                    } else {
                        Bukkit.getScheduler().cancelTask(startCounter);
                        updateLobbyItemGameStatus(GameStatus.GAME_INITIALIZATION);
                        // -------------------------------------------
                        Bukkit.getScheduler().scheduleSyncDelayedTask(MiniGames.getPlugin(MiniGames.class), () -> {
                            getMiniGame().closePlayersInventories();
                            getMiniGame().defaultStartGame();
                            addItemInsideGameMenu(8, MGItems.MGItem.SPECTATE_GAME);
                        }, 20L);
                    }
                    startTimer--;
                }
            }, 0L, 20L);
        } else {
            if (startCounter > 0) {
                getMiniGame().sendMessageToLobby(MGConstants.gameCounterCanceledMessage(getMiniGame().getMgSettings().getOwner().getName(), this.miniGame.getMgSettings().getGameType().getGameName()));
                Bukkit.getScheduler().cancelTask(startCounter);
                this.setStartCounter(0);
            }
        }
    }

    private void addItemInsideGameMenu(int slot, MGItems.MGItem mgItem) {
        for (Inventory inventory : this.playerTemporaryOutGameInventory.values()) {
            inventory.setItem(slot, mgItem.getItemStack());
        }
    }

    // Used to update both <MiniGame> and <LobbyItem> GameStatus.
    public void updateLobbyItemGameStatus(GameStatus gameStatus) {
        System.out.println("Game Status updated to " + gameStatus);
        getMiniGame().getMgSettings().setGameStatus(gameStatus);
        this.lobbyItem.setGameStatus(gameStatus);
    }

    private void swapJoinLeaveItem(Player player, boolean leave) {
        Inventory inventory = this.playerTemporaryOutGameInventory.get(player);
        if (leave) {
            inventory.remove(MGItems.MGItem.LEAVE_LOBBY.getItemStack());
            inventory.setItem(4, MGItems.MGItem.ADD_LOBBY.getItemStack());
            inventory.remove(MGItems.MGItem.GAME_SETTINGS.getItemStack());
            return;
        }
        inventory.remove(MGItems.MGItem.ADD_LOBBY.getItemStack());
        inventory.setItem(4, MGItems.MGItem.LEAVE_LOBBY.getItemStack());
        inventory.setItem(7, MGItems.MGItem.GAME_SETTINGS.getItemStack());
    }

    public void redirectOnOutGameLobbyDeleted(Player player) {
        this.updateLobbyItemGameStatus(GameStatus.ENDING_GAME);
        this.notifyPlayersRemoveLobby(player);
        this.redirectAllPlayers();
    }

    /* -------------------------------------------------------------------- */
    // EVENTS FOR THE GAME MENU

    @EventHandler
    private void onPlayerInventoryClick(InventoryClickEvent ice) {
        Player player = (Player) ice.getWhoClicked();
        MGSupport.denyItemClickIfMenusOpened(ice, this.playerTemporaryOutGameInventory.get(player));
        if (ice.getClickedInventory() == this.playerTemporaryOutGameInventory.get(player) && ice.getCurrentItem() != null) {
            ice.setCancelled(true);
            MGItems.MGItem item = MGItems.whatMenuItemIs(ice.getCurrentItem());
            if (item == null)
                return;

            // Verification layer
            if (!this.miniGame.getMgSettings().getGameStatus().isAllowToClickLobbyItems() && !item.getItemStack().isSimilar(MGItems.MGItem.SPECTATE_GAME.getItemStack())) {
                if (!this.getMiniGame().getLobbyPlayers().contains(player) && !item.getItemStack().isSimilar(MGItems.MGItem.BACK_BUTTON.getItemStack())) {
                    player.sendMessage("§6[§c§l" + this.miniGame.getMgSettings().getGameType().getGameName() + "§6] O jogo já começou. A Ação foi bloqueada.");
                    return;
                }
            }

            switch (item) {
                case DELETE_LOBBY:
                    this.redirectOnOutGameLobbyDeleted(player);
                    break;
                case BACK_BUTTON:
                    player.openInventory(MiniGames.preLobbyManager.findPreLobbyMenu(this.miniGame.getMgSettings().getGameType()).getInventory());
                    break;
                case START_GAME_BUTTON:
                    // starts the counter to start the game
                    // :TODO uncomment the code below once the plugin is finished
                    /*if (this.miniGame.getLobbyPlayers().size() < 2) {
                        player.sendMessage(MGConstants.chatTagUnsuccessful(this.miniGame.getMgSettings().getGameType().getGameName()) + "§6São necessários pelo menos 2 jogadores para começar um jogo.");
                        return;
                    }*/
                    this.enableCounter(false);
                    ice.getClickedInventory().remove(MGItems.MGItem.START_GAME_BUTTON.getItemStack());
                    ice.getClickedInventory().setItem(5, MGItems.MGItem.CANCEL_GAME_BUTTON.getItemStack());
                    break;
                case CANCEL_GAME_BUTTON:
                    this.enableCounter(true);
                    this.updateLobbyItemGameStatus(GameStatus.AWAITING_FOR_PLAYERS);
                    // cancel the counter to start the game
                    ice.getClickedInventory().remove(MGItems.MGItem.CANCEL_GAME_BUTTON.getItemStack());
                    ice.getClickedInventory().setItem(5, MGItems.MGItem.START_GAME_BUTTON.getItemStack());
                    break;
                case ADD_LOBBY:
                    if (!this.miniGame.getMgSettings().getGameStatus().usersCanJoin()) {
                        player.sendMessage(MGConstants.lobbyGameAlreadyStarted(this.miniGame.getMgSettings().getGameType().getGameName()));
                        return;
                    }
                    this.joinLobby(player);
                    this.swapJoinLeaveItem(player, false);
                    break;
                case LEAVE_LOBBY:
                    this.leaveLobby(player);
                    this.swapJoinLeaveItem(player, true);
                    break;
                case SPECTATE_GAME:
                    if (this.miniGame.getMgSettings().getGameStatus().spectatorsAllowed()) {
                        player.setGameMode(GameMode.SPECTATOR);
                        this.miniGame.teleportPlayer(player, this.miniGame.getMiniGamePlayers().get(0).getLocation());
                        this.miniGame.getMiniGameSpectators().add(player);
                    }
                    break;
                case GAME_SETTINGS:
                    this.miniGame.buildSettingsMenu(this.miniGame.getMgSettingsInventory());
                    player.openInventory(this.miniGame.getMgSettingsInventory());
                    break;
            }
        }
    }

    @EventHandler
    private void ownerOpenSettings(InventoryClickEvent ice) {
        MGSupport.denyItemClickIfMenusOpened(ice, this.miniGame.getMgSettingsInventory());
        if (ice.getClickedInventory() != this.miniGame.getMgSettingsInventory())
            return;
        if (ice.getCurrentItem() == null)
            return;

        ice.setCancelled(true);
        Player player = (Player) ice.getWhoClicked();

        if (ice.getCurrentItem().isSimilar(MGItems.MGItem.BACK_BUTTON.getItemStack())) {
            player.openInventory(this.playerTemporaryOutGameInventory.get(player));
            return;
        }

        if (!player.equals(this.miniGame.getMgSettings().getOwner())) {
            player.sendMessage(MGConstants.chatTagUnsuccessful(this.miniGame.getMgSettings().getGameType().getGameName()) + "Apenas o owner pode modificar as definições.");
            return;
        }

        this.miniGame.settingsMenuEventItemClick(ice.getCurrentItem());

    }

    @EventHandler
    private void playerClose(InventoryCloseEvent ice) {
        Player player = (Player) ice.getPlayer();
        if (ice.getInventory() == this.playerTemporaryOutGameInventory.get(player) && !this.miniGame.getLobbyPlayers().contains(player)) {
            this.playerTemporaryOutGameInventory.remove(player, this.playerTemporaryOutGameInventory.get(player));
            System.out.println("[i] removed '" + player.getName() + "' inventory");
        }
    }

    @EventHandler
    private void playerLeaveGame(PlayerQuitEvent pqe) {
        Player player = pqe.getPlayer();
        if (this.playerTemporaryOutGameInventory.containsKey(player))
            this.playerTemporaryOutGameInventory.remove(player, this.playerTemporaryOutGameInventory.get(player));

        if (!this.miniGame.getMgSettings().getGameStatus().equals(GameStatus.GAME_RUNNING) && this.miniGame.getMgSettings().getOwner().equals(player)) {
            // :TODO needs a teleport for players if they already got inside game
            this.redirectOnOutGameLobbyDeleted(player);
        }

        if (this.miniGame.getLobbyPlayers().contains(player))
            this.leaveLobby(player);

        Bukkit.getScheduler().scheduleSyncDelayedTask(MiniGames.getPlugin(MiniGames.class), this::checkForRemoveLobbyOwner, 20L);

    }

    public void checkForRemoveLobbyOwner() {
        if (this.miniGame.getMgSettings().getOwner() != null)
            return;

        this.miniGame.getMgSettings().changeNewLobbyOwnerItems(this, this.miniGame.getMgSettings().getGameType().getGameName());
    }

    @EventHandler
    private void onPlayerCraftRecipes(CraftItemEvent cie) {
        // :TODO The verification has to be made through the recipe
        if (!this.getMiniGame().getLobbyPlayers().contains((Player) cie.getWhoClicked()) && this.getMiniGame().getMgRecipes().hasItemResult(cie.getRecipe().getResult())) {
            cie.setCancelled(true);
            Player player = (Player) cie.getWhoClicked();
            cie.getWhoClicked().sendMessage(MGConstants.chatTagUnsuccessful(MiniGames.totalPlayersOnServer.getPlayerData(player).getGameType().getGameName()) + "Não tens permissão para craftar o item");
        }
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent pme) {
        if (!this.miniGame.getMgSettings().getGameStatus().isAllowPlayerToMove()) {
            if (!MiniGames.totalPlayersOnServer.getPlayerData(pme.getPlayer()).isInGame())
                pme.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c§lMovimento bloqueado. Há um mundo a ser gerado..."));
            pme.setCancelled(true);
        }
    }

}