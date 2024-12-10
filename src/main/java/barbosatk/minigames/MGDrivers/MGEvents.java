package barbosatk.minigames.MGDrivers;

import barbosatk.minigames.Config.Contants.MGConstants;
import barbosatk.minigames.Enums.GameStatus;
import barbosatk.minigames.Games.MiniGame;
import barbosatk.minigames.Helper.MGSupport;
import barbosatk.minigames.MiniGames;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class MGEvents implements Listener {

    private MiniGame miniGame;

    public MGEvents(MiniGame miniGame) {
        this.miniGame = miniGame;
        this.registerListener();
    }

    public void registerListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.getPlugin(MiniGames.class));
    }

    public void unregisterListener() {
        HandlerList.unregisterAll(this);
    }

    private void endGameCheck() {
        if (miniGame.getMiniGamePlayers().size() != 1)
            return;

        Player winner = miniGame.getMiniGamePlayers().get(0);
        miniGame.sendMessageToLobby("");
        miniGame.sendMessageToLobby(" ");
        miniGame.sendMessageToLobby(MGConstants.chatTagSuccess(miniGame.getMgSettings().getGameType().getGameName()) + "§6O Jogador §a" + winner.getName() + " §6ganhou o DeathSwap!");
        miniGame.sendMessageToLobby("");
        miniGame.sendMessageToLobby(" ");
        MGSupport.showTitleForPlayers(miniGame.getAllInGameAndSpecPlayers(), "§a" + winner.getName() + "§6 venceu o jogo", "§f O jogo encerra dentro de 8 segundos", 12, 126, 12);
        Bukkit.getScheduler().scheduleSyncDelayedTask(MiniGames.getPlugin(MiniGames.class), () -> {
            miniGame.defaultEndGame();
        }, 160L);
    }


    @EventHandler
    private void onPlayerLeaveWhileInGame(PlayerQuitEvent pqe) {
        if (!miniGame.eventIsAllowedForPlayer(pqe.getPlayer()) && !miniGame.getMiniGameSpectators().contains(pqe.getPlayer()))
            return;

        MiniGames.preLobbyManager.findPreLobbyMenu(this.miniGame.getMgSettings().getGameType())
                .findLobbyMenu(this.miniGame)
                .leaveLobby(pqe.getPlayer());

        this.miniGame.getInGameMenu().loadInGamePlayers();
        this.miniGame.getSpectatorMenu().loadInGamePlayers();

        this.miniGame.clearPlayerContents(pqe.getPlayer());

        this.miniGame.sendMessageToLobby(MGConstants.chatTagSuccess(this.miniGame.getMgSettings().getGameType().getGameName()) + "O jogador §a" + pqe.getPlayer().getName() + "§6 foi eliminado. Motivo: §eLEAVING_SERVER");

        this.endGameCheck();
    }

    @EventHandler
    private void cancelPlayerDamageOnGameInit(EntityDamageEvent ede) {
        if (!(ede.getEntity() instanceof Player))
            return;
        Player player = (Player) ede.getEntity();
        if (!this.miniGame.getLobbyPlayers().contains(player))
            return;
        if (this.miniGame.getMgSettings().getGameStatus().equals(GameStatus.GAME_INITIALIZATION))
            ede.setCancelled(true);
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageEvent ede) {
        if (!(ede.getEntity() instanceof Player))
            return;

        Player player = (Player) ede.getEntity();
        if (this.miniGame.getMiniGameSpectators().contains(player)) {
            ede.setCancelled(true);
            return;
        }

        if (!this.miniGame.eventIsAllowedForPlayer((Player) ede.getEntity()))
            return;

        if (player.getHealth() - ede.getDamage() <= 0) {

            ede.setCancelled(true);
            if (this.miniGame.getMiniGamePlayers().remove(player)) {
                this.miniGame.getInGameMenu().loadInGamePlayers();
                this.miniGame.getSpectatorMenu().loadInGamePlayers();
            }

            this.miniGame.clearPlayerContents(player);
            this.miniGame.getMiniGameSpectators().add(player);
            player.setGameMode(GameMode.SPECTATOR);
            player.setSpectatorTarget(this.miniGame.getMiniGamePlayers().get(0));
            player.closeInventory();
            player.sendTitle("§c  Foste Eliminado  ", "§fEstás em modo espectador", 12, 40, 12);
            System.out.println("[i] Set player '" + player.getName() + "' as spectator");
            this.miniGame.sendMessageToLobby(MGConstants.chatTagSuccess(this.miniGame.getMgSettings().getGameType().getGameName()) + "O jogador §a" + player.getName() + "§6 foi eliminado. Motivo: §e" + ede.getCause());

            this.endGameCheck();
        }

    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent pie) {

        Player player = pie.getPlayer();
        if (!this.miniGame.getMiniGameSpectators().contains(player))
            return;

        if (!player.getGameMode().equals(GameMode.SPECTATOR))
            return;

        pie.setCancelled(true);
        player.openInventory(this.miniGame.getSpectatorMenu().getSpectatorInventory());
    }

}
