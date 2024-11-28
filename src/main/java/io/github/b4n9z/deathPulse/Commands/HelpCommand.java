package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {
    private final DeathPulse plugin;

    public HelpCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§b§l[DeathPulse]§r §cPlugin Commands:");
        sender.sendMessage("/§cDeathPulse§e reload§f - Reloads the plugin configuration.");
        sender.sendMessage("/§cDeathPulse§e setHealth§a <player> <amount>§f - Sets the health of a player.");
        sender.sendMessage("/§cDeathPulse§e viewHealth§a <player>§f - Views the health of a player.");
        sender.sendMessage("/§cDeathPulse§e resetHealth§a <player|allPlayer>§f - Resets the health of a player or all players to StartHealth.");
        sender.sendMessage("/§cDeathPulse§e matchHealth§a <player|allPlayer>§f - Matches the health of a player or all players based on their death data.");
        sender.sendMessage("/§cDeathPulse§e removeDeathData§a <player|allPlayer>§f - Removes death data of a player or all players.");
        sender.sendMessage("/§cDeathPulse§e setStartHealth§a <amount>§f - Sets the starting health for players in config.");
        sender.sendMessage("/§cDeathPulse§e setGainedPerDeath§a <amount>§f - Sets the health gained per death.");
        sender.sendMessage("/§cDeathPulse§e setGainedMax§a <true/false> <amount>§f - Sets the maximum health gain per death.");
        sender.sendMessage("/§cDeathPulse§e setDecrease§a <true/false> <perDeathAmount>§f - Activates health decrease.");
        sender.sendMessage("/§cDeathPulse§e setDecreaseMin§a <true/false> <minHealthAmount/banTime>§f - Sets the minimum health decrease or ban time. True for min, false for ban time.");
        sender.sendMessage("/§cDeathPulse§e help§f - Shows this help message.");

        // Display Death Ignored
        sender.sendMessage("§cDeath Ignored§f:");
        for (String deathIgnored : plugin.getConfigManager().getDeathIgnored()) {
            sender.sendMessage("- §e" + deathIgnored);
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