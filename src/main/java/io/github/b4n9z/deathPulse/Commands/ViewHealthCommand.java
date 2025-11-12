package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import io.github.b4n9z.deathPulse.Managers.HealthManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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
            if (!plugin.getConfigManager().canUse(player, "viewHealth")) {
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§fThis command§c can only be run§f by a player or from the console.");
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage("§fUsage:§c /DeathPulse§b viewHealth§f <player>");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            sender.sendMessage("§fPlayer§b " + args[1] +"§c not found§f or§c not online§f.");
            return true;
        }

        double currentHealth = HealthManager.getHealth(targetPlayer);
        double maxPlayerHealth = HealthManager.getMaxHealth(targetPlayer);

        sender.sendMessage("§b" + targetPlayer.getName() + "§f currently has§d " + currentHealth + "§f health out of§d " + maxPlayerHealth + "§f their max health.");

        UUID targetUUID = targetPlayer.getUniqueId();
        Set<String> deathData = plugin.getDeathDataManager().loadPlayerDeaths(targetUUID);
        int debt = plugin.getDebtDataManager().getDebt(targetUUID);

        sender.sendMessage("§b" + targetPlayer.getName() + "§f has§d " + debt + "§f debt.");

        if (deathData.isEmpty()) {
            sender.sendMessage("§b" + targetPlayer.getName() + "§c has no recorded§f death data.");
        } else {
            sender.sendMessage("§b" + targetPlayer.getName() + "'s§f death data:§e");
            for (String deathCause : deathData) {
                sender.sendMessage("- " + deathCause);
            }
        }

        return true;
    }
}
