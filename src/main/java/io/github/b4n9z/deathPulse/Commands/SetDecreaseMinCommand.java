package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SetDecreaseMinCommand implements CommandExecutor {
    private final DeathPulse plugin;

    public SetDecreaseMinCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if (!(player.isOp()) || !(player.hasPermission("dp.setDecreaseMin"))){
                sender.sendMessage("You do not have permission to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("This command can only be run by a player or from the console.");
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /DeathPulse setDecreaseMin <true/false> <minHealthAmount/banTime>");
            return true;
        } else if ((args[1].equalsIgnoreCase("true")) && (args.length != 3)) {
            sender.sendMessage("When gained min is set to true, you must input amount.");
            return true;
        } else if ((args[1].equalsIgnoreCase("false")) && (args.length != 3)) {
            sender.sendMessage("When decreased min is set to false, you must input ban time in hours.");
            return true;
        }

        boolean decreasedMinEnabled;
        try {
            decreasedMinEnabled = Boolean.parseBoolean(args[1]);
        } catch (Exception e) {
            sender.sendMessage("Invalid value for decreased min enabled. Use true or false.");
            return true;
        }

        int newDecreasedMinAmount = 0;
        int newBanTime = 0;

        if (decreasedMinEnabled) {
            if (args.length != 3) {
                sender.sendMessage("When decreased min is set to true, you must input amount.");
                return true;
            }
            try {
                newDecreasedMinAmount = Integer.parseInt(args[2]);
                if (newDecreasedMinAmount < 0) {
                    sender.sendMessage("Decreased min amount must be positive.");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid health amount.");
                return true;
            }
        } else {
            if (args.length != 3) {
                sender.sendMessage("When decreased min is set to false, you must input ban time in hours.");
                return true;
            }
            try {
                newBanTime = Integer.parseInt(args[2]);
                if (newBanTime < 0) {
                    sender.sendMessage("Ban time must be positive.");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid ban time.");
                return true;
            }
        }

        plugin.getConfigManager().setDecreaseMinEnabled(decreasedMinEnabled);
        if (decreasedMinEnabled) {
            plugin.getConfigManager().setDecreaseMinAmount(newDecreasedMinAmount);
        } else {
            plugin.getConfigManager().setDecreaseBanTime(newBanTime);
        }
        plugin.saveConfig();
        plugin.reloadConfig();
        sender.sendMessage("Decrease min set to " + args[1] + (decreasedMinEnabled ? " with amount " + args[2] : " with ban time " + args[2]) + " hours");
        sender.sendMessage("Reload the plugin or restart the server for changes to take effect");

        return true;
    }
}