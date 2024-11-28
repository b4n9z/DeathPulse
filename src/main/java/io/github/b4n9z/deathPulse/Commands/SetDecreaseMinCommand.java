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
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§fThis command§c can only be run§f by a player or from the console.");
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage("§fUsage:§c /DeathPulse§b setDecreaseMin§f <true/false> <minHealthAmount/banTime>");
            return true;
        } else if ((args[1].equalsIgnoreCase("true")) && (args.length != 3)) {
            sender.sendMessage("§fWhen gained min is set to§a true§f,§b you§c must§f input§d amount§f.");
            return true;
        } else if ((args[1].equalsIgnoreCase("false")) && (args.length != 3)) {
            sender.sendMessage("§fWhen decreased min is set to§c false§f,§b you§c must input§d ban time in hours§f.");
            return true;
        }

        boolean decreasedMinEnabled;
        try {
            decreasedMinEnabled = Boolean.parseBoolean(args[1]);
        } catch (Exception e) {
            sender.sendMessage("§cInvalid value for decreased min enabled. Use true or false.");
            return true;
        }

        int newDecreasedMinAmount = 0;
        int newBanTime = 0;

        if (decreasedMinEnabled) {
            if (args.length != 3) {
                sender.sendMessage("§fWhen decreased min is set to§a true§f,§b you§c must§f input§d amount§f.");
                return true;
            }
            try {
                newDecreasedMinAmount = Integer.parseInt(args[2]);
                if (newDecreasedMinAmount < 0) {
                    sender.sendMessage("§cDecreased min amount must be positive.");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid health amount.");
                return true;
            }
        } else {
            if (args.length != 3) {
                sender.sendMessage("§fWhen decreased min is set to§c false§f,§b you§c must§f input§d ban time in hours.");
                return true;
            }
            try {
                newBanTime = Integer.parseInt(args[2]);
                if (newBanTime < 0) {
                    sender.sendMessage("§cBan time must be positive.");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid ban time.");
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
        sender.sendMessage("§fDecrease min set to " + args[1] + (decreasedMinEnabled ? " with amount§c " + args[2] : "§f with ban time§c " + args[2]) + " hours");
        sender.sendMessage("§fReload the plugin§c or§f restart the server§f for changes to take effect");

        return true;
    }
}