package barbosatk.minigames.Games;

import barbosatk.minigames.Config.Contants.MGConstants;
import barbosatk.minigames.Enums.GameStatus;
import barbosatk.minigames.Enums.GameType;
import barbosatk.minigames.Enums.LobbyActionType;
import barbosatk.minigames.Enums.MGItems;
import barbosatk.minigames.Helper.MGSupport;
import barbosatk.minigames.MGDrivers.*;
import barbosatk.minigames.Menus.MiniGameMenus.InOutMenus.InGameMenu;
import barbosatk.minigames.Menus.MiniGameMenus.InOutMenus.SpectatorMenu;
import barbosatk.minigames.Menus.MiniGameMenus.LobbyMenu;
import barbosatk.minigames.MiniGames;
import barbosatk.minigames.Player.PlayerData;

import barbosatk.minigames.Player.PlayerWorldInfo;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class MiniGame {

    private final MGWorlds mgWorlds;
    private final MGRecipes mgRecipes;
    private final MGSettings mgSettings;
    private final InGameMenu inGameMenu;
    private final SpectatorMenu spectatorMenu;
    private final MGScoreboards mgScoreboards;

    // events handler
    private Inventory mgSettingsInventory;
    private MGEvents mgEvents;

    private final List<Player> lobbyPlayers = new ArrayList<>();
    private final List<Player> miniGamePlayers = new ArrayList<>();
    private final List<Player> miniGameSpectators = new ArrayList<>();


    public MiniGame() {
        this.mgWorlds = new MGWorlds();
        this.mgRecipes = new MGRecipes();
        this.mgSettings = new MGSettings();
        this.inGameMenu = new InGameMenu();
        this.spectatorMenu = new SpectatorMenu();
        this.mgEvents = new MGEvents(this);
        this.mgScoreboards = new MGScoreboards();
    }

    // Getters & Setters
    public MGWorlds getMgWorlds() {
        return mgWorlds;
    }

    public MGRecipes getMgRecipes() {
        return mgRecipes;
    }

    public MGSettings getMgSettings() {
        return mgSettings;
    }

    public Inventory getMgSettingsInventory() {
        return mgSettingsInventory;
    }

    public List<Player> getMiniGamePlayers() {
        return miniGamePlayers;
    }

    public int getLobbyPlayersSize() {
        return lobbyPlayers.size();
    }

    public int getMiniGamePlayersSize() {
        return miniGamePlayers.size();
    }

    public List<Player> getLobbyPlayers() {
        return lobbyPlayers;
    }

    public List<Player> getMiniGameSpectators() {
        return miniGameSpectators;
    }

    public InGameMenu getInGameMenu() {
        return inGameMenu;
    }

    public SpectatorMenu getSpectatorMenu() {
        return spectatorMenu;
    }

    public Inventory getInGameInventory() {
        return inGameMenu.getInGameInventory();
    }

    public List<Player> getAllInGameAndSpecPlayers() {
        return Stream.concat(miniGamePlayers.stream(), miniGameSpectators.stream()).collect(Collectors.toList());
    }

    // :TODO 1 - player isn't removed from the inGamePlayers when he dcs.
    // :TODO 2 - When player does not belong to the lobby the OutGameMenu does not update once the game finishes.

    public void defaultStartGame() {
        // Here should be some default logic for the startGame
        this.mgWorlds.loadWorlds(this);
        this.mgRecipes.loadRecipes(() -> insertRecipes(this.mgRecipes.getMgRecipes()));
        this.mgScoreboards.initScoreBoard(() -> this.renderScoreboard(this.mgScoreboards.getScoreboard()));
        this.beforeStartGame();
        this.miniGamePlayers.addAll(this.lobbyPlayers);
        this.setInGamePlayerStatus(true);
        Bukkit.getScheduler().scheduleSyncDelayedTask(MiniGames.getPlugin(MiniGames.class), () -> {
            if (this.emptyGameCheck())
                return;
            this.createMenus();
            this.registerAllListeners();
            this.countInsideGameToStart();
        }, 20L);
    }

    private void registerAllListeners() {
        this.mgEvents.registerListener();
    }

    private boolean emptyGameCheck() {
        if (this.getMiniGamePlayersSize() < MGConstants.MINIMUM_REQUIRED_PLAYERS) {
            lobbyPlayers.forEach(player -> player.sendTitle("§c  Jogo Terminado  ", "§fNão há jogadores suficientes", 20, 65, 20));
            this.sendMessageToLobby("");
            this.sendMessageToLobby(MGConstants.chatTagUnsuccessful(this.getMgSettings().getGameType().getGameName()) + "§cJogo Terminado. §fNão há jogadores suficientes.");
            this.sendMessageToLobby("");
            this.defaultEndGame();
            return true;
        }
        return false;
    }

    private void createMenus() {
        this.inGameMenu.createInGameMenu(this);
        this.spectatorMenu.createSpectatorMenu(this);
    }

    private int countDownTask, countDownTimer;

    private void countInsideGameToStart() {
        countDownTimer = 10;
        countDownTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(MiniGames.getPlugin(MiniGames.class), new Runnable() {
            @Override
            public void run() {
                if (countDownTimer >= 1) {
                    MGSupport.showTitleForPlayers(lobbyPlayers, "§6" + countDownTimer, "", 20, 100, 20);
                    countDownTimer--;
                } else {
                    Bukkit.getScheduler().cancelTask(countDownTask);
                    MGSupport.showTitleForPlayers(lobbyPlayers, "§aO Jogo começou!", "§6Boa sorte", 20, 40, 20);

                    MiniGames.preLobbyManager.findPreLobbyMenu(mgSettings.getGameType()).getGameTypeLobbiesList().stream()
                            .filter(lobbyMenu -> lobbyMenu.getMiniGame().equals(MiniGame.this))
                            .findFirst().get().updateLobbyItemGameStatus(GameStatus.GAME_RUNNING);

                    afterGameStartedTasks();
                }
                MGSupport.playSoundForPlayers(lobbyPlayers, Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1f, 1f);
            }

        }, 60, 20);
    }

    public void defaultEndGame() {
        this.endGame();
        LobbyMenu lobbyMenu = MiniGames.preLobbyManager.findPreLobbyMenu(this.getMgSettings().getGameType()).getGameTypeLobbiesList().stream()
                .filter(lobbyMenuTemp -> lobbyMenuTemp.getMiniGame().equals(this)).findFirst().get();
        lobbyMenu.updateLobbyItemGameStatus(GameStatus.ENDING_GAME);
        // Teleport players out of the game including spectators

        this.unregisterScoreboardPlayerList();

        if (this.lobbyPlayers.size() < 1)
            lobbyMenu.removeLobby();
        else {
            this.teleportAllPlayers();

            // Give players their playerWorldInfo content back.
            this.setInGamePlayerStatus(false);

            this.checkForNewLobbyOwner();

            this.getLobbyPlayers().forEach(player -> {
                lobbyMenu.getPlayerTemporaryOutGameInventory().remove(player);
                lobbyMenu.buildMenu(player);
            });
        }

        this.unregisterAllEvents();

        this.mgRecipes.clearRecipes();
        this.resetMiniGamePlayersLists();

        // unload default worlds here
        Bukkit.getScheduler().scheduleSyncDelayedTask(MiniGames.getPlugin(MiniGames.class), this.mgWorlds::unloadWorlds, 20L);

        lobbyMenu.updateLobbyItemGameStatus(GameStatus.AWAITING_FOR_PLAYERS);
        System.out.println("[Task] EndGame finished successfully");
    }

    private void resetMiniGamePlayersLists() {
        this.miniGamePlayers.clear();
        this.miniGameSpectators.clear();
    }

    private void checkForNewLobbyOwner() {
        if (this.getMgSettings().getOwner() != null)
            return;

        Player newOwner = this.lobbyPlayers.get(0);
        this.mgSettings.setOwner(newOwner);
        LobbyMenu lobbyMenu = MiniGames.preLobbyManager.findPreLobbyMenu(this.mgSettings.getGameType()).findLobbyMenu(this);
        this.mgSettings.changeNewLobbyOwnerItems(lobbyMenu, newOwner.getName());

        this.sendMessageToLobby(MGConstants.chatTagSuccess(this.getMgSettings().getGameType().getGameName()) + "§eO jogador §a" + newOwner.getName() + "§e foi escolhido para novo owner da lobby!");
    }

    private void unregisterAllEvents() {
        this.inGameMenu.unregisterInGameMenu();
        this.spectatorMenu.unregisterSpecMenu();
        this.mgEvents.unregisterListener();
        unregisterAllListeners();
    }

    private void setInGamePlayerStatus(boolean inGame) {
        this.lobbyPlayers.forEach(player -> MiniGames.totalPlayersOnServer.getPlayerData(player).setInGame(inGame));
    }

    public void loadMGSettingsInventory() {
        this.mgSettingsInventory = Bukkit.createInventory(null, MGConstants.LOBBY_MG_SETTINGS_INVENTORY_SIZE, MGConstants.mgInventoryTitle(this.mgSettings.getGameType()));
        this.mgSettingsInventory.setItem(0, MGItems.MGItem.BACK_BUTTON.getItemStack());
    }

    // abstract methods
    public abstract void afterGameStartedTasks();

    public abstract void insertRecipes(List<ShapedRecipe> mgRecipes);

    public abstract void customizeWorld(World world, World netherWorld);

    public abstract void beforeStartGame();

    public abstract void endGame();

    public abstract void buildSettingsMenu(Inventory mgSettingsInventory);

    public abstract void settingsMenuEventItemClick(ItemStack itemClicked);

    public abstract void unregisterAllListeners();

    public abstract void renderScoreboard(Scoreboard board);

    public abstract void unregisterScoreboardPlayerList();

    // ----------------------------------------------------

    public void insertPlayerNotUse(Player player, LobbyMenu lobbyMenu) {
        MGSupport.removeFromLobby(player);
        this.lobbyPlayers.add(player);
        PlayerData playerData = MiniGames.totalPlayersOnServer.getPlayerData(player);
        playerData.setLobbyInv(lobbyMenu);
        playerData.setGameType(this.mgSettings.getGameType());
        playerData.setChatChannelToken(lobbyMenu.getMiniGame().getMgSettings().getChannelToken());
    }

    public void removePlayerNotUse(Player player) {
        this.lobbyPlayers.remove(player);
        this.miniGamePlayers.remove(player);
        this.miniGameSpectators.remove(player);
        if (this.getMgSettings().getOwner() == player)
            this.getMgSettings().setOwner(null);
        PlayerData playerData = MiniGames.totalPlayersOnServer.getPlayerData(player);
        playerData.setLobbyInv(null);
        playerData.setGameType(GameType.GLOBAL);
        playerData.setChatChannelToken("");
    }

    // send messages to all players inside the same lobby
    public void sendMessageToLobby(String message) {
        this.getLobbyPlayers().forEach(player -> player.sendMessage(message));
    }

    public void joinLeaveLobbyMessage(LobbyActionType lobbyActionType, String playerName) {
        if (lobbyActionType.equals(LobbyActionType.JOIN)) {
            this.sendMessageToLobby(MGConstants.chatTagSuccess(this.mgSettings.getGameType().getGameName()) + "O jogador " + ChatColor.GREEN + playerName + ChatColor.GOLD + " juntou-se à Lobby");
            return;
        }
        this.sendMessageToLobby(MGConstants.chatTagUnsuccessful(this.mgSettings.getGameType().getGameName()) + "O jogador " + ChatColor.RED + playerName + ChatColor.GOLD + " saiu da Lobby");
    }

    public void closePlayersInventories() {
        lobbyPlayers.forEach(HumanEntity::closeInventory);
    }

    public boolean eventIsAllowedForPlayer(Player player) {
        return this.getMiniGamePlayers().contains(player);
    }

    // MiniGame Help Functions

    // remove everything from player
    public void clearPlayerContents(Player p) {
        p.setExp(0);
        p.setLevel(0);
        p.setFoodLevel(20);
        p.setHealth(20);
        p.getInventory().clear();
        p.getEnderChest().clear();
        p.getInventory().setArmorContents(null);
        for (PotionEffect pe : p.getActivePotionEffects()) {
            p.removePotionEffect(pe.getType());
        }

        // Set player default Scoreboards
        p.setScoreboard(MGSupport.worldSideBarSB());
    }

    public void teleportPlayer(Player player, Location location) {
        MiniGames.playerInfoManager.addContent(player, new PlayerWorldInfo(player));
        this.clearPlayerContents(player);
        player.teleport(location);
        player.setScoreboard(this.mgScoreboards.getScoreboard());
        if (this.lobbyPlayers.contains(player))
            player.sendTitle("§6 O jogo começa em...", "A aguardar pelos jogadores", 20, 600, 20);
    }

    protected void teleportAllPlayers() {
        if (this.mgSettings.getGameStatus().equals(GameStatus.ENDING_GAME)) {
            this.teleportWhenEndGame();
            return;
        }
        this.teleportWhenStartingGame();
    }

    protected Objective registerScoreboardObjectives(Scoreboard board, String objectiveName, String presentingName) {
        return board.getObjective(objectiveName) != null ? board.getObjective(objectiveName) : board.registerNewObjective(objectiveName, presentingName, presentingName);
    }

    // Auxiliary teleport methods
    private void teleportWhenEndGame() {
        List<Player> playersPlusSpectators = this.getAllInGameAndSpecPlayers();
        playersPlusSpectators.forEach(player -> {
            this.clearPlayerContents(player);
            PlayerWorldInfo playerWorldInfo = MiniGames.playerInfoManager.getContent(player);
            player.teleport(playerWorldInfo.getLocation());
            playerWorldInfo.givePlayerContents(player);
        });
    }

    private void teleportWhenStartingGame() {
        this.lobbyPlayers.forEach(player -> {
            this.clearPlayerContents(player);
            player.teleport(this.mgWorlds.getMGWorld().getSpawnLocation());
        });
    }

}
