package me.manus.customenchants;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SoulboundListener implements Listener {

    private final JavaPlugin plugin;
    private final Enchantment soulboundEnchantment;
    private final Map<UUID, List<ItemStack>> savedItems = new HashMap<>();

    public SoulboundListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.soulboundEnchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                .get(Key.key("customenchants:soulbound"));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        List<ItemStack> toSave = new ArrayList<>();
        List<ItemStack> drops = event.getDrops();
        List<ItemStack> toRemoveFromDrops = new ArrayList<>();

        for (ItemStack item : drops) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasEnchant(soulboundEnchantment)) {
                toSave.add(item.clone());
                toRemoveFromDrops.add(item);
                EnchantXPManager.addXP(player, item, "soulbound", 100.0);
            }
        }

        drops.removeAll(toRemoveFromDrops);
        if (!toSave.isEmpty()) {
            savedItems.put(player.getUniqueId(), toSave);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (savedItems.containsKey(player.getUniqueId())) {
            List<ItemStack> items = savedItems.remove(player.getUniqueId());
            for (ItemStack item : items) {
                player.getInventory().addItem(item);
            }
        }
    }
}
