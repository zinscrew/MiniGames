package barbosatk.minigames.Player;

import barbosatk.minigames.Config.Contants.MGConstants;
import barbosatk.minigames.Helper.MGSupport;
import barbosatk.minigames.MiniGames;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerWorldInfo {

    private final ItemStack[] inventory;
    private final ItemStack[] armContent;
    private final float expBar;
    private final int expLevel;
    private final Location location;
    private final ItemStack[] enderChest;


    public PlayerWorldInfo(Player player) {
        this.inventory = player.getInventory().getContents();
        this.armContent = player.getInventory().getArmorContents();
        this.expBar = player.getExp();
        this.expLevel = player.getLevel();
        this.location = player.getLocation();
        this.enderChest = player.getEnderChest().getContents();
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public ItemStack[] getArmContent() {
        return armContent;
    }

    public float getExpBar() {
        return expBar;
    }

    public int getExpLevel() {
        return expLevel;
    }

    public Location getLocation() {
        return location;
    }

    public ItemStack[] getEnderChest() {
        return enderChest;
    }

    public void givePlayerContents(Player player) {
        // get contents back to player
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().setContents(this.inventory);
        player.getInventory().setArmorContents(this.armContent);
        player.setExp(this.expBar);
        player.setLevel(this.expLevel);
        player.getEnderChest().setContents(this.enderChest);

        MiniGames.playerInfoManager.removeContent(player);
    }
}
