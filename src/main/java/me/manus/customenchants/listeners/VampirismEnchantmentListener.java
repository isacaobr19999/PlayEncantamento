package me.manus.customenchants.listeners;

import me.manus.customenchants.managers.EnchantXPManager;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class VampirismEnchantmentListener implements Listener {

    private final JavaPlugin plugin;
    private final Enchantment vampirismEnchantment;

    public VampirismEnchantmentListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.vampirismEnchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                .get(Key.key("customenchants:vampirism"));
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;

        ItemStack handItem = player.getInventory().getItemInMainHand();

        if (vampirismEnchantment != null && handItem.hasItemMeta() && handItem.getItemMeta().hasEnchant(vampirismEnchantment)) {
            if (!plugin.getConfig().getBoolean("enchantments.vampirism.enabled", true)) return;

            long time = player.getWorld().getTime();
            boolean isNight = time >= 13000 && time <= 23000;

            if (isNight) {
                int level = handItem.getItemMeta().getEnchantLevel(vampirismEnchantment);
                double bonus = plugin.getConfig().getDouble("enchantments.vampirism.night_damage_bonus", 1.5);
                double extraDamage = level * bonus;
                event.setDamage(event.getDamage() + extraDamage);
                
                player.getWorld().spawnParticle(org.bukkit.Particle.SMOKE, event.getEntity().getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.05);
                EnchantXPManager.addXP(player, handItem, "vampirism", event.getDamage());
            }
        }
    }
}
