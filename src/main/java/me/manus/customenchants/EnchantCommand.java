package me.manus.customenchants;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import java.util.List;
import java.util.stream.Collectors;

public class EnchantCommand implements CommandExecutor, TabCompleter {
    private final CustomEnchants plugin;

    public EnchantCommand(CustomEnchants plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Uso correto: /ce <encantamento|menu|reload|orb|dust|whitescroll|blackscroll|gem|socket>", NamedTextColor.RED));
            return true;
        }

        // Reload (acessível via console)
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("customenchants.admin")) {
                sender.sendMessage(Component.text("Sem permissão.", NamedTextColor.RED));
                return true;
            }
            plugin.reloadConfig();
            plugin.getLangManager().reload();
            sender.sendMessage(Component.text("Configuração e mensagens recarregadas!", NamedTextColor.GREEN));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Este comando só pode ser executado por jogadores.", NamedTextColor.RED));
            return true;
        }

        // Menu
        if (args[0].equalsIgnoreCase("menu")) {
            plugin.getGuiManager().openMenu(player);
            return true;
        }

        // Orbes
        if (args[0].equalsIgnoreCase("orb")) {
            if (!player.hasPermission("customenchants.admin")) {
                player.sendMessage(plugin.getLangManager().getMessage("no_permission"));
                return true;
            }
            if (args.length < 2) {
                player.sendMessage(Component.text("Uso: /ce orb <id> [chance]", NamedTextColor.RED));
                return true;
            }
            String enchantId = args[1].toLowerCase();
            int chance = 100;
            if (args.length >= 3) {
                try { chance = Integer.parseInt(args[2]); } catch (NumberFormatException ignored) {}
            }
            ItemStack orb = plugin.getOrbManager().createOrb(enchantId, chance);
            player.getInventory().addItem(orb);
            player.sendMessage(Component.text("Orbe de " + enchantId + " (" + chance + "%) criado!", NamedTextColor.GREEN));
            return true;
        }

        // Pó Mágico
        if (args[0].equalsIgnoreCase("dust")) {
            if (!player.hasPermission("customenchants.admin")) {
                player.sendMessage(plugin.getLangManager().getMessage("no_permission"));
                return true;
            }
            int percent = 10;
            if (args.length >= 2) {
                try { percent = Integer.parseInt(args[1]); } catch (NumberFormatException ignored) {}
            }
            ItemStack dust = plugin.getOrbManager().createMagicDust(percent);
            player.getInventory().addItem(dust);
            player.sendMessage(Component.text("Pó Mágico (+" + percent + "%) criado!", NamedTextColor.GREEN));
            return true;
        }

        // White Scroll
        if (args[0].equalsIgnoreCase("whitescroll")) {
            if (!player.hasPermission("customenchants.admin")) {
                player.sendMessage(plugin.getLangManager().getMessage("no_permission"));
                return true;
            }
            ItemStack scroll = plugin.getOrbManager().createWhiteScroll();
            player.getInventory().addItem(scroll);
            player.sendMessage(Component.text("Pergaminho de Proteção criado!", NamedTextColor.WHITE));
            return true;
        }

        // Black Scroll
        if (args[0].equalsIgnoreCase("blackscroll")) {
            if (!player.hasPermission("customenchants.admin")) {
                player.sendMessage(plugin.getLangManager().getMessage("no_permission"));
                return true;
            }
            ItemStack scroll = plugin.getOrbManager().createBlackScroll();
            player.getInventory().addItem(scroll);
            player.sendMessage(Component.text("Pergaminho Negro criado!", NamedTextColor.DARK_GRAY));
            return true;
        }

        // Gemas
        if (args[0].equalsIgnoreCase("gem")) {
            if (!player.hasPermission("customenchants.admin")) {
                player.sendMessage(plugin.getLangManager().getMessage("no_permission"));
                return true;
            }
            if (args.length < 2) {
                player.sendMessage(Component.text("Uso: /ce gem <ruby|sapphire|emerald|topaz>", NamedTextColor.RED));
                return true;
            }
            ItemStack gem = plugin.getOrbManager().createGem(args[1]);
            player.getInventory().addItem(gem);
            player.sendMessage(Component.text("Gema de " + args[1] + " criada!", NamedTextColor.GREEN));
            return true;
        }

        // Sockets
        if (args[0].equalsIgnoreCase("socket")) {
            if (!player.hasPermission("customenchants.admin")) {
                player.sendMessage(plugin.getLangManager().getMessage("no_permission"));
                return true;
            }
            ItemStack adder = plugin.getOrbManager().createSocketAdder();
            player.getInventory().addItem(adder);
            player.sendMessage(Component.text("Ferramenta de Engaste criada!", NamedTextColor.GOLD));
            return true;
        }

        // Give
        if (args[0].equalsIgnoreCase("give")) {
            if (!player.hasPermission("customenchants.admin")) {
                player.sendMessage(plugin.getLangManager().getMessage("no_permission"));
                return true;
            }
            if (args.length < 3) {
                player.sendMessage(Component.text("Uso: /ce give <player> <encantamento> [level]", NamedTextColor.RED));
                return true;
            }
            Player targetPlayer = org.bukkit.Bukkit.getPlayer(args[1]);
            if (targetPlayer == null) {
                player.sendMessage(Component.text("Jogador não encontrado.", NamedTextColor.RED));
                return true;
            }
            String enchantName = args[2].toLowerCase();
            int level = 1;
            if (args.length >= 4) {
                try { level = Integer.parseInt(args[3]); } catch (NumberFormatException ignored) {}
            }
            
            Enchantment enchantment = getEnchantment(enchantName);
            if (enchantment == null) {
                player.sendMessage(plugin.getLangManager().getMessage("unknown_enchantment"));
                return true;
            }

            ItemStack item = targetPlayer.getInventory().getItemInMainHand();
            if (item.getType().isAir()) {
                player.sendMessage(Component.text("O jogador deve estar segurando um item!", NamedTextColor.RED));
                return true;
            }

            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(enchantment, level, true);
            item.setItemMeta(meta);
            NBTUtils.addEnchant(item, enchantName);
            LoreManager.updateLore(item);

            player.sendMessage(Component.text("Encantamento aplicado a " + targetPlayer.getName() + "!", NamedTextColor.GREEN));
            targetPlayer.sendMessage(plugin.getLangManager().getMessage("item_applied"));
            return true;
        }

        // Aplicação direta (na mão)
        if (args.length >= 2) {
            String enchantName = args[0].toLowerCase();
            int level;
            try {
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(Component.text("O nível deve ser um número.", NamedTextColor.RED));
                return true;
            }

            Enchantment enchantment = getEnchantment(enchantName);
            if (enchantment == null) {
                player.sendMessage(Component.text("Encantamento desconhecido.", NamedTextColor.RED));
                return true;
            }

            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType().isAir()) {
                player.sendMessage(Component.text("Você deve estar segurando um item.", NamedTextColor.RED));
                return true;
            }

            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(enchantment, level, true);
            item.setItemMeta(meta);
            NBTUtils.addEnchant(item, enchantName);
            LoreManager.updateLore(item);

            player.sendMessage(Component.text("Encantamento aplicado com sucesso!", NamedTextColor.GREEN));
            return true;
        }

        player.sendMessage(Component.text("Uso incorreto do comando.", NamedTextColor.RED));
        return true;
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
        if (args.length == 1) {
            List<String> options = List.of("lifesteal", "explosive", "thunder", "telekinesis", "vampirism", "soulbound", "flight", "hardened", "mending_two", "berserker", "frostbite", "divine_aura", "efficiency", "fortune", "sharpness", "protection", "unbreaking", "reload", "menu", "orb", "dust", "give", "whitescroll", "blackscroll", "gem", "socket");
            String input = args[0].toLowerCase();
            return options.stream().filter(s -> s.startsWith(input)).collect(Collectors.toList());
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("orb")) {
                return List.of("lifesteal", "explosive", "thunder", "telekinesis", "vampirism", "soulbound", "flight", "hardened", "mending_two", "berserker", "frostbite", "divine_aura");
            }
            if (args[0].equalsIgnoreCase("dust")) {
                return List.of("5", "10", "25", "50");
            }
            if (args[0].equalsIgnoreCase("give")) {
                return null; // Player names
            }
            if (args[0].equalsIgnoreCase("gem")) {
                return List.of("ruby", "sapphire", "emerald", "topaz");
            }
            if (!args[0].equalsIgnoreCase("reload") && !args[0].equalsIgnoreCase("menu")) {
                return List.of("1", "5", "10");
            }
        }
        return new ArrayList<>();
    }
}
