package me.manus.customenchants.listeners;
import me.manus.customenchants.utils.NotificationUtils;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BerserkerListener implements Listener {

    private final JavaPlugin plugin;
    private final Enchantment berserkerEnchantment;

    public BerserkerListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.berserkerEnchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                .get(Key.key("customenchants:berserker"));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (hand != null && hand.hasItemMeta() && hand.getItemMeta().hasEnchant(berserkerEnchantment)) {
            int level = hand.getItemMeta().getEnchantLevel(berserkerEnchantment);
            double healthPercent = player.getHealth() / player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
            
            if (healthPercent < 0.5) { // Ativa abaixo de 50% de vida
                double bonus = (1.0 - healthPercent) * level * 2.0;
                event.setDamage(event.getDamage() + bonus);
                
                if (healthPercent < 0.2) {
                    NotificationUtils.sendActionBar(player, "&c&lFÚRIA BERSERKER ATIVADA!");
                }
            }
        }
    }
}
