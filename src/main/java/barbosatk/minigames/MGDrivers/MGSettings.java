package barbosatk.minigames.MGDrivers;

import barbosatk.minigames.Enums.GameStatus;
import barbosatk.minigames.Enums.GameType;
import barbosatk.minigames.Helper.MGSupport;
import barbosatk.minigames.Menus.MiniGameMenus.LobbyMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class MGSettings {

    private Player owner;
    private int capacity;
    private GameType gameType;
    private GameStatus gameStatus;
    private final String channelToken;
    private boolean allowTeams;
    private boolean gameWithTeams = false;

    public MGSettings() {
        this.gameStatus = GameStatus.AWAITING_FOR_PLAYERS;
        this.channelToken = UUID.randomUUID().toString();
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public boolean isPlayerOwner(Player player) {
        return this.owner == player;
    }

    public String getChannelToken() {
        return channelToken;
    }

    public boolean getGameWithTeams() {
        return gameWithTeams;
    }

    public void setGameWithTeams(boolean gameWithTeams) {
        this.gameWithTeams = gameWithTeams;
    }

    public void changeNewLobbyOwnerItems(LobbyMenu lobbyMenu, String owner) {
        lobbyMenu.getLobbyItem().setLobbyOwner(owner);
        lobbyMenu.getPlayerTemporaryOutGameInventory().values().forEach(inventory -> {
                    inventory.setItem(1, MGSupport.createItem(Material.BOOK, "§6Lobby de §a" + owner, null));
                }
        );
    }

}
