package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MainCommandCompleter implements TabCompleter {
    private final DeathPulse plugin;

    public MainCommandCompleter(DeathPulse plugin) {
        this.plugin = plugin;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            if (plugin.getConfigManager().canUse(sender, "reload")) {
                commands.add("reload");
            }
            if (plugin.getConfigManager().canUse(sender, "setConfig")) {
                commands.add("setConfig");
            }
            if (plugin.getConfigManager().canUse(sender, "setMaxHealth")) {
                commands.add("setMaxHealth");
            }
            if (plugin.getConfigManager().canUse(sender, "viewHealth")) {
                commands.add("viewHealth");
            }
            if (plugin.getConfigManager().canUse(sender, "viewDeathData")) {
                commands.add("viewDeathData");
            }
            if (plugin.getConfigManager().canUse(sender, "viewDebtData")) {
                commands.add("viewDebtData");
            }
            if (plugin.getConfigManager().canUse(sender, "resetHealth")) {
                commands.add("resetHealth");
            }
            if (plugin.getConfigManager().canUse(sender, "matchHealth")) {
                commands.add("matchHealth");
            }
            if (plugin.getConfigManager().canUse(sender, "removeDeathData")) {
                commands.add("removeDeathData");
            }
            if (plugin.getConfigManager().canUse(sender, "removeDebtData")) {
                commands.add("removeDebtData");
            }
            if (plugin.getConfigManager().canUse(sender, "transferHealth")) {
                commands.add("transferHealth");
            }
            if (plugin.getConfigManager().canUse(sender, "withdrawHealth")) {
                commands.add("withdrawHealth");
            }
            if (plugin.getConfigManager().canUse(sender, "help")) {
                commands.add("help");
            }
            for (String commandOption : commands) {
                if (commandOption.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(commandOption);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setMaxHealth") || args[0].equalsIgnoreCase("viewHealth") || args[0].equalsIgnoreCase("resetHealth") || args[0].equalsIgnoreCase("matchHealth") || args[0].equalsIgnoreCase("removeDeathData") || args[0].equalsIgnoreCase("removeDebtData") || args[0].equalsIgnoreCase("transferHealth")) {
                // Autocomplete player names
                if (args[0].equalsIgnoreCase("resetHealth") || args[0].equalsIgnoreCase("matchHealth") || args[0].equalsIgnoreCase("removeDeathData") || args[0].equalsIgnoreCase("removeDebtData")) {
                    completions.add("allPlayer");
                    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                        if (offlinePlayer.isOnline()) {
                            continue;
                        }
                        completions.add(offlinePlayer.getUniqueId().toString());
                    }
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
            if (args[0].equalsIgnoreCase("withdrawHealth")) {
                completions.add("[<amount>]");
            }
            if (args[0].equalsIgnoreCase("setConfig")) {
                completions.addAll(plugin.getConfigManager().getAllConfigPaths());
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setMaxHealth") || args[0].equalsIgnoreCase("transferHealth")) {
                completions.add("[<amount>]");
            }
            if (args[0].equalsIgnoreCase("setConfig")) {
                String path = args[1];
                Object val = plugin.getConfig().get(path);
                if (val instanceof Boolean) {
                    completions.addAll(List.of("true", "false"));
                } else if (val instanceof List) {
                    completions.addAll(List.of("add", "remove"));
                } else if (val instanceof String) {
                    completions.add("\"your string here\"");
                } else if (val instanceof Integer) {
                    completions.add("[<number>]");
                }
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("setConfig")) {
                if (plugin.getConfig().get(args[1]) instanceof List) {
                    completions.add("<value>");
                }
            }
        }
        return completions;
    }
}
