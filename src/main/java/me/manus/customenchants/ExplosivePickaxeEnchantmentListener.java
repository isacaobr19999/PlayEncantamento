package me.manus.customenchants;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ExplosivePickaxeEnchantmentListener implements Listener {

    private final JavaPlugin plugin;
    private final Enchantment explosivePickaxeEnchantment;

    public ExplosivePickaxeEnchantmentListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.explosivePickaxeEnchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                .get(Key.key("customenchants:explosive_pickaxe"));
        if (this.explosivePickaxeEnchantment == null) {
            plugin.getLogger().warning("Explosive Pickaxe enchantment not found! Make sure it's registered correctly.");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack handItem = player.getInventory().getItemInMainHand();

        if (explosivePickaxeEnchantment != null && handItem.hasItemMeta() && handItem.getItemMeta().hasEnchant(explosivePickaxeEnchantment)) {
            if (!plugin.getConfig().getBoolean("enchantments.explosive_pickaxe.enabled", true)) return;
            
            if (player.getGameMode() == GameMode.SURVIVAL) {
                Block brokenBlock = event.getBlock();
                int radius = plugin.getConfig().getInt("enchantments.explosive_pickaxe.area_radius", 1);
                boolean breakBedrock = plugin.getConfig().getBoolean("enchantments.explosive_pickaxe.break_bedrock", false);
                int blocksBroken = 0;

                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            Block targetBlock = brokenBlock.getRelative(x, y, z);
                            if (targetBlock.getType() != Material.AIR) {
                                if (targetBlock.getType() == Material.BEDROCK && !breakBedrock) continue;
                                
                                if (ProtectionHook.canBreakBlock(player, targetBlock)) {
                                    targetBlock.breakNaturally(handItem);
                                    blocksBroken++;
                                    // Partículas de explosão
                                    targetBlock.getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION, targetBlock.getLocation().add(0.5, 0.5, 0.5), 1);
                                }
                            }
                        }
                    }
                }
                
                if (blocksBroken > 0) {
                    NBTUtils.addStat(handItem, "explosive_blocks", blocksBroken);
                    EnchantXPManager.addXP(player, handItem, "explosive_pickaxe", blocksBroken * 5.0);
                }
            }
        }
    }
}
