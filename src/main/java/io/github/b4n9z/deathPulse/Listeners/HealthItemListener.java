package io.github.b4n9z.deathPulse.Listeners;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import io.github.b4n9z.deathPulse.Managers.HealthManager;

import java.util.UUID;

public class HealthItemListener implements Listener {
    private final DeathPulse plugin;

    public HealthItemListener(DeathPulse plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!plugin.getHealthItemManager().isHealthItem(item)) return;

        event.setCancelled(true);

        double healthPerItem = plugin.getConfigManager().getHealthItemHealthPerItem();

        if (plugin.getConfigManager().isMaxHPEnabledAndMoreThanMax(HealthManager.getMaxHealth(player)+healthPerItem)) {
            player.sendMessage(plugin.getConfigManager().getNotificationPlayerMaxHealth());
            return;
        }

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        double newHealth = HealthManager.getMaxHealth(player) + healthPerItem;
        if (plugin.getConfigManager().isDecreaseDebt()) {
            if (plugin.getDebtDataManager().getDebt(playerUUID) > 0) {
                int leftOver = plugin.getDebtDataManager().reduceDebt(playerUUID, (int) healthPerItem);
                newHealth = HealthManager.getMaxHealth(player) + leftOver;

                if (leftOver > 0 || plugin.getDebtDataManager().getDebt(playerUUID) <= 0) {
                    player.sendMessage(plugin.getConfigManager().getNotificationPlayerDebtPaidOff()
                            .replace("{debtPaid}", (healthPerItem - leftOver) + ""));
                    healthPerItem = leftOver;
                } else {
                    player.sendMessage(plugin.getConfigManager().getNotificationPlayerDebtReduced()
                            .replace("{debtPaid}", healthPerItem + "")
                            .replace("{debtLeft}", plugin.getDebtDataManager().getDebt(playerUUID) + ""));
                    healthPerItem = 0;
                }
            }
        }
        HealthManager.setMaxHealth(newHealth, player);
        player.setHealth(Math.min(player.getHealth() + plugin.getHealthItemManager().getHealthPerItem(), newHealth));

        String msgPlayer = plugin.getConfigManager().getNotificationPlayerIncrease()
                .replace("{increase}",String.valueOf(healthPerItem))
                .replace("{cause}","Health Item");
        String msgServer = plugin.getConfigManager().getNotificationConsoleIncrease()
                .replace("{name}",player.getName())
                .replace("{increase}",String.valueOf(healthPerItem))
                .replace("{cause}","Health Item");

        player.sendMessage(msgPlayer);
        plugin.sendColoredMessageToConsole(msgServer);
    }

    @EventHandler
    public void onItemBurn(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Item item)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID || event.getCause() == EntityDamageEvent.DamageCause.WORLD_BORDER) return;
        if (plugin.getHealthItemManager().isHealthItem(item.getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();

        for (ItemStack item : inventory.getMatrix()) {
            if (item != null && plugin.getHealthItemManager().isHealthItem(item)) {
                ItemStack result = inventory.getResult();
                if (result != null && plugin.getConfigManager().isItemCannotCraftUsingHealthItem(result.getType().toString())) {
                    inventory.setResult(null);
                    break;
                }
            }
        }
    }
}
