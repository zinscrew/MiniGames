package barbosatk.minigames.Games.DeathSwap;

import barbosatk.minigames.Config.Contants.MGConstants;
import barbosatk.minigames.Enums.GameStatus;
import barbosatk.minigames.Enums.GameType;
import barbosatk.minigames.Games.DeathSwap.Enums.DSItems;
import barbosatk.minigames.Games.DeathSwap.Enums.SwapType;
import barbosatk.minigames.Games.MiniGame;
import barbosatk.minigames.MiniGames;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class DeathSwap extends MiniGame implements Listener {

    private DeathSwapLogic deathSwapLogic;
    @NotNull
    private SwapType swapType = SwapType.NORMAL;
    private final int INVINCIBILITY_TIMER = 60;
    private final String playerListSBObjective = "showHealth";
    private final String sideBarSBObjective = "DeathSwapSideBar";

    public @NotNull SwapType getSwapType() {
        return swapType;
    }

    public void setSwapType(SwapType swapType) {
        this.swapType = swapType;
        this.sendMessageToLobby(MGConstants.chatTagSuccess(this.getMgSettings().getGameType().getGameName()) + "Modo de troca em: " + "§f" + this.swapType.getName());
        this.getLobbyPlayers().forEach(player -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6Modo de troca em: " + "§f" + this.swapType.getName())));
    }

    @Override
    public void customizeWorld(World world, World netherWorld) {
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setTime(6000);
        netherWorld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
    }

    @Override
    public void beforeStartGame() {
        this.deathSwapLogic = new DeathSwapLogic(this);
        this.deathSwapLogic.teleportPlayers();
        this.deathSwapLogic.swapTimer(false);
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.getPlugin(MiniGames.class));
    }

    @Override
    public void afterGameStartedTasks() {
        this.sendMessageToLobby(MGConstants.chatTagSuccess(this.getMgSettings().getGameType().getGameName()) + "§6As trocas serão efetuadas a cada §a5 §6minutos.");
        this.noDamage(false);
        this.deathSwapLogic.setSwap(this.deathSwapLogic.swapPlayers().load());
    }

    private int noDamageTask, noDamageCountDown = this.INVINCIBILITY_TIMER;

    private void noDamage(boolean cancel) {
        if (cancel) {
            Bukkit.getScheduler().cancelTask(noDamageTask);
            return;
        }

        noDamageCountDown = this.INVINCIBILITY_TIMER;
        noDamageTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(MiniGames.getPlugin(MiniGames.class), new Runnable() {
            @Override
            public void run() {
                if (noDamageCountDown >= 1) {
                    getMiniGamePlayers().forEach(player -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6A invencibilidade termina dentro de §a" + noDamageCountDown + " §6segundos")));
                    noDamageCountDown--;
                } else {
                    Bukkit.getScheduler().cancelTask(noDamageTask);
                    getMiniGamePlayers().forEach(player -> player.sendTitle("", "§c A proteção inicial terminou", 15, 80, 15));
                }
            }
        }, 0, 20);
    }

    @Override
    public void endGame() {
        System.out.println("[i] EndGame was called!");
        this.noDamage(true);
        this.deathSwapLogic.swapTimer(true);
        // Remove World >> Later on when worlds are custom
    }

    @Override
    public void unregisterAllListeners() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void renderScoreboard(Scoreboard board) {

        Objective playerList = this.registerScoreboardObjectives(board, this.playerListSBObjective, "health");
        playerList.setRenderType(RenderType.HEARTS);
        playerList.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        Objective sideBar = this.registerScoreboardObjectives(board, this.sideBarSBObjective, ChatColor.GRAY + "~ " + this.getMgSettings().getGameType().getGameName() + " ~ ");
        sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score text = sideBar.getScore("DeathSwapp");
        text.setScore(1);
    }

    @Override
    public void insertRecipes(List<ShapedRecipe> mgRecipes) {
        ShapedRecipe goldenBoost = new ShapedRecipe(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1)).shape("aaa", "ggg", "aaa").setIngredient('g', Material.GOLDEN_APPLE).setIngredient('a', Material.AIR);
        ShapedRecipe slimeBall = new ShapedRecipe(new ItemStack(Material.SLIME_BALL, 1)).shape("aaa", "ili", "aaa").setIngredient('i', Material.IRON_INGOT).setIngredient('l', Material.LEATHER);
        ShapedRecipe diamonds = new ShapedRecipe(new ItemStack(Material.DIAMOND, 1)).shape("aaa", "rrr", "aaa").setIngredient('r', Material.REDSTONE).setIngredient('a', Material.AIR);
        mgRecipes.add(goldenBoost);
        mgRecipes.add(slimeBall);
        mgRecipes.add(diamonds);
    }

    @Override
    public void buildSettingsMenu(Inventory mgSettingsInventory) {
        System.out.println("[i] I'm going to show the Settings menu!");
        mgSettingsInventory.setItem(15, DSItems.SWAP_MODE_NORMAL.getItemStack());
        mgSettingsInventory.setItem(16, DSItems.SWAP_MODE_RANDOM.getItemStack());
        mgSettingsInventory.setItem(17, DSItems.SWAP_MODE_SEQUENTIAL.getItemStack());
        mgSettingsInventory.setItem(9, DSItems.SHOW_DAMAGE.getItemStack());
        mgSettingsInventory.setItem(10, DSItems.HIDE_DAMAGE.getItemStack());
    }

    @Override
    public void settingsMenuEventItemClick(ItemStack itemClicked) {
        DSItems dsItems = DSItems.whatItemIs(itemClicked);
        if (dsItems == null)
            return;
        switch (dsItems) {
            case SWAP_MODE_NORMAL:
                this.setSwapType(SwapType.NORMAL);
                break;
            case SWAP_MODE_RANDOM:
                this.setSwapType(SwapType.RANDOM);
                break;
            case SWAP_MODE_SEQUENTIAL:
                this.setSwapType(SwapType.SEQUENTIAL);
                break;
            case SHOW_DAMAGE:
                break;
            case HIDE_DAMAGE:
                break;
        }
    }

    @Override
    public void unregisterScoreboardPlayerList() {
    }


    // Events ---------------------------------------
    @EventHandler
    private void onPlayerDamageEvent(EntityDamageEvent ede) {
        if ((ede.getEntity() instanceof Player) && eventIsAllowedForPlayer((Player) ede.getEntity()))
            if (this.noDamageCountDown >= 1)
                ede.setCancelled(true);
    }

    @EventHandler
    private void playerCuttingLeaves(BlockBreakEvent bbe) {
        if (eventIsAllowedForPlayer(bbe.getPlayer()) && this.getMgSettings().getGameStatus().equals(GameStatus.GAME_RUNNING)) {
            Player player = bbe.getPlayer();
            Block block = bbe.getBlock();
            boolean itsLeafs = false;
            switch (block.getType()) {
                case ACACIA_LEAVES:
                case BIRCH_LEAVES:
                case DARK_OAK_LEAVES:
                case JUNGLE_LEAVES:
                case OAK_LEAVES:
                case SPRUCE_LEAVES:
                    itsLeafs = true;
                    break;
            }
            if ((player.getInventory().getItemInMainHand().getType() == Material.SHEARS) && itsLeafs) {
                Random random = new Random();
                int number = random.nextInt(190) + 1;
                if (number <= 3) { // 1.6% change of getting an apple
                    bbe.getPlayer().getWorld().dropItemNaturally(bbe.getBlock().getLocation(), new ItemStack(Material.APPLE, 1));
                }

                // for all the other natural drops, f.e.: leaf
                int randomDrop = random.nextInt(100) + 1;
                if (randomDrop <= 92) {
                    bbe.setDropItems(false);
                }
            }
        }
    }

    @EventHandler
    private void onAnimalKill(EntityDeathEvent evt) {
        if (evt.getEntity().getKiller() instanceof Player) {
            if (eventIsAllowedForPlayer(evt.getEntity().getKiller()) && this.getMgSettings().getGameStatus().equals(GameStatus.GAME_RUNNING)) {
                boolean cookedFood = false;
                Material type = null;
                Material meat = null;
                switch (evt.getEntity().getType()) {
                    case COW:
                        cookedFood = true;
                        meat = Material.BEEF;
                        type = Material.COOKED_BEEF;
                        break;
                    case CHICKEN:
                        cookedFood = true;
                        meat = Material.CHICKEN;
                        type = Material.COOKED_CHICKEN;
                        break;
                    case PIG:
                        cookedFood = true;
                        meat = Material.PORKCHOP;
                        type = Material.COOKED_PORKCHOP;
                        break;
                    case SHEEP:
                        cookedFood = true;
                        meat = Material.MUTTON;
                        type = Material.COOKED_MUTTON;
                        break;
                    case RABBIT:
                        cookedFood = true;
                        meat = Material.RABBIT;
                        type = Material.COOKED_RABBIT;
                        break;
                }
                if (cookedFood) {
                    int amount;
                    for (ItemStack i : evt.getDrops()) {
                        if (i.getType().equals(meat)) {
                            /*System.out.println("TRUE -> " + i.getType().toString() + " , " + i.getAmount());*/
                            amount = i.getAmount();
                            evt.getDrops().remove(new ItemStack(i.getType(), amount));
                            evt.getDrops().add(new ItemStack(type, amount));
                        }/* else {
                            System.out.println("FALSE -> " + i.getType().toString() + " , " + i.getAmount());
                        }*/
                    }
                }
            }
        }
    }

    @EventHandler
    private void onKillCreeper(EntityDeathEvent ede) {

        if (!(ede.getEntity().getKiller() instanceof Player))
            return;

        if (ede.getEntity().getType() != EntityType.CREEPER)
            return;

        ede.getDrops().add(new ItemStack(Material.GUNPOWDER, random(Material.GUNPOWDER)));
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Player p = event.getPlayer();
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) && eventIsAllowedForPlayer(event.getPlayer())) {
            int y = p.getLocation().getBlockY();
            if (p.getWorld().getName().equals(this.getMgWorlds().getMGWorld().getName())) {
                int x = p.getLocation().getBlockX() / 8;
                int z = p.getLocation().getBlockZ() / 8;
                event.setTo(new Location(Bukkit.getWorld(this.getMgWorlds().getMGNetherWorld().getName()), x, y, z));
            } else if (p.getWorld().getName().equals(this.getMgWorlds().getMGNetherWorld().getName())) {
                int x = p.getLocation().getBlockX() * 8;
                int z = p.getLocation().getBlockZ() * 8;
                event.setTo(new Location(Bukkit.getWorld(this.getMgWorlds().getMGWorld().getName()), x, y, z));
            }
        }
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
            if (this.getLobbyPlayers().contains(event.getPlayer())) {
                event.setCancelled(true);
                p.sendMessage(MGConstants.chatTagUnsuccessful(this.getMgSettings().getGameType().getGameName()) + "O mapa 'end' não está disponível para este MiniGame.");
            }
        }
        System.out.println("[Location] I'm at " + p.getWorld().getName());
    }

    @EventHandler
    public void onCraft(CraftItemEvent craft) {
        if (this.getMgSettings().getGameStatus().equals(GameStatus.GAME_RUNNING) && eventIsAllowedForPlayer((Player) craft.getWhoClicked()))
            if (craft.getRecipe().getResult().getType() == Material.SHIELD || craft.getRecipe().getResult().getType() == Material.ELYTRA || craft.getRecipe().getResult().getType() == Material.ENDER_CHEST)
                craft.setCancelled(true);
    }

    @EventHandler
    private void onBreakBlock(BlockBreakEvent blc) {
        if (this.getMgSettings().getGameStatus().equals(GameStatus.GAME_RUNNING) && eventIsAllowedForPlayer(blc.getPlayer())) {
            boolean oreType = false;
            Material type = null;
            switch (blc.getBlock().getType()) {
                case IRON_ORE:
                    type = Material.IRON_INGOT;
                    oreType = true;
                    break;
                case COAL_ORE:
                    type = Material.COAL;
                    oreType = true;
                    break;
                case GOLD_ORE:
                    type = Material.GOLD_INGOT;
                    oreType = true;
                    break;
                case DIAMOND_ORE:
                    type = Material.DIAMOND;
                    oreType = true;
                    break;
                case NETHER_GOLD_ORE:
                    type = Material.GOLD_NUGGET;
                    oreType = true;
                    break;
            }
            if (oreType) {
                blc.getBlock().setType(Material.AIR);
                blc.getBlock().getWorld().dropItemNaturally(blc.getBlock().getLocation(), new ItemStack(type, random(type)));
            }
        }
    }

    public int random(Material type) {
        Random r = new Random();
        int min, max;
        switch (type) {
            case GOLD_INGOT:
            case GUNPOWDER:
                min = 2;
                max = 4;
                break;
            case GOLD_NUGGET:
                min = 8;
                max = 14;
                break;
            case REDSTONE:
                min = 5;
                max = 10;
                break;
            case DIAMOND:
                min = 1;
                max = 3;
                break;
            default:
                min = 1;
                max = 4;
                break;
        }
        return r.nextInt((max - min) + 1) + min;
    }

    @EventHandler
    private void onEnterPortal(PlayerPortalEvent ppe) {
        if (this.getLobbyPlayers().contains(ppe.getPlayer()) && ppe.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL)
            ppe.setCancelled(true);
    }

}