package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SetStartHealthCommand implements CommandExecutor {
    private final DeathPulse plugin;

    public SetStartHealthCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if (!(player.isOp()) || !(player.hasPermission("dp.setStartHealth"))) {
                sender.sendMessage("You do not have permission to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("This command can only be run by a player or from the console.");
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage("Usage: /DeathPulse setStartHealth <amount>");
            return false;
        }

        try {
            int newStartHealth = Integer.parseInt(args[1]);
            if (newStartHealth <= 0) {
                sender.sendMessage("Start Health amount must be positive.");
                return false;
            }
            plugin.getConfigManager().setHpStart(newStartHealth);
            plugin.saveConfig();
            plugin.reloadConfig();
            sender.sendMessage("Start health set to " + newStartHealth);
            sender.sendMessage("Reload the plugin or restart the server for changes to take effect.");
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid health amount.");
        }

        return true;
    }
}