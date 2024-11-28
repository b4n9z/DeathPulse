package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import io.github.b4n9z.deathPulse.Managers.HealthManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MatchHealthCommand implements CommandExecutor {
    private final DeathPulse plugin;

    public MatchHealthCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if (!(player.isOp()) || !(player.hasPermission("dp.matchHealth"))){
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§fThis command§c can only be run§f by a player or from the console.");
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage("§fUsage:§c /DeathPulse§b matchHealth§f <player|allPlayer>");
            return true;
        }

        if (args[1].equalsIgnoreCase("allPlayer")) {
            // Match health for all players based on their death data
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                matchPlayerHealth(onlinePlayer, sender);
            }

            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                matchOfflinePlayerHealth(offlinePlayer, sender);
            }

            sender.sendMessage("§bAll players'§f health has been matched based on their§e death data§f.");
        } else {
            // Match health for specified player based on their death data
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer == null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(args[1]));
                if (!offlinePlayer.hasPlayedBefore()) {
                    sender.sendMessage("§cPlayer not found.");
                    return true;
                }
                matchOfflinePlayerHealth(offlinePlayer, sender);
            } else {
                matchPlayerHealth(targetPlayer, sender);
            }
        }
        return true;
    }

    private void matchPlayerHealth(Player player, CommandSender sender) {
        UUID playerUUID = player.getUniqueId();
        Set<String> deathData = plugin.getDeathDataManager().loadPlayerDeaths(playerUUID);
        if (deathData.isEmpty()) {
            sender.sendMessage("§b" + player.getName() + "§c has no recorded§e death data.");
        } else {
            int matchedHealth = calculateHealthFromDeathData(deathData);
            HealthManager.setMaxHealth(matchedHealth, player);
            player.sendMessage("§bYour§f health has been matched to§d " + matchedHealth + "§f based on your§e death data.");
            sender.sendMessage("§fSet§b " + player.getName() + "'s§f health to§d " + matchedHealth + "§f based on their§e death data.");
        }
    }

    private void matchOfflinePlayerHealth(OfflinePlayer player, CommandSender sender) {
        UUID playerUUID = player.getUniqueId();
        Set<String> deathData = plugin.getDeathDataManager().loadPlayerDeaths(playerUUID);
        if (deathData.isEmpty()) {
            sender.sendMessage("§b" + player.getName() + "§c has no recorded§e death data.");
        } else {
            int matchedHealth = calculateHealthFromDeathData(deathData);
            HealthManager.setOfflinePlayerMaxHealth(matchedHealth, player);
            sender.sendMessage("§fSet§b " + player.getName() + "'s§f health to§b " + matchedHealth + "§f based on their§e death data.");
        }
    }

    private int calculateHealthFromDeathData(Set<String> deathData) {
        int startHealth = plugin.getConfigManager().getHpStart();
        int gainedPerDeath = plugin.getConfigManager().getGainedPerDeath();
        int decreasePerDeath = plugin.getConfigManager().getDecreasePerDeath();
        Set<String> ignoredDeaths = new HashSet<>(plugin.getConfigManager().getDeathIgnored());
        Set<String> decreaseCauses = new HashSet<>(plugin.getConfigManager().getDecreaseCause());
        List<Integer> decreaseDays = plugin.getConfigManager().getDecreaseDays();
        int decreaseDayAmount = plugin.getConfigManager().getDecreaseDayAmount();

        long validDeathsCount = deathData.stream()
                .filter(deathCause -> !ignoredDeaths.contains(deathCause) && !decreaseCauses.contains(deathCause) && decreaseDays.stream().noneMatch(day -> deathCause.contains("decrease_day_" + day)))
                .count();

        long decreaseDeathsCount = deathData.stream()
                .filter(decreaseCauses::contains)
                .count();

        long decreaseDaysCount = deathData.stream()
                .filter(deathCause -> decreaseDays.stream()
                        .anyMatch(day -> deathCause.contains("decrease_day_" + day)))
                .count();

        int totalHealth = startHealth + (int) (validDeathsCount * gainedPerDeath) - (int) (decreaseDeathsCount * decreasePerDeath) - (int) (decreaseDaysCount * decreaseDayAmount);

        if (plugin.getConfigManager().isGainedMaxEnabled()){
            totalHealth = Math.min(totalHealth, plugin.getConfigManager().getGainedMaxAmount());
        }

        if (plugin.getConfigManager().isDecreaseMinEnabled()){
            totalHealth = Math.max(totalHealth, plugin.getConfigManager().getDecreaseMinAmount());
        }

        return totalHealth;
    }
}