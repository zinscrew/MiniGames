package barbosatk.minigames.Games.HideAndSeek;

import barbosatk.minigames.Enums.GameType;
import barbosatk.minigames.Games.MiniGame;
import barbosatk.minigames.MiniGames;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public class HideAndSeek extends MiniGame implements Listener {

    private final String sideBarSBObjective = "HideAndSeekBar";

    @Override
    public void customizeWorld(World world, World netherWorld) {
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
    }

    @Override
    public void beforeStartGame() {
        System.out.println("§6[*] I'm going to start a " + this.getMgSettings().getGameType() + " Game!");
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.getPlugin(MiniGames.class));

    }

    @Override
    public void afterGameStartedTasks() {

    }

    @Override
    public void endGame() {

    }

    @Override
    public void unregisterAllListeners() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void renderScoreboard(Scoreboard board) {
        Objective sideBar = this.registerScoreboardObjectives(board, this.sideBarSBObjective, ChatColor.GRAY + "~ " + this.getMgSettings().getGameType().getGameName() + " ~ ");
        sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    @Override
    public void buildSettingsMenu(Inventory mgSettingsInventory) {
        this.getMgSettings().getOwner().openInventory(mgSettingsInventory);
    }

    @Override
    public void insertRecipes(List<ShapedRecipe> mgRecipes) {

    }

    @Override
    public void settingsMenuEventItemClick(ItemStack itemClicked) {

    }

    @Override
    public void unregisterScoreboardPlayerList() {

    }
}
