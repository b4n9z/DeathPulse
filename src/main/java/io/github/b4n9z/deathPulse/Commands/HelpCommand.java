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
        sender.sendMessage("/§cDeathPulse§e setStartHealth <amount>§f - Sets the starting health for players.");
        sender.sendMessage("/§cDeathPulse§e setGainedPerDeath <amount>§f - Sets the health gained per death.");
        sender.sendMessage("/§cDeathPulse§e setGainedMax <true/false> <amount>§f - Sets the maximum health gain per death.");
        sender.sendMessage("/§cDeathPulse§e setDecrease <true/false> <perDeathAmount> <minHealthAmount>§f - Activates health decrease.");
        sender.sendMessage("/§cDeathPulse§e help§f - Shows this help message.");
        return true;
    }
}