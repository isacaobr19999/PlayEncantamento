package me.manus.customenchants.listeners;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class DivineAuraListener {

    private final JavaPlugin plugin;
    private final Enchantment divineAura;

    public DivineAuraListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.divineAura = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                .get(Key.key("customenchants:divine_aura"));
        startAuraTask();
    }

    private void startAuraTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (ItemStack piece : player.getInventory().getArmorContents()) {
                        if (piece != null && piece.hasItemMeta() && piece.getItemMeta().hasEnchant(divineAura)) {
                            // Aplicar Força I a aliados próximos
                            for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
                                if (entity instanceof Player ally) {
                                    ally.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 100, 0, false, false, true));
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 100L, 100L); // A cada 5 segundos
    }
}
