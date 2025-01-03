package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;

import java.util.UUID;

public class RemoveDeathDataCommand implements CommandExecutor {
    private final DeathPulse plugin;

    public RemoveDeathDataCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!(player.isOp()) || !(player.hasPermission("dp.removeDeathData"))) {
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§fThis command§c can only be run§f by a player or from the console.");
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage("§fUsage:§c /DeathPulse§b removeDeathData§f <player|allPlayer>");
            return false;
        }

        String target = args[1];
        if (target.equalsIgnoreCase("allPlayer")) {
            confirmRemoveAll(sender);
        } else {
            Player targetPlayer = Bukkit.getPlayer(target);
            if (targetPlayer == null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
                if (!offlinePlayer.hasPlayedBefore()) {
                    sender.sendMessage("§cPlayer not found.");
                    return true;
                }
                confirmRemoveSingle(sender, offlinePlayer);
            } else {
                confirmRemoveSingle(sender, targetPlayer);
            }
        }
        return true;
    }

    private void confirmRemoveSingle(CommandSender sender, OfflinePlayer player) {
        TextComponent message = new TextComponent("§fAre§b you§f sure§b you§f want to remove§e death data§f for§b " + player.getName() + "§f? ");
        TextComponent yes = new TextComponent("[YES]");
        yes.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathpulse confirmRemoveDeathData " + player.getUniqueId()));

        TextComponent no = new TextComponent("[NO]");
        no.setColor(net.md_5.bungee.api.ChatColor.RED);
        no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathpulse cancelRemoveDeathData"));

        TextComponent newline = new TextComponent("\n");

        message.addExtra(newline);
        message.addExtra(yes);
        message.addExtra(" ");
        message.addExtra(no);

        sender.spigot().sendMessage(message);
    }

    private void confirmRemoveAll(CommandSender sender) {
        TextComponent message = new TextComponent("§fAre§b you§f sure§b you§f want to remove§e death data§f for§b all players§f? ");
        TextComponent yes = new TextComponent("[YES]");
        yes.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathpulse confirmRemoveAllDeathData"));

        TextComponent no = new TextComponent("[NO]");
        no.setColor(net.md_5.bungee.api.ChatColor.RED);
        no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathpulse cancelRemoveDeathData"));

        TextComponent newline = new TextComponent("\n");

        message.addExtra(newline);
        message.addExtra(yes);
        message.addExtra(" ");
        message.addExtra(no);

        sender.spigot().sendMessage(message);
    }
}