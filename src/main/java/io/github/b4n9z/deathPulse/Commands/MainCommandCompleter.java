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
            if (sender.hasPermission("dp.reload") || plugin.getConfigManager().isPermissionAllPlayerReload()) {
                commands.add("reload");
            }
            if (sender.hasPermission("dp.setMaxHealth") || plugin.getConfigManager().isPermissionAllPlayerSetMaxHealth()) {
                commands.add("setMaxHealth");
            }
            if (sender.hasPermission("dp.viewHealth") || plugin.getConfigManager().isPermissionAllPlayerViewHealth()) {
                commands.add("viewHealth");
            }
            commands.add("viewDeathData");
            if (sender.hasPermission("dp.resetHealth") || plugin.getConfigManager().isPermissionAllPlayerResetHealth()) {
                commands.add("resetHealth");
            }
            if (sender.hasPermission("dp.matchHealth") || plugin.getConfigManager().isPermissionAllPlayerMatchHealth()) {
                commands.add("matchHealth");
            }
            if (sender.hasPermission("dp.removeDeathData") || plugin.getConfigManager().isPermissionAllPlayerRemoveDeathData()) {
                commands.add("removeDeathData");
            }
            if (sender.hasPermission("dp.transferHealth") || plugin.getConfigManager().isPermissionAllPlayerTransferHealth()) {
                commands.add("transferHealth");
            }
            if (sender.hasPermission("dp.help") || plugin.getConfigManager().isPermissionAllPlayerHelp()) {
                commands.add("help");
            }
            for (String commandOption : commands) {
                if (commandOption.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(commandOption);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setMaxHealth") || args[0].equalsIgnoreCase("viewHealth") || args[0].equalsIgnoreCase("resetHealth") || args[0].equalsIgnoreCase("matchHealth") || args[0].equalsIgnoreCase("removeDeathData") || args[0].equalsIgnoreCase("transferHealth")) {
                // Autocomplete player names
                if (args[0].equalsIgnoreCase("resetHealth") || args[0].equalsIgnoreCase("matchHealth") || args[0].equalsIgnoreCase("removeDeathData")) {
                    completions.add("allPlayer");
                    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                        completions.add(offlinePlayer.getUniqueId().toString());
                    }
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setMaxHealth") || args[0].equalsIgnoreCase("transferHealth")) {
                completions.add("[<amount>]");
            }
        }
        return completions;
    }
}
