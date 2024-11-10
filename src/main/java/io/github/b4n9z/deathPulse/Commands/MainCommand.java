package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {
    private final ReloadPluginCommand reloadPluginCommand;
    private final SetHealthCommand setHealthCommand;
    private final ViewHealthCommand viewHealthCommand;

    public MainCommand(DeathPulse plugin) {
        this.reloadPluginCommand = new ReloadPluginCommand(plugin);
        this.setHealthCommand = new SetHealthCommand(plugin);
        this.viewHealthCommand = new ViewHealthCommand();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Please specify a subcommand.");
            return true;
        }

        String subCommand = args[0];
        return switch (subCommand) {
            case "reload" -> reloadPluginCommand.onCommand(sender, command, label, args);
            case "setHealth" -> setHealthCommand.onCommand(sender, command, label, args);
            case "viewHealth" -> viewHealthCommand.onCommand(sender, command, label, args);
            default -> {
                sender.sendMessage("Unknown subcommand.");
                yield true;
            }
        };
    }
}
