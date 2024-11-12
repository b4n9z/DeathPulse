package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import io.github.b4n9z.deathPulse.Managers.HealthManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class ViewHealthCommand implements CommandExecutor {

    private final DeathPulse plugin;

    public ViewHealthCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if (!(player.isOp()) || !(player.hasPermission("dp.viewHealth"))) {
                sender.sendMessage("You do not have permission to use this command.");
                return false;
            }
        }

        if (args.length != 1) {
            sender.sendMessage("Usage: /DeathPulse viewHealth <player>");
            return false;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage("Player not found or not online.");
            return false;
        }

        double currentHealth = HealthManager.getHealth(targetPlayer);
        double maxPlayerHealth = HealthManager.getMaxHealth(targetPlayer);

        sender.sendMessage(targetPlayer.getName() + " currently has " + currentHealth + " health out of " + maxPlayerHealth + " their max health.");

        UUID targetUUID = targetPlayer.getUniqueId();
        Set<String> deathData = plugin.getDeathDataManager().loadPlayerDeaths(targetUUID);

        if (deathData.isEmpty()) {
            sender.sendMessage(targetPlayer.getName() + " has no recorded death data.");
        } else {
            sender.sendMessage(targetPlayer.getName() + "'s death data:");
            for (String deathCause : deathData) {
                sender.sendMessage("- " + deathCause);
            }
        }

        return true;
    }
}
