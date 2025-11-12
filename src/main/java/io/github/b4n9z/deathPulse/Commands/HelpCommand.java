package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.World;
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
            if (!plugin.getConfigManager().canUse(player, "help")) {
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§fThis command§c can only be run§f by a player or from the console.");
            return false;
        }

        sender.sendMessage("§b§l[DeathPulse]§r §cPlugin Commands:");
        sender.sendMessage("/§cDeathPulse§e reload§f - Reloads the plugin configuration without restarting the server.");
        sender.sendMessage("/§cDeathPulse§e setConfig§a <path> <value>§f - Sets/Change a configuration value for the plugin just using command.");
        sender.sendMessage("/§cDeathPulse§e setMaxHealth§a <player> <amount>§f - Sets the maximum health of a specified player.");
        sender.sendMessage("/§cDeathPulse§e viewHealth§a <player>§f - Views the health of a specified player their debt data and death data.");
        sender.sendMessage("/§cDeathPulse§e viewDeathData§f - Views your death data.");
        sender.sendMessage("/§cDeathPulse§e viewDebtData§f - Views your debt data.");
        sender.sendMessage("/§cDeathPulse§e resetHealth§a <player|allPlayer>§f - Resets the health of a specified player or all players to the starting health.");
        sender.sendMessage("/§cDeathPulse§e matchHealth§a <player|allPlayer>§f - Matches the health of a specified player or all players based on their death data.");
        sender.sendMessage("/§cDeathPulse§e removeDeathData§a <player|allPlayer>§f - Removes the death data of a specified player or all players.");
        sender.sendMessage("/§cDeathPulse§e removeDebtData§a <player|allPlayer>§f - Removes the debt data of a specified player or all players.");
        sender.sendMessage("/§cDeathPulse§e transferHealth§a <player> <amount>§f - Transfers health to another player.");
        sender.sendMessage("/§cDeathPulse§e withdrawHealth§a <amount>§f - Withdraws health from a your health to make HealthItem.");
        sender.sendMessage("/§cDeathPulse§e help§f - Shows this help message.");

        // Display available worlds
        sender.sendMessage("§cAvailable Worlds that allow Death Day§f:");
        for (World world : plugin.getConfigManager().getConfiguredWorlds()) {
            sender.sendMessage("- §e" + world);
        }
        sender.sendMessage("");

        // Display Priority Death Settings
        sender.sendMessage("§cPriority to Check for Death§f:");
        for (String priority : plugin.getConfigManager().getPriority()) {
            sender.sendMessage("- §e" + priority);
        }
        sender.sendMessage("");

        // Display HP Settings
        sender.sendMessage("§cHealth Settings§f:");
        sender.sendMessage("- §eStart Health: " + plugin.getConfigManager().getHPStart());
        sender.sendMessage("- §eMax Health: " + (plugin.getConfigManager().isMaxHPEnabled() ? plugin.getConfigManager().getMaxHPAmount() : "§cDisabled §f(Players Have No Health Limits)"));
        sender.sendMessage("- §eMin Health: " + (plugin.getConfigManager().isMinHPEnabled() ? plugin.getConfigManager().getMinHPAmount() : "§cDisabled §f(Zero Health Can Make You Got Banned)"));
        sender.sendMessage("- §eHealth Items: " + plugin.getConfigManager().getHealthItemHealthPerItem() + " Health per Item");
        sender.sendMessage("");

        // Display All Available Death Causes
        DamageCause[] allDamageCauses = DamageCause.values();
        sender.sendMessage("§cAvailable All Death Causes:");
        for (DamageCause cause : allDamageCauses) {
            sender.sendMessage("- §e" + cause.name());
        }
        sender.sendMessage("- §ePLAYER_ATTACK");
        sender.sendMessage("- §ePLAYER_SWEEP_ATTACK");
        sender.sendMessage("- §ePLAYER_PROJECTILE");
        sender.sendMessage("- §ePLAYER_EXPLOSION");
        sender.sendMessage("");

        // Display Death Ignored
        sender.sendMessage("§cDeath Cause Ignored§f:");
        if (plugin.getConfigManager().isIgnoredEnabled()) {
            for (String deathIgnored : plugin.getConfigManager().getIgnoredCause()) {
                sender.sendMessage("- §e" + deathIgnored);
            }
        } else {
            sender.sendMessage("§cDisabled");
        }

        // Display Day Ignored
        sender.sendMessage("§cDay Ignored§f:");
        if (plugin.getConfigManager().isIgnoredDayEnabled()) {
            for (Integer day : plugin.getConfigManager().getIgnoredDays()) {
                sender.sendMessage("- §eDay " + day);
            }
            sender.sendMessage("§cIgnored Day Death Causes:");
            for (String dayIgnored : plugin.getConfigManager().getIgnoredDayCause()) {
                sender.sendMessage("- §e" + dayIgnored);
            }
        } else {
            sender.sendMessage("§cDisabled");
        }

        // Display Death Increase
        sender.sendMessage("§cDeath Cause Increase§f:");
        if (plugin.getConfigManager().isIncreaseEnabled()) {
            for (String deathIncrease : plugin.getConfigManager().getIncreaseCauseName()) {
                sender.sendMessage("- §e" + deathIncrease + ": " + plugin.getConfigManager().getIncreaseCauseAmount(deathIncrease) + " Health");
            }
        } else {
            sender.sendMessage("§cDisabled");
        }

        // Display Day Increase
        sender.sendMessage("§cDay Increase§f:");
        if (plugin.getConfigManager().isIncreaseDayEnabled()) {
            for (Integer day : plugin.getConfigManager().getIncreaseDays()) {
                sender.sendMessage("- §eDay " + day);
            }
            sender.sendMessage("§cIncrease Day Death Causes:");
            for (String dayIncrease : plugin.getConfigManager().getIncreaseDayCauseName()) {
                sender.sendMessage("- §e" + dayIncrease + ": " + plugin.getConfigManager().getIncreaseDayCauseAmount(dayIncrease) + " Health");
            }
        } else {
            sender.sendMessage("§cDisabled");
        }

        // Display Death Decrease
        sender.sendMessage("§cDeath Cause Decrease§f:");
        if (plugin.getConfigManager().isDecreaseEnabled()) {
            for (String deathDecrease : plugin.getConfigManager().getDecreaseCauseName()) {
                sender.sendMessage("- §e" + deathDecrease + ": " + plugin.getConfigManager().getDecreaseCauseAmount(deathDecrease) + " Health");
            }
        } else {
            sender.sendMessage("§cDisabled");
        }

        // Display Day Decrease
        sender.sendMessage("§cDay Decrease§f:");
        if (plugin.getConfigManager().isDecreaseDayEnabled()) {
            for (Integer day : plugin.getConfigManager().getDecreaseDays()) {
                sender.sendMessage("- §eDay " + day);
            }
            sender.sendMessage("§cDecrease Day Death Causes:");
            for (String dayDecrease : plugin.getConfigManager().getDecreaseDayCauseName()) {
                sender.sendMessage("- §e" + dayDecrease + ": " + plugin.getConfigManager().getDecreaseDayCauseAmount(dayDecrease) + " Health");
            }
        } else {
            sender.sendMessage("§cDisabled");
        }

        return true;
    }
}