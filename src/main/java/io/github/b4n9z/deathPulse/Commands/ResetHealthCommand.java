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
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§fThis command§c can only be run§f by a player or from the console.");
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage("§fUsage:§c /DeathPulse§b resetHealth§f <player|allPlayer>");
            return true;
        }

        int startHealth;
        try {
            startHealth = plugin.getConfigManager().getHpStart();
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid health amount.");
            return true;
        }

        if (args[1].equalsIgnoreCase("allPlayer")) {
            // Reset health for all players
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                HealthManager.setMaxHealth(startHealth, onlinePlayer);
                onlinePlayer.sendMessage("§bYour§f health has been reset to§d " + startHealth + "§b by an admin.");
            }

            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                HealthManager.setOfflinePlayerMaxHealth(startHealth, offlinePlayer);
            }

            sender.sendMessage("§bAll players'§f health has been reset to§d " + startHealth);
        } else {
            // Reset health for specified player
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer == null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(args[1]));
                if (!offlinePlayer.hasPlayedBefore()) {
                    sender.sendMessage("§cPlayer not found.");
                    return true;
                }
                HealthManager.setOfflinePlayerMaxHealth(startHealth, offlinePlayer);
                sender.sendMessage("§fSet§b " + offlinePlayer.getName() + "'s§f health to§d " + startHealth);
            } else {
                HealthManager.setMaxHealth(startHealth, targetPlayer);
                targetPlayer.sendMessage("§bYour§f health has been reset to§d " + startHealth + "§b by an admin.");
                sender.sendMessage("§fSet§b " + targetPlayer.getName() + "'s§f health to§d " + startHealth);
            }
        }
        return true;
    }
}