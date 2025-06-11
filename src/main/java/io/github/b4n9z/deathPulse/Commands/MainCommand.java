package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {
    private final DeathPulse plugin;
    private final ReloadPluginCommand reloadPluginCommand;
    private final SetConfigCommand setConfigCommand;
    private final SetMaxHealthCommand setMaxHealthCommand;
    private final ViewHealthCommand viewHealthCommand;
    private final ViewDeathDataCommand viewDeathData;
    private final ViewDebtDataCommand viewDebtData;
    private final ResetHealthCommand resetHealthCommand;
    private final MatchHealthCommand matchHealthCommand;
    private final RemoveDeathDataCommand removeDeathDataCommand;
    private final RemoveDebtDataCommand removeDebtDataCommand;
    private final TransferHealthCommand transferHealthCommand;
    private final WithdrawHealthCommand withdrawHealthCommand;
    private final HelpCommand helpCommand;

    public MainCommand(DeathPulse plugin) {
        this.plugin = plugin;
        this.reloadPluginCommand = new ReloadPluginCommand(this.plugin);
        this.setConfigCommand = new SetConfigCommand(this.plugin);
        this.setMaxHealthCommand = new SetMaxHealthCommand(this.plugin);
        this.viewHealthCommand = new ViewHealthCommand(this.plugin);
        this.viewDeathData = new ViewDeathDataCommand(this.plugin);
        this.viewDebtData = new ViewDebtDataCommand(this.plugin);
        this.resetHealthCommand = new ResetHealthCommand(this.plugin);
        this.matchHealthCommand = new MatchHealthCommand(this.plugin);
        this.removeDeathDataCommand = new RemoveDeathDataCommand(this.plugin);
        this.removeDebtDataCommand = new RemoveDebtDataCommand(this.plugin);
        this.transferHealthCommand = new TransferHealthCommand(this.plugin);
        this.withdrawHealthCommand = new WithdrawHealthCommand(this.plugin);
        this.helpCommand = new HelpCommand(this.plugin);
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
            case "setConfig" -> setConfigCommand.onCommand(sender, command, label, args);
            case "setMaxHealth" -> setMaxHealthCommand.onCommand(sender, command, label, args);
            case "viewHealth" -> viewHealthCommand.onCommand(sender, command, label, args);
            case "viewDeathData" -> viewDeathData.onCommand(sender, command, label, args);
            case "viewDebtData" -> viewDebtData.onCommand(sender, command, label, args);
            case "resetHealth" -> resetHealthCommand.onCommand(sender, command, label, args);
            case "confirmResetHealth" -> resetHealthCommand.confirmResetHealth(sender, args);
            case "cancelResetHealth" -> resetHealthCommand.cancelResetHealth(sender, args);
            case "matchHealth" -> matchHealthCommand.onCommand(sender, command, label, args);
            case "confirmMatchHealth" -> matchHealthCommand.confirmMatchHealth(sender, args);
            case "cancelMatchHealth" -> matchHealthCommand.cancelMatchHealth(sender, args);
            case "removeDeathData" -> removeDeathDataCommand.onCommand(sender, command, label, args);
            case "confirmRemoveDeathData" -> removeDeathDataCommand.confirmRemoveDeathData(sender, args);
            case "confirmRemoveAllDeathData" -> removeDeathDataCommand.confirmRemoveAllDeathData(sender, args);
            case "cancelRemoveDeathData" -> removeDeathDataCommand.cancelRemoveDeathData(sender, args);
            case "removeDebtData" -> removeDebtDataCommand.onCommand(sender, command, label, args);
            case "confirmRemoveDebtData" -> removeDebtDataCommand.confirmRemoveDebtData(sender, args);
            case "confirmRemoveAllDebtData" -> removeDebtDataCommand.confirmRemoveAllDebtData(sender, args);
            case "cancelRemoveDebtData" -> removeDebtDataCommand.cancelRemoveDebtData(sender, args);
            case "transferHealth" -> transferHealthCommand.onCommand(sender, command, label, args);
            case "confirmTransferHealth" -> transferHealthCommand.confirmedTransferHealth(sender, args);
            case "cancelTransferHealth" -> transferHealthCommand.cancelTransferHealth(sender, args);
            case "withdrawHealth" -> withdrawHealthCommand.onCommand(sender, command, label, args);
            case "help" -> helpCommand.onCommand(sender, command, label, args);
            default -> {
                sender.sendMessage("§cUnknown subcommand.");
                yield true;
            }
        };
    }
}
