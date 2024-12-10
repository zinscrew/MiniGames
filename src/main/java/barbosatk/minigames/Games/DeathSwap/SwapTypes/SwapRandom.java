package barbosatk.minigames.Games.DeathSwap.SwapTypes;

import barbosatk.minigames.Games.DeathSwap.Class.Swap;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SwapRandom extends Swap {

    public SwapRandom(List<Player> playersListTemp) {
        this.playersListTemp = playersListTemp;
        Collections.shuffle(this.playersListTemp);
    }

    @Override
    public Swap load() {
        Random random = new Random();
        playersListTemp.stream().forEach(player -> {
            List<Player> playerListClone = playersListTemp.stream().filter(playerClone -> !picks.containsValue(playerClone)).collect(Collectors.toList());
            Player playerPicked = playerListClone.get(random.nextInt(playerListClone.size()));
            picks.put(player, playerPicked);
        });
        return this;
    }
}
