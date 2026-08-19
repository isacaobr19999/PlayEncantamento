package me.manus.customenchants.listeners;

import me.manus.customenchants.managers.EnchantXPManager;
import me.manus.customenchants.utils.NBTUtils;
import me.manus.customenchants.hooks.ProtectionHook;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ExplosivePickaxeEnchantmentListener implements Listener {

    private final JavaPlugin plugin;
    private final Enchantment explosivePickaxeEnchantment;
    private final Set<UUID> breaking = new HashSet<>();

    public ExplosivePickaxeEnchantmentListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.explosivePickaxeEnchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                .get(Key.key("customenchants:explosive_pickaxe"));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (breaking.contains(player.getUniqueId())) return;

        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (explosivePickaxeEnchantment == null || !handItem.hasItemMeta() || !handItem.getItemMeta().hasEnchant(explosivePickaxeEnchantment)) return;
        if (!plugin.getConfig().getBoolean("enchantments.explosive_pickaxe.enabled", true)) return;
        if (player.getGameMode() != GameMode.SURVIVAL) return;

        breaking.add(player.getUniqueId());
        try {
            Block brokenBlock = event.getBlock();
            int radius = plugin.getConfig().getInt("enchantments.explosive_pickaxe.area_radius", 1);
            boolean breakBedrock = plugin.getConfig().getBoolean("enchantments.explosive_pickaxe.break_bedrock", false);
            int blocksBroken = 0;

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;

                        Block targetBlock = brokenBlock.getRelative(x, y, z);
                        if (targetBlock.getType() == Material.AIR) continue;
                        if (targetBlock.getType() == Material.BEDROCK && !breakBedrock) continue;

                        // Simular evento de quebra para respeitar outros plugins
                        BlockBreakEvent subEvent = new BlockBreakEvent(targetBlock, player);
                        Bukkit.getPluginManager().callEvent(subEvent);

                        if (!subEvent.isCancelled() && ProtectionHook.canBreakBlock(player, targetBlock)) {
                            targetBlock.breakNaturally(handItem);
                            blocksBroken++;
                            targetBlock.getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION, targetBlock.getLocation().add(0.5, 0.5, 0.5), 1);
                        }
                    }
                }
            }

            if (blocksBroken > 0) {
                NBTUtils.addStat(handItem, "explosive_blocks", blocksBroken);
                EnchantXPManager.addXP(player, handItem, "explosive_pickaxe", blocksBroken * 5.0);
            }
        } finally {
            breaking.remove(player.getUniqueId());
        }
    }
}
