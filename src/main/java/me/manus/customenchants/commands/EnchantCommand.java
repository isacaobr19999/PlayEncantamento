package me.manus.customenchants.commands;

import me.manus.customenchants.CustomEnchants;
import me.manus.customenchants.managers.LoreManager;
import me.manus.customenchants.managers.LangManager;
import me.manus.customenchants.utils.NBTUtils;
import me.manus.customenchants.utils.CommandValidation;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EnchantCommand implements CommandExecutor, TabCompleter {
    private final CustomEnchants plugin;
    private final LangManager lang;

    public EnchantCommand(CustomEnchants plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLangManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(lang.getMessage("usage_main"));
            return true;
        }

        String sub = args[0].toLowerCase();

        String requiredPermission = permissionFor(sub);
        if (requiredPermission != null && !sender.hasPermission(requiredPermission)) {
            sender.sendMessage(lang.getMessage("no_permission"));
            return true;
        }

        switch (sub) {
            case "reload" -> {
                plugin.reloadConfig();
                lang.reload();
                sender.sendMessage(lang.getMessage("config_reloaded"));
                return true;
            }
            case "menu" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(lang.getMessage("only_players"));
                    return true;
                }
                plugin.getGuiManager().openMenu(player);
                return true;
            }
            case "orb" -> {
                if (!(sender instanceof Player player)) return true;
                if (args.length < 2) {
                    player.sendMessage(lang.getMessage("usage_orb"));
                    return true;
                }
                String enchantId = args[1].toLowerCase();
                int chance = 100;
                if (args.length >= 3) {
                    try { chance = Integer.parseInt(args[2]); } catch (NumberFormatException ignored) {
                        player.sendMessage(lang.getMessage("invalid_level"));
                        return true;
                    }
                }
                if (!CommandValidation.isPercentage(chance)) {
                    player.sendMessage(lang.getMessage("invalid_level"));
                    return true;
                }
                ItemStack orb = plugin.getOrbManager().createOrb(enchantId, chance);
                giveItem(player, orb);
                String msg = lang.getRawMessage("orb_created").replace("{enchant}", enchantId).replace("{chance}", String.valueOf(chance));
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
                return true;
            }
            case "dust" -> {
                if (!(sender instanceof Player player)) return true;
                int percent = 10;
                if (args.length >= 2) {
                    try { percent = Integer.parseInt(args[1]); } catch (NumberFormatException ignored) {
                        player.sendMessage(lang.getMessage("invalid_level"));
                        return true;
                    }
                }
                if (!CommandValidation.isPercentage(percent)) {
                    player.sendMessage(lang.getMessage("invalid_level"));
                    return true;
                }
                ItemStack dust = plugin.getOrbManager().createMagicDust(percent);
                giveItem(player, dust);
                String msg = lang.getRawMessage("dust_created").replace("{percent}", String.valueOf(percent));
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
                return true;
            }
            case "whitescroll" -> {
                if (!(sender instanceof Player player)) return true;
                giveItem(player, plugin.getOrbManager().createWhiteScroll());
                player.sendMessage(lang.getMessage("scroll_created"));
                return true;
            }
            case "blackscroll" -> {
                if (!(sender instanceof Player player)) return true;
                giveItem(player, plugin.getOrbManager().createBlackScroll());
                player.sendMessage(lang.getMessage("black_scroll_created"));
                return true;
            }
            case "gem" -> {
                if (!(sender instanceof Player player)) return true;
                if (args.length < 2) {
                    player.sendMessage(lang.getMessage("usage_gem"));
                    return true;
                }
                giveItem(player, plugin.getOrbManager().createGem(args[1]));
                String msg = lang.getRawMessage("gem_created").replace("{type}", args[1]);
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
                return true;
            }
            case "socket" -> {
                if (!(sender instanceof Player player)) return true;
                giveItem(player, plugin.getOrbManager().createSocketAdder());
                player.sendMessage(lang.getMessage("socket_tool_created"));
                return true;
            }
            case "give" -> {
                if (args.length < 3) {
                    sender.sendMessage(lang.getMessage("usage_give"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(lang.getMessage("player_not_found"));
                    return true;
                }
                String enchantName = args[2].toLowerCase();
                int level = 1;
                if (args.length >= 4) {
                    try { level = Integer.parseInt(args[3]); } catch (NumberFormatException ignored) {
                        sender.sendMessage(lang.getMessage("invalid_level"));
                        return true;
                    }
                }
                if (!CommandValidation.isEnchantmentLevel(level)) {
                    sender.sendMessage(lang.getMessage("invalid_level"));
                    return true;
                }

                Enchantment enchantment = getEnchantment(enchantName);
                if (enchantment == null) {
                    sender.sendMessage(lang.getMessage("unknown_enchantment"));
                    return true;
                }

                ItemStack item = target.getInventory().getItemInMainHand();
                if (item.getType().isAir()) {
                    sender.sendMessage(lang.getMessage("hold_item"));
                    return true;
                }

                ItemMeta meta = item.getItemMeta();
                meta.addEnchant(enchantment, level, true);
                item.setItemMeta(meta);
                NBTUtils.addEnchant(item, enchantName);
                LoreManager.updateLore(item);

                String msg = lang.getRawMessage("enchant_applied_target").replace("{enchant}", enchantName).replace("{player}", target.getName());
                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
                target.sendMessage(lang.getMessage("item_applied"));
                return true;
            }
            default -> {
                // Caso seja aplicação direta na mão (legado/atalho)
                if (sender instanceof Player player && args.length >= 2) {
                    String enchantName = args[0].toLowerCase();
                    int level;
                    try {
                        level = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(lang.getMessage("invalid_level"));
                        return true;
                    }

                    if (!player.hasPermission("customenchants.enchant")) {
                        player.sendMessage(lang.getMessage("no_permission"));
                        return true;
                    }
                    if (!CommandValidation.isEnchantmentLevel(level)) {
                        player.sendMessage(lang.getMessage("invalid_level"));
                        return true;
                    }

                    Enchantment enchantment = getEnchantment(enchantName);
                    if (enchantment == null) {
                        player.sendMessage(lang.getMessage("unknown_enchantment"));
                        return true;
                    }

                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (item.getType().isAir()) {
                        player.sendMessage(lang.getMessage("hold_item"));
                        return true;
                    }

                    ItemMeta meta = item.getItemMeta();
                    meta.addEnchant(enchantment, level, true);
                    item.setItemMeta(meta);
                    NBTUtils.addEnchant(item, enchantName);
                    LoreManager.updateLore(item);

                    player.sendMessage(lang.getMessage("item_applied"));
                    return true;
                }
                sender.sendMessage(lang.getMessage("usage_main"));
                return true;
            }
        }
    }

    private void giveItem(Player player, ItemStack item) {
        if (item == null || item.getType().isAir()) return;
        plugin.getLogger().fine("Entregando item " + item.getType() + " para " + player.getName());
        player.getInventory().addItem(item).values().forEach(leftover ->
                player.getWorld().dropItemNaturally(player.getLocation(), leftover));
    }

    private String permissionFor(String sub) {
        return switch (sub) {
            case "reload" -> "customenchants.reload";
            case "give" -> "customenchants.give";
            case "orb", "dust", "whitescroll", "blackscroll", "gem", "socket" -> "customenchants.items";
            case "menu" -> "customenchants.menu";
            default -> "customenchants.enchant";
        };
    }

    private Enchantment getEnchantment(String name) {
        return switch (name.toLowerCase()) {
            case "lifesteal" -> RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key("customenchants:lifesteal"));
            case "explosive", "explosive_pickaxe" -> RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key("customenchants:explosive_pickaxe"));
            case "thunder", "thunder_aspect" -> RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key("customenchants:thunder_aspect"));
            case "telekinesis" -> RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key("customenchants:telekinesis"));
            case "vampirism" -> RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key("customenchants:vampirism"));
            case "soulbound" -> RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key("customenchants:soulbound"));
            case "flight" -> RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key("customenchants:flight"));
            case "hardened" -> RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key("customenchants:hardened"));
            case "mending_two" -> RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key("customenchants:mending_two"));
            case "berserker" -> RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key("customenchants:berserker"));
            case "frostbite" -> RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key("customenchants:frostbite"));
            case "divine_aura" -> RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key("customenchants:divine_aura"));
            case "efficiency" -> Enchantment.EFFICIENCY;
            case "fortune" -> Enchantment.FORTUNE;
            case "sharpness" -> Enchantment.SHARPNESS;
            case "protection" -> Enchantment.PROTECTION;
            case "unbreaking" -> Enchantment.UNBREAKING;
            default -> null;
        };
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("customenchants.admin") && !sender.hasPermission("customenchants.enchant")) {
            if (args.length == 1 && "menu".startsWith(args[0].toLowerCase())) return List.of("menu");
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> subs = List.of("menu", "reload", "orb", "dust", "whitescroll", "blackscroll", "gem", "socket", "give", "lifesteal", "explosive", "thunder", "telekinesis", "vampirism", "soulbound", "flight", "hardened", "mending_two", "berserker", "frostbite", "divine_aura", "efficiency", "fortune", "sharpness", "protection", "unbreaking");
            return subs.stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            switch (sub) {
                case "orb" -> {
                    List<String> enchants = List.of("lifesteal", "explosive", "thunder", "telekinesis", "vampirism", "soulbound", "flight", "hardened", "mending_two", "berserker", "frostbite", "divine_aura");
                    return enchants.stream().filter(s -> s.startsWith(args[1].toLowerCase())).collect(Collectors.toList());
                }
                case "dust" -> { return List.of("5", "10", "25", "50"); }
                case "gem" -> { return List.of("ruby", "sapphire", "emerald", "topaz"); }
                case "give" -> { return null; } // Player names
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            List<String> all = List.of("lifesteal", "explosive", "thunder", "telekinesis", "vampirism", "soulbound", "flight", "hardened", "mending_two", "berserker", "frostbite", "divine_aura", "efficiency", "fortune", "sharpness", "protection", "unbreaking");
            return all.stream().filter(s -> s.startsWith(args[2].toLowerCase())).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
