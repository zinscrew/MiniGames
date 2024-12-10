package barbosatk.minigames.Player;

import barbosatk.minigames.Config.Contants.MGConstants;
import barbosatk.minigames.Enums.GameType;
import barbosatk.minigames.Enums.MGItems;
import barbosatk.minigames.Menus.MiniGameMenus.LobbyMenu;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.io.IOException;

public class PlayerData {

    private boolean isInGame;
    private Player player;
    private GameType lobbyGameType;
    private LobbyMenu lobbyInv;
    private int mgItemSlot;
    private ItemStack skull;
    private String chatChannelToken;

    private int deleteDataTask = -1;

    public PlayerData(Player player) {
        this.isInGame = false;
        this.player = player;
        this.lobbyGameType = GameType.GLOBAL;
        this.lobbyInv = null;
        this.chatChannelToken = "";
        loadPlayerAuxConfigs();
    }

    private void loadPlayerAuxConfigs() {
        this.setMgItemSlot();
        this.skull = this.generatePlayerSkull();
    }

    private void setMgItemSlot() {
        File file = new File(MGConstants.PATH_TO_PLAYER_DATA + "/" + this.player.getUniqueId() + ".yml");
        boolean wasCreated = this.createPlayerFile(file);
        YamlConfiguration config = new YamlConfiguration();
        boolean hasMgMenuItem = this.player.getInventory().contains(MGItems.MGItem.MG_MENU.getItemStack());
        if (wasCreated) {
            if (!hasMgMenuItem) {
                this.mgItemSlot = 2;
                config.set(MGConstants.ITEM_MG_SLOT, this.mgItemSlot);
                try {
                    config.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else this.saveItemSlot();
        } else {
            this.mgItemSlot = (int) YamlConfiguration.loadConfiguration(file).get(MGConstants.ITEM_MG_SLOT);
        }

        if (!hasMgMenuItem)
            this.player.getInventory().setItem(this.mgItemSlot, MGItems.MGItem.MG_MENU.getItemStack());
    }

    private boolean createPlayerFile(File file) {
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void saveItemSlot() {
        File file = new File(MGConstants.PATH_TO_PLAYER_DATA + "/" + this.player.getUniqueId() + ".yml");
        YamlConfiguration conf = new YamlConfiguration();
        this.player.getInventory().all(MGItems.MGItem.MG_MENU.getItemStack()).forEach((key, value) -> {
            if (value.isSimilar(MGItems.MGItem.MG_MENU.getItemStack())) {
                this.mgItemSlot = key;
                conf.set(MGConstants.ITEM_MG_SLOT, key);
            }
        });
        try {
            conf.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ItemStack generatePlayerSkull() {
        ItemStack skull;
        SkullMeta meta;
        try {
            OfflinePlayer p = Bukkit.getServer().getOfflinePlayer(this.player.getUniqueId());
            skull = new ItemStack(Material.PLAYER_HEAD);
            meta = (SkullMeta) skull.getItemMeta();
            assert meta != null;
            meta.setOwningPlayer(p);
            meta.setDisplayName(ChatColor.YELLOW + p.getName());
            skull.setItemMeta(meta);
        } catch (Exception e) {
            Player p = Bukkit.getServer().getPlayer(this.player.getUniqueId());
            skull = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
            meta = (SkullMeta) skull.getItemMeta();
            meta.setOwner(p.getName());
            meta.setDisplayName(ChatColor.YELLOW + p.getName());
            skull.setItemMeta(meta);
        }
        System.out.println("[i] Skull for " + this.player.getName() + " was generated.");
        return skull;
    }

    /* GETTERS AND SETTERS */

    public int getMgItemSlot() {
        return mgItemSlot;
    }

    public LobbyMenu getLobbyInv() {
        return lobbyInv;
    }

    public void setLobbyInv(LobbyMenu lobbyInv) {
        this.lobbyInv = lobbyInv;
    }

    public boolean isInGame() {
        return isInGame;
    }

    public void setInGame(boolean inGame) {
        isInGame = inGame;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public GameType getGameType() {
        return lobbyGameType;
    }

    public void setGameType(GameType gameType) {
        this.lobbyGameType = gameType;
    }

    public ItemStack getSkull() {
        return skull;
    }

    public String getChatChannelToken() {
        return chatChannelToken;
    }

    public void setChatChannelToken(String chatChannelToken) {
        this.chatChannelToken = chatChannelToken;
    }

    public int getDeleteDataTask() {
        return deleteDataTask;
    }

    public void setDeleteDataTask(int deleteDataTask) {
        this.deleteDataTask = deleteDataTask;
    }
}
