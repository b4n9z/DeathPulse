package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import io.github.b4n9z.deathPulse.Managers.HealthManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TransferHealthCommand implements CommandExecutor {
    private final DeathPulse plugin;

    public TransferHealthCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if (!(player.isOp()) || !(player.hasPermission("dp.transferHealth")) || !plugin.getConfigManager().isPermissionAllPlayerTransferHealth()) {
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
            if (args.length != 3) {
                sender.sendMessage("§fUsage:§c /DeathPulse§b transferHealth§f <player> <amount>");
                return true;
            }

            String targetPlayer = args[1];
            double amount = Double.parseDouble(args[2]);
            confirmTransferHealth(player, targetPlayer, amount);
        }

        return true;
    }

    private void confirmTransferHealth(Player player, String target, double amount) {
        Player playerTarget = Bukkit.getPlayer(target);
        if (playerTarget == null) {
            player.sendMessage("§cPlayer not found.");
            return;
        }
        UUID targetUUID = playerTarget.getUniqueId();

        TextComponent message = new TextComponent("§fAre§b you§f sure§b you§f want to transfer§d " + amount + "§e Health§f to§b " + target + "§f? ");
        TextComponent yes = new TextComponent("[YES]");
        yes.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathpulse confirmTransferHealth " + targetUUID + " " + amount));

        TextComponent no = new TextComponent("[NO]");
        no.setColor(net.md_5.bungee.api.ChatColor.RED);
        no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathpulse cancelTransferHealth"));

        TextComponent newline = new TextComponent("\n");

        message.addExtra(newline);
        message.addExtra(yes);
        message.addExtra(" ");
        message.addExtra(no);

        player.spigot().sendMessage(message);
    }

    public boolean confirmedTransferHealth(Player player, String target, double amount) {
        UUID playerUUID = UUID.fromString(target);
        Player playerTarget = Bukkit.getPlayer(playerUUID);
        if (playerTarget == null) {
            player.sendMessage("§cPlayer not found.");
            return false;
        }

        String targetName = playerTarget.getName();

        try {
            double oldHealthTarget = HealthManager.getMaxHealth(playerTarget);
            double oldHealthPlayer = HealthManager.getMaxHealth(player);
            double newHealthTarget = oldHealthTarget + amount;
            double newHealthPlayer = oldHealthPlayer - amount;

            if (plugin.getConfigManager().isMaxHPEnabled() && newHealthTarget > plugin.getConfigManager().getMaxHPAmount()) {
                player.sendMessage("§fHealth target§c exceeds§f the max limit if you transfer §c"+amount+"§f to §b" + targetName + " (§cMax:" + plugin.getConfigManager().getMaxHPAmount() + "§f).");
                return false;
            } else if (plugin.getConfigManager().isMinHPEnabled() && newHealthPlayer < plugin.getConfigManager().getMinHPAmount()) {
                player.sendMessage("§fYour Health amount is§c under§f the min limit if you transfer §c"+amount+"§f to §b" + targetName + " (§cMin:" + plugin.getConfigManager().getMinHPAmount() + "§f).");
                return false;
            }

            HealthManager.setMaxHealth(newHealthPlayer, player);
            player.sendMessage("§bYour§f health has been set to§d " + newHealthPlayer + "§f after transferring health to§b " + playerTarget.getName());

            HealthManager.setMaxHealth(newHealthTarget, playerTarget);
            playerTarget.sendMessage("§bYou§f get transferred health from§b " + player.getName() + "§f. Your health has been set to§d " + newHealthTarget);
            player.sendMessage("§fTransferred §d " + newHealthTarget + "§f Health to§b " + playerTarget.getName());

        } catch (Exception e) {
            player.sendMessage("§cFailed to transfer health.");
            return false;
        }
        return true;
    }
}
