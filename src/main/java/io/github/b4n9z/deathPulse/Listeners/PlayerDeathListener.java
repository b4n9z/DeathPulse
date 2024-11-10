package io.github.b4n9z.deathPulse.Listeners;

import io.github.b4n9z.deathPulse.DeathPulse;
import io.github.b4n9z.deathPulse.Managers.HealthManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class PlayerDeathListener implements Listener {
    private final DeathPulse plugin;

    public PlayerDeathListener(DeathPulse plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        UUID playerUUID = player.getUniqueId();
        String deathCause = event.getDamageSource().getDamageType().getTranslationKey();

        if (plugin.getConfigManager().getDeathIgnored().contains(deathCause)) {
            String msgPlayer = plugin.getConfigManager().getDeathMessagePlayerIgnored()
                    .replace("&","§")
                    .replace("{cause}",deathCause);
            player.sendMessage(msgPlayer);
            return;
        }

        if (plugin.getConfigManager().isDecreaseEnabled() && plugin.getConfigManager().getDecreaseCause().contains(deathCause)) {
            double newMaxHealth = HealthManager.getMaxHealth(player) - plugin.getConfigManager().getDecreasePerDeath();
            if(newMaxHealth < plugin.getConfigManager().getDecreaseMin()){
                newMaxHealth = plugin.getConfigManager().getDecreaseMin();
            }
            HealthManager.setMaxHealth(newMaxHealth, player);
            String msgPlayer = plugin.getConfigManager().getDeathMessagePlayerDecrease()
                    .replace("&","§")
                    .replace("{decrease}",String.valueOf(plugin.getConfigManager().getDecreasePerDeath()))
                    .replace("{cause}",deathCause);
            String msgServer = plugin.getConfigManager().getDeathMessageLogServerDecrease()
                    .replace("&","§")
                    .replace("{name}",player.getName())
                    .replace("{decrease}",String.valueOf(plugin.getConfigManager().getDecreasePerDeath()))
                    .replace("{cause}",deathCause);
            player.sendMessage(msgPlayer);
            plugin.getLogger().info(msgServer);
            return;
        }

        double currentMaxHealth = HealthManager.getMaxHealth(player);
        double newMaxHealth = currentMaxHealth + plugin.getConfigManager().getGainedPerDeath();
        String msgPlayer = plugin.getConfigManager().getDeathMessagePlayerGained()
                .replace("&", "§")
                .replace("{gain}", String.valueOf(plugin.getConfigManager().getGainedPerDeath()))
                .replace("{cause}", deathCause);
        String msgServer = plugin.getConfigManager().getDeathMessageLogServerGained()
                .replace("&", "§")
                .replace("{name}", player.getName())
                .replace("{gain}", String.valueOf(plugin.getConfigManager().getGainedPerDeath()))
                .replace("{cause}", deathCause);

        if (!plugin.getConfigManager().isGainedMaxEnabled() || newMaxHealth <= plugin.getConfigManager().getGainedMaxAmount()) {
            boolean isNewDeathType = plugin.getDeathDataManager().logDeath(playerUUID, deathCause);

            if (isNewDeathType) {
                HealthManager.setMaxHealth(newMaxHealth, player);
                player.sendMessage(msgPlayer);
                plugin.getLogger().info(msgServer);
            } else if (!plugin.getConfigManager().isDeathMustDifference()) {
                HealthManager.setMaxHealth(newMaxHealth, player);
                player.sendMessage(msgPlayer);
                plugin.getLogger().info(msgServer);
            } else {
                String sameWayDeath = plugin.getConfigManager().getDeathMessagePlayerIfSameWay()
                        .replace("&", "§");
                player.sendMessage(sameWayDeath);
            }
        } else {
            String msgPlayerIfMaxHealth = plugin.getConfigManager().getDeathMessagePlayerMaxHealth()
                    .replace("&", "§");
            player.sendMessage(msgPlayerIfMaxHealth);
        }
    }
}
