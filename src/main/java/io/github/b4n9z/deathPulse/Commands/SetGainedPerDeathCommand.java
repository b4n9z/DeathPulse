package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SetGainedPerDeathCommand implements CommandExecutor {
    private final DeathPulse plugin;

    public SetGainedPerDeathCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if (!(player.isOp()) || !(player.hasPermission("dp.setGainedPerDeath"))){
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§fThis command§c can only be run§f by a player or from the console.");
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage("§fUsage:§c /DeathPulse§b setGainedPerDeath§f <amount>");
            return true;
        }

        try {
            int newGainedPerDeath = Integer.parseInt(args[1]);
            if (newGainedPerDeath <= 0) {
                sender.sendMessage("§cGained per death amount must be positive.");
                return true;
            }
            plugin.getConfigManager().setGainedPerDeath(newGainedPerDeath);
            plugin.saveConfig();
            plugin.reloadConfig();
            sender.sendMessage("§fGained per death set to§d " + newGainedPerDeath);
            sender.sendMessage("§cReload the plugin§f or§c restart the server§f for changes to take effect.");
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid health amount.");
        }
        return true;
    }
}