package io.github.b4n9z.deathPulse.Listeners;

import io.github.b4n9z.deathPulse.DeathPulse;
import io.github.b4n9z.deathPulse.Managers.HealthManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

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
        World world = player.getWorld();
        UUID playerUUID = player.getUniqueId();

        String deathCause = Objects.requireNonNull(player.getLastDamageCause()).getCause().name();

        String season = "Season"+plugin.getDayManager().getSeason(world,"season");

        if (killer != null){
            deathCause = switch (deathCause) {
                case "ENTITY_ATTACK" -> "PLAYER_ATTACK";
                case "ENTITY_SWEEP_ATTACK" -> "PLAYER_SWEEP_ATTACK";
                case "PROJECTILE" -> "PLAYER_PROJECTILE";
                case "ENTITY_EXPLOSION" -> "PLAYER_EXPLOSION";
                default -> deathCause;
            };
        }

        // Disable death message
        if (!plugin.getConfigManager().isDefaultDeathMessageEnabled()){
            plugin.sendColoredMessageToConsole(event.getDeathMessage());
            event.setDeathMessage(null);
        }

        boolean status = false;

        for (String deathType: plugin.getConfigManager().getPriority()) {
            if (deathType.equalsIgnoreCase("IGNORE")) {
                status = runIgnored(player, playerUUID, world, season, deathCause);
            } else if (deathType.equalsIgnoreCase("INCREASE")) {
                status = runIncrease(player, playerUUID, world, season, deathCause);
            } else if (deathType.equalsIgnoreCase("DECREASE")) {
                status = runDecrease(player, playerUUID, world, season, deathCause);
            }

            if (status) break;
        }
    }

    // Check if ignored day
    public boolean isIgnoredDay(World world, String deathCause) {
        return (plugin.getConfigManager().isIgnoredDayEnabled() && plugin.getDayManager().isMultipleDay(world, "ignored") && (plugin.getConfigManager().getIgnoredDayCause().contains("ALL") || plugin.getConfigManager().getIgnoredDayCause().contains(deathCause)) && !plugin.getConfigManager().getIgnoredDayCauseExclude().contains(deathCause));
    }

    // Check if increase day
    private boolean isIncreaseDay(World world, String deathCause) {
        return (plugin.getConfigManager().isIncreaseDayEnabled() && plugin.getDayManager().isMultipleDay(world, "increase") && (plugin.getConfigManager().isIncreaseDayCauseValid("ALL") || plugin.getConfigManager().isIncreaseDayCauseValid(deathCause)) && !plugin.getConfigManager().getIncreaseDayCauseExclude().contains(deathCause));
    }

    // Check if decrease day
    private boolean isDecreaseDay(World world, String deathCause) {
        return (plugin.getConfigManager().isDecreaseDayEnabled() && plugin.getDayManager().isMultipleDay(world, "decrease") && (plugin.getConfigManager().isDecreaseDayCauseValid("ALL") || plugin.getConfigManager().isDecreaseDayCauseValid(deathCause)) && !plugin.getConfigManager().getDecreaseDayCauseExclude().contains(deathCause));
    }

    private boolean runIgnored(Player player, UUID playerUUID, World world, String season, String deathCause) {
        if (plugin.getDayManager().isMultipleDay(world, "increase") && plugin.getConfigManager().isIncreaseDayDeActiveIgnoredCause()) {
            return false;
        }
        if (plugin.getDayManager().isMultipleDay(world, "decrease") && plugin.getConfigManager().isDecreaseDayDeActiveIgnoredCause()) {
            return false;
        }
        if (plugin.getConfigManager().isIgnoredCause(deathCause) || isIgnoredDay(world, deathCause)){
            String deathCauseMessage = deathCause + "[" + season + "]" + "[Ignored]";
            boolean isIgnoredDay = false;
            // Init Variable if ignored day
            if (isIgnoredDay(world, deathCause)){
                if (plugin.getDayManager().isMultipleDay(world, "increase") && plugin.getConfigManager().isIncreaseDayDeActiveIgnoredDay()) {
                    return false;
                }
                if (plugin.getDayManager().isMultipleDay(world, "decrease") && plugin.getConfigManager().isDecreaseDayDeActiveIgnoredDay()) {
                    return false;
                }
                isIgnoredDay = true;
                int currentDay = plugin.getDayManager().getCurrentDay(world, plugin.getDayManager().getTypeDays("ignored"));
                String deathCounter = plugin.getConfigManager().isIgnoredDayMustDifference() ? "" : "_" + plugin.getDeathDataManager().getNextDeathCounter(playerUUID, currentDay);
                deathCauseMessage = "ignoredDay_" + currentDay + deathCounter + "(" + season + ")";
                deathCauseMessage = deathCause + "[" + deathCauseMessage + "]";
            }

            boolean isNewDeathType = true;
            if (isIgnoredDay) {
                if (plugin.getConfigManager().isIgnoredDayMustDifference()) isNewDeathType = plugin.getDeathDataManager().logDeath(playerUUID, deathCauseMessage);
            } else {
                if (plugin.getConfigManager().isIgnoredMustDifference()) isNewDeathType = plugin.getDeathDataManager().logDeath(playerUUID, deathCauseMessage);
            }
            if (isNewDeathType) {
                String msgPlayer = plugin.getConfigManager().getNotificationPlayerIgnored()
                        .replace("{cause}",deathCauseMessage);
                player.sendMessage(msgPlayer);
                return true;
            } else {
                String msgPlayer = plugin.getConfigManager().getNotificationPlayerIgnoredSameWay();
                player.sendMessage(msgPlayer);
                return false;
            }
        }
        return false;
    }

    private boolean runIncrease(Player player, UUID playerUUID, World world, String season, String deathCause) {
        if (plugin.getDayManager().isMultipleDay(world, "ignored") && plugin.getConfigManager().isIgnoredDayDeActiveIncreaseCause()) {
            return false;
        }
        if (plugin.getDayManager().isMultipleDay(world, "decrease") && plugin.getConfigManager().isDecreaseDayDeActiveIncreaseCause()) {
            return false;
        }
        if (plugin.getConfigManager().isIncreaseCause(deathCause) || isIncreaseDay(world, deathCause)){
            // Init Variables
            int increaseAmount;
            if (plugin.getConfigManager().isIncreaseCauseValid(deathCause)) {
                increaseAmount = plugin.getConfigManager().getIncreaseCauseAmount(deathCause);
            } else {
                increaseAmount = plugin.getConfigManager().getIncreaseCauseAmount("ALL");
            }
            String deathCauseMessage = deathCause + "[" + season + "]" + "[Increase]";
            boolean isIncreaseDay = false;

            // Init Variables if decrease day
            if (isIncreaseDay(world, deathCause)){
                if (plugin.getDayManager().isMultipleDay(world, "ignored") && plugin.getConfigManager().isIgnoredDayDeActiveIncreaseDay()) {
                    return false;
                }
                if (plugin.getDayManager().isMultipleDay(world, "decrease") && plugin.getConfigManager().isDecreaseDayDeActiveIncreaseDay()) {
                    return false;
                }
                isIncreaseDay = true;
                if (plugin.getConfigManager().isIncreaseDayCauseValid(deathCause)) {
                    increaseAmount = plugin.getConfigManager().getIncreaseDayCauseAmount(deathCause);
                } else {
                    increaseAmount = plugin.getConfigManager().getIncreaseDayCauseAmount("ALL");
                }
                int currentDay = plugin.getDayManager().getCurrentDay(world, plugin.getDayManager().getTypeDays("increase"));
                String deathCounter = plugin.getConfigManager().isIncreaseDayMustDifference() ? "" : "_" + plugin.getDeathDataManager().getNextDeathCounter(playerUUID, currentDay);
                deathCauseMessage = "increaseDay_" + currentDay + deathCounter + "(" + season + ")";
                deathCauseMessage = deathCause+"["+deathCauseMessage+"]";
            }

            double newMaxHealth = HealthManager.getMaxHealth(player) + increaseAmount;
            boolean isNewDeathType = true;
            if (isIncreaseDay) {
                if (plugin.getConfigManager().isIncreaseDayMustDifference()) isNewDeathType = plugin.getDeathDataManager().logDeath(playerUUID, deathCauseMessage);
            } else {
                if (plugin.getConfigManager().isIncreaseMustDifference()) isNewDeathType = plugin.getDeathDataManager().logDeath(playerUUID, deathCauseMessage);
            }
            // Check if new death or not must difference
            if (isNewDeathType) {
                if (plugin.getConfigManager().isDecreaseDebt()) {
                    if (plugin.getDebtDataManager().getDebt(playerUUID) > 0) {
                        int leftOver = plugin.getDebtDataManager().reduceDebt(playerUUID, increaseAmount);
                        newMaxHealth = HealthManager.getMaxHealth(player) + leftOver;

                        if (leftOver > 0 || plugin.getDebtDataManager().getDebt(playerUUID) <= 0) {
                            player.sendMessage(plugin.getConfigManager().getNotificationPlayerDebtPaidOff()
                                    .replace("{debtPaid}", (increaseAmount - leftOver) + ""));
                            increaseAmount = leftOver;
                        } else {
                            player.sendMessage(plugin.getConfigManager().getNotificationPlayerDebtReduced()
                                    .replace("{debtPaid}", increaseAmount + "")
                                    .replace("{debtLeft}", plugin.getDebtDataManager().getDebt(playerUUID) + ""));
                            increaseAmount = 0;
                        }
                    }
                }

                String msgPlayer = plugin.getConfigManager().getNotificationPlayerIncrease()
                        .replace("{increase}",String.valueOf(increaseAmount))
                        .replace("{cause}",deathCauseMessage);
                String msgServer = plugin.getConfigManager().getNotificationConsoleIncrease()
                        .replace("{name}",player.getName())
                        .replace("{increase}",String.valueOf(increaseAmount))
                        .replace("{cause}",deathCauseMessage);

                // Check if min hp enabled and new max health is less than min hp in config
                if (plugin.getConfigManager().isMaxHPEnabledAndMoreThanMax(newMaxHealth)) {
                    double excessHealth = newMaxHealth - plugin.getConfigManager().getMaxHPAmount();
                    int healthItemsToDrop = (int) Math.floor(excessHealth / plugin.getConfigManager().getHealthItemHealthPerItem());

                    if (healthItemsToDrop > 0 && plugin.getConfigManager().isDropHealthItem()) {
                        ItemStack healthItem = plugin.getHealthItemManager().getHealthItem();
                        healthItem.setAmount(healthItemsToDrop);
                        player.getWorld().dropItemNaturally(player.getLocation(), healthItem);
                    }

                    newMaxHealth = plugin.getConfigManager().getMaxHPAmount();
                    msgPlayer = plugin.getConfigManager().getNotificationPlayerMaxHealth();
                    msgServer = plugin.getConfigManager().getNotificationConsoleMaxHealth();
                }
                // Set new max health from config, decrease day, or min hp to decrease
                HealthManager.setMaxHealth(newMaxHealth, player);

                player.sendMessage(msgPlayer);
                plugin.sendColoredMessageToConsole(msgServer);

                return true;
            } else {
                String msgPlayer = plugin.getConfigManager().getNotificationPlayerIncreaseSameWay();
                player.sendMessage(msgPlayer);
                return false;
            }
        }
        return false;
    }

    private boolean runDecrease(Player player, UUID playerUUID, World world, String season, String deathCause) {
        if (plugin.getDayManager().isMultipleDay(world, "ignored") && plugin.getConfigManager().isIgnoredDayDeActiveDecreaseCause()) {
            return false;
        }
        if (plugin.getDayManager().isMultipleDay(world, "increase") && plugin.getConfigManager().isIncreaseDayDeActiveDecreaseCause()) {
            return false;
        }
        if (plugin.getConfigManager().isDecreaseCause(deathCause) || isDecreaseDay(world, deathCause)){
            // Init Variables
            int decreaseAmount;
            if (plugin.getConfigManager().isDecreaseCauseValid(deathCause)) {
                decreaseAmount = plugin.getConfigManager().getDecreaseCauseAmount(deathCause);
            } else {
                decreaseAmount = plugin.getConfigManager().getDecreaseCauseAmount("ALL");
            }
            String deathCauseMessage = deathCause + "[" + season + "]" + "[Decrease]";
            boolean isDecreaseDay = false;

            // Init Variables if decrease day
            if (isDecreaseDay(world, deathCause)){
                if (plugin.getDayManager().isMultipleDay(world, "ignored") && plugin.getConfigManager().isIgnoredDayDeActiveDecreaseDay()) {
                    return false;
                }
                if (plugin.getDayManager().isMultipleDay(world, "increase") && plugin.getConfigManager().isIncreaseDayDeActiveDecreaseDay()) {
                    return false;
                }
                isDecreaseDay = true;
                if (plugin.getConfigManager().isDecreaseDayCauseValid(deathCause)) {
                    decreaseAmount = plugin.getConfigManager().getDecreaseDayCauseAmount(deathCause);
                } else {
                    decreaseAmount = plugin.getConfigManager().getDecreaseDayCauseAmount("ALL");
                }
                int currentDay = plugin.getDayManager().getCurrentDay(world, plugin.getDayManager().getTypeDays("decrease"));
                String deathCounter = plugin.getConfigManager().isDecreaseDayMustDifference() ? "" : "_" + plugin.getDeathDataManager().getNextDeathCounter(playerUUID, currentDay);
                deathCauseMessage = "decreaseDay_" + currentDay + deathCounter + "(" + season + ")";
                deathCauseMessage = deathCause+"["+deathCauseMessage+"]";
            }

            double newMaxHealth = HealthManager.getMaxHealth(player) - decreaseAmount;
            boolean isNewDeathType = true;
            if (isDecreaseDay) {
                if (plugin.getConfigManager().isDecreaseDayMustDifference()) isNewDeathType = plugin.getDeathDataManager().logDeath(playerUUID, deathCauseMessage);
            } else {
                if (plugin.getConfigManager().isDecreaseMustDifference()) isNewDeathType = plugin.getDeathDataManager().logDeath(playerUUID, deathCauseMessage);
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

                int debt = 0;
                // Check if min hp enabled and new max health is less than min hp in config
                if (plugin.getConfigManager().isMinHPEnabledAndLessThanMin(newMaxHealth)) {
                    if (plugin.getConfigManager().isDecreaseDebt()){
                        if (newMaxHealth < 0) {
                            debt = (int) ((-newMaxHealth) + plugin.getConfigManager().getMinHPAmount());
                        }
                        if (newMaxHealth >= 0) {
                            debt = (int) (plugin.getConfigManager().getMinHPAmount() - newMaxHealth);
                        }
                        if (debt > 0) {
                            player.sendMessage(plugin.getConfigManager().getNotificationPlayerDebtAdded()
                                    .replace("{debtAdded}", debt + "")
                                    .replace("{debtLeft}", plugin.getDebtDataManager().getDebt(playerUUID) + ""));
                        }
                        plugin.getDebtDataManager().addDebt(playerUUID, debt);
                    }
                    newMaxHealth = plugin.getConfigManager().getMinHPAmount();
                    msgPlayer = plugin.getConfigManager().getNotificationPlayerMinHealth();
                    msgServer = plugin.getConfigManager().getNotificationConsoleMinHealth();
                } else if (plugin.getConfigManager().isMinHPDisabledAndSameOrLessThanZero(newMaxHealth)) { // Check if min hp is disabled and new max health is 0 or less
                    if (plugin.getConfigManager().isDecreaseDebt()){
                        debt = (int) ((-newMaxHealth) + plugin.getConfigManager().getHPAfterBan());
                        player.sendMessage(plugin.getConfigManager().getNotificationPlayerDebtAdded()
                                .replace("{debtAdded}", debt + "")
                                .replace("{debtLeft}", plugin.getDebtDataManager().getDebt(playerUUID) + ""));
                        plugin.getDebtDataManager().addDebt(playerUUID, debt);
                    }
                    newMaxHealth = plugin.getConfigManager().getHPAfterBan();
                    HealthManager.setMaxHealth(newMaxHealth, player);
                    // Check if ban time is 0
                    if (plugin.getConfigManager().getMinHPBanTime() == 0) {
                        plugin.getBanManager().banPlayerPermanently(player);
                    } else {
                        plugin.getBanManager().banPlayer(player, (long) plugin.getConfigManager().getMinHPBanTime() * 60 * 60 * 1000);
                    }
                    // return to skip another operation
                    return true;
                }
                // Set new max health from config, decrease day, or min hp to decrease
                HealthManager.setMaxHealth(newMaxHealth, player);

                player.sendMessage(msgPlayer);
                plugin.sendColoredMessageToConsole(msgServer);

                return true;
            } else {
                String msgPlayer = plugin.getConfigManager().getNotificationPlayerDecreaseSameWay();
                player.sendMessage(msgPlayer);
                return false;
            }
        }
        return false;
    }
}
