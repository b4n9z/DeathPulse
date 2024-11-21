package io.github.b4n9z.deathPulse.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§cDeathPulse§f Plugin Commands:");
        sender.sendMessage("/§cDeathPulse§e reload§f - Reloads the plugin configuration.");
        sender.sendMessage("/§cDeathPulse§e setHealth <player> <amount>§f - Sets the health of a player.");
        sender.sendMessage("/§cDeathPulse§e viewHealth <player>§f - Views the health of a player.");
        sender.sendMessage("/§cDeathPulse§e resetHealth <player|allPlayer>§f - Resets the health of a player or all players to StartHealth.");
        sender.sendMessage("/§cDeathPulse§e matchHealth <player|allPlayer>§f - Matches the health of a player or all players based on their death data.");
        sender.sendMessage("/§cDeathPulse§e removeDeathData <player|allPlayer>§f - Removes death data of a player or all players.");
        sender.sendMessage("/§cDeathPulse§e setStartHealth <amount>§f - Sets the starting health for players in config.");
        sender.sendMessage("/§cDeathPulse§e setGainedPerDeath <amount>§f - Sets the health gained per death.");
        sender.sendMessage("/§cDeathPulse§e setGainedMax <true/false> <amount>§f - Sets the maximum health gain per death.");
        sender.sendMessage("/§cDeathPulse§e setDecrease <true/false> <perDeathAmount>§f - Activates health decrease.");
        sender.sendMessage("/§cDeathPulse§e setDecreaseMin <true/false> <minHealthAmount/banTime>§f - Sets the minimum health decrease or ban time. True for min, false for ban time.");
        sender.sendMessage("/§cDeathPulse§e help§f - Shows this help message.");
        return true;
    }
}