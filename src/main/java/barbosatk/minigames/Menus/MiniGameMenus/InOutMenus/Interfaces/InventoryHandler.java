package barbosatk.minigames.Menus.MiniGameMenus.InOutMenus.Interfaces;

import barbosatk.minigames.Menus.MiniGameMenus.LobbyMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface InventoryHandler {
    void buildOwnerMenu(Inventory inventory);
    void buildPlayerMenu(LobbyMenu lobbyMenu, Player player, Inventory inventory);
}
