package me.nashplugz.mtnp.config;

import me.nashplugz.mtnp.MoneyTime;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationManager {

    private final static ConfigurationManager INSTANCE = new ConfigurationManager();

    private final Map<Config, Configuration> configurationMap = new HashMap<>();
    private final Plugin plugin = JavaPlugin.getProvidingPlugin(MoneyTime.class);

    public enum Config {
        MAIN,
        PLAYER_DATA
    }

    public ConfigurationManager() {
        registerConfiguration();
    }

    private void registerConfiguration() {
        configurationMap.put(
                Config.MAIN,
                new Configuration(plugin, "config.yml", true)
                        .copy("config.yml")
                        .load()
        );
        configurationMap.put(
                Config.PLAYER_DATA,
                new Configuration(plugin, "player_data.yml", false)
                        .load()
        );
    }

    public Map<Config, Configuration> getConfigurationMap() {
        return configurationMap;
    }

    public static void reload() {
        INSTANCE.getConfigurationMap().forEach(((config, configuration) -> configuration.reload()));
    }

    public static Configuration getConfig(Config config) {
        return INSTANCE.configurationMap.get(config);
    }
}
