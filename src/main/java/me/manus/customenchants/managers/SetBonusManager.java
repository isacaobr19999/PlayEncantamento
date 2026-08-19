package me.manus.customenchants.managers;
import me.manus.customenchants.utils.NBTUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class SetBonusManager {

    private final JavaPlugin plugin;

    public SetBonusManager(JavaPlugin plugin) {
        this.plugin = plugin;
        startSetBonusTask();
    }

    private void startSetBonusTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    checkSetBonuses(player);
                }
            }
        }.runTaskTimer(plugin, 40L, 40L); // A cada 2 segundos
    }

    private void checkSetBonuses(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        
        int hardenedCount = 0;
        int flightCount = 0;
        int vampirismCount = 0;

        for (ItemStack piece : armor) {
            if (piece == null || piece.getType().isAir()) continue;
            
            if (NBTUtils.hasEnchant(piece, "hardened")) hardenedCount++;
            if (NBTUtils.hasEnchant(piece, "flight")) flightCount++;
            if (NBTUtils.hasEnchant(piece, "vampirism")) vampirismCount++;
        }

        // Bônus de Resistência (Hardened Full Set)
        if (hardenedCount == 4) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, 1, false, false, true));
        }

        // Bônus de Velocidade (Flight Full Set)
        if (flightCount == 4) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1, false, false, true));
        }

        // Bônus de Visão Noturna (Vampirism Full Set - apenas à noite)
        if (vampirismCount == 4) {
            long time = player.getWorld().getTime();
            if (time > 13000 && time < 23000) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 300, 0, false, false, true));
            }
        }
    }
}
