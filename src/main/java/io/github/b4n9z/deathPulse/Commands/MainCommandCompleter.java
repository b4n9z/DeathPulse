package io.github.b4n9z.deathPulse.Commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MainCommandCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("dp.reload")) {
                commands.add("reload");
            }
            if (sender.hasPermission("dp.setHealth")) {
                commands.add("setHealth");
            }
            if (sender.hasPermission("dp.viewHealth")) {
                commands.add("viewHealth");
            }
            if (sender.hasPermission("dp.resetHealth")) {
                commands.add("resetHealth");
            }
            if (sender.hasPermission("dp.matchHealth")) {
                commands.add("matchHealth");
            }
            if (sender.hasPermission("dp.setStartHealth")) {
                commands.add("setStartHealth");
            }
            if (sender.hasPermission("dp.setGainedPerDeath")) {
                commands.add("setGainedPerDeath");
            }
            if (sender.hasPermission("dp.setGainedMax")) {
                commands.add("setGainedMax");
            }
            if (sender.hasPermission("dp.setDecrease")) {
                commands.add("setDecrease");
            }
            if (sender.hasPermission("dp.help")) {
                commands.add("help");
            }
            for (String commandOption : commands) {
                if (commandOption.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(commandOption);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setHealth") || args[0].equalsIgnoreCase("viewHealth") || args[0].equalsIgnoreCase("resetHealth") || args[0].equalsIgnoreCase("matchHealth") || args[0].equalsIgnoreCase("removeDeathData")) {
                // Autocomplete player names
                if (args[0].equalsIgnoreCase("resetHealth") || args[0].equalsIgnoreCase("matchHealth") || args[0].equalsIgnoreCase("removeDeathData")) {
                    completions.add("allPlayer");
                    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                        completions.add(offlinePlayer.getName());
                    }
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            } else if (args[0].equalsIgnoreCase("setStartHealth") || args[0].equalsIgnoreCase("setGainedPerDeath")) {
                // Autocomplete numbers
                completions.add("<amount>");
            } else if (args[0].equalsIgnoreCase("setGainedMax") || args[0].equalsIgnoreCase("setDecrease")) {
                // Autocomplete numbers
                completions.add("true");
                completions.add("false");
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setGainedMax") || args[0].equalsIgnoreCase("setHealth")) {
                // Autocomplete numbers
                completions.add("<amount>");
            } else if (args[0].equalsIgnoreCase("setDecrease")) {
                // Autocomplete numbers
                completions.add("<perDeathAmount>");
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("setDecrease")) {
                // Autocomplete numbers
                completions.add("<minHealthAmount>");
            }
        }
        return completions;
    }
}
