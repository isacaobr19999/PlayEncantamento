package me.manus.customenchants.managers;

import me.manus.customenchants.CustomEnchants;
import me.manus.customenchants.utils.NBTUtils;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantXPManager {

    public static void addXP(Player player, ItemStack item, String enchantId, double amount) {
        if (item == null || item.getType().isAir()) return;
        
        CustomEnchants plugin = CustomEnchants.getPlugin(CustomEnchants.class);
        String enchantKeyStr = enchantId.contains(":") ? enchantId : "customenchants:" + enchantId;
        Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key(enchantKeyStr));
        
        if (enchantment == null || !item.containsEnchantment(enchantment)) return;

        int currentLevel = item.getEnchantmentLevel(enchantment);
        if (currentLevel >= 10) return;

        NBTUtils.addEnchantXP(item, enchantId, amount);
        double currentXP = NBTUtils.getEnchantXP(item, enchantId);
        double xpPerLevel = plugin.getConfig().getDouble("settings.xp_per_level_required", 1000.0);
        double requiredXP = currentLevel * xpPerLevel;

        if (currentXP >= requiredXP) {
            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(enchantment, currentLevel + 1, true);
            item.setItemMeta(meta);
            
            NBTUtils.addEnchantXP(item, enchantId, -currentXP);
            LoreManager.updateLore(item);
            
            player.sendMessage(plugin.getLangManager().getMessage("enchant_evolved"));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 0.5f);
        } else {
            // Só atualiza lore se for necessário (ex: barra de XP mudou visivelmente)
            // Para simplicidade, atualizamos sempre, mas em produção poderíamos otimizar.
            LoreManager.updateLore(item);
        }
    }
}
