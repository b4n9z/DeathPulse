package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import io.github.b4n9z.deathPulse.Managers.HealthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class ViewDeathDataCommand implements CommandExecutor {

    private final DeathPulse plugin;

    public ViewDeathDataCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("§fUsage:§c /DeathPulse§b viewDeathData");
            return true;
        }
        if (sender instanceof Player player){
            if (player.isOp() || player.hasPermission("dp.viewDeathData") || plugin.getConfigManager().isPermissionAllPlayerViewDeathData()) {
                double currentHealth = HealthManager.getHealth(player);
                double maxPlayerHealth = HealthManager.getMaxHealth(player);

                sender.sendMessage("§b" + player.getName() + "§f currently has§d " + currentHealth + "§f health out of§d " + maxPlayerHealth + "§f max health.");

                UUID targetUUID = player.getUniqueId();
                Set<String> deathData = plugin.getDeathDataManager().loadPlayerDeaths(targetUUID);

                if (deathData.isEmpty()) {
                    sender.sendMessage("§b" + player.getName() + "§c you have no recorded§f death data.");
                } else {
                    sender.sendMessage("§b" + player.getName() + "'s§f death data:§e");
                    for (String deathCause : deathData) {
                        sender.sendMessage("- " + deathCause);
                    }
                }
            } else {
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
            }
        }
        return true;
    }
}
