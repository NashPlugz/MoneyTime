package me.nashplugz.mtnp.command;

import me.nashplugz.mtnp.PaymentGroupManager;
import me.nashplugz.mtnp.config.ConfigurationManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MoneyTimeCommand implements CommandExecutor {

    private final PaymentGroupManager paymentGroupManager;

    public MoneyTimeCommand(PaymentGroupManager paymentGroupManager) {
        this.paymentGroupManager = paymentGroupManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (args.length == 0) {
            if (commandSender.hasPermission("moneytime.command")) {
                commandSender.sendMessage(ChatColor.RED + "Use /moneytime reload to reload the plugin.");
                return true;
            }
            return false;
        }

        switch (args[0]) {
            case "reload":
                if (!commandSender.hasPermission("moneytime.command.reload")) return false;

                ConfigurationManager.reload();
                paymentGroupManager.reload();

                commandSender.sendMessage(ChatColor.GREEN + "Successfully reloaded MoneyTime plugin.");
            return true;
        }

        return false;
    }
}
