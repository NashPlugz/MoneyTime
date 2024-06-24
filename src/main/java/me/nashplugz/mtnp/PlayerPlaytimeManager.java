package me.nashplugz.mtnp;

import me.nashplugz.mtnp.config.Configuration;
import me.nashplugz.mtnp.config.ConfigurationManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerPlaytimeManager {

    private final Configuration mainConfiguration = ConfigurationManager.getConfig(ConfigurationManager.Config.MAIN);
    private final Configuration playerData = ConfigurationManager.getConfig(ConfigurationManager.Config.PLAYER_DATA);

    private final String ADDED_MONEY_MESSAGE = mainConfiguration.get("message.added_money", String.class);

    private final Map<UUID, Long> playtimeMap = new HashMap<>();
    private final Plugin plugin;
    private final Economy economy;
    private final PaymentGroupManager paymentGroupManager;

    private BukkitTask task;

    public PlayerPlaytimeManager(Plugin plugin, Economy economy, PaymentGroupManager paymentGroupManager) {
        this.plugin = plugin;
        this.economy = economy;
        this.paymentGroupManager = paymentGroupManager;
    }

    private void update() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                playtimeMap.forEach((uuid, time) -> {
                    long currentTime = time + 1;
                    Player player = Bukkit.getPlayer(uuid);
                    Set<PaymentGroup> groups = paymentGroupManager.getPaymentGroups(player);

                    groups.forEach((group) -> {
                        if (currentTime > 0 && group.getInterval() > 0 && currentTime % group.getInterval() == 0) {
                            addPlayerMoney(player, group);
                            player.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    TextComponent.fromLegacy(
                                            ChatColor.translateAlternateColorCodes('&', ADDED_MONEY_MESSAGE
                                                    .replaceAll("\\{amount}", Integer.toString(group.getAmount()))
                                            )
                                    )
                            );
                        }
                    });
                    playtimeMap.put(uuid, currentTime);
                });
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 20 * 60);
    }

    private void addPlayerMoney(Player player, PaymentGroup group) {
        economy.depositPlayer(player, group.getAmount());
    }

    // Add a player with their current playtime in minutes
    public void addPlayer(Player player) {
        UUID uuid = player.getUniqueId();

        if (!playerData.has(uuid + ".playtime")) {
            playerData.set(uuid + ".playtime", 0);
        }

        long playtime = (long) playerData.get(uuid + ".playtime", Integer.class);

        playtimeMap.put(player.getUniqueId(), playtime);
        if (task == null) update();
    }

    // Get the playtime of a player in minutes
    public long getPlaytime(Player player) {
        return playtimeMap.get(player.getUniqueId());
    }

    // Remove a player from the playtime manager
    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();

        playerData.set(uuid + ".playtime", getPlaytime(player));

        playtimeMap.remove(player.getUniqueId());
        if (playtimeMap.isEmpty()) {
            task.cancel();
            task = null;
        }
    }

}
