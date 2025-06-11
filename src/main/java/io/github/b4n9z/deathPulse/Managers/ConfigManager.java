package io.github.b4n9z.deathPulse.Managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ConfigManager {
    private final Plugin plugin;
    // First time Setup
    private int firstTimeSetup;
    // HP settings
    private int hpStart;
    private boolean hpMaxEnabled;
    private int hpMaxAmount;
    private boolean hpMinEnabled;
    private int hpMinAmount;
    private int hpMinBanTime;

    // Ignored settings
    private boolean ignoredEnabled;
    private boolean ignoredDayEnabled;
    private boolean ignoredDaySameCauseRequired;
    private String ignoredDayType;
    private List<Integer> ignoredDays;
    private boolean ignoredMustDifference;
    private List<String> ignoredCause;

    // Increase settings
    private boolean increaseEnabled;
    private int increasePerDeath;
    private boolean increaseDayEnabled;
    private boolean increaseDaySameCauseRequired;
    private String increaseDayType;
    private List<Integer> increaseDays;
    private int increaseDayAmount;
    private boolean increaseMustDifference;
    private List<String> increaseCause;

    // Decrease settings
    private boolean decreaseEnabled;
    private int decreasePerDeath;
    private boolean decreaseDayEnabled;
    private boolean decreaseDaySameCauseRequired;
    private String decreaseDayType;
    private List<Integer> decreaseDays;
    private int decreaseDayAmount;
    private boolean decreaseMustDifference;
    private List<String> decreaseCause;

    // Season settings
    private boolean seasonEnabled;
    private String seasonType;
    private int seasonDay;

    // Permission settings
    private boolean permissionAllPlayerReload;
    private boolean permissionAllPlayerSetMaxHealth;
    private boolean permissionAllPlayerViewHealth;
    private boolean permissionAllPlayerViewDeathData;
    private boolean permissionAllPlayerResetHealth;
    private boolean permissionAllPlayerMatchHealth;
    private boolean permissionAllPlayerRemoveDeathData;
    private boolean permissionAllPlayerTransferHealth;
    private boolean permissionAllPlayerHelp;

    // Notification
    private boolean defaultDeathMessageEnabled;

    // Notification Player settings
    private String notificationPlayerMaxHealth;
    private String notificationPlayerMinHealth;
    private String notificationPlayerBanReason;
    private String notificationPlayerKicked;

    private String notificationPlayerIgnored;
    private String notificationPlayerIgnoredSameWay;

    private String notificationPlayerIncrease;
    private String notificationPlayerIncreaseSameWay;

    private String notificationPlayerDecrease;

    // Warning messages
    private String ignoredDayWarning;
    private String increaseDayWarning;
    private String decreaseDayWarning;
    private String seasonChangeWarning;

    // Notification Log Server settings
    private String notificationLogServerMaxHealth;
    private String notificationLogServerMinHealth;

    private String notificationLogServerIncrease;
    private String notificationLogServerDecrease;
    private String notificationLogServerBanReason;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        try {
            this.firstTimeSetup = plugin.getConfig().getInt("firstTimeSetup",0);
            // HP settings
            this.hpStart = plugin.getConfig().getInt("HP.Start", 20);
            this.hpMaxEnabled = plugin.getConfig().getBoolean("HP.MaxHP.enabled", false);
            this.hpMaxAmount = plugin.getConfig().getInt("HP.MaxHP.amount", 140);
            this.hpMinEnabled = plugin.getConfig().getBoolean("HP.MinHP.enabled", false);
            this.hpMinAmount = plugin.getConfig().getInt("HP.MinHP.amount", 2);
            this.hpMinBanTime = plugin.getConfig().getInt("HP.MinHP.banTime", 24);

            // Ignored settings
            this.ignoredEnabled = plugin.getConfig().getBoolean("ignore.enabled", false);
            this.ignoredDayEnabled = plugin.getConfig().getBoolean("ignore.day.enabled", false);
            this.ignoredDaySameCauseRequired = plugin.getConfig().getBoolean("ignore.day.same_cause_required", false);
            this.ignoredDayType = plugin.getConfig().getString("ignore.day.type", "minecraft");
            this.ignoredDays = plugin.getConfig().getIntegerList("ignore.day.days");
            this.ignoredMustDifference = plugin.getConfig().getBoolean("ignore.must_difference", false);
            this.ignoredCause = plugin.getConfig().getStringList("ignore.cause");

            // Increase settings
            this.increaseEnabled = plugin.getConfig().getBoolean("increase.enabled", true);
            this.increasePerDeath = plugin.getConfig().getInt("increase.per_death", 2);
            this.increaseDayEnabled = plugin.getConfig().getBoolean("increase.day.enabled", false);
            this.increaseDaySameCauseRequired = plugin.getConfig().getBoolean("increase.day.same_cause_required", false);
            this.increaseDayType = plugin.getConfig().getString("increase.day.type", "minecraft");
            this.increaseDays = plugin.getConfig().getIntegerList("increase.day.days");
            this.increaseDayAmount = plugin.getConfig().getInt("increase.day.amount", 10);
            this.increaseMustDifference = plugin.getConfig().getBoolean("increase.must_difference", true);
            this.increaseCause = plugin.getConfig().getStringList("increase.cause");

            // Decrease settings
            this.decreaseEnabled = plugin.getConfig().getBoolean("decrease.enabled", false);
            this.decreasePerDeath = plugin.getConfig().getInt("decrease.per_death", 2);
            this.decreaseDayEnabled = plugin.getConfig().getBoolean("decrease.day.enabled", false);
            this.decreaseDaySameCauseRequired = plugin.getConfig().getBoolean("decrease.day.same_cause_required", false);
            this.decreaseDayType = plugin.getConfig().getString("decrease.day.type", "minecraft");
            this.decreaseDays = plugin.getConfig().getIntegerList("decrease.day.days");
            this.decreaseDayAmount = plugin.getConfig().getInt("decrease.day.amount", 10);
            this.decreaseMustDifference = plugin.getConfig().getBoolean("decrease.must_difference", false);
            this.decreaseCause = plugin.getConfig().getStringList("decrease.cause");

            // Season settings
            this.seasonEnabled = plugin.getConfig().getBoolean("season.enabled", false);
            this.seasonType = plugin.getConfig().getString("season.type", "real");
            this.seasonDay = plugin.getConfig().getInt("season.day", 30);

            // Permission settings
            this.permissionAllPlayerReload = plugin.getConfig().getBoolean("permissionsAllPlayer.reload", false);
            this.permissionAllPlayerSetMaxHealth = plugin.getConfig().getBoolean("permissionsAllPlayer.setMaxHealth", false);
            this.permissionAllPlayerViewHealth = plugin.getConfig().getBoolean("permissionsAllPlayer.viewHealth", false);
            this.permissionAllPlayerViewDeathData = plugin.getConfig().getBoolean("permissionsAllPlayer.viewDeathData", true);
            this.permissionAllPlayerResetHealth = plugin.getConfig().getBoolean("permissionsAllPlayer.resetHealth", false);
            this.permissionAllPlayerMatchHealth = plugin.getConfig().getBoolean("permissionsAllPlayer.matchHealth", false);
            this.permissionAllPlayerRemoveDeathData = plugin.getConfig().getBoolean("permissionsAllPlayer.removeDeathData", false);
            this.permissionAllPlayerTransferHealth = plugin.getConfig().getBoolean("permissionsAllPlayer.transferHealth", false);
            this.permissionAllPlayerHelp = plugin.getConfig().getBoolean("permissionsAllPlayer.help", true);

            // Notification
            this.defaultDeathMessageEnabled = plugin.getConfig().getBoolean("notifications.defaultDeathMessage", false);

            // Notification Player settings
            this.notificationPlayerMaxHealth = Objects.requireNonNull(plugin.getConfig().getString("notifications.player.maxHealth")).replace("&", "§");
            this.notificationPlayerMinHealth = Objects.requireNonNull(plugin.getConfig().getString("notifications.player.minHealth")).replace("&", "§");
            this.notificationPlayerBanReason = Objects.requireNonNull(plugin.getConfig().getString("notifications.player.banReason")).replace("&", "§");
            this.notificationPlayerKicked = Objects.requireNonNull(plugin.getConfig().getString("notifications.player.kicked")).replace("&", "§");

            this.notificationPlayerIgnored = Objects.requireNonNull(plugin.getConfig().getString("notifications.player.ignored")).replace("&", "§");
            this.notificationPlayerIgnoredSameWay = Objects.requireNonNull(plugin.getConfig().getString("notifications.player.ignoredSameWay")).replace("&", "§");

            this.notificationPlayerIncrease = Objects.requireNonNull(plugin.getConfig().getString("notifications.player.increased")).replace("&", "§");
            this.notificationPlayerIncreaseSameWay = Objects.requireNonNull(plugin.getConfig().getString("notifications.player.increaseSameWay")).replace("&", "§");

            this.notificationPlayerDecrease = Objects.requireNonNull(plugin.getConfig().getString("notifications.player.decreased")).replace("&", "§");

            // Warning messages
            this.ignoredDayWarning = Objects.requireNonNull(plugin.getConfig().getString("notifications.player.warning.ignoredDay")).replace("&", "§");
            this.increaseDayWarning = Objects.requireNonNull(plugin.getConfig().getString("notifications.player.warning.increaseDay")).replace("&", "§");
            this.decreaseDayWarning = Objects.requireNonNull(plugin.getConfig().getString("notifications.player.warning.decreaseDay")).replace("&", "§");
            this.seasonChangeWarning = Objects.requireNonNull(plugin.getConfig().getString("notifications.player.warning.seasonChange")).replace("&", "§");

            // Notification Log Server settings
            this.notificationLogServerMaxHealth = Objects.requireNonNull(plugin.getConfig().getString("notifications.logServer.maxHealth")).replace("&", "§");
            this.notificationLogServerMinHealth = Objects.requireNonNull(plugin.getConfig().getString("notifications.logServer.minHealth")).replace("&", "§");

            this.notificationLogServerIncrease = Objects.requireNonNull(plugin.getConfig().getString("notifications.logServer.increased")).replace("&", "§");
            this.notificationLogServerDecrease = Objects.requireNonNull(plugin.getConfig().getString("notifications.logServer.decreased")).replace("&", "§");
            this.notificationLogServerBanReason = Objects.requireNonNull(plugin.getConfig().getString("notifications.logServer.banReason")).replace("&", "§");

            validateConfig();
        } catch (Exception e) {
            Bukkit.getServer().getConsoleSender().sendMessage("§cFailed to load config:§a " + e.getMessage());
        }
    }

    private void validateConfig() throws Exception {
        if (hpStart <= 0) throw new Exception("HP.Start should be greater than 0:§e " + hpStart);
        if (hpMaxEnabled && hpMaxAmount <= 0) throw new Exception("HP.MaxHP.amount should be greater than 0:§e " + hpMaxAmount);
        if (hpMinEnabled && hpMinAmount <= 0) throw new Exception("HP.MinHP.amount should be greater than 0:§e " + hpMinAmount);
        if (hpStart < hpMinAmount || hpStart > hpMaxAmount) throw new Exception("HP.Start should be between HP.MinHP.amount and HP.MaxHP.amount:§e " + hpStart);
        if (!hpMinEnabled && hpMinBanTime <= 0) throw new Exception("HP.MinHP.banTime should be greater than 0:§e " + hpMinBanTime);

        if (!ignoredEnabled && ignoredDayEnabled) throw new Exception("ignore.enabled should be true when ignore.day.enabled is true:§e " + false);
        if (!ignoredDayEnabled && ignoredDaySameCauseRequired) throw new Exception("ignore.day.enabled should be true when ignore.day.same_cause_required is true:§e " + false);
        if (ignoredDayEnabled && !ignoredDayType.equals("real") && !ignoredDayType.equals("minecraft")) throw new Exception("ignore.day.type should be real or minecraft:§e " + ignoredDayType);
        if (ignoredDayEnabled && ignoredDays.isEmpty()) throw new Exception("ignore.day.days should not be empty.");
        if (!ignoredEnabled && ignoredMustDifference) throw new Exception("ignore.enabled should be true when ignore.must_difference is true:§e " + false);
        if (ignoredEnabled && ignoredCause.isEmpty()) throw new Exception("ignore.cause should not be empty.");

        if (increaseEnabled && increasePerDeath <= 0) throw new Exception("increase.per_death should be greater than 0:§e " + increasePerDeath);
        if (!increaseEnabled && increaseDayEnabled) throw new Exception("increase.enabled should be true when increase.day.enabled is true:§e " + false);
        if (!increaseDayEnabled && increaseDaySameCauseRequired) throw new Exception("increase.day.enabled should be true when increase.day.same_cause_required is true:§e " + false);
        if (increaseDayEnabled && !increaseDayType.equals("real") && !increaseDayType.equals("minecraft")) throw new Exception("increase.day.type should be real or minecraft:§e " + increaseDayType);
        if (increaseDayEnabled && increaseDays.isEmpty()) throw new Exception("increase.day.days should not be empty.");
        if (increaseDayEnabled && increaseDayAmount <= increasePerDeath) throw new Exception("increase.day.amount should be greater than increase.per_death:§e " + increaseDayAmount);
        if (!increaseEnabled && increaseMustDifference) throw new Exception("increase.enabled should be true when increase.must_difference is true:§e " + false);
        if (increaseEnabled && increaseCause.isEmpty()) throw new Exception("increase.cause should not be empty.");

        if (decreaseEnabled && decreasePerDeath < 0) throw new Exception("decrease.per_death should be greater than or equal to 0:§e " + decreasePerDeath);
        if (!decreaseEnabled && decreaseDayEnabled) throw new Exception("decrease.enabled should be true when decrease.day.enabled is true:§e " + false);
        if (!decreaseDayEnabled && decreaseDaySameCauseRequired) throw new Exception("decrease.day.enabled should be true when decrease.day.same_cause_required is true:§e " + false);
        if (decreaseDayEnabled && !decreaseDayType.equals("real") && !decreaseDayType.equals("minecraft")) throw new Exception("decrease.day.type should be real or minecraft:§e " + decreaseDayType);
        if (decreaseDayEnabled && decreaseDays.isEmpty()) throw new Exception("decrease.day.days should not be empty.");
        if (decreaseDayEnabled && decreaseDayAmount <= decreasePerDeath) throw new Exception("decrease.day.amount should be greater than decrease.per_death:§e " + decreaseDayAmount);
        if (!decreaseEnabled && decreaseMustDifference) throw new Exception("decrease.enabled should be true when decrease.must_difference is true:§e " + false);
        if (decreaseEnabled && decreaseCause.isEmpty()) throw new Exception("decrease.cause should not be empty.");

        if (ignoredEnabled && !ignoredMustDifference) {
            Set<String> intersectionIgnore = new HashSet<>(ignoredCause);
            intersectionIgnore.retainAll(ignoredCause);
            intersectionIgnore.retainAll(increaseCause);
            if (!intersectionIgnore.isEmpty()) {
                throw new Exception("ignore.cause list should not have common elements with increase.cause list and decrease.cause list when ignore.must_difference is false:§e " + String.join(", ", intersectionIgnore));
            }
        }

        if (increaseEnabled && !increaseMustDifference) {
            Set<String> intersectionIncrease = new HashSet<>(increaseCause);
            intersectionIncrease.retainAll(decreaseCause);
            if (!intersectionIncrease.isEmpty()) {
                throw new Exception("increase.cause list should not have common elements with decrease.cause list:§e " + String.join(", ", intersectionIncrease));
            }
        }

        if (ignoredDayEnabled || increaseDayEnabled || decreaseDayEnabled) {
            Set<Integer> intersectionIgnoredDays = new HashSet<>(ignoredDays);
            intersectionIgnoredDays.retainAll(increaseDays);
            intersectionIgnoredDays.retainAll(decreaseDays);
            if (!intersectionIgnoredDays.isEmpty()) {
                throw new Exception("ignored.day.days list should not have common elements with increase.day.days list and decrease.day.days list:§e " + String.join(", ", intersectionIgnoredDays.stream().map(String::valueOf).toArray(String[]::new)));
            }
        }
    }

    public int getFirstTimeSetup() {
        return firstTimeSetup;
    }

    public void setFirstTimeSetup(int firstTimeSetup) {
        plugin.getConfig().set("firstTimeSetup", firstTimeSetup);
        this.firstTimeSetup = firstTimeSetup;
        plugin.saveConfig();
        plugin.reloadConfig();
    }

    public int getHPStart() {
        return hpStart;
    }
    public boolean isMaxHPEnabled() {
        return hpMaxEnabled;
    }
    public int getMaxHPAmount() {
        return hpMaxAmount;
    }
    public boolean isMinHPEnabled() {
        return hpMinEnabled;
    }
    public int getMinHPAmount() {
        return hpMinAmount;
    }
    public int getMinHPBanTime() {
        return hpMinBanTime;
    }

    public boolean isIgnoredEnabled() {
        return ignoredEnabled;
    }
    public boolean isIgnoredDayEnabled() {
        return ignoredDayEnabled;
    }
    public boolean isIgnoredDaySameCauseRequired() {
        return ignoredDaySameCauseRequired;
    }
    public String getIgnoredDayType() {
        return ignoredDayType;
    }
    public List<Integer> getIgnoredDays() {
        return ignoredDays;
    }
    public boolean isIgnoredMustDifference() {
        return ignoredMustDifference;
    }
    public List<String> getIgnoredCause() {
        return ignoredCause;
    }

    public boolean isIncreaseEnabled() {
        return increaseEnabled;
    }
    public int getIncreasePerDeath() {
        return increasePerDeath;
    }
    public boolean isIncreaseDayEnabled() {
        return increaseDayEnabled;
    }
    public boolean isIncreaseDaySameCauseRequired() {
        return increaseDaySameCauseRequired;
    }
    public String getIncreaseDayType() {
        return increaseDayType;
    }
    public List<Integer> getIncreaseDays() {
        return increaseDays;
    }
    public int getIncreaseDayAmount() {
        return increaseDayAmount;
    }
    public boolean isIncreaseMustDifference() {
        return increaseMustDifference;
    }
    public List<String> getIncreaseCause() {
        return increaseCause;
    }

    public boolean isDecreaseEnabled() {
        return decreaseEnabled;
    }
    public int getDecreasePerDeath() {
        return decreasePerDeath;
    }
    public boolean isDecreaseDayEnabled() {
        return decreaseDayEnabled;
    }
    public boolean isDecreaseDaySameCauseRequired() {
        return decreaseDaySameCauseRequired;
    }
    public String getDecreaseDayType() {
        return decreaseDayType;
    }
    public List<Integer> getDecreaseDays() {
        return decreaseDays;
    }
    public int getDecreaseDayAmount() {
        return decreaseDayAmount;
    }
    public boolean isDecreaseMustDifference() {
        return decreaseMustDifference;
    }
    public List<String> getDecreaseCause() {
        return decreaseCause;
    }

    public boolean isSeasonEnabled() {
        return seasonEnabled;
    }

    public String getSeasonType() {
        return seasonType;
    }

    public int getSeasonDay() {
        return seasonDay;
    }

    public boolean isPermissionAllPlayerReload() {
        return permissionAllPlayerReload;
    }

    public boolean isPermissionAllPlayerSetMaxHealth() {
        return permissionAllPlayerSetMaxHealth;
    }

    public boolean isPermissionAllPlayerViewHealth() {
        return permissionAllPlayerViewHealth;
    }

    public boolean isPermissionAllPlayerViewDeathData() {
        return permissionAllPlayerViewDeathData;
    }

    public boolean isPermissionAllPlayerResetHealth() {
        return permissionAllPlayerResetHealth;
    }

    public boolean isPermissionAllPlayerMatchHealth() {
        return permissionAllPlayerMatchHealth;
    }

    public boolean isPermissionAllPlayerRemoveDeathData() {
        return permissionAllPlayerRemoveDeathData;
    }

    public boolean isPermissionAllPlayerTransferHealth() {
        return permissionAllPlayerTransferHealth;
    }

    public boolean isPermissionAllPlayerHelp() {
        return permissionAllPlayerHelp;
    }

    public boolean isDefaultDeathMessageEnabled() {
        return defaultDeathMessageEnabled;
    }

    public String getNotificationPlayerMaxHealth() {
        return notificationPlayerMaxHealth;
    }
    public String getNotificationPlayerMinHealth() {
        return notificationPlayerMinHealth;
    }
    public String getNotificationPlayerBanReason() {
        return notificationPlayerBanReason;
    }
    public String getNotificationPlayerKicked() {
        return notificationPlayerKicked;
    }

    public String getNotificationPlayerIgnored() {
        return notificationPlayerIgnored;
    }
    public String getNotificationPlayerIgnoredSameWay() {
        return notificationPlayerIgnoredSameWay;
    }

    public String getNotificationPlayerIncrease() {
        return notificationPlayerIncrease;
    }
    public String getNotificationPlayerIncreaseSameWay() {
        return notificationPlayerIncreaseSameWay;
    }

    public String getNotificationPlayerDecrease() {
        return notificationPlayerDecrease;
    }

    public String getIgnoredDayWarning() {
        return ignoredDayWarning;
    }

    public String getIncreaseDayWarning() {
        return increaseDayWarning;
    }

    public String getDecreaseDayWarning() {
        return decreaseDayWarning;
    }

    public String getSeasonChangeWarning() {
        return seasonChangeWarning;
    }

    public String getNotificationConsoleMaxHealth() {
        return notificationLogServerMaxHealth;
    }
    public String getNotificationConsoleMinHealth() {
        return notificationLogServerMinHealth;
    }

    public String getNotificationConsoleIncrease() {
        return notificationLogServerIncrease;
    }
    public String getNotificationConsoleDecrease() {
        return notificationLogServerDecrease;
    }
    public String getNotificationConsoleBanReason() {
        return notificationLogServerBanReason;
    }
}