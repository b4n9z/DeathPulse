package io.github.b4n9z.deathPulse.Commands;

import org.bukkit.Bukkit;
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

        if (args.length == 1) {
            if (sender.hasPermission("dp.reload")) {
                completions.add("reload");
            }
            if (sender.hasPermission("dp.setHealth")) {
                completions.add("setHealth");
            }
            if (sender.hasPermission("dp.viewHealth")) {
                completions.add("viewHealth");
            }
            if (sender.hasPermission("dp.setStartHealth")) {
                completions.add("setStartHealth");
            }
            if (sender.hasPermission("dp.setGainedPerDeath")) {
                completions.add("setGainedPerDeath");
            }
            if (sender.hasPermission("dp.setGainedMax")) {
                completions.add("setGainedMax");
            }
            if (sender.hasPermission("dp.setDecrease")) {
                completions.add("setDecrease");
            }
            if (sender.hasPermission("dp.help")) {
                completions.add("help");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setHealth") || args[0].equalsIgnoreCase("viewHealth")) {
                // Autocomplete player names
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
            if (args[0].equalsIgnoreCase("setGainedMax")) {
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
