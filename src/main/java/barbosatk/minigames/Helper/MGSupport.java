package barbosatk.minigames.Helper;

import barbosatk.minigames.Config.Contants.MGConstants;
import barbosatk.minigames.Enums.GameType;
import barbosatk.minigames.Menus.MiniGameMenus.LobbyMenu;
import barbosatk.minigames.MiniGames;
import barbosatk.minigames.Player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class MGSupport {

    public static ItemStack createItem(Material material, String itemName, List<String> itemContent) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(itemName);
        meta.setLore(itemContent);
        item.setItemMeta(meta);
        return item;
    }

    public static Player getPlayerFromUUID(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }

    public static LobbyMenu getPlayerLobby(Player player) {
        PlayerData playerData = MiniGames.totalPlayersOnServer.getPlayerData(player);
        if (playerData.getGameType() == GameType.GLOBAL)
            return null;
        return MiniGames.preLobbyManager.findPreLobbyMenu(playerData.getGameType()).getGameTypeLobbiesList().stream()
                .filter(lobbyMenu -> lobbyMenu.getMiniGame().getMgSettings().getChannelToken().equals(playerData.getChatChannelToken()))
                .findFirst().orElse(null);
    }

    public static void removeFromLobby(Player player) {
        LobbyMenu lobbyMenu = MGSupport.getPlayerLobby(player);
        if (lobbyMenu != null) {
            if (lobbyMenu.getMiniGame().getMgSettings().getOwner() == player) {
                lobbyMenu.redirectOnOutGameLobbyDeleted(player);
                return;
            }
            lobbyMenu.leaveLobby(player);
        }
    }

    public static void denyItemClickIfMenusOpened(InventoryClickEvent ice, Inventory inventoryMenu) {
        if (ice.getInventory() == inventoryMenu && ice.getClickedInventory() == ice.getWhoClicked().getInventory())
            ice.setCancelled(true);
    }

    public static void playSoundForPlayers(List<Player> players, Sound sound, Float lowPitchVol, Float highPitchVol) {
        players.forEach(player -> player.playSound(player.getLocation(), sound, lowPitchVol, highPitchVol));
    }

    public static void showTitleForPlayers(List<Player> players, String title, String subTitle, int fadeInTime, int stayTime, int fadeOutTime) {
        players.forEach(player -> player.sendTitle(title, subTitle, fadeInTime, stayTime, fadeOutTime));
    }

    public static Scoreboard worldSideBarSB() {
        //getServer().getScoreboardManager().getMainScoreboard().getObjective(MGConstants.worldObjectiveName).unregister();

        Scoreboard scoreBoard = Objects.requireNonNull(getServer().getScoreboardManager()).getNewScoreboard();

        Objective objective = scoreBoard.getObjective(MGConstants.worldSideBarSB) != null ? scoreBoard.getObjective(MGConstants.worldSideBarSB) : scoreBoard.registerNewObjective(MGConstants.worldSideBarSB, "defaultWorldSB", "defaultWorldSB");
        assert objective != null;
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "~~ MiniGames ~~");

        Score space = objective.getScore(" ");
        space.setScore(2);

        Score pluginType = objective.getScore("Welcome to MiniGame");
        pluginType.setScore(1);

       /* Objective playerList = scoreBoard.getObjective(MGConstants.playerListSB) != null ? scoreBoard.getObjective(MGConstants.playerListSB) : scoreBoard.registerNewObjective(MGConstants.playerListSB, "healths", "healths");
        playerList.setRenderType(RenderType.HEARTS);
        playerList.setDisplaySlot(DisplaySlot.PLAYER_LIST);*/

        return scoreBoard;
    }

}
