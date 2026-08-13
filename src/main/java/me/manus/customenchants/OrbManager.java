package me.manus.customenchants;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
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

    public OrbManager(CustomEnchants plugin) {
        this.plugin = plugin;
    }

    public ItemStack createOrb(String enchantId, int successRate) {
        ItemStack orb = new ItemStack(Material.SLIME_BALL);
        ItemMeta meta = orb.getItemMeta();
        meta.setCustomModelData(1001);
        
        String friendlyName = getFriendlyName(enchantId);
        meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(
                plugin.getLangManager().getRawMessage("orb_name").replace("{enchant}", friendlyName)));
        
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7Arraste este orbe sobre um item"));
        lore.add(Component.text("§7para aplicar o encantamento."));
        lore.add(Component.text("§7Chance de Sucesso: §a" + successRate + "%"));
        lore.add(Component.text("§8ID: " + enchantId));
        
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
                plugin.getLangManager().getRawMessage("magic_dust_name").replace("{percent}", String.valueOf(percent))));
        
        meta.lore(List.of(
                Component.text("§7Arraste sobre um Orbe de Encantamento"),
                Component.text("§7para aumentar sua chance de sucesso.")
        ));
        
        dust.setItemMeta(meta);
        NBTUtils.setDust(dust, percent);
        return dust;
    }

    public ItemStack createWhiteScroll() {
        ItemStack scroll = new ItemStack(Material.PAPER);
        ItemMeta meta = scroll.getItemMeta();
        meta.setCustomModelData(1003);
        
        meta.displayName(Component.text("§f§lPergaminho de Proteção (White Scroll)")
                .decoration(TextDecoration.ITALIC, false));
        
        meta.lore(List.of(
                Component.text("§7Arraste sobre um item para protegê-lo"),
                Component.text("§7contra falhas de orbes de encantamento."),
                Component.text("§7O pergaminho é consumido na proteção.")
        ));
        
        scroll.setItemMeta(meta);
        meta.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(plugin, "is_white_scroll"), PersistentDataType.BYTE, (byte) 1);
        scroll.setItemMeta(meta);
        return scroll;
    }

    public ItemStack createBlackScroll() {
        ItemStack scroll = new ItemStack(Material.INK_SAC);
        ItemMeta meta = scroll.getItemMeta();
        meta.setCustomModelData(1005);
        
        meta.displayName(Component.text("§8§lPergaminho Negro (Black Scroll)")
                .decoration(TextDecoration.ITALIC, false));
        
        meta.lore(List.of(
                Component.text("§7Arraste sobre um item para remover um"),
                Component.text("§7encantamento aleatório e transformá-lo em livro."),
                Component.text("§cRisco de destruir o item se falhar!")
        ));
        
        scroll.setItemMeta(meta);
        meta.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(plugin, "is_black_scroll"), PersistentDataType.BYTE, (byte) 1);
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
        meta.setCustomModelData(2000); // Base para gemas
        
        String name = switch (gemType.toLowerCase()) {
            case "ruby" -> "§c§lGema: Rubi";
            case "sapphire" -> "§9§lGema: Safira";
            case "emerald" -> "§a§lGema: Esmeralda";
            case "topaz" -> "§e§lGema: Topázio";
            default -> "§f§lGema";
        };
        
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(Component.text("§7Arraste sobre um item com engastes vazios.")));
        gem.setItemMeta(meta);
        
        meta.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(plugin, "gem_type"), PersistentDataType.STRING, gemType);
        gem.setItemMeta(meta);
        return gem;
    }

    public ItemStack createSocketAdder() {
        ItemStack item = new ItemStack(Material.NETHERITE_SCRAP);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(1004);
        meta.displayName(Component.text("§6§lFerramenta de Engaste").decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(Component.text("§7Adiciona um engaste vazio ao item.")));
        item.setItemMeta(meta);
        meta.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(plugin, "is_socket_adder"), PersistentDataType.BYTE, (byte) 1);
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

        // Lógica de Adicionar Socket
        if (cursorMeta.getPersistentDataContainer().has(new org.bukkit.NamespacedKey(plugin, "is_socket_adder"), PersistentDataType.BYTE)) {
            event.setCancelled(true);
            int currentSockets = NBTUtils.getSockets(target);
            if (currentSockets >= 3) {
                player.sendMessage(Component.text("§cEste item já atingiu o limite de engastes!", NamedTextColor.RED));
                return;
            }
            NBTUtils.setSockets(target, currentSockets + 1);
            LoreManager.updateLore(target);
            cursor.setAmount(cursor.getAmount() - 1);
            player.sendMessage(Component.text("§aEngaste adicionado!", NamedTextColor.GREEN));
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1f);
            return;
        }

        // Lógica de Adicionar Gema
        if (cursorMeta.getPersistentDataContainer().has(new org.bukkit.NamespacedKey(plugin, "gem_type"), PersistentDataType.STRING)) {
            event.setCancelled(true);
            int sockets = NBTUtils.getSockets(target);
            String[] gems = NBTUtils.getGems(target);
            
            if (gems.length >= sockets) {
                player.sendMessage(Component.text("§cNão há engastes vazios!", NamedTextColor.RED));
                return;
            }
            
            String gemType = cursorMeta.getPersistentDataContainer().get(new org.bukkit.NamespacedKey(plugin, "gem_type"), PersistentDataType.STRING);
            NBTUtils.addGem(target, gemType);
            LoreManager.updateLore(target);
            cursor.setAmount(cursor.getAmount() - 1);
            player.sendMessage(Component.text("§aGema encrustada!", NamedTextColor.GREEN));
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);
            return;
        }

        // Lógica de White Scroll
        if (cursorMeta.getPersistentDataContainer().has(new org.bukkit.NamespacedKey(plugin, "is_white_scroll"), PersistentDataType.BYTE)) {
            event.setCancelled(true);
            if (NBTUtils.isProtected(target)) {
                player.sendMessage(Component.text("Este item já está protegido!", NamedTextColor.RED));
                return;
            }
            NBTUtils.setProtected(target, true);
            LoreManager.updateLore(target);
            cursor.setAmount(cursor.getAmount() - 1);
            player.sendMessage(Component.text("Item protegido com sucesso!", NamedTextColor.WHITE));
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1f, 1f);
            return;
        }

        // Lógica de Black Scroll
        if (cursorMeta.getPersistentDataContainer().has(new org.bukkit.NamespacedKey(plugin, "is_black_scroll"), PersistentDataType.BYTE)) {
            event.setCancelled(true);
            ItemMeta targetMeta = target.getItemMeta();
            Map<Enchantment, Integer> enchants = targetMeta.getEnchants();
            
            if (enchants.isEmpty()) {
                player.sendMessage(Component.text("§cEste item não possui encantamentos!", NamedTextColor.RED));
                return;
            }

            List<Enchantment> list = new ArrayList<>(enchants.keySet());
            Enchantment toRemove = list.get(random.nextInt(list.size()));
            int level = enchants.get(toRemove);

            targetMeta.removeEnchant(toRemove);
            target.setItemMeta(targetMeta);
            LoreManager.updateLore(target);

            // Criar livro
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            org.bukkit.inventory.meta.EnchantmentStorageMeta bookMeta = (org.bukkit.inventory.meta.EnchantmentStorageMeta) book.getItemMeta();
            bookMeta.addStoredEnchant(toRemove, level, true);
            book.setItemMeta(bookMeta);
            player.getInventory().addItem(book);

            cursor.setAmount(cursor.getAmount() - 1);
            player.sendMessage(Component.text("§aEncantamento extraído com sucesso!", NamedTextColor.GREEN));
            player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1f, 1f);
            return;
        }

        // Lógica de Pó Mágico
        if (NBTUtils.isDust(cursor) && NBTUtils.getOrbEnchant(target) != null) {
            event.setCancelled(true);
            int dustPercent = NBTUtils.getDustPercent(cursor);
            int currentSuccess = NBTUtils.getSuccessRate(target);
            int newSuccess = Math.min(100, currentSuccess + dustPercent);
            NBTUtils.setSuccessRate(target, newSuccess);
            
            ItemMeta meta = target.getItemMeta();
            List<Component> lore = meta.lore();
            if (lore != null) {
                for (int i = 0; i < lore.size(); i++) {
                    String plain = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(lore.get(i));
                    if (plain.contains("Chance de Sucesso")) {
                        lore.set(i, Component.text("§7Chance de Sucesso: §a" + newSuccess + "%"));
                        break;
                    }
                }
                meta.lore(lore);
                target.setItemMeta(meta);
            }
            cursor.setAmount(cursor.getAmount() - 1);
            player.sendMessage(plugin.getLangManager().getMessage("magic_dust_applied"));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f);
            return;
        }

        // Lógica de Orbe aplicado em Item
        String enchantId = NBTUtils.getOrbEnchant(cursor);
        if (enchantId == null) return;

        event.setCancelled(true);
        String enchantKeyStr = enchantId.contains(":") ? enchantId : "customenchants:" + enchantId;
        Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Key.key(enchantKeyStr));

        if (enchantment == null) {
            enchantment = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(enchantId));
        }

        if (enchantment != null) {
            int successRate = NBTUtils.getSuccessRate(cursor);
            if (random.nextInt(100) >= successRate) {
                cursor.setAmount(cursor.getAmount() - 1);
                if (NBTUtils.isProtected(target)) {
                    NBTUtils.setProtected(target, false);
                    LoreManager.updateLore(target);
                    player.sendMessage(Component.text("§f§lO Pergaminho de Proteção salvou seu item!", NamedTextColor.WHITE));
                    player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 1f);
                } else {
                    player.sendMessage(plugin.getLangManager().getMessage("orb_failed"));
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
            player.sendMessage(plugin.getLangManager().getMessage("orb_applied"));
            player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
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
            default -> key;
        };
    }
}
