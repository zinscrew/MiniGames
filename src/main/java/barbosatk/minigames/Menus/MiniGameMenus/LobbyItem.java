package barbosatk.minigames.Menus.MiniGameMenus;

import barbosatk.minigames.Enums.GameStatus;
import barbosatk.minigames.Enums.GameType;
import barbosatk.minigames.Games.MiniGame;
import barbosatk.minigames.MiniGames;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LobbyItem {

    private ItemStack item;
    private final GameType gameType;
    private final List<String> loreInfo = new ArrayList<>(); // 0 - dashes | 1 - gameStatus | 2 - playersInLobby | 3 - spectatorsCount
    private int playerCapacity;

    public LobbyItem(GameType gameType, MiniGame miniGame) {
        this.gameType = gameType;
        this.createLobbyItem(miniGame);
    }

    public void createLobbyItem(MiniGame miniGame) {
        this.playerCapacity = miniGame.getMgSettings().getCapacity();
        item = new ItemStack(Material.BOOKSHELF, 1);
        this.setInitialInfo(miniGame);
    }

    public ItemStack getItem() {
        return item;
    }

    private void setInitialInfo(MiniGame miniGame) {
        loreInfo.add(0, "§7-----------------");
        loreInfo.add(1, "§6Estado: " + GameStatus.AWAITING_FOR_PLAYERS.getStatusName());
        loreInfo.add(2, "§6Lotação: §a" + miniGame.getLobbyPlayersSize() + "§6/" + miniGame.getMgSettings().getCapacity());
        loreInfo.add(3, "§7-----------------");
        loreInfo.add(4, "§6Espectadores: §e0");
        ItemMeta itemMeta = this.item.getItemMeta();
        itemMeta.setDisplayName("§6Lobby de §a" + miniGame.getMgSettings().getOwner().getName());
        itemMeta.setLore(loreInfo);
        this.item.setItemMeta(itemMeta);
    }

    public void setGameStatus(GameStatus gameStatus) {
        loreInfo.set(1, "§6Estado: " + gameStatus.getStatusName());
        this.doRebuild();
    }

    public void setPlayerCapacity(int currentCapacity) {
        loreInfo.set(2, "§6Lotação: §a" + currentCapacity + "§6/" + this.playerCapacity);
        this.doRebuild();
    }

    public void setSpectatorsCount(int message) {
        loreInfo.set(4, "§6Espectadores: §e" + message);
        this.doRebuild();
    }

    public void setLobbyOwner(String newPlayerName) {
        this.doRebuild(newPlayerName);
    }

    private void doRebuild() {
        ItemMeta itemMeta = this.item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setLore(loreInfo);
        this.item.setItemMeta(itemMeta);
        MiniGames.preLobbyManager.findPreLobbyMenu(this.gameType).buildLobbyItems();
    }

    // Only if the lobby owner has to be changed
    private void doRebuild(String newPlayerName) {
        ItemMeta itemMeta = this.item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§6Lobby de §a" + newPlayerName);
        itemMeta.setLore(loreInfo);
        this.item.setItemMeta(itemMeta);
        MiniGames.preLobbyManager.findPreLobbyMenu(this.gameType).buildLobbyItems();
    }

}