package barbosatk.minigames.DefaultEvents;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class NormalEvents implements Listener {

    @EventHandler
    private void onPlayerInventoryFull(PlayerPickupItemEvent event) {
        if (event.getRemaining() >= 1)
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c§lInventory Full"));
    }

}
