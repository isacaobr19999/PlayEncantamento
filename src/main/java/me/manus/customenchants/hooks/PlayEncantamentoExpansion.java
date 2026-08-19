package me.manus.customenchants.hooks;
import me.manus.customenchants.CustomEnchants;
import me.manus.customenchants.utils.NBTUtils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayEncantamentoExpansion extends PlaceholderExpansion {

    private final CustomEnchants plugin;

    public PlayEncantamentoExpansion(CustomEnchants plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getAuthor() {
        return "_Nube";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "playencantamento";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || !player.isOnline()) return "";
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) return "";

        ItemStack item = onlinePlayer.getInventory().getItemInMainHand();
        
        // Exemplo: %playencantamento_has_lifesteal%
        if (params.startsWith("has_")) {
            String enchantId = params.substring(4);
            return NBTUtils.hasEnchant(item, enchantId) ? "Sim" : "Não";
        }

        // Exemplo: %playencantamento_level_lifesteal%
        if (params.startsWith("level_")) {
            String enchantId = params.substring(6);
            for (Enchantment ench : item.getEnchantments().keySet()) {
                if (ench.getKey().value().equalsIgnoreCase(enchantId)) {
                    return String.valueOf(item.getEnchantmentLevel(ench));
                }
            }
            return "0";
        }

        return null;
    }
}
