package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import io.github.b4n9z.deathPulse.Managers.HealthManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MainCommand implements CommandExecutor {
    private final DeathPulse plugin;
    private final ReloadPluginCommand reloadPluginCommand;
    private final SetMaxHealthCommand setMaxHealthCommand;
    private final ViewHealthCommand viewHealthCommand;
    private final ViewDeathData viewDeathData;
    private final ResetHealthCommand resetHealthCommand;
    private final MatchHealthCommand matchHealthCommand;
    private final RemoveDeathDataCommand removeDeathDataCommand;
    private final TransferHealthCommand transferHealthCommand;
    private final HelpCommand helpCommand;

    public MainCommand(DeathPulse plugin) {
        this.plugin = plugin;
        this.reloadPluginCommand = new ReloadPluginCommand(plugin);
        this.setMaxHealthCommand = new SetMaxHealthCommand(plugin);
        this.viewHealthCommand = new ViewHealthCommand(plugin);
        this.viewDeathData = new ViewDeathData(plugin);
        this.resetHealthCommand = new ResetHealthCommand(plugin);
        this.matchHealthCommand = new MatchHealthCommand(plugin);
        this.removeDeathDataCommand = new RemoveDeathDataCommand(plugin);
        this.transferHealthCommand = new TransferHealthCommand(plugin);
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
            case "setMaxHealth" -> setMaxHealthCommand.onCommand(sender, command, label, args);
            case "viewHealth" -> viewHealthCommand.onCommand(sender, command, label, args);
            case "viewDeathData" -> viewDeathData.onCommand(sender, command, label, args);
            case "resetHealth" -> resetHealthCommand.onCommand(sender, command, label, args);
            case "matchHealth" -> matchHealthCommand.onCommand(sender, command, label, args);
            case "removeDeathData" -> removeDeathDataCommand.onCommand(sender, command, label, args);
            case "transferHealth" -> transferHealthCommand.onCommand(sender, command, label, args);
            case "confirmRemoveDeathData" -> {
                if (sender instanceof Player player) {
                    if (!(player.isOp()) || !(player.hasPermission("dp.removeDeathData")) || !plugin.getConfigManager().isPermissionAllPlayerRemoveDeathData()) {
                        sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                        yield false;
                    }
                }
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
                if (sender instanceof Player player) {
                    if (!(player.isOp()) || !(player.hasPermission("dp.removeDeathData")) || !plugin.getConfigManager().isPermissionAllPlayerRemoveDeathData()) {
                        sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                        yield false;
                    }
                }
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
            case "confirmTransferHealth" -> {
                if (sender instanceof Player player) {
                    if (!(player.isOp()) || !(player.hasPermission("dp.transferHealth")) || !plugin.getConfigManager().isPermissionAllPlayerTransferHealth()) {
                        sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                        yield false;
                    }
                    if (args.length != 3) {
                        sender.sendMessage("§fUsage:§c /DeathPulse§b confirmTransferHealth§f <playerUUID> <amount>");
                        yield false;
                    }

                    transferHealthCommand.confirmedTransferHealth(player, args[1], Double.parseDouble(args[2]));

                    yield true;
                }
                yield false;
            }
            case "cancelTransferHealth" -> {
                sender.sendMessage("§bTransfer health§f has been§c cancelled.");
                yield true;
            }
            case "help" -> helpCommand.onCommand(sender, command, label, args);
            default -> {
                sender.sendMessage("§cUnknown subcommand.");
                yield true;
            }
        };
    }
}
