package barbosatk.minigames.Menus.MiniGameMenus.InOutMenus;

import barbosatk.minigames.Config.Contants.MGConstants;
import barbosatk.minigames.Enums.MGItems;
import barbosatk.minigames.Games.MiniGame;
import barbosatk.minigames.Helper.MGSupport;
import barbosatk.minigames.Menus.MiniGameMenus.LobbyMenu;
import barbosatk.minigames.MiniGames;
import barbosatk.minigames.Player.PlayerWorldInfo;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SpectatorMenu extends Menu implements Listener {

    private Inventory spectatorInventory;
    private MiniGame miniGame;

    public Inventory getSpectatorInventory() {
        return spectatorInventory;
    }

    public void buildMenu(MiniGame miniGame) {
        // Load lobby separator
        this.miniGame = miniGame;
        Inventory inventory = this.createInventory(MGConstants.SPECTATOR_MENU_INVENTORY_SIZE, "Espectador - " + miniGame.getMgSettings().getGameType().getGameName());

        inventory.setItem(4, MGItems.MGItem.LEAVE_LOBBY.getItemStack());

        int separatorCurrentSlot = 9, separatorMaxSlot = 17;
        ItemStack separator = MGSupport.createItem(Material.SPRUCE_BUTTON, " ", null);
        while (separatorCurrentSlot <= separatorMaxSlot) {
            inventory.setItem(separatorCurrentSlot, separator);
            separatorCurrentSlot++;
        }
        this.spectatorInventory = inventory;
        this.loadInGamePlayers();
    }

    public void loadInGamePlayers() {
        this.clearAllSkulls(this.spectatorInventory, this.miniGame.getMiniGamePlayers().size());
        int currentSlot = 18;
        for (Player player : this.miniGame.getMiniGamePlayers()) {
            if (currentSlot > this.spectatorInventory.getSize())
                break;
            this.spectatorInventory.setItem(currentSlot, MiniGames.totalPlayersOnServer.getPlayerData(player).getSkull());
            currentSlot++;
        }
    }

    private void clearAllSkulls(Inventory inventory, int size) {
        for (int i = 18; i <= (18 + size); i++) {
            inventory.clear(i);
        }
    }

    public void createSpectatorMenu(MiniGame miniGame) {
        this.buildMenu(miniGame);
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.getPlugin(MiniGames.class));
    }

    public void unregisterSpecMenu() {
        HandlerList.unregisterAll(this);
    }


    // EVENTS ------------------------------------

    @EventHandler
    private void onSpectatorInventoryClick(InventoryClickEvent ice) {

        if (ice.getClickedInventory() != this.spectatorInventory)
            return;
        MGSupport.denyItemClickIfMenusOpened(ice, this.spectatorInventory);

        if (ice.getCurrentItem() == null)
            return;

        MGItems.MGItem item = MGItems.whatMenuItemIs(ice.getCurrentItem());

        Player player = (Player) ice.getWhoClicked();

        if (item == null) {
            ItemStack skull = ice.getCurrentItem();
            Player playerToSpec = this.miniGame.getMiniGamePlayers().stream()
                    .filter(playerTemp -> skull.getItemMeta().getDisplayName().equals("§e" + playerTemp.getName()))
                    .findFirst().get();
            player.setSpectatorTarget(playerToSpec);
            player.closeInventory();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6Estás a visualziar o jogador §f" + playerToSpec.getName()));
            return;
        }

        switch (item) {
            case LEAVE_LOBBY:
                this.miniGame.getMiniGameSpectators().remove(player);

                LobbyMenu lobbyMenu = MiniGames.preLobbyManager.findPreLobbyMenu(this.miniGame.getMgSettings().getGameType())
                        .findLobbyMenu(this.miniGame);

                if(lobbyMenu.getMiniGame().getLobbyPlayers().contains(player))
                    lobbyMenu.leaveLobby(player);

                this.miniGame.clearPlayerContents(player);

                PlayerWorldInfo playerWorldInfo = MiniGames.playerInfoManager.getContent(player);
                player.teleport(playerWorldInfo.getLocation());
                playerWorldInfo.givePlayerContents(player);
                break;
        }

    }

}
