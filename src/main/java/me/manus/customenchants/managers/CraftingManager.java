package me.manus.customenchants.managers;
import me.manus.customenchants.CustomEnchants;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class CraftingManager {

    private final CustomEnchants plugin;

    public CraftingManager(CustomEnchants plugin) {
        this.plugin = plugin;
        registerRecipes();
    }

    public void registerRecipes() {
        // Receita para Orbe de Voo (Exemplo Elite)
        NamespacedKey flightOrbKey = new NamespacedKey(plugin, "recipe_flight_orb");
        ShapedRecipe flightOrbRecipe = new ShapedRecipe(flightOrbKey, plugin.getOrbManager().createOrb("flight", 50));
        flightOrbRecipe.shape("FGF", "GNG", "FGF");
        flightOrbRecipe.setIngredient('F', Material.FEATHER);
        flightOrbRecipe.setIngredient('G', Material.GOLD_INGOT);
        flightOrbRecipe.setIngredient('N', Material.NETHER_STAR);
        Bukkit.addRecipe(flightOrbRecipe);

        // Receita para Pó Mágico
        NamespacedKey dustKey = new NamespacedKey(plugin, "recipe_magic_dust");
        ShapedRecipe dustRecipe = new ShapedRecipe(dustKey, plugin.getOrbManager().createMagicDust(10));
        dustRecipe.shape("SSS", "SGS", "SSS");
        dustRecipe.setIngredient('S', Material.SUGAR);
        dustRecipe.setIngredient('G', Material.GLOWSTONE_DUST);
        Bukkit.addRecipe(dustRecipe);
    }
}
