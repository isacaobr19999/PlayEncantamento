package me.manus.customenchants.utils;

/** Validações puras usadas pelos comandos administrativos. */
public final class CommandValidation {
    private CommandValidation() {
    }

    public static boolean isPercentage(int value) {
        return value >= 0 && value <= 100;
    }

    public static boolean isEnchantmentLevel(int value) {
        return value >= 1 && value <= 255;
    }
}
