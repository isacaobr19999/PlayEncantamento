package me.manus.customenchants.listeners;
import me.manus.customenchants.CustomEnchants;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class BossLootListener implements Listener {

    private final CustomEnchants plugin;
    private final Random random = new Random();

    public BossLootListener(CustomEnchants plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        EntityType type = entity.getType();

        // Verificar se é um Boss
        if (type == EntityType.WITHER || type == EntityType.ENDER_DRAGON || type == EntityType.WARDEN) {
            handleLoot(event, true);
        } else if (entity instanceof org.bukkit.entity.Monster && random.nextDouble() < 0.01) { // 1% de chance para monstros normais
            handleLoot(event, false);
        }
    }

    private void handleLoot(EntityDeathEvent event, boolean isBoss) {
        // Drop Aleatório
        int type = random.nextInt(4);
        ItemStack drop = null;

        switch (type) {
            case 0 -> drop = plugin.getOrbManager().createOrb("divine_aura", 25);
            case 1 -> drop = plugin.getOrbManager().createBlackScroll();
            case 2 -> drop = plugin.getOrbManager().createMagicDust(25);
            case 3 -> drop = plugin.getOrbManager().createGem("ruby");
        }

        if (drop != null) {
            event.getDrops().add(drop);
        }
    }
}
