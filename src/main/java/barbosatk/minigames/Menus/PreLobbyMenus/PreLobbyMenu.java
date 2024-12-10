package barbosatk.minigames.Menus.PreLobbyMenus;

import barbosatk.minigames.Config.Contants.MGConstants;
import barbosatk.minigames.Enums.GameType;
import barbosatk.minigames.Enums.MGItems;
import barbosatk.minigames.Games.MiniGame;
import barbosatk.minigames.Helper.MGSupport;
import barbosatk.minigames.Menus.MiniGameMenus.LobbyMenu;
import barbosatk.minigames.MiniGames;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PreLobbyMenu implements Listener {

    private final GameType gameType;
    private Inventory inventory;
    private final int inventorySize = MGConstants.PRE_LOBBY_MENU_INVENTORY_SIZE;
    private final List<LobbyMenu> gameTypeLobbiesList = new ArrayList<>();

    public PreLobbyMenu(GameType gameType) {
        this.gameType = gameType;
        this.loadPreLobbyInventory();
    }

    public List<LobbyMenu> getGameTypeLobbiesList() {
        return gameTypeLobbiesList;
    }

    public LobbyMenu findLobbyMenu(MiniGame miniGame) {
        return gameTypeLobbiesList.stream().filter(lobbyMenu -> lobbyMenu.getMiniGame().equals(miniGame)).findFirst().get();
    }

    private void loadPreLobbyInventory() {
        this.inventory = Bukkit.getServer().createInventory(null, inventorySize, gameType.getGameNameWAbbrev());
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.getPlugin(MiniGames.class));

        ItemStack createLobby = MGItems.MGItem.CREATE_LOBBY.getItemStack();
        this.inventory.setItem(4, createLobby);

        // Load "Go Back" Button
        this.inventory.setItem(0, MGItems.MGItem.BACK_BUTTON.getItemStack());

        // Load lobby separator
        int separatorCurrentSlot = 9, separatorMaxSlot = 17;
        ItemStack separator = new ItemStack(Material.SPRUCE_BUTTON);
        ItemMeta itemMeta = separator.getItemMeta();
        itemMeta.setDisplayName(" ");
        separator.setItemMeta(itemMeta);
        while (separatorCurrentSlot <= separatorMaxSlot) {
            this.inventory.setItem(separatorCurrentSlot, separator);
            separatorCurrentSlot++;
        }

        this.buildLobbyItems();
    }

    public void buildLobbyItems() {
        // Needs to be redone using the commented method below
        // this.gameTypeLobbiesList.stream().filter(gameMenu -> gameMenu == gmTemp);

        int lobbiesCurrentSlot = 18;
        for (LobbyMenu gmTemp : this.gameTypeLobbiesList) {
            if (lobbiesCurrentSlot > inventorySize)
                break;
            ItemStack lobbyItems = gmTemp.getLobbyItem().getItem();
            this.inventory.clear(lobbiesCurrentSlot);
            this.inventory.setItem(lobbiesCurrentSlot, lobbyItems);
            lobbiesCurrentSlot++;
        }
        // clear 1 slot after (the removed game)
        this.inventory.clear(lobbiesCurrentSlot);
    }

    // Create and delete lobby
    private LobbyMenu createLobby(Player player) {
        LobbyMenu lobbyMenu = new LobbyMenu(player, this.gameType);
        this.gameTypeLobbiesList.add(lobbyMenu);
        this.buildLobbyItems();
        player.sendMessage("§6[§a*§6] Criaste a tua lobby de §a" + lobbyMenu.getMiniGame().getMgSettings().getGameType().getGameName() + "§6!");
        return lobbyMenu;
    }

    public void deleteLobby(LobbyMenu lobbyMenu) {
        this.gameTypeLobbiesList.remove(lobbyMenu);
        this.buildLobbyItems();
    }

    public Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    private void onPlayerClick(InventoryClickEvent ice) {
        MGSupport.denyItemClickIfMenusOpened(ice, this.inventory);
        if (ice.getClickedInventory() == this.inventory) {
            ice.setCancelled(true);
            Player player = (Player) ice.getWhoClicked();
            // ItemStack lobby = null;
            if (ice.getCurrentItem() != null && !ice.getCurrentItem().getItemMeta().getDisplayName().equals(" ")) {
                MGItems.MGItem item = MGItems.whatMenuItemIs(ice.getCurrentItem());
                LobbyMenu lobbyMenu = null;
                if (item != null) {
                    switch (item) {
                        case CREATE_LOBBY:
                            // This should be inside buildMenu
                            if (playerHasLobby(player)) {
                                for (LobbyMenu gmTemp : this.gameTypeLobbiesList) {
                                    if (gmTemp.getMiniGame().getMgSettings().getGameType() == this.gameType) {
                                        if (gmTemp.getMiniGame().getMgSettings().getOwner() == player) {
                                            lobbyMenu = gmTemp;
                                        }
                                    }
                                }
                            } else {
                                if (gameTypeLobbiesList.size() == MGConstants.LOBBY_QUANTITY_LIMIT) {
                                    player.sendMessage("§6[§c§lErro§6] Não existe espaço para lobbies adicionais. Espera por um jogo terminar!");
                                    return;
                                }
                                lobbyMenu = this.createLobby(player);
                            }
                            break;
                        case BACK_BUTTON:
                            MiniGames.mgMenu.buildMGMenu(player);
                            return;
                    }
                } else {
                    // Searches for the lobbies' items.
                    for (LobbyMenu gmTemp : this.gameTypeLobbiesList) {
                        ItemStack lobbyItem = gmTemp.getLobbyItem().getItem();
                        if (ice.getCurrentItem().isSimilar(lobbyItem)) {
                            // Gets the player inside an already created lobby
                            lobbyMenu = gmTemp;
                        }
                    }
                }
                assert lobbyMenu != null;
                if (lobbyMenu.getMiniGame().getMgSettings().getGameStatus().canOpenLobby())
                    lobbyMenu.buildMenu(player);
                else
                    player.sendMessage("§6[§c§lError§6] A lobby está atualmente em processo de fecho.");
            }
        }
    }

    private boolean playerHasLobby(Player player) {
        return this.gameTypeLobbiesList.stream().anyMatch(gameMenu -> gameMenu.getMiniGame().getMgSettings().getOwner() == player);
    }

}
