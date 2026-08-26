package me.manus.customenchants.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandValidationTest {
    @Test
    void acceptsOnlyPercentagesBetweenZeroAndOneHundred() {
        assertTrue(CommandValidation.isPercentage(0));
        assertTrue(CommandValidation.isPercentage(100));
        assertFalse(CommandValidation.isPercentage(-1));
        assertFalse(CommandValidation.isPercentage(101));
    }

    @Test
    void acceptsOnlyEnchantmentLevelsBetweenOneAndTwoHundredFiftyFive() {
        assertTrue(CommandValidation.isEnchantmentLevel(1));
        assertTrue(CommandValidation.isEnchantmentLevel(255));
        assertFalse(CommandValidation.isEnchantmentLevel(0));
        assertFalse(CommandValidation.isEnchantmentLevel(256));
    }
}
