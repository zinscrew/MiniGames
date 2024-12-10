package barbosatk.minigames.Classes;

import barbosatk.minigames.Enums.GameType;
import barbosatk.minigames.MiniGames;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class NameTagManager {

    public static void setNameTags(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        for (GameType gm : GameType.values()) {
            Team team = player.getScoreboard().registerNewTeam(gm.getMGAbbreviation());
            team.setSuffix(ChatColor.translateAlternateColorCodes('&', gm.getMGAbbreviation()));
        }
    }

    public static void insertTag(Player player) {
        GameType playerGameType = MiniGames.totalPlayersOnServer.getPlayerData(player).getGameType();
        for (Player target : Bukkit.getOnlinePlayers()) {
            target.getScoreboard().getTeam(playerGameType.getMGAbbreviation()).addEntry(player.getName());
        }
    }

    public static void removeTag(Player player) {
        for (UUID targetUUID : MiniGames.totalPlayersOnServer.getTotalPlayersData().keySet()) {
            Player targetPlayer = Bukkit.getPlayer(targetUUID);
            targetPlayer.getScoreboard().getEntryTeam(player.getName()).removeEntry(player.getName());
        }
    }

}
