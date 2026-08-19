package me.manus.customenchants.managers;
import me.manus.customenchants.CustomEnchants;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LangManager {

    private final CustomEnchants plugin;
    private FileConfiguration langConfig;
    private File langFile;

    public LangManager(CustomEnchants plugin) {
        this.plugin = plugin;
        saveDefaultLang();
        reloadLang();
    }

    public void reloadLang() {
        if (langFile == null) {
            langFile = new File(plugin.getDataFolder(), "lang.yml");
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);

        InputStream defaultLangStream = plugin.getResource("lang.yml");
        if (defaultLangStream != null) {
            YamlConfiguration defaultLang = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultLangStream, StandardCharsets.UTF_8));
            langConfig.setDefaults(defaultLang);
        }
    }

    public void reload() {
        reloadLang();
    }

    public void saveDefaultLang() {
        if (langFile == null) {
            langFile = new File(plugin.getDataFolder(), "lang.yml");
        }
        if (!langFile.exists()) {
            plugin.saveResource("lang.yml", false);
        }
    }

    public String getRawMessage(String path) {
        return langConfig.getString(path, "Message not found: " + path);
    }

    public Component getMessage(String path) {
        String prefix = langConfig.getString("prefix", "");
        String message = langConfig.getString(path, path);
        return LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + message);
    }

    public Component getMessageNoPrefix(String path) {
        String message = langConfig.getString(path, path);
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }
}
