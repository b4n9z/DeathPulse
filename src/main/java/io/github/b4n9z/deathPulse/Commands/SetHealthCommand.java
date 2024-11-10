package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import io.github.b4n9z.deathPulse.Managers.HealthManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        }

        if (args.length != 3) {
            sender.sendMessage("Usage: /DeathPulse setHealth <player> <amount>");
            return false;
        }

        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            sender.sendMessage("Player not found.");
            return false;
        }

        double newHealth;
        try {
            newHealth = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid health amount.");
            return false;
        }

        if (newHealth > plugin.getConfigManager().getGainedMaxAmount()) {
            sender.sendMessage("Health amount exceeds the max limit (" + plugin.getConfigManager().getGainedMaxAmount() + ").");
            return true;
        } else if (newHealth < plugin.getConfigManager().getDecreaseMin()) {
            sender.sendMessage("Health amount under the min limit (" +  plugin.getConfigManager().getDecreaseMin() + ").");
            return true;
        }

        HealthManager.setMaxHealth(newHealth, targetPlayer);
        sender.sendMessage("Set " + targetPlayer.getName() + "'s health to " + newHealth);
        targetPlayer.sendMessage("Your health has been set to " + newHealth + " by an admin.");
        return true;
    }
}
