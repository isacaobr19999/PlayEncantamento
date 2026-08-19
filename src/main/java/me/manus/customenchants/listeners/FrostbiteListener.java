package me.manus.customenchants.listeners;
import me.manus.customenchants.utils.NotificationUtils;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class FrostbiteListener implements Listener {

    private final JavaPlugin plugin;
    private final Enchantment frostbiteEnchantment;
    private final Random random = new Random();

    public FrostbiteListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.frostbiteEnchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                .get(Key.key("customenchants:frostbite"));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        
        Player player = (Player) event.getDamager();
        LivingEntity target = (LivingEntity) event.getEntity();
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (hand != null && hand.hasItemMeta() && hand.getItemMeta().hasEnchant(frostbiteEnchantment)) {
            int level = hand.getItemMeta().getEnchantLevel(frostbiteEnchantment);
            double chance = 0.1 * level; // 10% por nível

            if (random.nextDouble() < chance) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 4)); // Lentidão V por 2 segundos
                target.getWorld().spawnParticle(Particle.SNOWFLAKE, target.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
                
                NotificationUtils.sendActionBar(player, "&b&lALVO CONGELADO!");
            }
        }
    }
}
