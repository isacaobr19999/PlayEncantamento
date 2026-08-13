package me.manus.customenchants;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TelekinesisEnchantmentListener implements Listener {

    private final JavaPlugin plugin;
    private final Enchantment telekinesisEnchantment;
    private final Map<Material, Material> smeltMap = new HashMap<>();

    public TelekinesisEnchantmentListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.telekinesisEnchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                .get(Key.key("customenchants:telekinesis"));
        
        // Inicializar mapa de fundição
        smeltMap.put(Material.RAW_IRON, Material.IRON_INGOT);
        smeltMap.put(Material.RAW_GOLD, Material.GOLD_INGOT);
        smeltMap.put(Material.RAW_COPPER, Material.COPPER_INGOT);
        smeltMap.put(Material.IRON_ORE, Material.IRON_INGOT);
        smeltMap.put(Material.GOLD_ORE, Material.GOLD_INGOT);
        smeltMap.put(Material.COPPER_ORE, Material.COPPER_INGOT);
        smeltMap.put(Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT);
        smeltMap.put(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT);
        smeltMap.put(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT);
        smeltMap.put(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        ItemStack handItem = player.getInventory().getItemInMainHand();

        if (telekinesisEnchantment != null && handItem.hasItemMeta() && handItem.getItemMeta().hasEnchant(telekinesisEnchantment)) {
            if (!plugin.getConfig().getBoolean("enchantments.telekinesis.enabled", true)) return;

            event.setDropItems(false);
            Collection<ItemStack> drops = event.getBlock().getDrops(handItem);
            
            int itemsTeleported = 0;
            for (ItemStack drop : drops) {
                Material result = smeltMap.get(drop.getType());
                if (result != null) {
                    drop.setType(result);
                }
                
                // Adicionar diretamente ao inventário
                Map<Integer, ItemStack> remaining = player.getInventory().addItem(drop);
                for (ItemStack item : remaining.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
                itemsTeleported++;
            }
            
            if (itemsTeleported > 0) {
                EnchantXPManager.addXP(player, handItem, "telekinesis", itemsTeleported * 2.0);
            }
        }
    }
}
