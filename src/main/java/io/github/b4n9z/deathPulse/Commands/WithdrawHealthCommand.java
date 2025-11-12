package io.github.b4n9z.deathPulse.Commands;

import io.github.b4n9z.deathPulse.DeathPulse;
import io.github.b4n9z.deathPulse.Managers.HealthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class WithdrawHealthCommand implements CommandExecutor {
    private final DeathPulse plugin;

    public WithdrawHealthCommand(DeathPulse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if (!plugin.getConfigManager().canUse(player, "withdrawHealth")) {
                sender.sendMessage("§fYou§c do not have permission§f to use this command.");
                return false;
            }
            if (args.length != 2) {
                sender.sendMessage("§fUsage:§c /DeathPulse§b withdrawHealth§f <amount>");
                return true;
            }

            PlayerInventory inventory = player.getInventory();

            if (inventory.firstEmpty() == -1) {
                sender.sendMessage("§cYour inventory is full.");
                return true;
            }

            double currentHealth = HealthManager.getMaxHealth(player);
            double amountItem = Double.parseDouble(args[1]);
            double amountHealth = plugin.getConfigManager().getHealthItemHealthPerItem() * amountItem;

            if (amountHealth >= currentHealth) {
                sender.sendMessage("§cYou don't have enough health.");
                return true;
            }

            HealthManager.setMaxHealth(currentHealth - amountHealth, player);

            ItemStack healthItem = plugin.getHealthItemManager().getHealthItem();
            healthItem.setAmount((int) amountItem);
            inventory.addItem(healthItem);
            
            player.sendMessage("§fYou§b withdrew§f " + amountItem + "§b health§f items.");
            return true;
        }

        return true;
    }
}
