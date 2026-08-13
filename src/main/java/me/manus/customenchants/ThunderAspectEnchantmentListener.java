package me.manus.customenchants;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class ThunderAspectEnchantmentListener implements Listener {

    private final JavaPlugin plugin;
    private final Enchantment thunderAspectEnchantment;
    private final Random random = new Random();

    public ThunderAspectEnchantmentListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.thunderAspectEnchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                .get(Key.key("customenchants:thunder_aspect"));
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;

        Player player = (Player) event.getDamager();
        LivingEntity target = (LivingEntity) event.getEntity();
        ItemStack handItem = player.getInventory().getItemInMainHand();

        if (thunderAspectEnchantment != null && handItem.hasItemMeta() && handItem.getItemMeta().hasEnchant(thunderAspectEnchantment)) {
            CustomEnchants cePlugin = (CustomEnchants) plugin;
            double cd = plugin.getConfig().getDouble("enchantments.thunder_aspect.cooldown", 5.0);

            if (cePlugin.getCooldownManager().isOnCooldown(player.getUniqueId(), "thunder", cd)) return;

            int level = handItem.getItemMeta().getEnchantLevel(thunderAspectEnchantment);
            double chance = plugin.getConfig().getDouble("enchantments.thunder_aspect.chance_per_level", 0.2) * level;
            
            if (random.nextDouble() < chance) {
                if (plugin.getConfig().getBoolean("enchantments.thunder_aspect.effects.lightning", true)) {
                    target.getWorld().strikeLightningEffect(target.getLocation());
                }
                
                if (plugin.getConfig().getBoolean("enchantments.thunder_aspect.effects.sound", true)) {
                    target.getWorld().playSound(target.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
                }

                if (plugin.getConfig().getBoolean("enchantments.thunder_aspect.effects.particles", true)) {
                    target.getWorld().spawnParticle(org.bukkit.Particle.ELECTRIC_SPARK, target.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
                }
                
                double extraDamage = plugin.getConfig().getDouble("enchantments.thunder_aspect.damage", 5.0);
                target.damage(extraDamage, player);
                
                // Rastreamento de Estatísticas e XP
                NBTUtils.addStat(handItem, "thunder_strikes", 1);
                EnchantXPManager.addXP(player, handItem, "thunder_aspect", 50.0);
                
                cePlugin.getCooldownManager().setCooldown(player.getUniqueId(), "thunder", cd);
            }
        }
    }
}
