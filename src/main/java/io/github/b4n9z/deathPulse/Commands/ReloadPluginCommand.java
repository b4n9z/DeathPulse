package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadPluginCommand implements CommandExecutor {
    private final DeathPulse plugin;

    public ReloadPluginCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if (player.isOp() || player.hasPermission("dp.reload")) {
                plugin.reloadConfig();
                plugin.loadConfigManager();
                player.sendMessage("§fPlugin reloaded§a successfully.");
            } else {
                player.sendMessage("§fYou§c do not have permission§f to use this command.");
            }
        } else {
            // If run from console
            plugin.reloadConfig();
            plugin.loadConfigManager();
            sender.sendMessage("§fPlugin reloaded§a successfully.");
        }
        return true;
    }
}

