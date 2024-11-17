package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {
    private final ReloadPluginCommand reloadPluginCommand;
    private final SetHealthCommand setHealthCommand;
    private final ViewHealthCommand viewHealthCommand;
    private final SetStartHealthCommand setStartHealthCommand;
    private final SetGainedPerDeathCommand setGainedPerDeathCommand;
    private final SetGainedMaxCommand setGainedMaxCommand;
    private final SetDecreaseCommand setDecreaseCommand;
    private final HelpCommand helpCommand;

    public MainCommand(DeathPulse plugin) {
        this.reloadPluginCommand = new ReloadPluginCommand(plugin);
        this.setHealthCommand = new SetHealthCommand(plugin);
        this.viewHealthCommand = new ViewHealthCommand(plugin);
        this.setStartHealthCommand = new SetStartHealthCommand(plugin);
        this.setGainedPerDeathCommand = new SetGainedPerDeathCommand(plugin);
        this.setGainedMaxCommand = new SetGainedMaxCommand(plugin);
        this.setDecreaseCommand = new SetDecreaseCommand(plugin);
        this.helpCommand = new HelpCommand();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Please specify a subcommand.");
            return false;
        }

        String subCommand = args[0];
        return switch (subCommand) {
            case "reload" -> reloadPluginCommand.onCommand(sender, command, label, args);
            case "setHealth" -> setHealthCommand.onCommand(sender, command, label, args);
            case "viewHealth" -> viewHealthCommand.onCommand(sender, command, label, args);
            case "setStartHealth" -> setStartHealthCommand.onCommand(sender, command, label, args);
            case "setGainedPerDeath" -> setGainedPerDeathCommand.onCommand(sender, command, label, args);
            case "setGainedMax" -> setGainedMaxCommand.onCommand(sender, command, label, args);
            case "setDecrease" -> setDecreaseCommand.onCommand(sender, command, label, args);
            case "help" -> helpCommand.onCommand(sender, command, label, args);
            default -> {
                sender.sendMessage("Unknown subcommand.");
                yield true;
            }
        };
    }
}
