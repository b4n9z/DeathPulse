package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class HelpCommand implements CommandExecutor {
    private final DeathPulse plugin;

    public HelpCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!(player.isOp()) || !(player.hasPermission("dp.help")) || !plugin.getConfigManager().isPermissionAllPlayerHelp()) {
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§fThis command§c can only be run§f by a player or from the console.");
            return false;
        }

        sender.sendMessage("§b§l[DeathPulse]§r §cPlugin Commands:");
        sender.sendMessage("/§cDeathPulse§e reload§f - Reloads the plugin configuration.");
        sender.sendMessage("/§cDeathPulse§e setMaxHealth§a <player> <amount>§f - Sets the health of a player.");
        sender.sendMessage("/§cDeathPulse§e viewHealth§a <player>§f - Views the health of a player.");
        sender.sendMessage("/§cDeathPulse§e resetHealth§a <player|allPlayer>§f - Resets the health of a player or all players to StartHealth.");
        sender.sendMessage("/§cDeathPulse§e matchHealth§a <player|allPlayer>§f - Matches the health of a player or all players based on their death data.");
        sender.sendMessage("/§cDeathPulse§e removeDeathData§a <player|allPlayer>§f - Removes death data of a player or all players.");
        sender.sendMessage("/§cDeathPulse§e help§f - Shows this help message.");

        // Display All Available Death Causes
        DamageCause[] allDamageCauses = DamageCause.values();
        sender.sendMessage("§cAvailable All Death Causes in Minecraft:");
        for (DamageCause cause : allDamageCauses) {
            sender.sendMessage("- §e" + cause.name());
        }

        // Display Death Ignored
        sender.sendMessage("§cDeath Ignored§f:");
        if (plugin.getConfigManager().isIgnoredEnabled()) {
            for (String deathIgnored : plugin.getConfigManager().getIgnoredCause()) {
                sender.sendMessage("- §e" + deathIgnored);
            }
        } else {
            sender.sendMessage("§cDisabled");
        }

        // Display Day Ignored
        sender.sendMessage("§cDay Ignored§f:");
        if (plugin.getConfigManager().isIgnoredEnabled() && plugin.getConfigManager().isIgnoredDayEnabled()) {
            for (Integer day : plugin.getConfigManager().getIgnoredDays()) {
                sender.sendMessage("- §eDay " + day);
            }
        } else {
            sender.sendMessage("§cDisabled");
        }

        // Display Death Increase
        sender.sendMessage("§cDeath Increase§f:");
        if (plugin.getConfigManager().isIncreaseEnabled()) {
            for (String deathIncrease : plugin.getConfigManager().getIncreaseCause()) {
                sender.sendMessage("- §e" + deathIncrease);
            }
        } else {
            sender.sendMessage("§cDisabled");
        }

        // Display Day Increase
        sender.sendMessage("§cDay Increase§f:");
        if (plugin.getConfigManager().isIncreaseEnabled() && plugin.getConfigManager().isIncreaseDayEnabled()) {
            for (Integer day : plugin.getConfigManager().getIncreaseDays()) {
                sender.sendMessage("- §eDay " + day + ": " + plugin.getConfigManager().getIncreaseDayAmount() + " HP");
            }
        } else {
            sender.sendMessage("§cDisabled");
        }

        // Display Death Decrease
        sender.sendMessage("§cDeath Decrease§f:");
        if (plugin.getConfigManager().isDecreaseEnabled()) {
            for (String deathDecrease : plugin.getConfigManager().getDecreaseCause()) {
                sender.sendMessage("- §e" + deathDecrease);
            }
        } else {
            sender.sendMessage("§cDisabled");
        }

        // Display Day Decrease
        sender.sendMessage("§cDay Decrease§f:");
        if (plugin.getConfigManager().isDecreaseEnabled() && plugin.getConfigManager().isDecreaseDayEnabled()) {
            for (Integer day : plugin.getConfigManager().getDecreaseDays()) {
                sender.sendMessage("- §eDay " + day + ": " + plugin.getConfigManager().getDecreaseDayAmount() + " HP");
            }
        } else {
            sender.sendMessage("§cDisabled");
        }

        return true;
    }
}