package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SetConfigCommand implements CommandExecutor {
    private final DeathPulse plugin;

    public SetConfigCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!plugin.getConfigManager().canUse(player, "setConfig")) {
                sender.sendMessage("§cYou do not have permission to use this command.");
                return false;
            }
        }  else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§fThis command§c can only be run§f by a player or from the console.");
            return false;
        }

        if (args.length < 3) {
            sender.sendMessage("§fUsage:§c /DeathPulse§b setConfig§f <path> <value>");
            return true;
        }

        String path = args[1];
        String value = String.join(" ", Arrays.copyOfRange(args, 2, args.length)).trim();

        // Check for list modification
        if (value.startsWith("add ") || value.startsWith("remove ")) {
            String[] parts = value.split(" ", 2);
            if (parts.length < 2) {
                sender.sendMessage("§cYou must provide a value to add/remove.");
                return true;
            }
            String action = parts[0];
            String item = parts[1];

            List<String> list = plugin.getConfig().getStringList(path);
            if (action.equalsIgnoreCase("add")) {
                if (!list.contains(item)) {
                    list.add(item);
                    plugin.getConfig().set(path, list);
                    plugin.saveConfig();
                    sender.sendMessage("§aAdded§f " + item + "§a to list at§f " + path);
                } else {
                    sender.sendMessage("§eItem already exists in the list.");
                }
            } else if (action.equalsIgnoreCase("remove")) {
                if (list.remove(item)) {
                    plugin.getConfig().set(path, list);
                    plugin.saveConfig();
                    sender.sendMessage("§aRemoved§f " + item + "§a from list at§f " + path);
                } else {
                    sender.sendMessage("§eItem not found in the list.");
                }
            }
            return true;
        }

        // Handle regular value
        Object parsedValue;
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            parsedValue = Boolean.parseBoolean(value);
        } else if (value.matches("-?\\d+")) {
            parsedValue = Integer.parseInt(value);
        } else if (value.startsWith("\"") && value.endsWith("\"")) {
            parsedValue = value.substring(1, value.length() - 1).trim() + " ";
        } else {
            sender.sendMessage("§cInvalid value format. Use quotes for strings.");
            return true;
        }

        if (!plugin.getConfig().contains(path)) {
            sender.sendMessage("§cPath not found in config: §f" + path);
            return true;
        }

        plugin.getConfig().set(path, parsedValue);
        plugin.saveConfig();
        sender.sendMessage("§aConfig updated: §f" + path + " = " + parsedValue);
        return true;
    }
}
