package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class MainCommand implements CommandExecutor {
    private final DeathPulse plugin;
    private final ReloadPluginCommand reloadPluginCommand;
    private final SetHealthCommand setHealthCommand;
    private final ViewHealthCommand viewHealthCommand;
    private final ResetHealthCommand resetHealthCommand;
    private final MatchHealthCommand matchHealthCommand;
    private final RemoveDeathDataCommand removeDeathDataCommand;
    private final SetStartHealthCommand setStartHealthCommand;
    private final SetGainedPerDeathCommand setGainedPerDeathCommand;
    private final SetGainedMaxCommand setGainedMaxCommand;
    private final SetDecreaseCommand setDecreaseCommand;
    private final SetDecreaseMinCommand setDecreaseMinCommand;
    private final HelpCommand helpCommand;

    public MainCommand(DeathPulse plugin) {
        this.plugin = plugin;
        this.reloadPluginCommand = new ReloadPluginCommand(plugin);
        this.setHealthCommand = new SetHealthCommand(plugin);
        this.viewHealthCommand = new ViewHealthCommand(plugin);
        this.resetHealthCommand = new ResetHealthCommand(plugin);
        this.matchHealthCommand = new MatchHealthCommand(plugin);
        this.removeDeathDataCommand = new RemoveDeathDataCommand(plugin);
        this.setStartHealthCommand = new SetStartHealthCommand(plugin);
        this.setGainedPerDeathCommand = new SetGainedPerDeathCommand(plugin);
        this.setGainedMaxCommand = new SetGainedMaxCommand(plugin);
        this.setDecreaseCommand = new SetDecreaseCommand(plugin);
        this.setDecreaseMinCommand = new SetDecreaseMinCommand(plugin);
        this.helpCommand = new HelpCommand(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cPlease specify a subcommand.");
            return false;
        }

        String subCommand = args[0];
        return switch (subCommand) {
            case "reload" -> reloadPluginCommand.onCommand(sender, command, label, args);
            case "setHealth" -> setHealthCommand.onCommand(sender, command, label, args);
            case "viewHealth" -> viewHealthCommand.onCommand(sender, command, label, args);
            case "resetHealth" -> resetHealthCommand.onCommand(sender, command, label, args);
            case "matchHealth" -> matchHealthCommand.onCommand(sender, command, label, args);
            case "removeDeathData" -> removeDeathDataCommand.onCommand(sender, command, label, args);
            case "confirmRemoveDeathData" -> {
                if (args.length != 2) {
                    sender.sendMessage("§fUsage:§c /DeathPulse§b confirmRemoveDeathData§f <playerUUID>");
                    yield false;
                }
                UUID playerUUID = UUID.fromString(args[1]);
                boolean success = plugin.getDeathDataManager().removePlayerDeathData(playerUUID);
                if (success) {
                    sender.sendMessage("§bDeath data§f for player§b " + Bukkit.getOfflinePlayer(playerUUID).getName() + "§f has been§c removed§f.");
                } else {
                    sender.sendMessage("§cFailed to remove§b death data§f for player§b " + Bukkit.getOfflinePlayer(playerUUID).getName() + "§f. §cPlease try again.");
                }
                yield true;
            }
            case "confirmRemoveAllDeathData" -> {
                boolean success = plugin.getDeathDataManager().removeAllDeathData();
                if (success) {
                    sender.sendMessage("§bDeath data§f for§b all players§f has been§c removed§f.");
                } else {
                    sender.sendMessage("§cFailed to remove§b death data§f for§b all players§f.§c Please try again.");
                }
                yield true;
            }
            case "cancelRemoveDeathData" -> {
                sender.sendMessage("§bDeath data§f removal has been§c cancelled.");
                yield true;
            }
            case "setStartHealth" -> setStartHealthCommand.onCommand(sender, command, label, args);
            case "setGainedPerDeath" -> setGainedPerDeathCommand.onCommand(sender, command, label, args);
            case "setGainedMax" -> setGainedMaxCommand.onCommand(sender, command, label, args);
            case "setDecrease" -> setDecreaseCommand.onCommand(sender, command, label, args);
            case "setDecreaseMin" -> setDecreaseMinCommand.onCommand(sender, command, label, args);
            case "help" -> helpCommand.onCommand(sender, command, label, args);
            default -> {
                sender.sendMessage("§cUnknown subcommand.");
                yield true;
            }
        };
    }
}
