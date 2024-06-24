package me.nashplugz.mtnp;

import me.nashplugz.mtnp.command.MoneyTimeCommand;
import me.nashplugz.mtnp.event.PlayerConnectionListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class MoneyTime extends JavaPlugin {

    private Logger logger;
    private PlayerPlaytimeManager playtimeManager;

    @Override
    public void onEnable() {
        logger = Bukkit.getLogger();
        PluginManager pluginManager = Bukkit.getPluginManager();

        if (!setupEconomy()) pluginManager.disablePlugin(this);

        Economy economy = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
        PaymentGroupManager paymentGroupManager = new PaymentGroupManager();
        playtimeManager = new PlayerPlaytimeManager(this, economy, paymentGroupManager);

        pluginManager.registerEvents(new PlayerConnectionListener(playtimeManager), this);

        getCommand("moneytime").setExecutor(new MoneyTimeCommand(paymentGroupManager));

        Bukkit.getOnlinePlayers().forEach((player) -> playtimeManager.addPlayer(player));
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach((player) -> playtimeManager.removePlayer(player));
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            logger.severe("Vault plugin not found! Economy features will not be available.");
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            logger.severe("Economy plugin not found! Economy features will not be available.");
            return false;
        }

        logger.info("Economy setup successful.");
        return true;
    }
}