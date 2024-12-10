package barbosatk.minigames.Enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MGItems {

    public enum MGItem {

        // MiniGames Items
        DEATH_SWAP_GAME_BUTTON(Material.DIAMOND_SWORD, "§a" + GameType.DEATH_SWAP.getGameNameWAbbrev(), GameType.DEATH_SWAP, Arrays.asList("§7---------------------------", "§6De §a5 §6em §a5 §6minutos, os jogadores", "§6trocam de posições entre si.", "§aGanha§6 o último sobrevivente.")),
        HNS_GAME_BUTTON(Material.GOLDEN_SWORD, "§a" + GameType.HIDE_AND_SEEK.getGameNameWAbbrev(), GameType.HIDE_AND_SEEK, Arrays.asList("§7-------------------------------------", "§6Haverá sempre uma proporção de §a1:5 §6entre", "§6quem está a apanhar e quem se está a es-", "§6conder. §aGanha §6quem conseguir mais pontos.", "§6- §a1 Ponto §6por cada pessoa que apanhares.", "§6- §a1 Ponto §6se estiveres vivo, quando um", "§6jogador for apanhado.", "§7-------------------------------------", "§6Haverá uma ronda para cada jogador a", "§6apanhar os restantes.")),
        /* ---------------------------------------------------------------------------------------------------- */

        // Item for MiniGames menu
        MG_MENU(Material.NETHER_STAR, "§6Menu de Mini-Games", null, Collections.singletonList("§aClica para abrires o menu de Mini-Games")),
        // Items to manage lobbies
        BACK_BUTTON(Material.RED_STAINED_GLASS_PANE, "§eVoltar", null, null),
        CREATE_LOBBY(Material.LECTERN, "§aCriar Lobby", null, Arrays.asList("§6Clica para criares a tua lobby","§7---------------------------","","§a* §6Se já tiveres uma lobby, serás","§6redirectionado para a mesma.")),
        DELETE_LOBBY(Material.RED_BANNER, "§cApagar a Lobby", null, Collections.singletonList("§6Clica para apagares a tua lobby")),
        SPECTATE_GAME(Material.ENDER_EYE, "§eAssistir ao jogo", null, Collections.singletonList("§6Clica para assistires ao jogo")),
        GAME_SETTINGS(Material.BREWING_STAND, "§dDefinições do jogo", null, Arrays.asList("§6Clicar para alterar as definições", "§6padrão do jogo.")),
        //BACK_TO_LOBBY(Material.LECTERN, "§aVoltar à Lobby", Collections.singletonList("§6Clica para entrares na tua Lobby"), Arrays.asList(Material.LECTERN, Material.LECTERN), null),

        ADD_LOBBY(Material.LIME_WOOL, "§aEntrar na Lobby", null, Collections.singletonList("§6Clica para entrares na Lobby")),
        LEAVE_LOBBY(Material.RED_WOOL, "§cSair da Lobby", null, Collections.singletonList("§6Clica para saires da Lobby atual")),

        CANCEL_GAME_BUTTON(Material.WHITE_BANNER, "§cCancelar o jogo", null, Collections.singletonList("§6Clica para cancelar a contagem de início de jogo")),
        START_GAME_BUTTON(Material.GOLDEN_SWORD, "§fComeçar jogo", null, Collections.singletonList("§6Clica para começar o jogo")),

        // InGame Items
        END_GAME_REQUEST_BUTTON(Material.RED_BANNER, "§cPedir para terminar o jogo", null, Collections.singletonList("§6Clica para pedires que o jogo termine")),
        SURRENDER_BUTTON(Material.CHICKEN, "§fSou fraco", null, Collections.singletonList("§6Clica para desistires do jogo"));


        private Material material;
        private String displayName;
        private GameType gameType;
        private List<String> lore;

        MGItem(Material material, String displayName, GameType gameType, List<String> lore) {
            this.material = material;
            this.displayName = displayName;
            this.gameType = gameType;
            this.lore = lore;
        }

        public ItemStack getItemStack() {
            ItemStack itemstack = new ItemStack(material, 1);
            ItemMeta itemMeta = itemstack.getItemMeta();
            itemMeta.setDisplayName(displayName);
            itemMeta.setLore(lore);
            itemstack.setItemMeta(itemMeta);
            return itemstack;
        }

        public GameType getGameType() {
            return this.gameType;
        }

    }

    // MGItems class methods
    public static MGItem whatMenuItemIs(ItemStack item) {
        for (MGItem mgItem : MGItems.MGItem.values()) {
            if (item.isSimilar(mgItem.getItemStack())) {
                return mgItem;
            }
        }
        return null;
    }

}
