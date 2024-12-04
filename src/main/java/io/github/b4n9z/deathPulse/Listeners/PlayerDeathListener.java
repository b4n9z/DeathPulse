package io.github.b4n9z.deathPulse.Listeners;

import io.github.b4n9z.deathPulse.DeathPulse;
import io.github.b4n9z.deathPulse.Managers.HealthManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.*;

public class PlayerDeathListener implements Listener {
    private final DeathPulse plugin;

    public PlayerDeathListener(DeathPulse plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        if (player == null) return;
        UUID playerUUID = player.getUniqueId();
        String deathCause = event.getDamageSource().getDamageType().getTranslationKey();

        if (plugin.getConfigManager().getDeathIgnored().contains("all") || plugin.getConfigManager().getDeathIgnored().contains(deathCause)) {
            String msgPlayer = plugin.getConfigManager().getDeathMessagePlayerIgnored()
                    .replace("&","§")
                    .replace("{cause}",deathCause);
            player.sendMessage(msgPlayer);
            return;
        }

        if ((plugin.getConfigManager().isDecreaseEnabled() && plugin.getConfigManager().getDecreaseCause().contains("all"))
                || (plugin.getConfigManager().isDecreaseEnabled() && plugin.getConfigManager().getDecreaseCause().contains(deathCause))
                || (plugin.getConfigManager().isDecreaseEnabled() && plugin.getConfigManager().isDecreaseDayEnabled() && isMultipleDayDecrease(player))
        ) {
            int decreaseAmount;
            decreaseAmount = plugin.getConfigManager().getDecreasePerDeath();
            double newMaxHealth = HealthManager.getMaxHealth(player) - decreaseAmount;
            String deathCauseMessage = deathCause;

            if (plugin.getConfigManager().isDecreaseDayEnabled() && isMultipleDayDecrease(player)) {
                decreaseAmount = plugin.getConfigManager().getDecreaseDayAmount();
                newMaxHealth = HealthManager.getMaxHealth(player) - decreaseAmount;
                int currentDay = getCurrentDay(player.getWorld());
                int deathCounter = plugin.getDeathDataManager().getNextDeathCounter(playerUUID, currentDay);
                deathCauseMessage = "decrease_day_" + currentDay + "_" + deathCounter;
            }

            if (plugin.getConfigManager().getDecreasePerDeath() == 0 && plugin.getConfigManager().isDecreaseDayEnabled()) {
                if (isMultipleDayDecrease(player)) {
                    plugin.getDeathDataManager().logDeath(playerUUID, deathCauseMessage);
                }
            } else {
                plugin.getDeathDataManager().logDeath(playerUUID, deathCauseMessage);
            }

            if(plugin.getConfigManager().isDecreaseMinEnabled() && newMaxHealth < plugin.getConfigManager().getDecreaseMinAmount()){
                newMaxHealth = plugin.getConfigManager().getDecreaseMinAmount();
            } else if(!plugin.getConfigManager().isDecreaseMinEnabled() && newMaxHealth <= 0){
                newMaxHealth = 2;
                if (plugin.getConfigManager().getDecreaseBanTime() == 0) {
                    plugin.getBanManager().banPlayerPermanently(player);
                } else {
                    plugin.getBanManager().banPlayer(player, (long) plugin.getConfigManager().getDecreaseBanTime() * 60 * 60 * 1000);
                }
                HealthManager.setMaxHealth(newMaxHealth, player);
                return;
            }

            HealthManager.setMaxHealth(newMaxHealth, player);

            String msgPlayer = plugin.getConfigManager().getDeathMessagePlayerDecrease()
                    .replace("&","§")
                    .replace("{decrease}",String.valueOf(decreaseAmount))
                    .replace("{cause}",deathCauseMessage);
            String msgServer = plugin.getConfigManager().getDeathMessageLogServerDecrease()
                    .replace("&","§")
                    .replace("{name}",player.getName())
                    .replace("{decrease}",String.valueOf(decreaseAmount))
                    .replace("{cause}",deathCauseMessage);

            player.sendMessage(msgPlayer);
            plugin.getLogger().info(msgServer);

            return;
        }

        double currentMaxHealth = HealthManager.getMaxHealth(player);
        double newMaxHealth;
        double gainedAmount;
        if (plugin.getConfigManager().isGainedSpecialDayEnabled() && isMultipleSpecialIncreaseDay(player.getWorld())) {
            newMaxHealth = currentMaxHealth + plugin.getConfigManager().getGainedSpecialDayAmount();
            deathCause = deathCause + "[special_day_" + getCurrentDay(player.getWorld()) + "]";
            gainedAmount = plugin.getConfigManager().getGainedSpecialDayAmount();
        } else {
            newMaxHealth = currentMaxHealth + plugin.getConfigManager().getGainedPerDeath();
            gainedAmount = plugin.getConfigManager().getGainedPerDeath();
        }

        String msgPlayer = plugin.getConfigManager().getDeathMessagePlayerGained()
                .replace("&", "§")
                .replace("{gain}", String.valueOf(gainedAmount))
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

    private int getCurrentDay(World world) {
        String dayType = plugin.getConfigManager().getDecreaseDayType();
        if ("real".equalsIgnoreCase(dayType)) {
            // Real-world day calculation
            long currentTimeMillis = System.currentTimeMillis();
            return (int) (currentTimeMillis / (1000 * 60 * 60 * 24)); // Days since epoch
        } else {
            // Server uptime day calculation
            long worldTime = world.getFullTime();
            return (int) (worldTime / 24000); // Days since server start
        }
    }

    private boolean isMultipleDayDecrease(Player player) {
        int currentDay = getCurrentDay(player.getWorld());
        List<Integer> decreaseDays = plugin.getConfigManager().getDecreaseDays();
        // Check if today is a decrease day
        for (int day : decreaseDays) {
            if (currentDay % day == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isMultipleSpecialIncreaseDay(World world) {
        int currentDay = getCurrentDay(world);
        List<Integer> specialDays = plugin.getConfigManager().getGainedSpecialDays();
        for (int day : specialDays) {
            if (currentDay % day == 0) {
                return true;
            }
        }
        return false;
    }
}
