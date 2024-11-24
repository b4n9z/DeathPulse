package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import io.github.b4n9z.deathPulse.Managers.HealthManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SetHealthCommand implements CommandExecutor {
    private final DeathPulse plugin;

    public SetHealthCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if (!(player.isOp()) || !(player.hasPermission("dp.setHealth"))) {
                sender.sendMessage("You do not have permission to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("This command can only be run by a player or from the console.");
            return false;
        }

        if (args.length != 3) {
            sender.sendMessage("Usage: /DeathPulse setHealth <player> <amount>");
            return true;
        }

        String targetPlayerName = args[1];
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        double newHealth;
        try {
            newHealth = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid health amount.");
            return true;
        }

        if (newHealth > plugin.getConfigManager().getGainedMaxAmount()) {
            sender.sendMessage("Health amount exceeds the max limit (" + plugin.getConfigManager().getGainedMaxAmount() + ").");
            return true;
        } else if (newHealth < plugin.getConfigManager().getDecreaseMinAmount()) {
            sender.sendMessage("Health amount under the min limit (" +  plugin.getConfigManager().getDecreaseMinAmount() + ").");
            return true;
        }

        if (targetPlayer == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(targetPlayerName));
            if (!offlinePlayer.hasPlayedBefore()) {
                sender.sendMessage("Player not found.");
                return false;
            }
            HealthManager.setOfflinePlayerMaxHealth(newHealth, offlinePlayer);
            sender.sendMessage("Set " + offlinePlayer.getName() + "'s health to " + newHealth);
        } else {
            HealthManager.setMaxHealth(newHealth, targetPlayer);
            targetPlayer.sendMessage("Your health has been set to " + newHealth + " by an admin.");
            sender.sendMessage("Set " + targetPlayer.getName() + "'s health to " + newHealth);
        }

        return true;
    }
}
