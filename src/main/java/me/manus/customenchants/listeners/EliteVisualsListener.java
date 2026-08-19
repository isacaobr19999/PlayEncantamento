package me.manus.customenchants.listeners;
import me.manus.customenchants.utils.NBTUtils;

import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EliteVisualsListener implements Listener {

    private final JavaPlugin plugin;

    public EliteVisualsListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getProjectile() instanceof Arrow arrow)) return;
        
        ItemStack bow = event.getBow();
        if (bow == null) return;

        // Rastro de Partículas para Arcos Míticos/Divinos
        String tier = NBTUtils.getTier(bow);
        if (tier.equalsIgnoreCase("DIVINO") || tier.equalsIgnoreCase("MÍTICO")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (arrow.isDead() || arrow.isOnGround()) {
                        this.cancel();
                        return;
                    }
                    arrow.getWorld().spawnParticle(Particle.END_ROD, arrow.getLocation(), 2, 0.05, 0.05, 0.05, 0.01);
                }
            }.runTaskTimer(plugin, 1L, 1L);
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player player)) return;

        ItemStack boots = player.getInventory().getBoots();
        if (boots != null && NBTUtils.hasEnchant(boots, "flight")) {
            // Efeito de impacto ao cair (simulado por projétil para fins de elite)
            arrow.getWorld().spawnParticle(Particle.EXPLOSION, arrow.getLocation(), 1, 0, 0, 0, 0.1);
        }
    }
}
