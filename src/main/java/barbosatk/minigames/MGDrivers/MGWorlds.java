package barbosatk.minigames.MGDrivers;

import barbosatk.minigames.Config.Contants.MGConstants;
import barbosatk.minigames.Games.MiniGame;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.bukkit.Bukkit.getServer;

public final class MGWorlds {

    // :TODO This needs to be dynamic due to the load of different maps according to the MiniGame

    private World defaultWorld;
    private World netherWorld;

    public MGWorlds() {
    }

    public World getMGWorld() {
        return defaultWorld;
    }

    public World getMGNetherWorld() {
        return netherWorld;
    }

    public void loadWorlds(MiniGame miniGame) {
        miniGame.sendMessageToLobby(MGConstants.creatingWorldsMessage(miniGame.getMgSettings().getGameType().getGameName()));
        String mgWorldName = miniGame.getMgSettings().getOwner().getName() + "_" + miniGame.getMgSettings().getGameType().getWorldName() + "_" + new Random().nextInt(100000);
        String mgNetherWorldName = mgWorldName + "_nether";

        boolean fromServer = false;
        if (!fromServer) {
            WorldCreator wc = new WorldCreator(MGConstants.PATH_TO_WORLDS_FOLDERS + mgWorldName);
            wc.type(WorldType.NORMAL);
            wc.environment(World.Environment.NORMAL);
            this.defaultWorld = wc.createWorld();
            this.netherWorld = null;

            miniGame.sendMessageToLobby(MGConstants.chatTagSuccess(miniGame.getMgSettings().getGameType().getGameName()) + "§6A criar o mundo do nether...");
            WorldCreator wcNether = new WorldCreator(MGConstants.PATH_TO_WORLDS_FOLDERS + mgNetherWorldName);
            wcNether.environment(World.Environment.NETHER);
            this.netherWorld = wcNether.createWorld();
            miniGame.customizeWorld(this.defaultWorld, this.netherWorld);
        }
        miniGame.sendMessageToLobby(MGConstants.worldsCreatedMessage(miniGame.getMgSettings().getGameType().getGameName()));
    }

    public void unloadWorlds() {
        this.unloadWorld(this.defaultWorld);
        this.unloadWorld(this.netherWorld);
    }

    private void unloadWorld(World world) {
        getServer().unloadWorld(world.getName(), true);
        try {
            FileUtils.deleteDirectory(new File(world.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[Task] World '" + world.getName() + "' unloaded.");
    }

}
