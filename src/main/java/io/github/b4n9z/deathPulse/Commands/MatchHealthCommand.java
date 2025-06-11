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
            if (!(player.isOp()) || !(player.hasPermission("dp.matchHealth")) || !(plugin.getConfigManager().isPermissionAllPlayerMatchHealth())) {
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
            HealthManager.setMaxHealth(plugin.getConfigManager().getHPStart(), player);
            sender.sendMessage("§b" + player.getName() + "§c has no recorded§e death data.");
        } else {
            int matchedHealth = calculateHealthFromDeathData(deathData);
            if (matchedHealth <= 0) {
                if (plugin.getConfigManager().getMinHPBanTime() == 0) {
                    plugin.getBanManager().banPlayerPermanently(player);
                } else {
                    plugin.getBanManager().banPlayer(player, (long) plugin.getConfigManager().getMinHPBanTime() * 60 * 60 * 1000);
                }
                sender.sendMessage("§fBanned§b " + player.getName() + "§f because their health is§c 0§f or less.");
            } else {
                HealthManager.setMaxHealth(matchedHealth, player);
                player.sendMessage("§bYour§f health has been matched to§d " + matchedHealth + "§f based on your§e death data.");
                sender.sendMessage("§fSet§b " + player.getName() + "§f health to§d " + matchedHealth + "§f based on their§e death data.");
            }
        }
    }

    private void matchOfflinePlayerHealth(OfflinePlayer player, CommandSender sender) {
        if(player.isOnline()) return;
        UUID playerUUID = player.getUniqueId();
        Set<String> deathData = plugin.getDeathDataManager().loadPlayerDeaths(playerUUID);
        if (deathData.isEmpty()) {
            HealthManager.setOfflinePlayerMaxHealth(plugin.getConfigManager().getHPStart(), player);
            sender.sendMessage("§b" + player.getName() + "§c has no recorded§e death data.");
        } else {
            int matchedHealth = calculateHealthFromDeathData(deathData);
            if (matchedHealth <= 0) {
                if (plugin.getConfigManager().getMinHPBanTime() == 0) {
                    plugin.getBanManager().banOfflinePlayerPermanently(player);
                } else {
                    plugin.getBanManager().banOfflinePlayer(player, (long) plugin.getConfigManager().getMinHPBanTime() * 60 * 60 * 1000);
                }
                sender.sendMessage("§fBanned§b " + player.getName() + "§f because their health is§c 0§f or less, and they are§c offline§f.");
            } else {
                HealthManager.setOfflinePlayerMaxHealth(matchedHealth, player);
                sender.sendMessage("§fSet§b " + player.getName() + "§f health to§b " + matchedHealth + "§f based on their§e death data.");
            }
        }
    }

    private boolean isValidDay(String deathCause, int day, String typeDeath) {
        String keyword = typeDeath+"Day_";
        int startIndex = deathCause.indexOf(keyword);
        if (startIndex == -1) {
            return false; // If "increaseDay_" or " "decreaseDay_" not found, return false
        }
        startIndex += keyword.length(); // Position after "increaseDay_" or "decreaseDay_"

        int endIndex = deathCause.indexOf('_', startIndex); // Position of the next underscore
        if (endIndex == -1) {
            endIndex = deathCause.indexOf(']', startIndex); // If no next underscore, use position of the closing bracket
        }

        try {
            int deathDay = Integer.parseInt(deathCause.substring(startIndex, endIndex));
            return deathDay % day == 0;
        } catch (NumberFormatException e) {
            return false; // If parsing fails, return false
        }
    }

    private boolean isCauseMatch(Set<String> causes, String deathCause, String suffix, String typeDeath) {
        if (!deathCause.endsWith(suffix) && !typeDeath.equals("DAY")) {
            return false;
        }
        if (causes.contains("all")) {
            return true;
        }
        return causes.stream().anyMatch(cause -> {
            int bracketIndex = deathCause.indexOf('[');
            String deathCauseWithoutSuffix = bracketIndex != -1 ? deathCause.substring(0, bracketIndex) : deathCause;
            return deathCauseWithoutSuffix.equals(cause);
        });
    }

    private int calculateHealthFromDeathData(Set<String> deathData) {
        int startHealth = plugin.getConfigManager().getHPStart();
        boolean isMaxHPEnabled = plugin.getConfigManager().isMaxHPEnabled();
        int MaxHPAmount = plugin.getConfigManager().getMaxHPAmount();
        boolean isMinHPEnabled = plugin.getConfigManager().isMinHPEnabled();
        int MinHPAmount = plugin.getConfigManager().getMinHPAmount();

        boolean isIncreaseEnabled = plugin.getConfigManager().isIncreaseEnabled();
        int increasePerDeath = plugin.getConfigManager().getIncreasePerDeath();
        boolean isIncreaseDayEnabled = plugin.getConfigManager().isIncreaseDayEnabled();
        boolean isIncreaseDaySameCauseRequired = plugin.getConfigManager().isIncreaseDaySameCauseRequired();
        List<Integer> increaseDays = plugin.getConfigManager().getIncreaseDays();
        int increaseDayAmount = plugin.getConfigManager().getIncreaseDayAmount();
        Set<String> increaseCause = new HashSet<>(plugin.getConfigManager().getIncreaseCause());

        boolean isDecreaseEnabled = plugin.getConfigManager().isDecreaseEnabled();
        int decreasePerDeath = plugin.getConfigManager().getDecreasePerDeath();
        boolean isDecreaseDayEnabled = plugin.getConfigManager().isDecreaseDayEnabled();
        boolean isDecreaseDaySameCauseRequired = plugin.getConfigManager().isDecreaseDaySameCauseRequired();
        List<Integer> decreaseDays = plugin.getConfigManager().getDecreaseDays();
        int decreaseDayAmount = plugin.getConfigManager().getDecreaseDayAmount();
        Set<String> decreaseCause = new HashSet<>(plugin.getConfigManager().getDecreaseCause());

        long validIncreaseCount = deathData.stream()
                .filter(deathCause -> (isIncreaseEnabled && (isCauseMatch(increaseCause, deathCause, "[Increase]",""))))
                .count();
        long validIncreaseDaysCount = deathData.stream()
                .filter(deathCause -> (isIncreaseEnabled && isIncreaseDayEnabled && (
                        (!isIncreaseDaySameCauseRequired && deathCause.startsWith("increaseDay") && increaseDays.stream().anyMatch(day -> isValidDay(deathCause, day, "increase")))
                        || (isIncreaseDaySameCauseRequired && (isCauseMatch(increaseCause, deathCause, "[Increase]","DAY")) && (increaseDays.stream().anyMatch(day -> isValidDay(deathCause, day, "increase"))))
                )))
                .count();

        long validDecreaseCount = deathData.stream()
                .filter(deathCause -> (isDecreaseEnabled && (isCauseMatch(decreaseCause, deathCause, "[Decrease]",""))))
                .count();
        long validDecreaseDaysCount = deathData.stream()
                .filter(deathCause -> (isDecreaseEnabled && isDecreaseDayEnabled && (
                        (!isDecreaseDaySameCauseRequired && deathCause.startsWith("decreaseDay") && decreaseDays.stream().anyMatch(day -> isValidDay(deathCause, day, "decrease")))
                        || (isDecreaseDaySameCauseRequired && (isCauseMatch(decreaseCause, deathCause, "[Decrease]","DAY")) && (decreaseDays.stream().anyMatch(day -> isValidDay(deathCause, day, "decrease"))))
                )))
                .count();

        int totalHealth = (int) (startHealth + (validIncreaseCount * increasePerDeath) + (validIncreaseDaysCount * increaseDayAmount) - (validDecreaseCount * decreasePerDeath) - (validDecreaseDaysCount * decreaseDayAmount));

        if (isMaxHPEnabled){
            totalHealth = Math.min(totalHealth, MaxHPAmount);
        }

        if (isMinHPEnabled){
            totalHealth = Math.max(totalHealth, MinHPAmount);
        }

        return totalHealth;
    }
}