package barbosatk.minigames.Games.DeathSwap;

import barbosatk.minigames.Config.Contants.MGConstants;
import barbosatk.minigames.Enums.MGItems;
import barbosatk.minigames.Games.DeathSwap.Class.Swap;
import barbosatk.minigames.Games.DeathSwap.SwapTypes.SwapNormal;
import barbosatk.minigames.Games.DeathSwap.SwapTypes.SwapRandom;
import barbosatk.minigames.Games.DeathSwap.SwapTypes.SwapSequential;
import barbosatk.minigames.Helper.MGSupport;
import barbosatk.minigames.MiniGames;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class DeathSwapLogic {

    private DeathSwap deathSwap;
    private Swap swap = null;

    public void setSwap(Swap swap) {
        this.swap = swap;
    }

    public DeathSwapLogic(DeathSwap miniGame) {
        this.deathSwap = miniGame;
    }

    // finds all the players that joined the Trader and do stuff with them
    // :TODO restructure teleport to allow start game teleport and end game teleport
    public void teleportPlayers() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(MiniGames.getPlugin(MiniGames.class), () -> {
            for (Player player : deathSwap.getLobbyPlayers()) {
                // Remove player privileges
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("deop %s", player.getName()));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("gamemode survival %s", player.getName()));

                // Save players' world information
                Location loc;
                do {
                    int x = locationReturn();
                    int z = locationReturn();
                    int yZ = deathSwap.getMgWorlds().getMGWorld().getHighestBlockYAt(x, z) + 1;
                    loc = new Location(deathSwap.getMgWorlds().getMGWorld(), x, yZ, z);
                } while (checkWaterOrRoof(loc));

                deathSwap.teleportPlayer(player, loc);

                // Set player contents after teleport
                this.setPlayerKit(player);
            }
        }, 0);
    }

    // return random location to use in x and z positions of teleport.
    private int locationReturn() {
        Random r = new Random();
        int low = -((30000 / 2) - 1);
        int high = ((30000 / 2) - 1);
        return r.nextInt(high - low) + low;
    }

    // check if the under block is water or above if it has any block
    private boolean checkWaterOrRoof(Location loc) {
        int yCheck = loc.getBlockY() + 2;

        Block bCheck = Objects.requireNonNull(deathSwap.getMgWorlds().getMGWorld().getBlockAt(loc.getBlockX(), yCheck, loc.getBlockZ()));
        return (bCheck.getType() != Material.AIR) || (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.WATER);
    }

    public void setPlayerKit(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 13000, 1));
        player.getInventory().addItem(new ItemStack(Material.IRON_AXE, 1));
        player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE, 1));
        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 14));
        player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET, 1));
        player.getInventory().addItem(new ItemStack(Material.CRAFTING_TABLE, 1));
        player.getInventory().setItem(17, MGItems.MGItem.MG_MENU.getItemStack());
    }

    public void createRecipe(boolean create) {
        ShapedRecipe goldenBoost = new ShapedRecipe(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1)).shape("aaa", "ggg", "aaa").setIngredient('g', Material.GOLDEN_APPLE).setIngredient('a', Material.AIR);
        ShapedRecipe slimeBall = new ShapedRecipe(new ItemStack(Material.SLIME_BALL, 1)).shape("iii", "ibi", "iii").setIngredient('i', Material.IRON_BLOCK).setIngredient('b', Material.BOOK);
        ShapedRecipe diamonds = new ShapedRecipe(new ItemStack(Material.DIAMOND, 1)).shape("aaa", "rrr", "aaa").setIngredient('r', Material.REDSTONE_BLOCK).setIngredient('a', Material.AIR);
        if (create) {
            getServer().addRecipe(goldenBoost);
            getServer().addRecipe(slimeBall);
            getServer().addRecipe(diamonds);
        } else {
            getServer().removeRecipe(goldenBoost.getKey());
            getServer().removeRecipe(slimeBall.getKey());
            getServer().removeRecipe(diamonds.getKey());
        }
    }


    private final int DEATH_SWAP_INTERVAL_TIME = 300;
    private int timerCount, timer = this.DEATH_SWAP_INTERVAL_TIME;

    public void swapTimer(boolean cancel) {
        if (!cancel) {
            timerCount = Bukkit.getScheduler().scheduleSyncRepeatingTask(MiniGames.getPlugin(MiniGames.class), new Runnable() {
                @Override
                public void run() {
                    if (timer != -1) {
                        if (timer != 0) {
                            if (timer == 25) {
                                deathSwap.sendMessageToLobby(MGConstants.chatTagSuccess(deathSwap.getMgSettings().getGameType().getGameName()) + "§6Teleporte em §a25 §6segundos.");
                                MGSupport.playSoundForPlayers(deathSwap.getMiniGamePlayers(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1f, 1f);
                            }
                            if (timer <= 5) {
                                deathSwap.sendMessageToLobby(MGConstants.chatTagSuccess(deathSwap.getMgSettings().getGameType().getGameName()) + "§6Teleporte em §a" + timer + "§6 segundos.");
                                MGSupport.playSoundForPlayers(deathSwap.getMiniGamePlayers(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1f, 1f);
                                MGSupport.showTitleForPlayers(deathSwap.getMiniGamePlayers(), "", "§6" + timer, 12, 5, 0);
                            }
                        } else {
                            deathSwap.sendMessageToLobby(MGConstants.chatTagSuccess(deathSwap.getMgSettings().getGameType().getGameName()) + "§6Efetuada a troca! Boa Sorte" + "§c `-´ ");
                            if (swap != null)
                                swap.run();
                            Bukkit.getScheduler().scheduleSyncDelayedTask(MiniGames.getPlugin(MiniGames.class), () -> {
                                swap = swapPlayers().load();
                            }, 400L); // 20 sec.

                        }
                        timer--;
                    } else {
                        timer = DEATH_SWAP_INTERVAL_TIME;
                    }
                }
            }, 0L, 20L);
        } else {
            Bukkit.getScheduler().cancelTask(timerCount);
        }
    }


    public Swap swapPlayers() {
        Swap swap;
        List<Player> tempPlayers = this.deathSwap.getMiniGamePlayers();
        switch (this.deathSwap.getSwapType()) {
            case NORMAL:
                swap = new SwapNormal(tempPlayers);
                break;
            case RANDOM:
                swap = new SwapRandom(tempPlayers);
                break;
            case SEQUENTIAL:
                swap = new SwapSequential(tempPlayers);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.deathSwap.getSwapType());
        }
        return swap;
    }

}
