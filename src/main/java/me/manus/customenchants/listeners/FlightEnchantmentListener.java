package me.manus.customenchants.listeners;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class FlightEnchantmentListener implements Listener {

    private final JavaPlugin plugin;
    private final Enchantment flightEnchantment;
    private final Set<UUID> grantedFlight = new HashSet<>();

    public FlightEnchantmentListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.flightEnchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                .get(Key.key("customenchants:flight"));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

        ItemStack boots = player.getInventory().getBoots();
        boolean hasFlight = boots != null && boots.hasItemMeta() && boots.getItemMeta().hasEnchant(flightEnchantment);

        if (hasFlight) {
            if (!player.getAllowFlight()) {
                player.setAllowFlight(true);
                grantedFlight.add(player.getUniqueId());
            }
        } else if (grantedFlight.remove(player.getUniqueId())) {
            // Remove somente o voo concedido por este plugin.
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        grantedFlight.remove(event.getPlayer().getUniqueId());
    }
}
