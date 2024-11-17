package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SetGainedMaxCommand implements CommandExecutor {
    private final DeathPulse plugin;

    public SetGainedMaxCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if (!(player.isOp()) || !(player.hasPermission("dp.setGainedMax"))){
                sender.sendMessage("You do not have permission to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("This command can only be run by a player or from the console.");
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /DeathPulse setGainedMax <true/false> <maxHealthAmount>");
            return true;
        } else if ((args[1].equalsIgnoreCase("true")) && (args.length != 3)) {
            sender.sendMessage("When gained max set to true, you must input amount.");
            return true;
        }

        boolean gainedMaxEnabled;
        try {
            gainedMaxEnabled = Boolean.parseBoolean(args[1]);
        } catch (Exception e) {
            sender.sendMessage("Invalid value for gained max enabled. Use true or false.");
            return true;
        }

        int newGainedMaxAmount = 0;
        if (gainedMaxEnabled) {
            if (args.length != 3) {
                sender.sendMessage("When gained max is set to true, you must input amount.");
                return true;
            }
            try {
                newGainedMaxAmount = Integer.parseInt(args[2]);
                if (newGainedMaxAmount <= 0) {
                    sender.sendMessage("Gained per death amount must be positive.");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid health amount.");
                return true;
            }
        }

        plugin.getConfigManager().setGainedMaxEnabled(gainedMaxEnabled);
        if (gainedMaxEnabled) {
            plugin.getConfigManager().setGainedMaxAmount(newGainedMaxAmount);
        }
        plugin.saveConfig();
        plugin.reloadConfig();
        sender.sendMessage("Gained max set to " + args[1] + (gainedMaxEnabled ? " with amount " + args[2] : ""));
        sender.sendMessage("Reload the plugin or restart the server for changes to take effect.");

        return true;
    }
}