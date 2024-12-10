package barbosatk.minigames.Menus.MiniGameMenus.InOutMenus;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public abstract class Menu {

    public Inventory createInventory(int inventorySize, String title) {
        return Bukkit.createInventory(null, inventorySize, title);
    }

}
