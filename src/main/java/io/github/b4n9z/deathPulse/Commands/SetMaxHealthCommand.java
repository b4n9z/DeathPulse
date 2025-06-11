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

public class SetMaxHealthCommand implements CommandExecutor {
    private final DeathPulse plugin;

    public SetMaxHealthCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if (!(player.isOp()) || !(player.hasPermission("dp.setMaxHealth")) || !plugin.getConfigManager().isPermissionAllPlayerSetMaxHealth()) {
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§fThis command§c can only be run§f by a player or from the console.");
            return false;
        }

        if (args.length != 3) {
            sender.sendMessage("§fUsage:§c /DeathPulse§b setMaxHealth§f <player> <amount>");
            return true;
        }

        String targetPlayerName = args[1];
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        double newHealth;
        try {
            newHealth = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid health amount.");
            return true;
        }

        if (plugin.getConfigManager().isMaxHPEnabled() && newHealth > plugin.getConfigManager().getMaxHPAmount()) {
            sender.sendMessage("§fHealth amount§c exceeds§f the max limit (§c" + plugin.getConfigManager().getMaxHPAmount() + "§f).");
            return true;
        } else if (plugin.getConfigManager().isMinHPEnabled() && newHealth < plugin.getConfigManager().getMinHPAmount()) {
            sender.sendMessage("§fHealth amount is§c under§f the min limit (§c" +  plugin.getConfigManager().getMinHPAmount() + "§f).");
            return true;
        }

        if (targetPlayer == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(targetPlayerName));
            if (!offlinePlayer.hasPlayedBefore()) {
                sender.sendMessage("§cPlayer not found.");
                return false;
            }
            HealthManager.setOfflinePlayerMaxHealth(newHealth, offlinePlayer);
            sender.sendMessage("§fSet§b " + offlinePlayer.getName() + "'s§f health to§d " + newHealth);
        } else {
            HealthManager.setMaxHealth(newHealth, targetPlayer);
            targetPlayer.sendMessage("§bYour§f health has been set to§d " + newHealth + "§b by an admin.");
            sender.sendMessage("§fSet§b " + targetPlayer.getName() + "'s§f health to§d " + newHealth);
        }

        return true;
    }
}
