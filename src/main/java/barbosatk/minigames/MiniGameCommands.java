package barbosatk.minigames;

import barbosatk.minigames.Config.Contants.MGConstants;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MiniGameCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        switch (strings[0]) {
            case "create":
                WorldCreator wc = new WorldCreator(MGConstants.PATH_TO_MG_WORKING_WORLDS + strings[1]);
                wc.type(WorldType.FLAT);
                wc = wc.generator(new ChunkGenerator() {
                    @Override
                    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
                        ChunkData chunkData = super.createChunkData(world);
                        return chunkData;
                    }
                });
                World world = wc.createWorld();
                world.setAutoSave(false);
                world.setSpawnLocation(0, 50, 0);
                world.getBlockAt(0, 49, 0).setType(Material.STONE);
                world.setTime(4000);
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                break;
            case "tp":
                if (strings[1].equals("world")) {
                    player.teleport(Bukkit.getWorld("world").getSpawnLocation());
                    break;
                }

                World tempWorld = Bukkit.getWorld(MGConstants.PATH_TO_MG_WORKING_WORLDS + strings[1]);
                if (tempWorld != null)
                    player.teleport(tempWorld.getSpawnLocation());
                else
                    player.sendMessage("Não existe nenhum mundo com o nome '" + strings[1] + "'");
                break;
        }

        return false;
    }
}