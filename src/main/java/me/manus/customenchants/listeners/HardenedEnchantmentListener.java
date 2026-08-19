package me.manus.customenchants.listeners;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class HardenedEnchantmentListener implements Listener {

    private final JavaPlugin plugin;
    private final Enchantment hardenedEnchantment;

    public HardenedEnchantmentListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.hardenedEnchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                .get(Key.key("customenchants:hardened"));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        int totalLevel = 0;
        for (org.bukkit.inventory.ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null && armor.hasItemMeta() && armor.getItemMeta().hasEnchant(hardenedEnchantment)) {
                totalLevel += armor.getItemMeta().getEnchantLevel(hardenedEnchantment);
            }
        }

        if (totalLevel > 0) {
            double reduction = totalLevel * 0.05; // 5% de redução por nível total
            event.setDamage(event.getDamage() * (1 - reduction));
            
            // Partículas de escudo
            if (totalLevel > 5) {
                player.getWorld().spawnParticle(org.bukkit.Particle.ENCHANTED_HIT, player.getLocation().add(0, 1, 0), 5, 0.3, 0.3, 0.3, 0.05);
            }
        }
    }
}
