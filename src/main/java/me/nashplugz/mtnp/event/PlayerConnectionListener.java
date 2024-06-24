package me.nashplugz.mtnp.event;

import me.nashplugz.mtnp.PlayerPlaytimeManager;
import me.nashplugz.mtnp.config.Configuration;
import me.nashplugz.mtnp.config.ConfigurationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerConnectionListener implements Listener {

    private final Configuration playerData = ConfigurationManager.getConfig(ConfigurationManager.Config.PLAYER_DATA);

    private final PlayerPlaytimeManager playtimeManager;

    public PlayerConnectionListener(PlayerPlaytimeManager playtimeManager) {
        this.playtimeManager = playtimeManager;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        playtimeManager.addPlayer(player);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        playtimeManager.removePlayer(player);
    }

}
