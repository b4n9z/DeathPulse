package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SetDecreaseCommand implements CommandExecutor {
    private final DeathPulse plugin;

    public SetDecreaseCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if (!(player.isOp()) || !(player.hasPermission("dp.setDecrease"))){
                sender.sendMessage("You do not have permission to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("This command can only be run by a player or from the console.");
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /DeathPulse setDecrease <true/false> <perDeathAmount> <minHealthAmount>");
            return false;
        } else if ((args[1].equalsIgnoreCase("true")) && (args.length != 4)) {
            sender.sendMessage("When Decrease set to true, you must input perDeathAmount and minHealthAmount.");
            return false;
        }

        boolean decreaseEnabled;
        try {
            decreaseEnabled = Boolean.parseBoolean(args[1]);
        } catch (Exception e) {
            sender.sendMessage("Invalid value for decrease enabled. Use true or false.");
            return false;
        }

        int newDecreasePerDeath = 0, newDecreaseMin = 0;
        if (decreaseEnabled) {
            if (args.length != 4) {
                sender.sendMessage("When Decrease set to true, you must input perDeathAmount and minHealthAmount.");
                return false;
            }
            try {
                newDecreasePerDeath = Integer.parseInt(args[2]);
                newDecreaseMin = Integer.parseInt(args[3]);
                if (newDecreasePerDeath <= 0) {
                    sender.sendMessage("Decrease per death amount must be positive.");
                    return false;
                }
                if (newDecreaseMin <= 0) {
                    sender.sendMessage("Decrease Health minimum must be positive.");
                    return false;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid Format Number for perDeathAmount or minHealthAmount.");
                return false;
            }
        }

        plugin.getConfigManager().setGainedMaxEnabled(decreaseEnabled);
        if (decreaseEnabled) {
            plugin.getConfigManager().setDecreasePerDeath(newDecreasePerDeath);
            plugin.getConfigManager().setDecreaseMin(newDecreaseMin);
        }
        plugin.saveConfig();
        plugin.reloadConfig();
        sender.sendMessage("Decrease set to " + args[1] + (decreaseEnabled ? " with Health per death " + args[2] + " and minimum amount " + args[3] : ""));
        sender.sendMessage("Reload the plugin or restart the server for changes to take effect.");

        return true;
    }
}