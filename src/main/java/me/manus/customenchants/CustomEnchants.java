package me.manus.customenchants;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;

public class CustomEnchants extends JavaPlugin {
    private CooldownManager cooldownManager;
    private GUIManager guiManager;
    private OrbManager orbManager;
    private LangManager langManager;
    private CraftingManager craftingManager;
    private AuraManager auraManager;
    private SetBonusManager setBonusManager;
    private GemManager gemManager;
    private static Economy econ = null;

    @Override
    public void onEnable() {
        this.langManager = new LangManager(this);
        NBTUtils.init(this);
        this.cooldownManager = new CooldownManager();
        this.guiManager = new GUIManager(this);
        this.orbManager = new OrbManager(this);
        this.craftingManager = new CraftingManager(this);
        this.auraManager = new AuraManager(this);
        this.setBonusManager = new SetBonusManager(this);
        this.gemManager = new GemManager(this);
        
        // PlaceholderAPI Integration
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlayEncantamentoExpansion(this).register();
            getLogger().info("Integração com PlaceholderAPI habilitada!");
        }
        
        // Setup Economy
        if (!setupEconomy()) {
            getLogger().warning("Vault não encontrado! Sistema de economia desabilitado.");
        }

        // Salvar configuração padrão
        saveDefaultConfig();
        
        // Plugin startup logic
        getLogger().info("PlayEncantamento habilitado!");

        // Registrar listeners dos encantamentos
        Bukkit.getPluginManager().registerEvents(new LifestealEnchantmentListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ExplosivePickaxeEnchantmentListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ThunderAspectEnchantmentListener(this), this);
        Bukkit.getPluginManager().registerEvents(new TelekinesisEnchantmentListener(this), this);
        Bukkit.getPluginManager().registerEvents(new VampirismEnchantmentListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SoulboundListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FlightEnchantmentListener(this), this);
        Bukkit.getPluginManager().registerEvents(new HardenedEnchantmentListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BerserkerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FrostbiteListener(this), this);
        new MendingTwoListener(this); // Task interna
        Bukkit.getPluginManager().registerEvents(guiManager, this);
        Bukkit.getPluginManager().registerEvents(orbManager, this);
        Bukkit.getPluginManager().registerEvents(new EliteVisualsListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BossLootListener(this), this);
        new DivineAuraListener(this);

        // Registrar comando
        if (getCommand("ce") != null) {
            EnchantCommand enchantCommand = new EnchantCommand(this);
            getCommand("ce").setExecutor(enchantCommand);
            getCommand("ce").setTabCompleter(enchantCommand);
        }
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public OrbManager getOrbManager() {
        return orbManager;
    }

    public LangManager getLangManager() {
        return langManager;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("PlayEncantamento desabilitado!");
    }
}
