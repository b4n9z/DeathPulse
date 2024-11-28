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
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§fThis command§c can only be run§f by a player or from the console.");
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage("§fUsage:§c /DeathPulse§b setGainedMax§f <true/false> <maxHealthAmount>");
            return true;
        } else if ((args[1].equalsIgnoreCase("true")) && (args.length != 3)) {
            sender.sendMessage("§fWhen gained max set to§a true§f,§b you§c must§f input§d max amount§f.");
            return true;
        }

        boolean gainedMaxEnabled;
        try {
            gainedMaxEnabled = Boolean.parseBoolean(args[1]);
        } catch (Exception e) {
            sender.sendMessage("§cInvalid value for gained max enabled. Use true or false.");
            return true;
        }

        int newGainedMaxAmount = 0;
        if (gainedMaxEnabled) {
            if (args.length != 3) {
                sender.sendMessage("§fWhen gained max is set to§a true§f,§b you§c must§f input§d max amount§f.");
                return true;
            }
            try {
                newGainedMaxAmount = Integer.parseInt(args[2]);
                if (newGainedMaxAmount <= 0) {
                    sender.sendMessage("§cGained max amount must be positive.");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid health amount.");
                return true;
            }
        }

        plugin.getConfigManager().setGainedMaxEnabled(gainedMaxEnabled);
        if (gainedMaxEnabled) {
            plugin.getConfigManager().setGainedMaxAmount(newGainedMaxAmount);
        }
        plugin.saveConfig();
        plugin.reloadConfig();
        sender.sendMessage("§fGained max set to " + args[1] + (gainedMaxEnabled ? " with amount§d " + args[2] : ""));
        sender.sendMessage("§cReload the plugin§f or§c restart the server§f for changes to take effect.");

        return true;
    }
}