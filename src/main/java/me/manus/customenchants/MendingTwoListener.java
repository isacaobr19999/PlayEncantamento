package me.manus.customenchants;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MendingTwoListener {

    private final JavaPlugin plugin;
    private final Enchantment mendingTwo;

    public MendingTwoListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.mendingTwo = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                .get(Key.key("customenchants:mending_two"));
        startRepairTask();
    }

    private void startRepairTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (item != null && item.hasItemMeta() && item.getItemMeta().hasEnchant(mendingTwo)) {
                            if (item.getItemMeta() instanceof Damageable damageable) {
                                if (damageable.getDamage() > 0) {
                                    // Reparar 1 de durabilidade a cada 10 segundos se o jogador tiver XP
                                    if (player.getTotalExperience() > 2) {
                                        damageable.setDamage(damageable.getDamage() - 1);
                                        item.setItemMeta(damageable);
                                        player.giveExp(-2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 200L, 200L); // A cada 10 segundos
    }
}
