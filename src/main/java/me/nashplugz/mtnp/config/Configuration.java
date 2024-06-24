package me.nashplugz.mtnp.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Configuration {

    private final Plugin plugin;
    private final File file;
    private final boolean shouldReload;

    private YamlConfiguration configuration;

    public Configuration(Plugin plugin, String path, boolean shouldReload) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), path);
        this.shouldReload = shouldReload;
    }

    public Configuration copy(String from) {
        if (!file.exists()) {
            try {
                Files.createDirectories(file.getParentFile().toPath());

                try (InputStream in = plugin.getResource("config/" + from)){
                    if (in == null) throw new IOException("Resource not found: config/" + from);
                    Files.copy(in, file.toPath());
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }

        return this;
    }

    public Configuration load() {
        configuration = YamlConfiguration.loadConfiguration(file);

        configuration.options().copyDefaults();
        save();

        return this;
    }

    // This is experimental, haven't tried this yet. Should work but needs testing.
    public <T> T get(String path, Class<T> type) {
        Object value = configuration.get(path);
        if (type.isInstance(value)) {
            return type.cast(value);
        } else {
            throw new RuntimeException("Your requested config value \"" + path + "\" isn't of type " + type.getName());
        }
    }

    public boolean has(String path) {
        return configuration.contains(path);
    }

    public Configuration set(String path, Object value) {
        configuration.set(path, value);
        save();

        return this;
    }

    public void reload() {
        load();
    }

    private void save() {
        try {
            configuration.save(file);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

}
