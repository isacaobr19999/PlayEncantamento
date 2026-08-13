package me.manus.customenchants;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;

public class CustomEnchantsBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        // Modificar encantamentos Vanilla para permitir nível 10
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.entryAdd().newHandler(event -> {
            event.builder().maxLevel(10);
        }).filter(EnchantmentKeys.EFFICIENCY));

        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.entryAdd().newHandler(event -> {
            event.builder().maxLevel(10);
        }).filter(EnchantmentKeys.FORTUNE));

        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.entryAdd().newHandler(event -> {
            event.builder().maxLevel(10);
        }).filter(EnchantmentKeys.SHARPNESS));

        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.entryAdd().newHandler(event -> {
            event.builder().maxLevel(10);
        }).filter(EnchantmentKeys.PROTECTION));

        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.entryAdd().newHandler(event -> {
            event.builder().maxLevel(10);
        }).filter(EnchantmentKeys.UNBREAKING));

        // Registrar encantamentos personalizados usando compose()
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
            // Lifesteal
            event.registry().register(
                    TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("customenchants:lifesteal")),
                    b -> b.description(Component.text("Roubo de Vida"))
                            .supportedItems(event.getOrCreateTag(TagKey.create(RegistryKey.ITEM, Key.key("minecraft:swords"))))
                            .anvilCost(1)
                            .maxLevel(3)
                            .weight(2)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(10, 10))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 10))
                            .activeSlots(EquipmentSlotGroup.ANY)
            );

            // Explosive Pickaxe
            event.registry().register(
                    TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("customenchants:explosive_pickaxe")),
                    b -> b.description(Component.text("Picareta Explosiva"))
                            .supportedItems(event.getOrCreateTag(TagKey.create(RegistryKey.ITEM, Key.key("minecraft:pickaxes"))))
                            .anvilCost(2)
                            .maxLevel(1)
                            .weight(1)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(15, 15))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(45, 15))
                            .activeSlots(EquipmentSlotGroup.MAINHAND)
            );

            // Thunder Aspect
            event.registry().register(
                    TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("customenchants:thunder_aspect")),
                    b -> b.description(Component.text("Aspecto do Trovão"))
                            .supportedItems(event.getOrCreateTag(TagKey.create(RegistryKey.ITEM, Key.key("minecraft:swords"))))
                            .anvilCost(2)
                            .maxLevel(2)
                            .weight(5)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(5, 5))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(25, 5))
                            .activeSlots(EquipmentSlotGroup.ANY)
            );

            // Telekinesis
            event.registry().register(
                    TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("customenchants:telekinesis")),
                    b -> b.description(Component.text("Teleforese"))
                            .supportedItems(event.getOrCreateTag(TagKey.create(RegistryKey.ITEM, Key.key("minecraft:pickaxes"))))
                            .anvilCost(5)
                            .maxLevel(1)
                            .weight(1)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(20, 20))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(50, 20))
                            .activeSlots(EquipmentSlotGroup.MAINHAND)
            );

            // Vampirism
            event.registry().register(
                    TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("customenchants:vampirism")),
                    b -> b.description(Component.text("Vampirismo"))
                            .supportedItems(event.getOrCreateTag(TagKey.create(RegistryKey.ITEM, Key.key("minecraft:swords"))))
                            .anvilCost(3)
                            .maxLevel(5)
                            .weight(2)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(15, 5))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(40, 5))
                            .activeSlots(EquipmentSlotGroup.ANY)
            );

            // Soulbound
            event.registry().register(
                    TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("customenchants:soulbound")),
                    b -> b.description(Component.text("Vínculo de Alma"))
                            .supportedItems(event.getOrCreateTag(TagKey.create(RegistryKey.ITEM, Key.key("minecraft:enchantable/vanishing_revealable"))))
                            .anvilCost(10)
                            .maxLevel(1)
                            .weight(1)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(25, 25))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(50, 25))
                            .activeSlots(EquipmentSlotGroup.ANY)
            );

            // Flight (Boots)
            event.registry().register(
                    TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("customenchants:flight")),
                    b -> b.description(Component.text("Voo"))
                            .supportedItems(event.getOrCreateTag(TagKey.create(RegistryKey.ITEM, Key.key("minecraft:enchantable/foot_armor"))))
                            .anvilCost(15)
                            .maxLevel(1)
                            .weight(1)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 30))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(60, 30))
                            .activeSlots(EquipmentSlotGroup.FEET)
            );

            // Hardened (Armor)
            event.registry().register(
                    TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("customenchants:hardened")),
                    b -> b.description(Component.text("Resistência"))
                            .supportedItems(event.getOrCreateTag(TagKey.create(RegistryKey.ITEM, Key.key("minecraft:enchantable/armor"))))
                            .anvilCost(5)
                            .maxLevel(3)
                            .weight(5)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(10, 10))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 10))
                            .activeSlots(EquipmentSlotGroup.ARMOR)
            );

            // Mending II (Auto-Repair)
            event.registry().register(
                    TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("customenchants:mending_two")),
                    b -> b.description(Component.text("Auto-Reparo II"))
                            .supportedItems(event.getOrCreateTag(TagKey.create(RegistryKey.ITEM, Key.key("minecraft:enchantable/durability"))))
                            .anvilCost(20)
                            .maxLevel(1)
                            .weight(1)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(40, 40))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(80, 40))
                            .activeSlots(EquipmentSlotGroup.ANY)
            );

            // Berserker
            event.registry().register(
                    TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("customenchants:berserker")),
                    b -> b.description(Component.text("Berserker"))
                            .supportedItems(event.getOrCreateTag(TagKey.create(RegistryKey.ITEM, Key.key("minecraft:swords"))))
                            .anvilCost(10)
                            .maxLevel(3)
                            .weight(2)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(20, 15))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(50, 15))
                            .activeSlots(EquipmentSlotGroup.ANY)
            );

            // Frostbite
            event.registry().register(
                    TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("customenchants:frostbite")),
                    b -> b.description(Component.text("Congelamento"))
                            .supportedItems(event.getOrCreateTag(TagKey.create(RegistryKey.ITEM, Key.key("minecraft:swords"))))
                            .anvilCost(8)
                            .maxLevel(2)
                            .weight(2)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(15, 10))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(40, 10))
                            .activeSlots(EquipmentSlotGroup.ANY)
            );

            // Divine Aura (Global Buff)
            event.registry().register(
                    TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("customenchants:divine_aura")),
                    b -> b.description(Component.text("Aura Divina"))
                            .supportedItems(event.getOrCreateTag(TagKey.create(RegistryKey.ITEM, Key.key("minecraft:enchantable/armor"))))
                            .anvilCost(25)
                            .maxLevel(1)
                            .weight(1)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(50, 50))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(100, 50))
                            .activeSlots(EquipmentSlotGroup.ARMOR)
            );
        }));
    }
}
