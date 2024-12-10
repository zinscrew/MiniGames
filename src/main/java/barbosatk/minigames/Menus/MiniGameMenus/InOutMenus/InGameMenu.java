package barbosatk.minigames.Menus.MiniGameMenus.InOutMenus;

import barbosatk.minigames.Config.Contants.MGConstants;
import barbosatk.minigames.Enums.GameStatus;
import barbosatk.minigames.Enums.MGItems;
import barbosatk.minigames.Games.MiniGame;
import barbosatk.minigames.Helper.MGSupport;
import barbosatk.minigames.Menus.MiniGameMenus.InOutMenus.Interfaces.InGameInventory;
import barbosatk.minigames.Menus.MiniGameMenus.LobbyMenu;
import barbosatk.minigames.MiniGames;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InGameMenu extends Menu implements InGameInventory, Listener {

    private Inventory inGameInventory;
    private MiniGame miniGame;
    private final List<Player> votesToEndGame = new ArrayList<>();

    public Inventory getInGameInventory() {
        return inGameInventory;
    }

    @Override
    public void buildMenu(MiniGame miniGame) {
        this.miniGame = miniGame;
        Inventory inventory = this.createInventory(MGConstants.LOBBY_MENU_INVENTORY_SIZE, "Lobby de " + miniGame.getMgSettings().getGameType().getGameName());

        inventory.setItem(3, MGItems.MGItem.SURRENDER_BUTTON.getItemStack());
        inventory.setItem(5, MGItems.MGItem.END_GAME_REQUEST_BUTTON.getItemStack());

        // Load lobby separator
        int separatorCurrentSlot = 9, separatorMaxSlot = 17;
        ItemStack separator = MGSupport.createItem(Material.SPRUCE_BUTTON, " ", null);
        while (separatorCurrentSlot <= separatorMaxSlot) {
            inventory.setItem(separatorCurrentSlot, separator);
            separatorCurrentSlot++;
        }
        this.inGameInventory = inventory;
        this.loadInGamePlayers();
    }

    public void loadInGamePlayers() {
        this.clearAllSkulls(this.inGameInventory, this.miniGame.getMiniGamePlayers().size());
        int currentSlot = 18;
        for (Player player : this.miniGame.getMiniGamePlayers()) {
            if (currentSlot > this.inGameInventory.getSize())
                break;
            this.inGameInventory.setItem(currentSlot, MiniGames.totalPlayersOnServer.getPlayerData(player).getSkull());
            currentSlot++;
        }
    }

    private void clearAllSkulls(Inventory inventory, int size) {
        for (int i = 18; i <= (18 + size); i++) {
            inventory.clear(i);
        }
    }

    public void createInGameMenu(MiniGame miniGame) {
        this.buildMenu(miniGame);
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.getPlugin(MiniGames.class));
    }

    public void unregisterInGameMenu() {
        HandlerList.unregisterAll(this);
    }

    private void addVoteToEndGame(Player player) {
        if (this.votesToEndGame.contains(player))
            return;

        this.votesToEndGame.add(player);
        String gameTypeName = this.miniGame.getMgSettings().getGameType().getGameName();
        this.miniGame.sendMessageToLobby(MGConstants.playerRequestedEndGame(gameTypeName, player.getName()));

        int playersNeeded = this.miniGame.getMiniGamePlayers().size() - ((this.votesToEndGame.size() / 2) + 1);
        if (playersNeeded > 0)
            this.miniGame.sendMessageToLobby(MGConstants.playersNeededToEndGame(gameTypeName, playersNeeded));

        Bukkit.getScheduler().scheduleSyncDelayedTask(MiniGames.getPlugin(MiniGames.class), () -> {
            if (!this.miniGame.getMgSettings().getGameStatus().equals(GameStatus.GAME_RUNNING))
                return;

            this.votesToEndGame.remove(player);
            player.sendMessage(MGConstants.playerRequestEndGameExpired(gameTypeName));
        }, 400L);
    }

    // EVENTS ------------------------------------

    @EventHandler
    private void onInGameInventoryClick(InventoryClickEvent ice) {
        if (ice.getClickedInventory() != this.inGameInventory)
            return;

        ice.setCancelled(true);
        MGSupport.denyItemClickIfMenusOpened(ice, this.inGameInventory);

        if (ice.getCurrentItem() == null)
            return;

        MGItems.MGItem item = MGItems.whatMenuItemIs(ice.getCurrentItem());
        if (item == null)
            return;

        Player player = (Player) ice.getWhoClicked();
        switch (item) {
            case SURRENDER_BUTTON:
                ice.getWhoClicked().damage(9000);
                break;
            case END_GAME_REQUEST_BUTTON:
                addVoteToEndGame(player);
                System.out.println("[i] EndGameRequest button was clicked");

                int votesNeeded = (votesToEndGame.size() / 2) + 1;
                if (votesNeeded > miniGame.getMiniGamePlayers().size() / 2) {
                    miniGame.defaultEndGame();
                    votesToEndGame.clear();
                }
                break;
        }
    }

}
