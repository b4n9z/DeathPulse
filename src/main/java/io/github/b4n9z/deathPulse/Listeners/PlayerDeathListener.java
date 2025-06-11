package io.github.b4n9z.deathPulse.Listeners;

import io.github.b4n9z.deathPulse.DeathPulse;
import io.github.b4n9z.deathPulse.Managers.HealthManager;
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
        Player killer = event.getEntity().getKiller();

        if (player == null) return;
        UUID playerUUID = player.getUniqueId();

        //String deathMsg =  Objects.requireNonNull(event.getDeathMessage()).replace(player.getName(),"").replace(killerName,"Player");
        String deathCause = Objects.requireNonNull(player.getLastDamageCause()).getCause().name();

        String season = "Season"+plugin.getDayManager().getSeason(player,"season");

        if (killer != null){
            deathCause = switch (deathCause) {
                case "ENTITY_ATTACK" -> "PLAYER_ATTACK";
                case "ENTITY_SWEEP_ATTACK" -> "PLAYER_SWEEP_ATTACK";
                case "PROJECTILE" -> "PLAYER_PROJECTILE";
                case "ENTITY_EXPLOSION" -> "PLAYER_EXPLOSION";
                default -> deathCause;
            };
        }
        //player.sendMessage(deathCause);
        //player.sendMessage(deathMsg);

        // Disable death message
        if (!plugin.getConfigManager().isDefaultDeathMessageEnabled()){
            plugin.sendColoredMessageToConsole(event.getDeathMessage());
            event.setDeathMessage(null);
        }

        // Ignored
        if (plugin.getConfigManager().isIgnoredEnabled()){
            if (isIgnoredCause(deathCause) || isIgnoredDay(player, deathCause)){
                String deathCauseMessage = deathCause + "[" + season + "]" + "[Ignored]";
                // Init Variable if ignored day
                if (isIgnoredDay(player, deathCause)){
                    int currentDay = plugin.getDayManager().getCurrentDay(player.getWorld(), "ignored");
                    String deathCounter = plugin.getConfigManager().isIgnoredMustDifference() ? "" : "_" + plugin.getDeathDataManager().getNextDeathCounter(playerUUID, currentDay);
                    deathCauseMessage = "ignoredDay_" + currentDay + deathCounter + "(" + season + ")";
                    if (plugin.getConfigManager().isIgnoredDaySameCauseRequired()){
                        deathCauseMessage = deathCause + "[" + deathCauseMessage + "]";
                    }
                }

                boolean isNewDeathType = true;
                if (plugin.getConfigManager().isIgnoredMustDifference()){
                    isNewDeathType = plugin.getDeathDataManager().logDeath(playerUUID, deathCauseMessage);
                }
                if (isNewDeathType) {
                    String msgPlayer = plugin.getConfigManager().getNotificationPlayerIgnored()
                            .replace("{cause}",deathCauseMessage);
                    player.sendMessage(msgPlayer);
                    return;
                } else {
                    String msgPlayer = plugin.getConfigManager().getNotificationPlayerIgnoredSameWay();
                    player.sendMessage(msgPlayer);
                }
            }
        }

        // Increase
        if (plugin.getConfigManager().isIncreaseEnabled()){
            // Check if death cause is decrease or decrease day
            if (isIncreaseCause(deathCause) || isIncreaseDay(player, deathCause)){
                // Init Variables
                int increaseAmount;
                increaseAmount = plugin.getConfigManager().getIncreasePerDeath();
//                double newMaxHealth = HealthManager.getMaxHealth(player) + increaseAmount;
                String deathCauseMessage = deathCause + "[" + season + "]" + "[Increase]";

                // Init Variables if decrease day
                if (isIncreaseDay(player, deathCause)){
                    increaseAmount = plugin.getConfigManager().getIncreaseDayAmount();
//                    newMaxHealth = HealthManager.getMaxHealth(player) + increaseAmount;
                    int currentDay = plugin.getDayManager().getCurrentDay(player.getWorld(),"increase");
                    String deathCounter = plugin.getConfigManager().isIncreaseMustDifference() ? "" : "_" + plugin.getDeathDataManager().getNextDeathCounter(playerUUID, currentDay);
                    deathCauseMessage = "increaseDay_" + currentDay + deathCounter + "(" + season + ")";
                    if (plugin.getConfigManager().isIncreaseDaySameCauseRequired()){
                        deathCauseMessage = deathCause+"["+deathCauseMessage+"]";
                    }
                }

                double newMaxHealth = HealthManager.getMaxHealth(player) + increaseAmount;
                boolean isNewDeathType = true;
                if (plugin.getConfigManager().isIncreaseMustDifference()){
                    isNewDeathType = plugin.getDeathDataManager().logDeath(playerUUID, deathCauseMessage);
                }
                // Check if new death or not must difference
                if (isNewDeathType) {
                    String msgPlayer = plugin.getConfigManager().getNotificationPlayerIncrease()
                            .replace("{increase}",String.valueOf(increaseAmount))
                            .replace("{cause}",deathCauseMessage);
                    String msgServer = plugin.getConfigManager().getNotificationConsoleIncrease()
                            .replace("{name}",player.getName())
                            .replace("{increase}",String.valueOf(increaseAmount))
                            .replace("{cause}",deathCauseMessage);

                    // Check if min hp enabled and new max health is less than min hp in config
                    if (isMaxHPEnabledAndMoreThanMax(newMaxHealth)) {
                        newMaxHealth = plugin.getConfigManager().getMaxHPAmount();
                        msgPlayer = plugin.getConfigManager().getNotificationPlayerMaxHealth();
                        msgServer = plugin.getConfigManager().getNotificationConsoleMaxHealth();
                    }
                    // Set new max health from config, decrease day, or min hp to decrease
                    HealthManager.setMaxHealth(newMaxHealth, player);

                    player.sendMessage(msgPlayer);
                    plugin.sendColoredMessageToConsole(msgServer);

                    return;
                } else {
                    String msgPlayer = plugin.getConfigManager().getNotificationPlayerIncreaseSameWay();
                    player.sendMessage(msgPlayer);
                }
            }
        }

        // Decrease
        if (plugin.getConfigManager().isDecreaseEnabled()){
            // Check if death cause is decrease or decrease day
            if (isDecreaseCause(deathCause) || isDecreaseDay(player, deathCause)){
                // Init Variables
                int decreaseAmount;
                decreaseAmount = plugin.getConfigManager().getDecreasePerDeath();
//                double newMaxHealth = HealthManager.getMaxHealth(player) - decreaseAmount;
                String deathCauseMessage = deathCause + "[" + season + "]" + "[Decrease]";

                // Init Variables if decrease day
                if (isDecreaseDay(player, deathCause)){
                    decreaseAmount = plugin.getConfigManager().getDecreaseDayAmount();
//                    newMaxHealth = HealthManager.getMaxHealth(player) - decreaseAmount;
                    int currentDay = plugin.getDayManager().getCurrentDay(player.getWorld(), "decrease");
                    String deathCounter = plugin.getConfigManager().isDecreaseMustDifference() ? "" : "_" + plugin.getDeathDataManager().getNextDeathCounter(playerUUID, currentDay);
                    deathCauseMessage = "decreaseDay_" + currentDay + deathCounter + "(" + season + ")";
                    if (plugin.getConfigManager().isDecreaseDaySameCauseRequired()){
                        deathCauseMessage = deathCause+"["+deathCauseMessage+"]";
                    }
                }

                double newMaxHealth = HealthManager.getMaxHealth(player) - decreaseAmount;
                boolean isNewDeathType = true;
                if (plugin.getConfigManager().isDecreaseMustDifference()) {
                    isNewDeathType = plugin.getDeathDataManager().logDeath(playerUUID, deathCauseMessage);
                }
                // Check if new death or not must difference
                if (isNewDeathType) {
                    String msgPlayer = plugin.getConfigManager().getNotificationPlayerDecrease()
                            .replace("{decrease}",String.valueOf(decreaseAmount))
                            .replace("{cause}",deathCauseMessage);
                    String msgServer = plugin.getConfigManager().getNotificationConsoleDecrease()
                            .replace("{name}",player.getName())
                            .replace("{decrease}",String.valueOf(decreaseAmount))
                            .replace("{cause}",deathCauseMessage);

                    // Check if min hp enabled and new max health is less than min hp in config
                    if (isMinHPEnabledAndLessThanMin(newMaxHealth)) {
                        newMaxHealth = plugin.getConfigManager().getMinHPAmount();
                        msgPlayer = plugin.getConfigManager().getNotificationPlayerMinHealth();
                        msgServer = plugin.getConfigManager().getNotificationConsoleMinHealth();
                    } else if (isMinHPDisabledAndSameOrLessThanZero(newMaxHealth)) { // Check if min hp is disabled and new max health is 0 or less
                        newMaxHealth = 1;
                        HealthManager.setMaxHealth(newMaxHealth, player);
                        // Check if ban time is 0
                        if (plugin.getConfigManager().getMinHPBanTime() == 0) {
                            plugin.getBanManager().banPlayerPermanently(player);
                        } else {
                            plugin.getBanManager().banPlayer(player, (long) plugin.getConfigManager().getMinHPBanTime() * 60 * 60 * 1000);
                        }
                        // return to skip another operation
                        return;
                    }
                    // Set new max health from config, decrease day, or min hp to decrease
                    HealthManager.setMaxHealth(newMaxHealth, player);

                    player.sendMessage(msgPlayer);
                    plugin.sendColoredMessageToConsole(msgServer);
                }
            }
        }
    }

    // Check if max hp is enabled and more than max config
    private boolean isMaxHPEnabledAndMoreThanMax(double newMaxHealth) {
        return (plugin.getConfigManager().isMaxHPEnabled() && newMaxHealth > plugin.getConfigManager().getMaxHPAmount());
    }
    // Check if min hp is enabled and less than min config
    private boolean isMinHPEnabledAndLessThanMin(double newMaxHealth) {
        return (plugin.getConfigManager().isMinHPEnabled() && newMaxHealth < plugin.getConfigManager().getMinHPAmount());
    }
    // Check if min hp is disabled and less or same than 0
    private boolean isMinHPDisabledAndSameOrLessThanZero(double newMaxHealth) {
        return (!plugin.getConfigManager().isMinHPEnabled() && newMaxHealth <= 0);
    }

    // Check if death cause is ignored
    private boolean isIgnoredCause(String deathCause) {
        return (plugin.getConfigManager().getIgnoredCause().contains("all") || plugin.getConfigManager().getIgnoredCause().contains(deathCause));
    }
    public boolean isIgnoredDay(Player player, String deathCause) {
        return ((plugin.getConfigManager().isIgnoredDayEnabled() && plugin.getDayManager().isMultipleDay(player, "ignored") && !plugin.getConfigManager().isIgnoredDaySameCauseRequired()) || (plugin.getConfigManager().isIgnoredDayEnabled() && plugin.getConfigManager().isIgnoredDaySameCauseRequired() && plugin.getDayManager().isMultipleDay(player, "ignored") && isIgnoredCause(deathCause)));
    }

    // Check if death cause is increase
    private boolean isIncreaseCause(String deathCause) {
        return (plugin.getConfigManager().getIncreaseCause().contains("all") || plugin.getConfigManager().getIncreaseCause().contains(deathCause));
    }
    // Check if increase day
    private boolean isIncreaseDay(Player player, String deathCause) {
        return ((plugin.getConfigManager().isIncreaseDayEnabled() && plugin.getDayManager().isMultipleDay(player, "increase") && !plugin.getConfigManager().isIncreaseDaySameCauseRequired()) || (plugin.getConfigManager().isIncreaseDayEnabled() && plugin.getConfigManager().isIncreaseDaySameCauseRequired() && plugin.getDayManager().isMultipleDay(player, "increase") && isIncreaseCause(deathCause)));
    }

    // Check if death cause is decrease
    private boolean isDecreaseCause(String deathCause) {
        return (plugin.getConfigManager().getDecreaseCause().contains("all") || plugin.getConfigManager().getDecreaseCause().contains(deathCause));
    }
    // Check if decrease day
    private boolean isDecreaseDay(Player player, String deathCause) {
        return ((plugin.getConfigManager().isDecreaseDayEnabled() && plugin.getDayManager().isMultipleDay(player, "decrease") && !plugin.getConfigManager().isDecreaseDaySameCauseRequired()) || (plugin.getConfigManager().isDecreaseDayEnabled() && plugin.getConfigManager().isDecreaseDaySameCauseRequired() && plugin.getDayManager().isMultipleDay(player, "decrease") && isDecreaseCause(deathCause)));
    }
}
