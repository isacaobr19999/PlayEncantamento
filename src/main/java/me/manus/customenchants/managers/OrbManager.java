package me.manus.customenchants.managers;

import me.manus.customenchants.CustomEnchants;
import me.manus.customenchants.utils.NBTUtils;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OrbManager implements Listener {

    private final CustomEnchants plugin;
    private final Random random = new Random();
    private final LangManager lang;

    public OrbManager(CustomEnchants plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLangManager();
    }

    public ItemStack createOrb(String enchantId, int successRate) {
        ItemStack orb = new ItemStack(Material.SLIME_BALL);
        ItemMeta meta = orb.getItemMeta();
        meta.setCustomModelData(1001);
        
        String friendlyName = getFriendlyName(enchantId);
        meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(
                lang.getRawMessage("orb_name").replace("{enchant}", friendlyName)));
        
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7Arraste este orbe sobre um item").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7para aplicar o encantamento.").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7Chance de Sucesso: §a" + successRate + "%").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§8ID: " + enchantId).decoration(TextDecoration.ITALIC, false));
        
        meta.lore(lore);
        orb.setItemMeta(meta);
        NBTUtils.setOrb(orb, enchantId);
        NBTUtils.setSuccessRate(orb, successRate);
        return orb;
    }

    public ItemStack createMagicDust(int percent) {
        ItemStack dust = new ItemStack(Material.SUGAR);
        ItemMeta meta = dust.getItemMeta();
        meta.setCustomModelData(1002);
        
        meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(
                lang.getRawMessage("magic_dust_name").replace("{percent}", String.valueOf(percent))));
        
        meta.lore(List.of(
                Component.text("§7Arraste sobre um Orbe de Encantamento").decoration(TextDecoration.ITALIC, false),
                Component.text("§7para aumentar sua chance de sucesso.").decoration(TextDecoration.ITALIC, false)
        ));
        
        dust.setItemMeta(meta);
        NBTUtils.setDust(dust, percent);
        return dust;
    }

    public ItemStack createWhiteScroll() {
        ItemStack scroll = new ItemStack(Material.PAPER);
        ItemMeta meta = scroll.getItemMeta();
        meta.setCustomModelData(1003);
        meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(lang.getRawMessage("scroll_created")));
        meta.lore(List.of(
                Component.text("§7Protege contra falhas de orbes.").decoration(TextDecoration.ITALIC, false),
                Component.text("§7Consumido na proteção.").decoration(TextDecoration.ITALIC, false)
        ));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_white_scroll"), PersistentDataType.BYTE, (byte) 1);
        scroll.setItemMeta(meta);
        return scroll;
    }

    public ItemStack createBlackScroll() {
        ItemStack scroll = new ItemStack(Material.INK_SAC);
        ItemMeta meta = scroll.getItemMeta();
        meta.setCustomModelData(1005);
        meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(lang.getRawMessage("black_scroll_created")));
        meta.lore(List.of(
                Component.text("§7Remove um encantamento aleatório.").decoration(TextDecoration.ITALIC, false),
                Component.text("§7Transforma em livro de orbe.").decoration(TextDecoration.ITALIC, false)
        ));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_black_scroll"), PersistentDataType.BYTE, (byte) 1);
        scroll.setItemMeta(meta);
        return scroll;
    }

    public ItemStack createGem(String gemType) {
        Material mat = switch (gemType.toLowerCase()) {
            case "ruby" -> Material.REDSTONE;
            case "sapphire" -> Material.LAPIS_LAZULI;
            case "emerald" -> Material.EMERALD;
            case "topaz" -> Material.GOLD_NUGGET;
            default -> Material.QUARTZ;
        };
        ItemStack gem = new ItemStack(mat);
        ItemMeta meta = gem.getItemMeta();
        meta.setCustomModelData(2000);
        meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(lang.getRawMessage("gem_created").replace("{type}", gemType)));
        meta.lore(List.of(Component.text("§7Arraste sobre um item com engastes.").decoration(TextDecoration.ITALIC, false)));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "gem_type"), PersistentDataType.STRING, gemType);
        gem.setItemMeta(meta);
        return gem;
    }

    public ItemStack createSocketAdder() {
        ItemStack item = new ItemStack(Material.NETHERITE_SCRAP);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(1004);
        meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(lang.getRawMessage("socket_tool_created")));
        meta.lore(List.of(Component.text("§7Adiciona um engaste vazio.").decoration(TextDecoration.ITALIC, false)));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_socket_adder"), PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        ItemStack target = event.getCurrentItem();

        if (cursor == null || cursor.getType() == Material.AIR) return;
        if (target == null || target.getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();
        ItemMeta cursorMeta = cursor.getItemMeta();

        // Socket Adder
        if (cursorMeta.getPersistentDataContainer().has(new NamespacedKey(plugin, "is_socket_adder"), PersistentDataType.BYTE)) {
            event.setCancelled(true);
            int currentSockets = NBTUtils.getSockets(target);
            int maxSockets = plugin.getConfig().getInt("settings.max_sockets", 3);
            if (currentSockets >= maxSockets) {
                player.sendMessage(lang.getMessage("socket_limit"));
                return;
            }
            NBTUtils.setSockets(target, currentSockets + 1);
            LoreManager.updateLore(target);
            cursor.setAmount(cursor.getAmount() - 1);
            player.sendMessage(lang.getMessage("socket_added"));
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1f);
            return;
        }

        // Gem Application
        if (cursorMeta.getPersistentDataContainer().has(new NamespacedKey(plugin, "gem_type"), PersistentDataType.STRING)) {
            event.setCancelled(true);
            int sockets = NBTUtils.getSockets(target);
            String[] gems = NBTUtils.getGems(target);
            if (gems.length >= sockets) {
                player.sendMessage(lang.getMessage("no_empty_sockets"));
                return;
            }
            String gemType = cursorMeta.getPersistentDataContainer().get(new NamespacedKey(plugin, "gem_type"), PersistentDataType.STRING);
            NBTUtils.addGem(target, gemType);
            LoreManager.updateLore(target);
            cursor.setAmount(cursor.getAmount() - 1);
            player.sendMessage(lang.getMessage("gem_applied"));
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);
            return;
        }

        // White Scroll
        if (cursorMeta.getPersistentDataContainer().has(new NamespacedKey(plugin, "is_white_scroll"), PersistentDataType.BYTE)) {
            event.setCancelled(true);
            if (NBTUtils.isProtected(target)) return;
            NBTUtils.setProtected(target, true);
            LoreManager.updateLore(target);
            cursor.setAmount(cursor.getAmount() - 1);
            player.sendMessage(lang.getMessage("item_applied"));
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1f, 1f);
            return;
        }

        // Black Scroll
        if (cursorMeta.getPersistentDataContainer().has(new NamespacedKey(plugin, "is_black_scroll"), PersistentDataType.BYTE)) {
            event.setCancelled(true);
            ItemMeta targetMeta = target.getItemMeta();
            Map<Enchantment, Integer> enchants = targetMeta.getEnchants();
            if (enchants.isEmpty()) return;

            List<Enchantment> list = new ArrayList<>(enchants.keySet());
            Enchantment toRemove = list.get(random.nextInt(list.size()));
            int level = enchants.get(toRemove);

            targetMeta.removeEnchant(toRemove);
            target.setItemMeta(targetMeta);
            
            // Limpeza de NBT específica
            String enchantId = toRemove.getKey().value();
            NBTUtils.removeEnchant(target, enchantId);
            LoreManager.updateLore(target);

            player.getInventory().addItem(createOrb(enchantId, 100));
            cursor.setAmount(cursor.getAmount() - 1);
            player.sendMessage(lang.getMessage("black_scroll_success"));
            player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1f, 1f);
            return;
        }

        // Magic Dust
        if (NBTUtils.isDust(cursor) && NBTUtils.getOrbEnchant(target) != null) {
            event.setCancelled(true);
            int dustPercent = NBTUtils.getDustPercent(cursor);
            int currentSuccess = NBTUtils.getSuccessRate(target);
            NBTUtils.setSuccessRate(target, Math.min(100, currentSuccess + dustPercent));
            
            // Atualiza lore do orbe
            ItemMeta meta = target.getItemMeta();
            List<Component> lore = meta.lore();
            if (lore != null) {
                for (int i = 0; i < lore.size(); i++) {
                    String plain = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(lore.get(i));
                    if (plain.contains("Chance de Sucesso")) {
                        lore.set(i, Component.text("§7Chance de Sucesso: §a" + NBTUtils.getSuccessRate(target) + "%").decoration(TextDecoration.ITALIC, false));
                        break;
                    }
                }
                meta.lore(lore);
                target.setItemMeta(meta);
            }
            cursor.setAmount(cursor.getAmount() - 1);
            player.sendMessage(lang.getMessage("magic_dust_applied"));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f);
            return;
        }

        // Orb Application
        String enchantId = NBTUtils.getOrbEnchant(cursor);
        if (enchantId != null) {
            event.setCancelled(true);
            Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key("customenchants:" + enchantId));
            if (enchantment == null) enchantment = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(enchantId));

            if (enchantment != null) {
                if (random.nextInt(100) >= NBTUtils.getSuccessRate(cursor)) {
                    cursor.setAmount(cursor.getAmount() - 1);
                    if (NBTUtils.isProtected(target)) {
                        NBTUtils.setProtected(target, false);
                        LoreManager.updateLore(target);
                        player.sendMessage(lang.getMessage("white_scroll_saved"));
                        player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 1f);
                    } else {
                        player.sendMessage(lang.getMessage("orb_failed"));
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
                    }
                    return;
                }
                ItemMeta targetMeta = target.getItemMeta();
                targetMeta.addEnchant(enchantment, 1, true);
                target.setItemMeta(targetMeta);
                NBTUtils.addEnchant(target, enchantId);
                LoreManager.updateLore(target);
                cursor.setAmount(cursor.getAmount() - 1);
                player.sendMessage(lang.getMessage("orb_applied"));
                player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
            }
        }
    }

    private String getFriendlyName(String key) {
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
            case "divine_aura" -> "Aura Divina";
            default -> key;
        };
    }
}
