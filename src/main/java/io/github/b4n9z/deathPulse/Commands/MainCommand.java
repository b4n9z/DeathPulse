package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class MainCommand implements CommandExecutor {
    private final Map<String, CommandExecutor> subCommands = new HashMap<>();

    public MainCommand(DeathPulse plugin) {
        subCommands.put("reload", new ReloadPluginCommand(plugin));
        subCommands.put("setHealth", new SetHealthCommand(plugin));
        subCommands.put("viewHealth", new ViewHealthCommand(plugin));
        subCommands.put("setStartHealth", new SetStartHealthCommand(plugin));
        subCommands.put("setGainedPerDeath", new SetGainedPerDeathCommand(plugin));
        subCommands.put("setGainedMax", new SetGainedMaxCommand(plugin));
        subCommands.put("setDecrease", new SetDecreaseCommand(plugin));
        subCommands.put("help", new HelpCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Please specify a subcommand.");
            return false;
        }

        CommandExecutor executor = subCommands.get(args[0].toLowerCase());
        if (executor == null) {
            sender.sendMessage("Unknown subcommand.");
            return false;
        }

        return executor.onCommand(sender, command, label, args);
    }
}
