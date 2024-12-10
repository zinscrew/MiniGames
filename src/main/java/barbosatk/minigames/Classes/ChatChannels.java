package barbosatk.minigames.Classes;

import barbosatk.minigames.Config.Contants.MGConstants;
import barbosatk.minigames.Enums.GameType;
import barbosatk.minigames.MiniGames;
import barbosatk.minigames.Player.PlayerData;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.stream.Collectors;

public class ChatChannels implements Listener {

    public ChatChannels() {}

    public void sendMessageToChannel(Player sender, String message, boolean global) {
        if (global) {
            MiniGames.totalPlayersOnServer.getTotalPlayersData().values().stream()
                    .filter(playerData -> playerData.getChatChannelToken().isEmpty() && playerData.getGameType() == GameType.GLOBAL)
                    .collect(Collectors.toList()).forEach(playerData -> playerData.getPlayer().sendMessage(message));
            return;
        }

        PlayerData playerData = MiniGames.totalPlayersOnServer.getPlayerData(sender);
        String token = playerData.getChatChannelToken();
        MiniGames.preLobbyManager.findPreLobbyMenu(playerData.getGameType()).getGameTypeLobbiesList()
                .stream().filter(gameMenu -> gameMenu.getMiniGame().getMgSettings().getChannelToken().equals(token))
                .findFirst().get().getMiniGame().sendMessageToLobby(message);
    }

    private void messageForGlobalOrSpecific(PlayerData playerData, String message) {
        boolean sendToGlobal = playerData.getChatChannelToken().isEmpty();
        this.sendMessageToChannel(playerData.getPlayer(), message, sendToGlobal);
    }

    @EventHandler
    private void onPlayerSendMessage(AsyncPlayerChatEvent pce) {
        // :TODO Here should be implemented all the logic for communication (Chat Channels).
        pce.setCancelled(true);
        Player player = pce.getPlayer();
        PlayerData playerData = MiniGames.totalPlayersOnServer.getPlayerData(player);
        String chatChannelTag = MGConstants.chatTagSuccess(playerData.getGameType().getGameName());
        String message = chatChannelTag + "§f<" + player.getName() + "> " + pce.getMessage();
        this.messageForGlobalOrSpecific(playerData, message);
    }

    @EventHandler
    private void onPlayerDiedMessage(PlayerDeathEvent pde) {
        Player player = pde.getEntity();
        PlayerData playerData = MiniGames.totalPlayersOnServer.getPlayerData(player);
        String chatChannelTag = MGConstants.chatTagSuccess(playerData.getGameType().getGameName());

        String deathMessage = chatChannelTag + "§f" + pde.getDeathMessage();
        pde.setDeathMessage("");

        this.messageForGlobalOrSpecific(playerData, deathMessage);
    }

}
