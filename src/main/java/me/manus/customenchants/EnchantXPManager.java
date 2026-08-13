package me.manus.customenchants;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantXPManager {

    public static void addXP(Player player, ItemStack item, String enchantId, double amount) {
        if (item == null || item.getType().isAir()) return;
        
        String enchantKeyStr = enchantId.contains(":") ? enchantId : "customenchants:" + enchantId;
        Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key(enchantKeyStr));
        
        if (enchantment == null || !item.containsEnchantment(enchantment)) return;

        int currentLevel = item.getEnchantmentLevel(enchantment);
        if (currentLevel >= 10) return; // Limite máximo evolutivo

        NBTUtils.addEnchantXP(item, enchantId, amount);
        double currentXP = NBTUtils.getEnchantXP(item, enchantId);
        double requiredXP = currentLevel * 1000.0;

        if (currentXP >= requiredXP) {
            // Level Up!
            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(enchantment, currentLevel + 1, true);
            item.setItemMeta(meta);
            
            // Reset XP para o próximo nível
            NBTUtils.addEnchantXP(item, enchantId, -currentXP);
            LoreManager.updateLore(item);
            
            player.sendMessage(Component.text("§d§l✨ SEU ENCANTAMENTO EVOLUIU! ✨"));
            player.sendMessage(Component.text("§f" + enchantId + " §7subiu para o nível §b" + (currentLevel + 1)));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 0.5f);
        } else {
            LoreManager.updateLore(item);
        }
    }
}
