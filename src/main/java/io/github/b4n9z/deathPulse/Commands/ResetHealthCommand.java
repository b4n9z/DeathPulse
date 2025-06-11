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
import java.util.UUID;

public class ResetHealthCommand implements CommandExecutor {
    private final DeathPulse plugin;

    public ResetHealthCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if (!(player.isOp()) && !(player.hasPermission("dp.resetHealth")) && !plugin.getConfigManager().isPermissionAllPlayerResetHealth()){
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§fThis command§c can only be run§f by a player or from the console.");
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage("§fUsage:§c /DeathPulse§b resetHealth§f <player|allPlayer>");
            return true;
        }

        if (args[1].equalsIgnoreCase("allPlayer")) {
            requestResetHealthAll(sender);
        } else {
            // Reset health for specified player
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer == null) {
                if (plugin.getConfigManager().isValidUUID(args[1])) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(args[1]));
                    if (!offlinePlayer.hasPlayedBefore()) {
                        sender.sendMessage("§cPlayer not found.");
                        return true;
                    }
                    requestResetHealth(sender, offlinePlayer);
                }
            } else {
                requestResetHealth(sender, targetPlayer);
            }
        }
        return true;
    }

    private void requestResetHealth(CommandSender sender, OfflinePlayer target) {
        String transactionId = TransactionManager.generateTransactionId(sender);
        TransactionManager.openTransaction(plugin, sender, transactionId);

        TextComponent message = new TextComponent("§fAre§b you§f sure you want to reset health for§b " + target.getName() + "§f? ");
        TextComponent yes = new TextComponent("[YES]");
        yes.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathpulse confirmResetHealth " + target.getUniqueId() + " " + transactionId));

        TextComponent no = new TextComponent("[NO]");
        no.setColor(net.md_5.bungee.api.ChatColor.RED);
        no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathpulse cancelResetHealth " + transactionId));

        message.addExtra("\n");
        message.addExtra(yes);
        message.addExtra(" ");
        message.addExtra(no);

        sender.spigot().sendMessage(message);
    }

    private void requestResetHealthAll(CommandSender sender) {
        String transactionId = TransactionManager.generateTransactionId(sender);
        TransactionManager.openTransaction(plugin, sender, transactionId);

        TextComponent message = new TextComponent("§fAre§b you§f sure you want to reset health for§e ALL players§f? ");
        TextComponent yes = new TextComponent("[YES]");
        yes.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathpulse confirmResetHealth allPlayer " + transactionId));

        TextComponent no = new TextComponent("[NO]");
        no.setColor(net.md_5.bungee.api.ChatColor.RED);
        no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathpulse cancelResetHealth " + transactionId));

        message.addExtra("\n");
        message.addExtra(yes);
        message.addExtra(" ");
        message.addExtra(no);

        sender.spigot().sendMessage(message);
    }

    public boolean confirmResetHealth(CommandSender sender, String[] args) {
        if (sender instanceof Player player){
            if (!(player.isOp()) && !(player.hasPermission("dp.resetHealth")) && !plugin.getConfigManager().isPermissionAllPlayerResetHealth()){
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§fThis command§c can only be run§f by a player or from the console.");
            return false;
        }

        if (args.length != 3) {
            sender.sendMessage("§fUsage:§c /DeathPulse§b resetHealth§f <player|allPlayer> <transactionID>");
            return true;
        }

        String target = args[1];
        String transactionId = args[2];

        if (!TransactionManager.isValidTransaction(sender, transactionId)) {
            sender.sendMessage("§cThis confirmation has expired or is invalid.");
            return false;
        }

        int startHealth;
        try {
            startHealth = plugin.getConfigManager().getHPStart();
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid health amount.");
            TransactionManager.closeTransaction(sender);
            return true;
        }

        if (target.equalsIgnoreCase("allPlayer")) {
            // Reset health for all players
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                HealthManager.setMaxHealth(startHealth, onlinePlayer);
                onlinePlayer.sendMessage("§bYour§f health has been reset to§d " + startHealth + "§b by an admin.");
            }

            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                HealthManager.setOfflinePlayerMaxHealth(startHealth, offlinePlayer);
            }

            sender.sendMessage("§bAll players'§f health has been reset to§d " + startHealth);
        } else {
            // Reset health for specified player
            try {
                UUID uuid = UUID.fromString(target);
                Player online = Bukkit.getPlayer(uuid);
                if (online != null) {
                    HealthManager.setMaxHealth(startHealth, online);
                    online.sendMessage("§bYour§f health has been reset to§d " + startHealth + "§b by an admin.");
                    sender.sendMessage("§fSet§b " + online.getName() + "'s§f health to§d " + startHealth);
                } else {
                    OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);
                    if (!off.hasPlayedBefore()) {
                        sender.sendMessage("§cPlayer not found.");
                        TransactionManager.closeTransaction(sender);
                        return true;
                    }
                    HealthManager.setOfflinePlayerMaxHealth(startHealth, off);
                    sender.sendMessage("§fSet§b " + off.getName() + "'s§f health to§d " + startHealth);
                }
            } catch (IllegalArgumentException ex) {
                sender.sendMessage("§cInvalid player UUID.");
                TransactionManager.closeTransaction(sender);
                return true;
            }
        }
        TransactionManager.closeTransaction(sender);
        return true;
    }

    public boolean cancelResetHealth(CommandSender sender, String[] args) {
        if (sender instanceof Player player){
            if (!(player.isOp()) && !(player.hasPermission("dp.resetHealth")) && !plugin.getConfigManager().isPermissionAllPlayerResetHealth()){
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§fThis command§c can only be run§f by a player or from the console.");
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage("§fUsage:§c /DeathPulse§b resetHealth§f <player|allPlayer>");
            return true;
        }

        String transactionId = args[1];
        if (!transactionId.isEmpty() && TransactionManager.isValidTransaction(sender, transactionId)) {
            TransactionManager.closeTransaction(sender);
            sender.sendMessage("§bReset health§f has been§c cancelled.");
        } else {
            sender.sendMessage("§cNo active transaction to cancel or invalid transaction ID.");
        }
        return true;
    }
}