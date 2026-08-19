package me.manus.customenchants.listeners;

import me.manus.customenchants.CustomEnchants;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.scheduler.BukkitRunnable;

public class MendingTwoListener {

    private final CustomEnchants plugin;
    private final Enchantment mendingTwo;

    public MendingTwoListener(CustomEnchants plugin) {
        this.plugin = plugin;
        this.mendingTwo = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                .get(Key.key("customenchants:mending_two"));
        startRepairTask();
    }

    private void startRepairTask() {
        int interval = plugin.getConfig().getInt("enchantments.mending_two.repair_interval_seconds", 10) * 20;
        int xpCost = plugin.getConfig().getInt("enchantments.mending_two.xp_cost_per_repair", 2);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getConfig().getBoolean("enchantments.mending_two.enabled", true)) return;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    // Verificar apenas itens equipados e na mão para performance
                    ItemStack[] itemsToCheck = {
                            player.getInventory().getItemInMainHand(),
                            player.getInventory().getHelmet(),
                            player.getInventory().getChestplate(),
                            player.getInventory().getLeggings(),
                            player.getInventory().getBoots()
                    };

                    for (ItemStack item : itemsToCheck) {
                        if (item != null && item.hasItemMeta() && item.getItemMeta().hasEnchant(mendingTwo)) {
                            if (item.getItemMeta() instanceof Damageable damageable) {
                                if (damageable.getDamage() > 0) {
                                    if (player.getTotalExperience() >= xpCost) {
                                        damageable.setDamage(damageable.getDamage() - 1);
                                        item.setItemMeta(damageable);
                                        player.giveExp(-xpCost);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, interval, interval);
    }
}
