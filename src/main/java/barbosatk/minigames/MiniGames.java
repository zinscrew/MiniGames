package barbosatk.minigames;

import barbosatk.minigames.Classes.ChatChannels;
import barbosatk.minigames.Classes.PlayerInfoManager;
import barbosatk.minigames.Classes.TotalPlayersOnServer;
import barbosatk.minigames.Config.MGFolders;
import barbosatk.minigames.DefaultEvents.NormalEvents;
import barbosatk.minigames.Menus.MGMenu;
import barbosatk.minigames.Menus.PreLobbyMenus.PreLobbyManager;
import barbosatk.minigames.Player.PlayerMGMenuEvents;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class MiniGames extends JavaPlugin {

    public static TotalPlayersOnServer totalPlayersOnServer;
    public static PreLobbyManager preLobbyManager;
    public static MGMenu mgMenu;
    public static ChatChannels chatChannels;
    public static PlayerInfoManager playerInfoManager;
    private MGFolders mgFolders;

    @Override
    public void onEnable() {
        //delete all listeners
        HandlerList.unregisterAll();
        this.loadMiniGameSupports();
        this.mgFolders.create();
        //this.mgFolders.deleteWorlds();


        // :TODO registerEvents can only be instantiated once, otherwise it'll run the same data for each event registered.
        getServer().getPluginCommand("worlds").setExecutor(new MiniGameCommands());
        getServer().getPluginManager().registerEvents(chatChannels, this);
        getServer().getPluginManager().registerEvents(new NormalEvents(), this);
        getServer().getPluginManager().registerEvents(new PlayerMGMenuEvents(this), this);


        // dev configs
        World defaultWorld = Bukkit.getWorld("world");
        assert defaultWorld != null;
        defaultWorld.setTime(6000);
        defaultWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        defaultWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        // -----------;
    }

    private void loadMiniGameSupports() {
        totalPlayersOnServer = new TotalPlayersOnServer();
        /* ----------------------------- */
        this.mgFolders = new MGFolders();
        preLobbyManager = new PreLobbyManager();
        chatChannels = new ChatChannels();
        playerInfoManager = new PlayerInfoManager();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}