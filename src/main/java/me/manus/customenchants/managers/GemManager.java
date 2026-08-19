package me.manus.customenchants.managers;
import me.manus.customenchants.utils.NBTUtils;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GemManager {

    private final JavaPlugin plugin;
    private final NamespacedKey RUBY_KEY;
    private final NamespacedKey TOPAZ_KEY;

    public GemManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.RUBY_KEY = new NamespacedKey(plugin, "gem_ruby_strength");
        this.TOPAZ_KEY = new NamespacedKey(plugin, "gem_topaz_speed");
        startGemTask();
    }

    private void startGemTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    applyGemBonuses(player);
                }
            }
        }.runTaskTimer(plugin, 60L, 60L);
    }

    private void applyGemBonuses(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack[] armor = player.getInventory().getArmorContents();
        
        int rubyCount = 0;
        int topazCount = 0;
        int emeraldCount = 0;

        if (mainHand != null && !mainHand.getType().isAir()) {
            for (String gem : NBTUtils.getGems(mainHand)) {
                if (gem.equalsIgnoreCase("ruby")) rubyCount++;
                if (gem.equalsIgnoreCase("topaz")) topazCount++;
                if (gem.equalsIgnoreCase("emerald")) emeraldCount++;
            }
        }

        for (ItemStack piece : armor) {
            if (piece == null || piece.getType().isAir()) continue;
            for (String gem : NBTUtils.getGems(piece)) {
                if (gem.equalsIgnoreCase("ruby")) rubyCount++;
                if (gem.equalsIgnoreCase("topaz")) topazCount++;
                if (gem.equalsIgnoreCase("emerald")) emeraldCount++;
            }
        }

        // Ruby (Strength)
        AttributeInstance attackDamage = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.removeModifier(RUBY_KEY);
            if (rubyCount > 0) {
                attackDamage.addModifier(new AttributeModifier(RUBY_KEY, rubyCount * 1.5, AttributeModifier.Operation.ADD_NUMBER));
            }
        }

        // Topaz (Speed)
        AttributeInstance movementSpeed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(TOPAZ_KEY);
            if (topazCount > 0) {
                movementSpeed.addModifier(new AttributeModifier(TOPAZ_KEY, topazCount * 0.01, AttributeModifier.Operation.ADD_NUMBER));
            }
        }
        
        // Emerald (Luck)
        if (emeraldCount > 0) {
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.LUCK, 100, emeraldCount - 1, false, false, true));
        }
    }
}
