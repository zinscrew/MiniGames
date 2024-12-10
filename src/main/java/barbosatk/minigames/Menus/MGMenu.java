package barbosatk.minigames.Menus;

import barbosatk.minigames.Config.Contants.MGConstants;
import barbosatk.minigames.Enums.MGItems;
import barbosatk.minigames.Helper.MGSupport;
import barbosatk.minigames.MiniGames;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.atomic.AtomicInteger;

public class MGMenu implements Listener {

    private Plugin plugin;
    private Inventory inventory;

    public MGMenu(Plugin plugin) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(null, MGConstants.MG_MENU_INVENTORY_SIZE, "Menu de Mini-Games");
        this.populateInventory();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void populateInventory() {
        AtomicInteger itemsIndex = new AtomicInteger(13);
        // :TODO check time complexity later on - O(n^2)
        // An option could be adding the MGItem to the GameType enum to make a direct reference to the item
        MiniGames.preLobbyManager.getPreLobbiesMenus().keySet().forEach(game -> {
            for (MGItems.MGItem item : MGItems.MGItem.values()) {
                if (item.getGameType() != null && item.getGameType() == game && item.getGameType().isActive()) {
                    this.inventory.setItem(itemsIndex.get(), item.getItemStack());
                    itemsIndex.getAndIncrement();
                }
            }
        });
    }

    public void buildMGMenu(Player player) {
        player.openInventory(this.inventory);
    }

    @EventHandler
    private void onPlayerClick(InventoryClickEvent event) {
        MGSupport.denyItemClickIfMenusOpened(event, this.inventory);
        if (event.getClickedInventory() == this.inventory) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null) {
                MGItems.MGItem item = MGItems.whatMenuItemIs(event.getCurrentItem());
                if (item != null) {
                    event.getWhoClicked().openInventory(MiniGames.preLobbyManager.findPreLobbyMenu(item.getGameType()).getInventory());
                }
            }
        }
    }
}

   /*  @EventHandler
    private void playerClose(InventoryCloseEvent ice) {
        if (ice.getInventory() == this.inventory)
            HandlerList.unregisterAll(this);
    }*/