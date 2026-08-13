package me.manus.customenchants;

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
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;

        Player player = (Player) event.getDamager();
        ItemStack handItem = player.getInventory().getItemInMainHand();

        if (vampirismEnchantment != null && handItem.hasItemMeta() && handItem.getItemMeta().hasEnchant(vampirismEnchantment)) {
            long time = player.getWorld().getTime();
            boolean isNight = time >= 13000 && time <= 23000;

            if (isNight) {
                int level = handItem.getItemMeta().getEnchantLevel(vampirismEnchantment);
                double extraDamage = level * 1.5; // +1.5 de dano por nível à noite
                event.setDamage(event.getDamage() + extraDamage);
                
                // Partículas de fumaça preta
                player.getWorld().spawnParticle(org.bukkit.Particle.SMOKE, event.getEntity().getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.05);
                
                EnchantXPManager.addXP(player, handItem, "vampirism", event.getDamage());
            }
        }
    }
}
