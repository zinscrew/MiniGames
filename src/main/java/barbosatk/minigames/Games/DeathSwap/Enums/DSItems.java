package barbosatk.minigames.Games.DeathSwap.Enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum DSItems {

    SWAP_MODE_NORMAL(Material.BOOK, "§6Alterar o modo de troca", Arrays.asList("§6Modos de troca:", "§f" + SwapType.NORMAL.getName())),
    SWAP_MODE_RANDOM(Material.BOOK, "§6Alterar o modo de troca", Arrays.asList("§6Modos de troca:", "§f" + SwapType.RANDOM.getName())),
    SWAP_MODE_SEQUENTIAL(Material.BOOK, "§6Alterar o modo de troca", Arrays.asList("§6Modos de troca:", "§f" + SwapType.SEQUENTIAL.getName())),
    SHOW_DAMAGE(Material.SLIME_BALL, "§6Visibilidade do dano dos jogadores", Collections.singletonList("§fMostrar")),
    HIDE_DAMAGE(Material.SLIME_BALL, "§6Visibilidade do dano dos jogadores", Collections.singletonList("§fEsconder"));

    private Material material;
    private String displayName;
    private List<String> lore;

    DSItems(Material material, String displayName, List<String> lore) {
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static DSItems whatItemIs(ItemStack itemStack) {
        for (DSItems dsItem : DSItems.values()) {
            if (itemStack.isSimilar(dsItem.getItemStack()))
                return dsItem;
        }
        return null;
    }

}