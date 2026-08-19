package me.manus.customenchants.managers;

import me.manus.customenchants.CustomEnchants;
import me.manus.customenchants.utils.NBTUtils;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
import java.util.Random;

public class GUIManager implements Listener {

    private final CustomEnchants plugin;
    private final String menuTitle;

    public GUIManager(CustomEnchants plugin) {
        this.plugin = plugin;
        this.menuTitle = plugin.getConfig().getString("gui.title", "&b&lPlayEncantamento &8- &fMenu");
    }

    public void openMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, LegacyComponentSerializer.legacyAmpersand().deserialize(menuTitle));

        Material filler = Material.valueOf(plugin.getConfig().getString("gui.filler_item", "BLACK_STAINED_GLASS_PANE"));
        ItemStack glass = new ItemStack(filler);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.displayName(Component.empty());
        glass.setItemMeta(glassMeta);
        for (int i = 0; i < 45; i++) inv.setItem(i, glass);

        // Encantamentos em posições estratégicas
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
        inv.setItem(23, createGuiItem(Material.GOLDEN_APPLE, "§eAura Divina", "divine_aura"));

        // Opções Especiais
        int disenchantCost = plugin.getConfig().getInt("settings.disenchant_cost_xp", 50);
        inv.setItem(31, createSpecialItem(Material.GRINDSTONE, "§c§lDESENCANTAR ITEM", List.of(
                "§7Remove todos os encantamentos customizados",
                "§7e devolve os Orbes com chance configurada.",
                "§7Custo: §e" + disenchantCost + " Níveis XP"
        )));
        
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
        Component title = event.getView().title();
        if (!LegacyComponentSerializer.legacyAmpersand().serialize(title).equals(menuTitle)) return;
        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;

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

        double moneyCost = plugin.getConfig().getDouble("enchantments." + configKey + ".cost_money", 0);
        int xpCost = plugin.getConfig().getInt("enchantments." + configKey + ".cost_xp", 0);

        if (plugin.getConfig().getBoolean("settings.use_vault") && CustomEnchants.getEconomy() != null) {
            if (CustomEnchants.getEconomy().getBalance(player) < moneyCost) {
                String msg = plugin.getLangManager().getRawMessage("insufficient_funds").replace("{cost}", String.valueOf(moneyCost));
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
                return;
            }
        }

        if (plugin.getConfig().getBoolean("settings.use_xp")) {
            if (player.getLevel() < xpCost) {
                String msg = plugin.getLangManager().getRawMessage("insufficient_xp").replace("{cost}", String.valueOf(xpCost));
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
                return;
            }
        }

        Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key("customenchants:" + configKey));

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

        int cost = plugin.getConfig().getInt("settings.disenchant_cost_xp", 50);
        if (player.getLevel() < cost) {
            player.sendMessage(Component.text("§cVocê precisa de " + cost + " níveis de XP!", NamedTextColor.RED));
            return;
        }

        ItemMeta meta = item.getItemMeta();
        Map<Enchantment, Integer> enchants = meta.getEnchants();
        boolean removed = false;
        Random random = new Random();
        double returnChance = plugin.getConfig().getDouble("settings.disenchant_orb_return_chance", 0.5);

        for (Enchantment ench : new ArrayList<>(enchants.keySet())) {
            if (ench.getKey().namespace().equals("customenchants")) {
                meta.removeEnchant(ench);
                removed = true;
                
                String enchantId = ench.getKey().value();
                NBTUtils.removeEnchant(item, enchantId); // Limpeza total NBT

                if (random.nextDouble() < returnChance) {
                    player.getInventory().addItem(plugin.getOrbManager().createOrb(enchantId, 100));
                }
            }
        }

        if (removed) {
            player.setLevel(player.getLevel() - cost);
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
