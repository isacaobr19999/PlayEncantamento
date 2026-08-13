package me.manus.customenchants;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GUIManager implements Listener {

    private final CustomEnchants plugin;
    private final String menuTitle = "§lMenu Mythic Edition";

    public GUIManager(CustomEnchants plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, Component.text(menuTitle));

        // Vidros decorativos
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.displayName(Component.empty());
        glass.setItemMeta(glassMeta);
        for (int i = 0; i < 45; i++) inv.setItem(i, glass);

        // Encantamentos
        inv.setItem(10, createGuiItem(Material.IRON_SWORD, "§cRoubo de Vida", "lifesteal"));
        inv.setItem(11, createGuiItem(Material.DIAMOND_PICKAXE, "§ePicareta Explosiva", "explosive_pickaxe"));
        inv.setItem(12, createGuiItem(Material.GOLDEN_SWORD, "§bAspecto do Trovão", "thunder_aspect"));
        inv.setItem(13, createGuiItem(Material.NETHERITE_PICKAXE, "§6Teleforese", "telekinesis"));
        inv.setItem(14, createGuiItem(Material.WITHER_SKELETON_SKULL, "§8Vampirismo", "vampirism"));
        inv.setItem(15, createGuiItem(Material.NETHER_STAR, "§dSoulbound", "soulbound"));
        inv.setItem(16, createGuiItem(Material.FEATHER, "§fVoo", "flight"));
        
        inv.setItem(19, createGuiItem(Material.IRON_CHESTPLATE, "§9Resistência", "hardened"));
        inv.setItem(20, createGuiItem(Material.ENCHANTED_BOOK, "§aAuto-Reparo II", "mending_two"));
        inv.setItem(21, createGuiItem(Material.REDSTONE, "§4Berserker", "berserker"));
        inv.setItem(22, createGuiItem(Material.SNOWBALL, "§bCongelamento", "frostbite"));

        // Opções Especiais
        inv.setItem(31, createSpecialItem(Material.GRINDSTONE, "§c§lDESENCANTAR ITEM", List.of("§7Remove todos os encantamentos customizados", "§7e devolve os Orbes (50% chance).", "§7Custo: §e50 Níveis XP")));
        
        player.openInventory(inv);
    }

    private ItemStack createGuiItem(Material material, String name, String configKey) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
        
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7Clique para aplicar ao item na mão").decoration(TextDecoration.ITALIC, false));
        
        double moneyCost = plugin.getConfig().getDouble("enchantments." + configKey + ".cost_money", 0);
        int xpCost = plugin.getConfig().getInt("enchantments." + configKey + ".cost_xp", 0);
        
        if (moneyCost > 0) lore.add(Component.text("§7Custo: §a$" + moneyCost).decoration(TextDecoration.ITALIC, false));
        if (xpCost > 0) lore.add(Component.text("§7Custo XP: §e" + xpCost + " níveis").decoration(TextDecoration.ITALIC, false));
        
        lore.add(Component.text("§8ID: " + configKey).decoration(TextDecoration.ITALIC, false));
        
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createSpecialItem(Material material, String name, List<String> loreStrings) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        for (String s : loreStrings) lore.add(Component.text(s).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().title().equals(Component.text(menuTitle))) return;
        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.BLACK_STAINED_GLASS_PANE) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack handItem = player.getInventory().getItemInMainHand();

        if (event.getSlot() == 31) {
            handleDisenchant(player, handItem);
            return;
        }

        if (handItem.getType().isAir()) {
            player.sendMessage(plugin.getLangManager().getMessage("hold_item"));
            return;
        }

        ItemMeta guiMeta = event.getCurrentItem().getItemMeta();
        List<Component> lore = guiMeta.lore();
        if (lore == null) return;

        String configKey = "";
        for (Component line : lore) {
            String plain = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(line);
            if (plain.startsWith("ID: ")) {
                configKey = plain.substring(4);
                break;
            }
        }
        
        if (configKey.isEmpty()) return;

        // Lógica de compra e aplicação (já existente)
        double moneyCost = plugin.getConfig().getDouble("enchantments." + configKey + ".cost_money", 0);
        int xpCost = plugin.getConfig().getInt("enchantments." + configKey + ".cost_xp", 0);

        if (plugin.getConfig().getBoolean("settings.use_vault") && CustomEnchants.getEconomy() != null) {
            if (CustomEnchants.getEconomy().getBalance(player) < moneyCost) {
                player.sendMessage(plugin.getLangManager().getMessage("insufficient_funds").replaceText(b -> b.match("\\{cost\\}").replacement(String.valueOf(moneyCost))));
                return;
            }
        }

        if (plugin.getConfig().getBoolean("settings.use_xp")) {
            if (player.getLevel() < xpCost) {
                player.sendMessage(plugin.getLangManager().getMessage("insufficient_xp").replaceText(b -> b.match("\\{cost\\}").replacement(String.valueOf(xpCost))));
                return;
            }
        }

        String enchantKeyStr = configKey.contains(":") ? configKey : "customenchants:" + configKey;
        Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key(enchantKeyStr));

        if (enchantment != null) {
            if (plugin.getConfig().getBoolean("settings.use_vault") && CustomEnchants.getEconomy() != null) {
                CustomEnchants.getEconomy().withdrawPlayer(player, moneyCost);
            }
            if (plugin.getConfig().getBoolean("settings.use_xp")) {
                player.setLevel(player.getLevel() - xpCost);
            }

            ItemMeta handMeta = handItem.getItemMeta();
            handMeta.addEnchant(enchantment, 1, true);
            handItem.setItemMeta(handMeta);
            NBTUtils.addEnchant(handItem, configKey);
            LoreManager.updateLore(handItem);
            player.sendMessage(plugin.getLangManager().getMessage("item_applied"));
            player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
            player.closeInventory();
        }
    }

    private void handleDisenchant(Player player, ItemStack item) {
        if (item == null || item.getType().isAir()) {
            player.sendMessage(plugin.getLangManager().getMessage("hold_item"));
            return;
        }

        if (player.getLevel() < 50) {
            player.sendMessage(Component.text("§cVocê precisa de 50 níveis de XP para desencantar!", NamedTextColor.RED));
            return;
        }

        ItemMeta meta = item.getItemMeta();
        Map<Enchantment, Integer> enchants = meta.getEnchants();
        boolean removed = false;

        for (Enchantment ench : new ArrayList<>(enchants.keySet())) {
            if (ench.getKey().namespace().equals("customenchants")) {
                meta.removeEnchant(ench);
                removed = true;
                
                // Chance de devolver o orbe
                if (new java.util.Random().nextBoolean()) {
                    player.getInventory().addItem(plugin.getOrbManager().createOrb(ench.getKey().value(), 100));
                }
            }
        }

        if (removed) {
            player.setLevel(player.getLevel() - 50);
            item.setItemMeta(meta);
            // Limpar NBT de encantamentos customizados
            meta.getPersistentDataContainer().remove(new org.bukkit.NamespacedKey(plugin, "custom_enchants"));
            item.setItemMeta(meta);
            LoreManager.updateLore(item);
            player.sendMessage(Component.text("§aItem desencantado com sucesso!", NamedTextColor.GREEN));
            player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1f, 1f);
        } else {
            player.sendMessage(Component.text("§cEste item não possui encantamentos customizados!", NamedTextColor.RED));
        }
        player.closeInventory();
    }
}
