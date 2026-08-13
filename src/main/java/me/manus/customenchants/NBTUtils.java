package me.manus.customenchants;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class NBTUtils {

    private static NamespacedKey ENCHANT_KEY;
    private static NamespacedKey ORB_KEY;
    private static NamespacedKey SUCCESS_KEY;
    private static NamespacedKey DUST_KEY;
    private static NamespacedKey WHITE_SCROLL_KEY;
    private static NamespacedKey TIER_KEY;
    private static NamespacedKey SOCKETS_KEY;
    private static NamespacedKey GEMS_KEY;
    private static JavaPlugin pluginInstance;

    public static void init(JavaPlugin plugin) {
        pluginInstance = plugin;
        ENCHANT_KEY = new NamespacedKey(plugin, "custom_enchants");
        ORB_KEY = new NamespacedKey(plugin, "enchant_orb");
        SUCCESS_KEY = new NamespacedKey(plugin, "success_rate");
        DUST_KEY = new NamespacedKey(plugin, "magic_dust");
        WHITE_SCROLL_KEY = new NamespacedKey(plugin, "white_scroll");
        TIER_KEY = new NamespacedKey(plugin, "enchant_tier");
        SOCKETS_KEY = new NamespacedKey(plugin, "item_sockets");
        GEMS_KEY = new NamespacedKey(plugin, "item_gems");
    }

    public static void addEnchant(ItemStack item, String enchantId) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        String current = meta.getPersistentDataContainer().getOrDefault(ENCHANT_KEY, PersistentDataType.STRING, "");
        if (!current.contains(enchantId)) {
            String updated = current.isEmpty() ? enchantId : current + "," + enchantId;
            meta.getPersistentDataContainer().set(ENCHANT_KEY, PersistentDataType.STRING, updated);
            item.setItemMeta(meta);
        }
    }

    public static boolean hasEnchant(ItemStack item, String enchantId) {
        if (item == null || !item.hasItemMeta()) return false;
        String current = item.getItemMeta().getPersistentDataContainer().get(ENCHANT_KEY, PersistentDataType.STRING);
        return current != null && current.contains(enchantId);
    }

    public static void setOrb(ItemStack item, String enchantId) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(ORB_KEY, PersistentDataType.STRING, enchantId);
        item.setItemMeta(meta);
    }

    public static String getOrbEnchant(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().get(ORB_KEY, PersistentDataType.STRING);
    }

    public static void setSuccessRate(ItemStack item, int rate) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(SUCCESS_KEY, PersistentDataType.INTEGER, rate);
        item.setItemMeta(meta);
    }

    public static int getSuccessRate(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 100;
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(SUCCESS_KEY, PersistentDataType.INTEGER, 100);
    }

    public static void setDust(ItemStack item, int percent) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(DUST_KEY, PersistentDataType.INTEGER, percent);
        item.setItemMeta(meta);
    }

    public static int getDustPercent(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(DUST_KEY, PersistentDataType.INTEGER, 0);
    }

    public static boolean isDust(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(DUST_KEY, PersistentDataType.INTEGER);
    }

    public static void setProtected(ItemStack item, boolean isProtected) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (isProtected) {
            meta.getPersistentDataContainer().set(WHITE_SCROLL_KEY, PersistentDataType.BYTE, (byte) 1);
        } else {
            meta.getPersistentDataContainer().remove(WHITE_SCROLL_KEY);
        }
        item.setItemMeta(meta);
    }

    public static boolean isProtected(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(WHITE_SCROLL_KEY, PersistentDataType.BYTE);
    }

    public static void addStat(ItemStack item, String statName, double value) {
        if (item == null || !item.hasItemMeta()) return;
        NamespacedKey key = new NamespacedKey(pluginInstance, "stat_" + statName);
        ItemMeta meta = item.getItemMeta();
        double current = meta.getPersistentDataContainer().getOrDefault(key, PersistentDataType.DOUBLE, 0.0);
        meta.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, current + value);
        item.setItemMeta(meta);
    }

    public static double getStat(ItemStack item, String statName) {
        if (item == null || !item.hasItemMeta()) return 0.0;
        NamespacedKey key = new NamespacedKey(pluginInstance, "stat_" + statName);
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(key, PersistentDataType.DOUBLE, 0.0);
    }

    public static void setTier(ItemStack item, String tier) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(TIER_KEY, PersistentDataType.STRING, tier);
        item.setItemMeta(meta);
    }

    public static String getTier(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return "COMUM";
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(TIER_KEY, PersistentDataType.STRING, "COMUM");
    }

    public static void addEnchantXP(ItemStack item, String enchantId, double amount) {
        if (item == null || !item.hasItemMeta()) return;
        NamespacedKey key = new NamespacedKey(pluginInstance, "exp_" + enchantId);
        ItemMeta meta = item.getItemMeta();
        double current = meta.getPersistentDataContainer().getOrDefault(key, PersistentDataType.DOUBLE, 0.0);
        meta.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, current + amount);
        item.setItemMeta(meta);
    }

    public static double getEnchantXP(ItemStack item, String enchantId) {
        if (item == null || !item.hasItemMeta()) return 0.0;
        NamespacedKey key = new NamespacedKey(pluginInstance, "exp_" + enchantId);
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(key, PersistentDataType.DOUBLE, 0.0);
    }

    public static void setSockets(ItemStack item, int count) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(SOCKETS_KEY, PersistentDataType.INTEGER, count);
        item.setItemMeta(meta);
    }

    public static int getSockets(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(SOCKETS_KEY, PersistentDataType.INTEGER, 0);
    }

    public static void addGem(ItemStack item, String gemType) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        String current = meta.getPersistentDataContainer().getOrDefault(GEMS_KEY, PersistentDataType.STRING, "");
        String updated = current.isEmpty() ? gemType : current + "," + gemType;
        meta.getPersistentDataContainer().set(GEMS_KEY, PersistentDataType.STRING, updated);
        item.setItemMeta(meta);
    }

    public static String[] getGems(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return new String[0];
        String gems = item.getItemMeta().getPersistentDataContainer().getOrDefault(GEMS_KEY, PersistentDataType.STRING, "");
        return gems.isEmpty() ? new String[0] : gems.split(",");
    }

    public static boolean hasGem(ItemStack item, String gemType) {
        for (String gem : getGems(item)) {
            if (gem.equalsIgnoreCase(gemType)) return true;
        }
        return false;
    }
}
