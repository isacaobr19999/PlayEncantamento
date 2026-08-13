package me.manus.customenchants;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AuraManager {

    private final JavaPlugin plugin;

    public AuraManager(JavaPlugin plugin) {
        this.plugin = plugin;
        startAuraTask();
    }

    private void startAuraTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ItemStack mainHand = player.getInventory().getItemInMainHand();
                    
                    // Aura de Trovão (Aspecto do Trovão)
                    if (NBTUtils.hasEnchant(mainHand, "thunder_aspect")) {
                        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation().add(0, 1, 0), 3, 0.4, 0.4, 0.4, 0.02);
                    }
                    
                    // Aura de Fogo (Vampirismo - efeito visual)
                    if (NBTUtils.hasEnchant(mainHand, "vampirism")) {
                        player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation().add(0, 1, 0), 2, 0.3, 0.3, 0.3, 0.01);
                    }

                    // Aura de Voo (Botas)
                    ItemStack boots = player.getInventory().getBoots();
                    if (NBTUtils.hasEnchant(boots, "flight")) {
                        if (player.isFlying()) {
                            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 5, 0.2, 0.1, 0.2, 0.01);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // A cada 1 segundo
    }
}
