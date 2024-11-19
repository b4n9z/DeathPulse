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

public class ResetHealthCommand implements CommandExecutor {
    private final DeathPulse plugin;

    public ResetHealthCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if (!(player.isOp()) || !(player.hasPermission("dp.resetHealth"))){
                sender.sendMessage("You do not have permission to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("This command can only be run by a player or from the console.");
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage("Usage: /DeathPulse resetHealth <player|allPlayer>");
            return true;
        }

        int startHealth = plugin.getConfigManager().getHpStart();
        if (args[1].equalsIgnoreCase("allPlayer")) {
            // Reset health for all players
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                HealthManager.setMaxHealth(startHealth, onlinePlayer);
                onlinePlayer.sendMessage("Your health has been reset to " + startHealth + " by an admin.");
            }

            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                HealthManager.setOfflinePlayerMaxHealth(startHealth, offlinePlayer);
            }

            sender.sendMessage("All players' health has been reset to " + startHealth);
        } else {
            // Reset health for specified player
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer == null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(args[1]));
                if (!offlinePlayer.hasPlayedBefore()) {
                    sender.sendMessage("Player not found.");
                    return true;
                }
                HealthManager.setOfflinePlayerMaxHealth(startHealth, offlinePlayer);
                sender.sendMessage("Set " + offlinePlayer.getName() + "'s health to " + startHealth);
            } else {
                HealthManager.setMaxHealth(startHealth, targetPlayer);
                targetPlayer.sendMessage("Your health has been reset to " + startHealth + " by an admin.");
                sender.sendMessage("Set " + targetPlayer.getName() + "'s health to " + startHealth);
            }
        }
        return true;
    }
}