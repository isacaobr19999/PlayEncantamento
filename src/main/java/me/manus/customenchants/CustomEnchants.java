package me.manus.customenchants;

import me.manus.customenchants.commands.EnchantCommand;
import me.manus.customenchants.hooks.PlayEncantamentoExpansion;
import me.manus.customenchants.listeners.*;
import me.manus.customenchants.managers.*;
import me.manus.customenchants.utils.NBTUtils;

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
        // 1. Inicializar Configuração e Mensagens
        saveDefaultConfig();
        this.langManager = new LangManager(this);
        
        // 2. Inicializar Utilitários
        NBTUtils.init(this);
        
        // 3. Inicializar Managers
        this.cooldownManager = new CooldownManager();
        this.guiManager = new GUIManager(this);
        this.orbManager = new OrbManager(this);
        this.craftingManager = new CraftingManager(this);
        this.auraManager = new AuraManager(this);
        this.setBonusManager = new SetBonusManager(this);
        this.gemManager = new GemManager(this);
        
        // 4. Registrar Hooks Externos
        setupHooks();
        
        // 5. Registrar Listeners
        registerListeners();
        
        // 6. Registrar Comandos
        registerCommands();
        
        getLogger().info("PlayEncantamento v" + getDescription().getVersion() + " habilitado com sucesso!");
    }

    private void setupHooks() {
        // PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlayEncantamentoExpansion(this).register();
            getLogger().info("Hook: PlaceholderAPI detectado.");
        }
        
        // Vault (Economy)
        if (!setupEconomy()) {
            getLogger().warning("Vault não encontrado! Sistema de economia desabilitado.");
        }
    }

    private void registerListeners() {
        var pm = Bukkit.getPluginManager();
        
        // Encantamentos
        pm.registerEvents(new LifestealEnchantmentListener(this), this);
        pm.registerEvents(new ExplosivePickaxeEnchantmentListener(this), this);
        pm.registerEvents(new ThunderAspectEnchantmentListener(this), this);
        pm.registerEvents(new TelekinesisEnchantmentListener(this), this);
        pm.registerEvents(new VampirismEnchantmentListener(this), this);
        pm.registerEvents(new SoulboundListener(this), this);
        pm.registerEvents(new FlightEnchantmentListener(this), this);
        pm.registerEvents(new HardenedEnchantmentListener(this), this);
        pm.registerEvents(new BerserkerListener(this), this);
        pm.registerEvents(new FrostbiteListener(this), this);
        
        // Mecânicas Especiais
        new MendingTwoListener(this);
        pm.registerEvents(guiManager, this);
        pm.registerEvents(orbManager, this);
        pm.registerEvents(new EliteVisualsListener(this), this);
        pm.registerEvents(new BossLootListener(this), this);
        new DivineAuraListener(this);
    }

    private void registerCommands() {
        EnchantCommand enchantCommand = new EnchantCommand(this);
        registerCommand("ce", new io.papermc.paper.command.brigadier.BasicCommand() {
            @Override
            public void execute(io.papermc.paper.command.brigadier.CommandSourceStack source, String[] args) {
                enchantCommand.onCommand(source.getSender(), null, "ce", args);
            }

            @Override
            public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack source, String[] args) {
                return enchantCommand.onTabComplete(source.getSender(), null, "ce", args);
            }
        });
    }

    public CooldownManager getCooldownManager() { return cooldownManager; }
    public GUIManager getGuiManager() { return guiManager; }
    public OrbManager getOrbManager() { return orbManager; }
    public LangManager getLangManager() { return langManager; }
    public CraftingManager getCraftingManager() { return craftingManager; }
    public AuraManager getAuraManager() { return auraManager; }
    public SetBonusManager getSetBonusManager() { return setBonusManager; }
    public GemManager getGemManager() { return gemManager; }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() { return econ; }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        econ = null;
        getLogger().info("PlayEncantamento desabilitado!");
    }
}
