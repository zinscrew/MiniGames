package barbosatk.minigames.Interfaces;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public interface PlaySounds {
    void playSoundForPlayers(List<Player> players, Sound sound, Float lowPitchVol, Float highPitchVol);
}
