package com.example.mtnp;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class MoneyTime extends JavaPlugin {
    private Economy economy;
    private int rewardTime;
    private int rewardAmount;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("MoneyTime plugin enabled!");

        // Initialize Vault and Economy
        if (!setupEconomy()) {
            getLogger().warning("Vault or an economy plugin not found. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Create a folder for configs in the plugins directory
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            if (dataFolder.mkdir()) {
                getLogger().info("Created folder: " + dataFolder.getAbsolutePath());
            } else {
                getLogger().warning("Failed to create data folder: " + dataFolder.getAbsolutePath());
            }
        }

        // Copy the default config.yml file from resources to the newly created folder
        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            try (InputStream inputStream = getResource("config.yml")) {
                Files.copy(inputStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                getLogger().info("Copied default config.yml file to: " + configFile.getAbsolutePath());
            } catch (IOException e) {
                getLogger().warning("Failed to copy config.yml file: " + e.getMessage());
            }
        }

        // Load reward time and amount from config
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        rewardTime = config.getInt("rewardTime");
        rewardAmount = config.getInt("rewardAmount");

        // Schedule a task to reward players based on config
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    // Get total play time in minutes for player (Placeholder method)
                    int playTimeMinutes = getTotalPlayTime(player);

                    // Check if play time matches config and reward the player
                    if (playTimeMinutes >= rewardTime) {
                        // Reward player with money
                        economy.depositPlayer(player, rewardAmount);

                        // Send message to player
                        player.sendMessage("You have played for " + playTimeMinutes + " minutes. Here is " + rewardAmount + " money.");

                        getLogger().info(player.getName() + " rewarded " + rewardAmount + " for playing " + rewardTime + " minutes.");

                        // Execute a command to give the player money (replace 'eco' with your economy plugin command)
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + player.getName() + " " + rewardAmount);
                    }
                }
            }
        }.runTaskTimer(this, 0, 20 * 60); // Check every minute (20 ticks per second * 60 seconds)
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("MoneyTime plugin disabled!");
    }

    // Method to get total play time in minutes for a player (For testing purposes)
    private int getTotalPlayTime(Player player) {
        // Simulate playtime (e.g., 2 minutes for testing)
        return 2;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }
}