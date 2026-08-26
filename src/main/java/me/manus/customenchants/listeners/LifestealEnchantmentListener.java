package me.manus.customenchants.listeners;
import me.manus.customenchants.CustomEnchants;
import me.manus.customenchants.managers.EnchantXPManager;
import me.manus.customenchants.utils.NBTUtils;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class LifestealEnchantmentListener implements Listener {

    private final JavaPlugin plugin;
    private final Enchantment lifestealEnchantment;

    public LifestealEnchantmentListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.lifestealEnchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                .get(Key.key("customenchants:lifesteal"));
        if (this.lifestealEnchantment == null) {
            plugin.getLogger().warning("Lifesteal enchantment not found! Make sure it's registered correctly.");
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack handItem = player.getInventory().getItemInMainHand();

            if (lifestealEnchantment != null && handItem.hasItemMeta() && handItem.getItemMeta().hasEnchant(lifestealEnchantment)) {
                if (!plugin.getConfig().getBoolean("enchantments.lifesteal.enabled", true)) return;

                CustomEnchants cePlugin = (CustomEnchants) plugin;
                double cd = plugin.getConfig().getDouble("enchantments.lifesteal.cooldown", 2.0);
                
                if (cePlugin.getCooldownManager().isOnCooldown(player.getUniqueId(), "lifesteal", cd)) {
                    String cdMsg = plugin.getConfig().getString("enchantments.lifesteal.messages.cooldown", " &6Aguarde <time>s!")
                            .replace("<time>", String.format("%.1f", cePlugin.getCooldownManager().getRemaining(player.getUniqueId(), "lifesteal")));
                    player.sendActionBar(Component.text(cdMsg.replace("&", "§")));
                    return;
                }

                int level = handItem.getItemMeta().getEnchantLevel(lifestealEnchantment);
                double damage = event.getDamage();
                double multiplier = plugin.getConfig().getDouble("enchantments.lifesteal.heal_multiplier", 0.05);
                double healAmount = damage * (multiplier * level);

                double maxHealth = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
                double newHealth = Math.min(player.getHealth() + healAmount, maxHealth);
                player.setHealth(newHealth);
                
                // Rastreamento de Estatísticas e XP
                NBTUtils.addStat(handItem, "lifesteal_healed", healAmount);
                EnchantXPManager.addXP(player, handItem, "lifesteal", damage * 2.0);
                
                // Efeito de partículas (Corações)
                player.getWorld().spawnParticle(org.bukkit.Particle.HEART, player.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0.1);
                
                cePlugin.getCooldownManager().setCooldown(player.getUniqueId(), "lifesteal", cd);

                String msg = plugin.getConfig().getString("enchantments.lifesteal.messages.action_bar", " &c+<amount>❤")
                        .replace("<amount>", String.format("%.1f", healAmount));
                player.sendActionBar(Component.text(msg.replace("&", "§")));
            }
        }
    }
}
