package barbosatk.minigames.MGDrivers;

import org.bukkit.scoreboard.*;

import static org.bukkit.Bukkit.getServer;

public final class MGScoreboards {

    private Scoreboard scoreboard;

    public Scoreboard getPlayerList() {
        return scoreboard;
    }

    public void initScoreBoard(Runnable renderScoreboard) {
        this.scoreboard = getServer().getScoreboardManager().getNewScoreboard();
        renderScoreboard.run();
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

}
