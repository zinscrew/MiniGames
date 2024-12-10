package barbosatk.minigames.Classes;

import barbosatk.minigames.Player.PlayerWorldInfo;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerInfoManager {

    private HashMap<UUID, PlayerWorldInfo> playerWorldContents = new HashMap<>();

    //:TODO Add and Remove player content to YML file and loadIt everytime the server inits

    public void addContent(Player player, PlayerWorldInfo playerWorldInfo) {
        if (!this.hasContent(player))
            this.playerWorldContents.put(player.getUniqueId(), playerWorldInfo);
    }

    public void removeContent(Player player) {
        if (this.hasContent(player))
            this.playerWorldContents.remove(player.getUniqueId(), this.getContent(player));
    }

    public boolean hasContent(Player player) {
        return playerWorldContents.containsKey(player.getUniqueId());
    }

    public PlayerWorldInfo getContent(Player player) {
        return playerWorldContents.get(player.getUniqueId());
    }
}
