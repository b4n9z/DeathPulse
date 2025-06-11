package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import io.github.b4n9z.deathPulse.Managers.HealthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ViewDebtDataCommand implements CommandExecutor {

    private final DeathPulse plugin;

    public ViewDebtDataCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("§fUsage:§c /DeathPulse§b viewDebtData");
            return true;
        }
        if (sender instanceof Player player){
            if (player.isOp() || player.hasPermission("dp.viewDebtData") || plugin.getConfigManager().isPermissionAllPlayerViewDebtData()) {
                double currentHealth = HealthManager.getHealth(player);
                double maxPlayerHealth = HealthManager.getMaxHealth(player);

                sender.sendMessage("§bYou§f currently has§d " + currentHealth + "§f health out of§d " + maxPlayerHealth + "§f max health.");

                UUID targetUUID = player.getUniqueId();
                int debt = plugin.getDebtDataManager().getDebt(targetUUID);
                sender.sendMessage("§bYou§f currently has§d " + debt + "§f debt.");
            } else {
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
            }
        }
        return true;
    }
}
