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
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setHealth")) {
            // Autocomplete player names
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("viewHealth")) {
            // Autocomplete player names
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }

        return completions;
    }
}
