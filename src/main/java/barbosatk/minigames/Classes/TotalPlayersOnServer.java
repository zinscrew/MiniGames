package barbosatk.minigames.Classes;

import barbosatk.minigames.Player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class TotalPlayersOnServer {

    private HashMap<UUID, PlayerData> totalPlayersData = new HashMap<>();

    public TotalPlayersOnServer() {
        this.populateOnReload();
        this.closeInventories();
    }

    public HashMap<UUID, PlayerData> getTotalPlayersData() {
        return totalPlayersData;
    }

    public void insertPlayer(Player player) {
        boolean match = totalPlayersData.keySet().stream().anyMatch(playerDataUUID -> {
            if (playerDataUUID.equals(player.getUniqueId())) {
                // Refresh player object
                totalPlayersData.get(player.getUniqueId()).setPlayer(player);
                return true;
            }
            return false;
        });

        if (!match)
            this.totalPlayersData.put(player.getUniqueId(), new PlayerData(player));
    }

    public void removePlayer(Player player) {
        this.getPlayerData(player).saveItemSlot();
        this.totalPlayersData.remove(player.getUniqueId());
    }

    public PlayerData getPlayerData(Player player) {
        return totalPlayersData.get(player.getUniqueId());
    }

    private void populateOnReload() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            this.insertPlayer(player);
            this.getPlayerData(player).saveItemSlot();
            player.setGameMode(GameMode.SURVIVAL);
        }
        System.out.println("[Task] players' data were populated");
    }

    private void closeInventories() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.closeInventory();
        }
    }
}
