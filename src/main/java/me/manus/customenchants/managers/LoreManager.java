package me.manus.customenchants.managers;
import me.manus.customenchants.utils.NBTUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoreManager {

    public static void updateLore(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        Map<Enchantment, Integer> enchants = meta.getEnchants();
        List<Component> lore = new ArrayList<>();

        // Tier e Proteção
        String tier = NBTUtils.getTier(item);
        lore.add(Component.text("Raridade: ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(tier).color(getTierColor(tier)))
                .decoration(TextDecoration.ITALIC, false));

        if (NBTUtils.isProtected(item)) {
            lore.add(Component.text("PROTEGIDO")
                    .color(NamedTextColor.WHITE)
                    .decoration(TextDecoration.BOLD, true)
                    .decoration(TextDecoration.ITALIC, false));
        }

        lore.add(Component.empty());

        // Sockets e Gemas
        int sockets = NBTUtils.getSockets(item);
        if (sockets > 0) {
            String[] gems = NBTUtils.getGems(item);
            lore.add(Component.text("Engastes (" + gems.length + "/" + sockets + "):").color(NamedTextColor.YELLOW));
            for (int i = 0; i < sockets; i++) {
                if (i < gems.length) {
                    lore.add(Component.text(" ● ").color(getGemColor(gems[i]))
                            .append(Component.text(getGemName(gems[i])).color(NamedTextColor.GRAY)));
                } else {
                    lore.add(Component.text(" ○ ").color(NamedTextColor.DARK_GRAY)
                            .append(Component.text("Vazio").color(NamedTextColor.DARK_GRAY)));
                }
            }
            lore.add(Component.empty());
        }

        // Encantamentos
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment ench = entry.getKey();
            int level = entry.getValue();
            String namespace = ench.getKey().namespace();
            
            if (namespace.equals("customenchants")) {
                String key = ench.getKey().value();
                String name = getFriendlyName(key);
                String romanLevel = toRoman(level);
                
                lore.add(Component.text(name + " " + romanLevel)
                        .color(getTierColor(tier))
                        .decoration(TextDecoration.ITALIC, false));
                
                // XP do Encantamento
                double xp = NBTUtils.getEnchantXP(item, key);
                double nextLevelXp = level * 1000.0;
                if (level < 10) { // Limite de evolução
                    lore.add(Component.text(" [")
                            .color(NamedTextColor.DARK_GRAY)
                            .append(Component.text(getProgressBar((int)xp, (int)nextLevelXp)).color(NamedTextColor.GREEN))
                            .append(Component.text("] " + (int)xp + "/" + (int)nextLevelXp + " XP").color(NamedTextColor.GRAY)));
                }

                // Estatísticas
                addStatToLore(item, key, lore);
            }
        }

        meta.lore(lore);
        item.setItemMeta(meta);
    }

    private static String getProgressBar(int current, int max) {
        int bars = 10;
        int filled = (int) ((double) current / max * bars);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bars; i++) {
            if (i < filled) sb.append("■");
            else sb.append("□");
        }
        return sb.toString();
    }

    private static void addStatToLore(ItemStack item, String key, List<Component> lore) {
        switch (key) {
            case "lifesteal" -> {
                double healed = NBTUtils.getStat(item, "lifesteal_healed");
                if (healed > 0) lore.add(Component.text(" Vida Roubada: " + String.format("%.1f", healed) + "❤").color(NamedTextColor.DARK_GRAY));
            }
            case "explosive_pickaxe" -> {
                double blocks = NBTUtils.getStat(item, "explosive_blocks");
                if (blocks > 0) lore.add(Component.text(" Blocos Explodidos: " + (int)blocks).color(NamedTextColor.DARK_GRAY));
            }
            case "thunder_aspect" -> {
                double strikes = NBTUtils.getStat(item, "thunder_strikes");
                if (strikes > 0) lore.add(Component.text(" Raios Invocados: " + (int)strikes).color(NamedTextColor.DARK_GRAY));
            }
        }
    }

    private static TextColor getTierColor(String tier) {
        return switch (tier.toUpperCase()) {
            case "COMUM" -> NamedTextColor.WHITE;
            case "RARO" -> NamedTextColor.AQUA;
            case "ÉPICO" -> NamedTextColor.DARK_PURPLE;
            case "LENDÁRIO" -> NamedTextColor.GOLD;
            case "MÍTICO" -> NamedTextColor.LIGHT_PURPLE;
            case "DIVINO" -> TextColor.color(255, 215, 0);
            case "DEMONÍACO" -> TextColor.color(139, 0, 0);
            default -> NamedTextColor.GRAY;
        };
    }

    private static TextColor getGemColor(String gemType) {
        return switch (gemType.toLowerCase()) {
            case "ruby" -> NamedTextColor.RED;
            case "sapphire" -> NamedTextColor.BLUE;
            case "emerald" -> NamedTextColor.GREEN;
            case "topaz" -> NamedTextColor.YELLOW;
            default -> NamedTextColor.WHITE;
        };
    }

    private static String getGemName(String gemType) {
        return switch (gemType.toLowerCase()) {
            case "ruby" -> "Rubi (+Força)";
            case "sapphire" -> "Safira (+Mana/XP)";
            case "emerald" -> "Esmeralda (+Sorte)";
            case "topaz" -> "Topázio (+Velocidade)";
            default -> gemType;
        };
    }

    private static String getFriendlyName(String key) {
        return switch (key) {
            case "lifesteal" -> "Roubo de Vida";
            case "explosive_pickaxe" -> "Picareta Explosiva";
            case "thunder_aspect" -> "Aspecto do Trovão";
            case "telekinesis" -> "Teleforese";
            case "vampirism" -> "Vampirismo";
            case "soulbound" -> "Vínculo de Alma";
            case "flight" -> "Voo";
            case "hardened" -> "Resistência";
            case "mending_two" -> "Auto-Reparo II";
            case "berserker" -> "Berserker";
            case "frostbite" -> "Congelamento";
            default -> key;
        };
    }

    private static String toRoman(int n) {
        if (n <= 0) return "";
        String[] roman = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        if (n <= 10) return roman[n-1];
        return String.valueOf(n);
    }
}
