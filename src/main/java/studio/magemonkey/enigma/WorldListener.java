package studio.magemonkey.enigma;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;

public class WorldListener implements Listener {
    public static void init() {
        Bukkit.getPluginManager().registerEvents(new WorldListener(), Enigma.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChunkLoad(final ChunkLoadEvent event) {
        final WorldChests c = Enigma.getWorld(event.getWorld().getName());
        if (c == null) {
            return;
        }
        c.onLoadChunk(event.getChunk());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if ((event.getAction() != Action.RIGHT_CLICK_BLOCK) && (event.getAction() != Action.LEFT_CLICK_BLOCK)) {
            return;
        }
        final WorldChests c = Enigma.getWorld(event.getPlayer().getWorld().getName());
        if (c == null) {
            return;
        }
        if (c.onInteract(event.getPlayer(), event.getClickedBlock())) {
            event.setCancelled(true);
        }
    }

}
