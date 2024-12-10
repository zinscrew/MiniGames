package barbosatk.minigames.Menus.MiniGameMenus.InOutMenus.Interfaces;

import barbosatk.minigames.Menus.MiniGameMenus.LobbyMenu;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface OutGameInventory {
    void buildMenu(LobbyMenu lobbyMenu, Player player);
}
