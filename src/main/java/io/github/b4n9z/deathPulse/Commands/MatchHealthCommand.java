package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import io.github.b4n9z.deathPulse.Managers.HealthManager;
import io.github.b4n9z.deathPulse.Managers.TransactionManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
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
            if (!(player.isOp()) && !(player.hasPermission("dp.matchHealth")) && !(plugin.getConfigManager().isPermissionAllPlayerMatchHealth())) {
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
            requestMatchHealthAll(sender);
        } else {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer == null) {
                if (plugin.getConfigManager().isValidUUID(args[1])) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(args[1]));
                    if (!offlinePlayer.hasPlayedBefore()) {
                        sender.sendMessage("§cPlayer not found.");
                        return true;
                    }
                    requestMatchHealthSingle(sender, offlinePlayer);
                } else {
                    sender.sendMessage("§cInvalid player name or UUID.");
                }
            } else {
                requestMatchHealthSingle(sender, targetPlayer);
            }
        }
        return true;
    }

    private void requestMatchHealthSingle(CommandSender sender, OfflinePlayer target) {
        String transactionId = TransactionManager.generateTransactionId(sender);
        TransactionManager.openTransaction(plugin, sender, transactionId);

        TextComponent message = new TextComponent("§fAre§b you§f sure you want to match health for§b " + target.getName() + "§f? ");
        TextComponent yes = new TextComponent("[YES]");
        yes.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathpulse confirmMatchHealth " + target.getUniqueId() + " " + transactionId));

        TextComponent no = new TextComponent("[NO]");
        no.setColor(net.md_5.bungee.api.ChatColor.RED);
        no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathpulse cancelMatchHealth " + transactionId));

        message.addExtra("\n");
        message.addExtra(yes);
        message.addExtra(" ");
        message.addExtra(no);

        sender.spigot().sendMessage(message);
    }

    private void requestMatchHealthAll(CommandSender sender) {
        String transactionId = TransactionManager.generateTransactionId(sender);
        TransactionManager.openTransaction(plugin, sender, transactionId);

        TextComponent message = new TextComponent("§fAre§b you§f sure you want to match health for§e ALL players§f? ");
        TextComponent yes = new TextComponent("[YES]");
        yes.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathpulse confirmMatchHealth allPlayer " + transactionId));

        TextComponent no = new TextComponent("[NO]");
        no.setColor(net.md_5.bungee.api.ChatColor.RED);
        no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathpulse cancelMatchHealth " + transactionId));

        message.addExtra("\n");
        message.addExtra(yes);
        message.addExtra(" ");
        message.addExtra(no);

        sender.spigot().sendMessage(message);
    }

    public boolean confirmMatchHealth(CommandSender sender, String[] args) {
        if (sender instanceof Player player){
            if (!(player.isOp()) && !(player.hasPermission("dp.matchHealth")) && !(plugin.getConfigManager().isPermissionAllPlayerMatchHealth())) {
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§fThis command§c can only be run§f by a player or from the console.");
            return false;
        }

        if (args.length != 3) {
            sender.sendMessage("§fUsage:§c /DeathPulse§b confirmMatchHealth§f <playerUUID|allPlayer> <transactionID>");
            return false;
        }

        String target = args[1];
        String transactionId = args[2];

        if (!TransactionManager.isValidTransaction(sender, transactionId)) {
            sender.sendMessage("§cThis confirmation has expired or is invalid.");
            return false;
        }

        if (target.equalsIgnoreCase("allPlayer")) {
            // Match health for all players based on their death data
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                matchPlayerHealth(onlinePlayer, sender);
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    if (!offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) continue;

                    matchOfflinePlayerHealth(offlinePlayer, sender);
                }
                if (sender instanceof Player) {
                    sender.sendMessage("§bAll players'§f health has been matched (async) based on their§e death data§f.");
                }
            });
        } else {
            // Match health for specified player based on their death data
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer == null) {
                if (plugin.getConfigManager().isValidUUID(args[1])) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(args[1]));
                    if (!offlinePlayer.hasPlayedBefore()) {
                        sender.sendMessage("§cPlayer not found.");
                        TransactionManager.closeTransaction(sender);
                        return true;
                    }
                    matchOfflinePlayerHealth(offlinePlayer, sender);
                } else {
                    sender.sendMessage("§cInvalid player name or UUID.");
                    TransactionManager.closeTransaction(sender);
                    return true;
                }
            } else {
                matchPlayerHealth(targetPlayer, sender);
            }
        }

        TransactionManager.closeTransaction(sender);
        return true;
    }

    public boolean cancelMatchHealth(CommandSender sender, String[] args) {
        if (sender instanceof Player player){
            if (!(player.isOp()) && !(player.hasPermission("dp.matchHealth")) && !(plugin.getConfigManager().isPermissionAllPlayerMatchHealth())) {
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§fThis command§c can only be run§f by a player or from the console.");
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage("§fUsage:§c /DeathPulse§b cancelMatchHealth§f <transactionID>");
            return false;
        }

        String transactionId = args[1];
        if (!transactionId.isEmpty() && TransactionManager.isValidTransaction(sender, transactionId)) {
            TransactionManager.closeTransaction(sender);
            sender.sendMessage("§bMatch health§f has been§c cancelled.");
        } else {
            sender.sendMessage("§cNo active transaction to cancel or invalid transaction ID.");
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
        UUID playerUUID = player.getUniqueId();
        Set<String> deathData = plugin.getDeathDataManager().loadPlayerDeaths(playerUUID);
        int matchedHealth;

        if (deathData.isEmpty()) {
            matchedHealth = plugin.getConfigManager().getHPStart();
        } else {
            matchedHealth = calculateHealthFromDeathData(deathData);
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (matchedHealth <= 0) {
                if (plugin.getConfigManager().getMinHPBanTime() == 0) {
                    plugin.getBanManager().banOfflinePlayerPermanently(player);
                } else {
                    plugin.getBanManager().banOfflinePlayer(player, plugin.getConfigManager().getMinHPBanTime() * 60L * 60 * 1000);
                }
                sender.sendMessage("§fBanned§b " + player.getName() + "§f because their health is§c 0§f or less (offline).");
            } else {
                HealthManager.setOfflinePlayerMaxHealth(matchedHealth, player);
                sender.sendMessage("§fSet§b " + player.getName() + "§f health to§b " + matchedHealth + "§f based on their§e death data.");
            }
        });
    }

    private boolean isValidDay(String deathCause, int day, String typeDeath) {
        String keyword = typeDeath+"Day_";
        int startIndex = deathCause.indexOf(keyword);
        if (startIndex == -1) return false; // If "increaseDay_" or " "decreaseDay_" not found, return false

        startIndex += keyword.length(); // Position after "increaseDay_" or "decreaseDay_"

        int endIndex = deathCause.indexOf('_', startIndex); // Position of the next underscore
        if (endIndex == -1) endIndex = deathCause.indexOf(']', startIndex); // If no next underscore, use position of the closing bracket
        if (endIndex == -1) return false;

        try {
            int deathDay = Integer.parseInt(deathCause.substring(startIndex, endIndex));
            return day != 0 && deathDay % day == 0;
        } catch (NumberFormatException e) {
            return false; // If parsing fails, return false
        }
    }

    private boolean isCauseMatch(Set<String> causes, String deathCause, String suffix, String typeDeath) {
        if (!deathCause.endsWith(suffix) && !typeDeath.equals("DAY")) return false;

        if (causes.contains("ALL")) return true;

        return causes.stream().anyMatch(cause -> {
            int bracketIndex = deathCause.indexOf('[');
            String deathCauseWithoutSuffix = bracketIndex != -1 ? deathCause.substring(0, bracketIndex) : deathCause;
            return deathCauseWithoutSuffix.equals(cause);
        });
    }

    private String splitCause(String deathCause) {
        int bracketIndex = deathCause.indexOf('[');
        return bracketIndex != -1 ? deathCause.substring(0, bracketIndex) : deathCause;
    }

    private int calculateHealthFromDeathData(Set<String> deathData) {
        Set<String> increaseCause = new HashSet<>(plugin.getConfigManager().getIncreaseCauseName());
        Set<String> increaseDayCause = new HashSet<>(plugin.getConfigManager().getIncreaseDayCauseName());

        Set<String> decreaseCause = new HashSet<>(plugin.getConfigManager().getDecreaseCauseName());
        Set<String> decreaseDayCause = new HashSet<>(plugin.getConfigManager().getDecreaseDayCauseName());

        long increaseHealth = 0;
        long increaseDaysHealth = 0;
        long decreaseHealth = 0;
        long decreaseDaysHealth = 0;

        for (String deathCause : deathData) {
            String causeDeath = splitCause(deathCause);

            if (plugin.getConfigManager().isIncreaseEnabled() && (isCauseMatch(increaseCause, deathCause, "[Increase]",""))){
                increaseHealth += plugin.getConfigManager().getIncreaseCauseAmount(causeDeath);
            }
            if (plugin.getConfigManager().isIncreaseDayEnabled() && (isCauseMatch(increaseDayCause, deathCause, "[Increase]","DAY"))){
                for (int day : plugin.getConfigManager().getIncreaseDays()) {
                    if (isValidDay(deathCause, day, "increase")) {
                        increaseDaysHealth += plugin.getConfigManager().getIncreaseDayCauseAmount(causeDeath);
                        break;
                    }
                }
            }
            if (plugin.getConfigManager().isDecreaseEnabled() && (isCauseMatch(decreaseCause, deathCause, "[Decrease]",""))){
                decreaseHealth += plugin.getConfigManager().getDecreaseCauseAmount(causeDeath);
            }
            if (plugin.getConfigManager().isDecreaseDayEnabled() && (isCauseMatch(decreaseDayCause, deathCause, "[Decrease]","DAY"))){
                for (int day : plugin.getConfigManager().getDecreaseDays()) {
                    if (isValidDay(deathCause, day, "decrease")) {
                        decreaseDaysHealth += plugin.getConfigManager().getDecreaseDayCauseAmount(causeDeath);
                        break;
                    }
                }
            }
        }

        int totalHealth = (int) (plugin.getConfigManager().getHPStart() + increaseHealth + increaseDaysHealth - decreaseHealth - decreaseDaysHealth);

        if (plugin.getConfigManager().isMaxHPEnabled()) totalHealth = Math.min(totalHealth, plugin.getConfigManager().getMaxHPAmount());

        if (plugin.getConfigManager().isMinHPEnabled()) totalHealth = Math.max(totalHealth, plugin.getConfigManager().getMinHPAmount());

        return totalHealth;
    }
}