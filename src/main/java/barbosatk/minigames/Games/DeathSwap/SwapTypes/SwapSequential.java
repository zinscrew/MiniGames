package barbosatk.minigames.Games.DeathSwap.SwapTypes;

import barbosatk.minigames.Games.DeathSwap.Class.Swap;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class SwapSequential extends Swap {

    public SwapSequential(List<Player> playersListTemp) {
        this.playersListTemp = playersListTemp;
    }

    @Override
    public Swap load() {
        Player firstPlayer = playersListTemp.get(0);
        playersListTemp.stream().forEach(player -> {
            List<Player> playerListClone = playersListTemp.stream().filter(playerClone -> !playerClone.equals(player) && !picks.containsValue(playerClone)).collect(Collectors.toList());
            Player playerPicked;
            if (playerListClone.get(0) == null)
                playerPicked = firstPlayer;
            else
                playerPicked = playerListClone.get(0);
            picks.put(player, playerPicked);
        });
        return this;
    }

}
