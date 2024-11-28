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
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§fThis command§c can only be run§f by a player or from the console.");
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage("§fUsage:§c /DeathPulse§b setDecrease§f <true/false> <perDeathAmount>");
            return true;
        } else if ((args[1].equalsIgnoreCase("true")) && (args.length != 3)) {
            sender.sendMessage("§fWhen Decrease set to§a true§f,§b you§c must input§d perDeathAmount§f.");
            return true;
        }

        boolean decreaseEnabled;
        try {
            decreaseEnabled = Boolean.parseBoolean(args[1]);
        } catch (Exception e) {
            sender.sendMessage("§cInvalid value for decrease enabled. Use true or false.");
            return true;
        }

        int newDecreasePerDeath = 0;
        if (decreaseEnabled) {
            if (args.length != 3) {
                sender.sendMessage("§fWhen Decrease set to§a true§f,§b you§c must§f input§d perDeathAmount§f.");
                return true;
            }
            try {
                newDecreasePerDeath = Integer.parseInt(args[2]);
                if (newDecreasePerDeath <= 0) {
                    sender.sendMessage("§cDecrease per death amount must be positive.");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid Format Number for perDeathAmount.");
                return true;
            }
        }

        plugin.getConfigManager().setDecreaseEnabled(decreaseEnabled);
        if (decreaseEnabled) {
            plugin.getConfigManager().setDecreasePerDeath(newDecreasePerDeath);
        }
        plugin.saveConfig();
        plugin.reloadConfig();
        sender.sendMessage("§fDecrease set to " + args[1] + (decreaseEnabled ? " with Health per death§d " + args[2] : ""));
        sender.sendMessage("§cReload the plugin§f or§c restart the server§f for changes to take effect.");

        return true;
    }
}