package barbosatk.minigames.Menus.MiniGameMenus.InOutMenus;

import barbosatk.minigames.Enums.GameStatus;
import barbosatk.minigames.Enums.MGItems;
import barbosatk.minigames.Helper.MGSupport;
import barbosatk.minigames.Menus.MiniGameMenus.InOutMenus.Interfaces.InventoryHandler;
import barbosatk.minigames.Menus.MiniGameMenus.InOutMenus.Interfaces.OutGameInventory;
import barbosatk.minigames.Menus.MiniGameMenus.LobbyMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OutGameMenu extends Menu implements OutGameInventory, InventoryHandler {

    @Override
    public void buildMenu(LobbyMenu lobbyMenu, Player player) {
        Inventory inventory = this.createInventory(lobbyMenu.getInventorySize(), "Lobby de " + lobbyMenu.getMiniGame().getMgSettings().getGameType().getGameName());

        // Logic for building either Owner or other players' menu.
        if (lobbyMenu.getMiniGame().getMgSettings().getOwner() == player) {
            this.buildOwnerMenu(inventory);
        } else {
            this.buildPlayerMenu(lobbyMenu, player, inventory);
        }
        // Load "Go Back" Button
        inventory.setItem(0, MGItems.MGItem.BACK_BUTTON.getItemStack());
        if (lobbyMenu.getMiniGame().getMgSettings().getOwner() != null)
            inventory.setItem(1, MGSupport.createItem(Material.BOOK, "§6Lobby de §a" + lobbyMenu.getMiniGame().getMgSettings().getOwner().getName(), null));
        else
            inventory.setItem(1, MGSupport.createItem(Material.BOOK, "§6Lobby de §a" + lobbyMenu.getMiniGame().getMgSettings().getGameType().getGameName(), null));

        // Load the spectator item
        if (lobbyMenu.getMiniGame().getMgSettings().getGameStatus() == GameStatus.GAME_RUNNING)
            inventory.setItem(8, MGItems.MGItem.SPECTATE_GAME.getItemStack());

        // Load lobby separator
        int separatorCurrentSlot = 9, separatorMaxSlot = 17;
        ItemStack separator = MGSupport.createItem(Material.SPRUCE_BUTTON, " ", null);
        while (separatorCurrentSlot <= separatorMaxSlot) {
            inventory.setItem(separatorCurrentSlot, separator);
            separatorCurrentSlot++;
        }

        // Load Players
        player.openInventory(inventory);
        lobbyMenu.getPlayerTemporaryOutGameInventory().put(player, inventory);
        lobbyMenu.loadLobbyPlayers();
    }


    @Override
    public void buildOwnerMenu(Inventory inventory) {
        inventory.setItem(3, MGItems.MGItem.DELETE_LOBBY.getItemStack());
        inventory.setItem(5, MGItems.MGItem.START_GAME_BUTTON.getItemStack());
        inventory.setItem(7, MGItems.MGItem.GAME_SETTINGS.getItemStack());
    }

    @Override
    public void buildPlayerMenu(LobbyMenu lobbyMenu, Player player, Inventory inventory) {
        // check if player is already in lobby
        if (lobbyMenu.getMiniGame().getLobbyPlayers().contains(player)) {
            inventory.setItem(4, MGItems.MGItem.LEAVE_LOBBY.getItemStack());
            inventory.setItem(7, MGItems.MGItem.GAME_SETTINGS.getItemStack());
            return;
        }
        inventory.setItem(4, MGItems.MGItem.ADD_LOBBY.getItemStack());
    }

}
