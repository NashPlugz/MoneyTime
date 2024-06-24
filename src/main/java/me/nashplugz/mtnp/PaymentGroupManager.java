package me.nashplugz.mtnp;

import me.nashplugz.mtnp.config.Configuration;
import me.nashplugz.mtnp.config.ConfigurationManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PaymentGroupManager {

    private final Configuration mainConfiguration = ConfigurationManager.getConfig(ConfigurationManager.Config.MAIN);
    private final Set<PaymentGroup> paymentGroups = new HashSet<>();

    public PaymentGroupManager() {
        loadPaymentGroups();
    }

    private void loadPaymentGroups() {
        ConfigurationSection groupSection = mainConfiguration.get("groups", ConfigurationSection.class);

        groupSection.getKeys(false).forEach((key) -> {
            long interval = groupSection.getInt(key + ".interval");
            int amount = groupSection.getInt(key + ".amount");

            paymentGroups.add(new PaymentGroup(key, interval, amount));
        });
    }

    public Set<PaymentGroup> getPaymentGroups(Player player) {
        return paymentGroups.stream()
                .filter(group -> hasExactPermission(player, "mt.payment." + group.getName())).collect(Collectors.toSet());
    }

    public void reload() {
        paymentGroups.clear();
        loadPaymentGroups();
    }

    private boolean hasExactPermission(Player player, String permission) {
        for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
            if (permissionInfo.getPermission().equals(permission) && permissionInfo.getValue()) {
                return true;
            }
        }
        return false;
    }
}
