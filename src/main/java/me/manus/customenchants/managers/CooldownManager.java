package me.manus.customenchants.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();

    public boolean isOnCooldown(UUID uuid, String enchantment, double seconds) {
        if (!cooldowns.containsKey(enchantment)) return false;
        Map<UUID, Long> userCooldowns = cooldowns.get(enchantment);
        if (!userCooldowns.containsKey(uuid)) return false;
        
        long remaining = userCooldowns.get(uuid) - System.currentTimeMillis();
        return remaining > 0;
    }

    public void setCooldown(UUID uuid, String enchantment, double seconds) {
        cooldowns.computeIfAbsent(enchantment, k -> new HashMap<>())
                .put(uuid, System.currentTimeMillis() + (long)(seconds * 1000));
    }

    public double getRemaining(UUID uuid, String enchantment) {
        if (!cooldowns.containsKey(enchantment)) return 0;
        Long end = cooldowns.get(enchantment).get(uuid);
        if (end == null) return 0;
        return Math.max(0, (end - System.currentTimeMillis()) / 1000.0);
    }
}
