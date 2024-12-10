package barbosatk.minigames.Games.DeathSwap.Class;

import barbosatk.minigames.MiniGames;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Swap {

    public List<Player> playersListTemp = new ArrayList<>();
    public HashMap<Player, Location> playersLocation = new HashMap<>();
    public HashMap<Player, Player> picks = new HashMap<>();

    public void run() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(MiniGames.getPlugin(MiniGames.class), () -> {
            playersListTemp.forEach(player -> playersLocation.put(player, player.getLocation()));

            picks.forEach((player, playerPicked) -> {
                player.teleport(playersLocation.get(playerPicked));
                if (player.equals(playerPicked)) {
                    player.sendMessage("foste para a tua própria posição!");
                    return;
                }
                player.sendMessage("foste para a posição do jogador §a" + playerPicked.getName());
                //playerPicked.sendMessage("§6O jogador §a" + player.getName() + "§6 foi para a tua posição");
            });

        }, 0);
    }

    public abstract Swap load();


}
