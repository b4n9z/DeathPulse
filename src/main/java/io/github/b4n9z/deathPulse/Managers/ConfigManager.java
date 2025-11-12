package io.github.b4n9z.deathPulse.Managers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ConfigManager {
    private final Plugin plugin;
    // Crucial settings
    private int configVersion;
    private int firstTimeSetup;
    private int checkDayPeriod;
    // World settings
    private Map<World, Long> worldsList;
    private List<String> invalidWorlds;
    private List<String> tickErrorWorlds;
    // HP settings
    private int hpStart;
    private boolean hpMaxEnabled;
    private int hpMaxAmount;
    private boolean dropHealthItem;
    private boolean hpMinEnabled;
    private int hpMinAmount;
    private int hpMinBanTime;
    private int hpAfterBan;
    // Health Item settings
    private String healthItemMaterial;
    private String healthItemName;
    private List<String> healthItemLore;
    private double healthItemHealthPerItem;
    private List<String> healthItemCannotToCraft;

    // Priority settings
    private List<String> priority;
    // Ignored settings
    private boolean ignoredEnabled;
    private boolean ignoredMustDifference;
    private List<String> ignoredCause;
    private List<String> ignoredCauseExclude;
    private boolean ignoredDayEnabled;
    private boolean ignoredDayMustDifference;
    private boolean ignoredDayDeActiveIncreaseCause;
    private boolean ignoredDayDeActiveDecreaseCause;
    private boolean ignoredDayDeActiveIncreaseDay;
    private boolean ignoredDayDeActiveDecreaseDay;
    private String ignoredDayType;
    private List<Integer> ignoredDays;
    private List<String> ignoredDayCause;
    private List<String> ignoredDayCauseExclude;

    // Increase settings
    private boolean increaseEnabled;
    private boolean increaseMustDifference;
    private Map<String, Integer> increaseCause;
    private List<String> increaseCauseName;
    private List<String> increaseCauseExclude;
    private boolean increaseDayEnabled;
    private boolean increaseDayMustDifference;
    private boolean increaseDayDeActiveIgnoreCause;
    private boolean increaseDayDeActiveDecreaseCause;
    private boolean increaseDayDeActiveIgnoreDay;
    private boolean increaseDayDeActiveDecreaseDay;
    private String increaseDayType;
    private List<Integer> increaseDays;
    private Map<String, Integer> increaseDayCause;
    private List<String> increaseDayCauseName;
    private List<String> increaseDayCauseExclude;

    // Decrease settings
    private boolean decreaseEnabled;
    private boolean decreaseMustDifference;
    private boolean decreaseDebt;
    private Map<String, Integer> decreaseCause;
    private List<String> decreaseCauseName;
    private List<String> decreaseCauseExclude;
    private boolean decreaseDayEnabled;
    private boolean decreaseDayMustDifference;
    private boolean decreaseDayDeActiveIgnoreCause;
    private boolean decreaseDayDeActiveIncreaseCause;
    private boolean decreaseDayDeActiveIgnoreDay;
    private boolean decreaseDayDeActiveIncreaseDay;
    private String decreaseDayType;
    private List<Integer> decreaseDays;
    private Map<String, Integer> decreaseDayCause;
    private List<String> decreaseDayCauseName;
    private List<String> decreaseDayCauseExclude;

    // Season settings
    private boolean seasonEnabled;
    private String seasonType;
    private int seasonDay;
    private boolean resetWorldDay;

    // Permission settings
    private final Map<String, Boolean> permissionAllPlayer = new HashMap<>();
    private static final String PERM_PREFIX = "dp.";

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
    private String notificationPlayerDecreaseSameWay;

    private String notificationPlayerDebtPaidOff;
    private String notificationPlayerDebtReduced;
    private String notificationPlayerDebtAdded;

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

    private List<String> cachedConfigPaths = null;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        loadConfig();
        this.plugin.getConfig().options().copyDefaults(true);
        this.plugin.saveConfig();
    }

    public void loadConfig() {
        try {
            // Crucial settings
            this.configVersion = plugin.getConfig().getInt("config-version", 0);
            this.firstTimeSetup = plugin.getConfig().getInt("firstTimeSetup",0);
            this.checkDayPeriod = plugin.getConfig().getInt("checkDayPeriod",30);
            // World settings
            ConfigurationSection worldSettings = plugin.getConfig().getConfigurationSection("worldDaySettings");
            Map<String, Long> worldTicks = new HashMap<>();
            if (worldSettings != null) {
                for (String worldName : worldSettings.getKeys(false)) {
                    long ticks = plugin.getConfig().getLong("worldDaySettings." + worldName);
                    worldTicks.put(worldName, ticks);
                }
            }
            if (worldTicks.isEmpty()) {
                worldTicks.put("world", 24000L);
            }
            this.invalidWorlds = new ArrayList<>();
            this.tickErrorWorlds = new ArrayList<>();
            this.worldsList = new HashMap<>();
            for (Map.Entry<String, Long> entry : worldTicks.entrySet()) {
                World worldBukkit = Bukkit.getWorld(entry.getKey());
                if (worldBukkit != null) {
                    this.worldsList.put(worldBukkit, entry.getValue());
                    if (entry.getValue() <= 0) {
                        this.tickErrorWorlds.add(entry.getKey());
                    }
                } else {
                    this.invalidWorlds.add(entry.getKey());
                }
            }
            // HP settings
            this.hpStart = plugin.getConfig().getInt("hp.start", 20);
            this.hpMaxEnabled = plugin.getConfig().getBoolean("hp.maxHP.enabled", false);
            this.hpMaxAmount = plugin.getConfig().getInt("hp.maxHP.amount", 140);
            this.dropHealthItem = plugin.getConfig().getBoolean("hp.maxHP.dropHealthItem", true);
            this.hpMinEnabled = plugin.getConfig().getBoolean("hp.minHP.enabled", false);
            this.hpMinAmount = plugin.getConfig().getInt("hp.minHP.amount", 2);
            this.hpMinBanTime = plugin.getConfig().getInt("hp.minHP.banTime", 24);
            this.hpAfterBan = plugin.getConfig().getInt("hp.afterBan", 2);
            // Health Item settings
            this.healthItemMaterial = plugin.getConfig().getString("healthItem.material", "NETHER_STAR");
            this.healthItemName = plugin.getConfig().getString("healthItem.name", "Health Token");
            this.healthItemLore = plugin.getConfig().getStringList("healthItem.lore");
            this.healthItemHealthPerItem = plugin.getConfig().getDouble("healthItem.healthPerItem", 2);
            this.healthItemCannotToCraft = plugin.getConfig().getStringList("healthItem.cannotToCraft");

            // Priority settings
            this.priority = plugin.getConfig().getStringList("priority");
            // Ignored settings
            this.ignoredEnabled = plugin.getConfig().getBoolean("ignore.enabled", false);
            this.ignoredMustDifference = plugin.getConfig().getBoolean("ignore.must_difference", false);
            this.ignoredCause = new ArrayList<>();
            this.ignoredCauseExclude = new ArrayList<>();
            List<String> ignoredCauseList = plugin.getConfig().getStringList("ignore.cause");
            String causeName;
            for (String cause : ignoredCauseList) {
                String[] parts = cause.split("::");

                if (parts.length > 2) {
                    plugin.getLogger().warning("Invalid cause format: " + cause);
                    continue;
                }
                causeName = parts[0].trim();

                if (parts.length == 2 && parts[1].trim().equals("EXCLUDE")) {
                    this.ignoredCauseExclude.add(causeName);
                } else {
                    this.ignoredCause.add(causeName);
                }
            }
            this.ignoredDayEnabled = plugin.getConfig().getBoolean("ignore.day.enabled", false);
            this.ignoredDayMustDifference = plugin.getConfig().getBoolean("ignore.day.must_difference", false);
            this.ignoredDayDeActiveIncreaseCause = plugin.getConfig().getBoolean("ignore.day.deActiveIncrease", false);
            this.ignoredDayDeActiveDecreaseCause = plugin.getConfig().getBoolean("ignore.day.deActiveDecrease", false);
            this.ignoredDayDeActiveIncreaseDay = plugin.getConfig().getBoolean("ignore.day.deActiveIncreaseDay", false);
            this.ignoredDayDeActiveDecreaseDay = plugin.getConfig().getBoolean("ignore.day.deActiveDecreaseDay", false);
            this.ignoredDayType = plugin.getConfig().getString("ignore.day.type", "minecraft");
            this.ignoredDays = plugin.getConfig().getIntegerList("ignore.day.days");
            this.ignoredDayCause = new ArrayList<>();
            this.ignoredDayCauseExclude = new ArrayList<>();
            List<String> ignoredDayCauseList = plugin.getConfig().getStringList("ignore.day.cause");
            for (String cause : ignoredDayCauseList) {
                String[] parts = cause.split("::");

                if (parts.length > 2) {
                    plugin.getLogger().warning("Invalid cause format: " + cause);
                    continue;
                }
                causeName = parts[0].trim();

                if (parts.length == 2 && parts[1].trim().equals("EXCLUDE")) {
                    this.ignoredDayCauseExclude.add(causeName);
                } else {
                    this.ignoredDayCause.add(causeName);
                }
            }

            // Increase settings
            this.increaseEnabled = plugin.getConfig().getBoolean("increase.enabled", true);
            this.increaseMustDifference = plugin.getConfig().getBoolean("increase.must_difference", true);
            if (this.increaseCause != null) {
                this.increaseCause.clear();
            }
            if (this.increaseCauseName != null) {
                this.increaseCauseName.clear();
            }
            this.increaseCauseName = new ArrayList<>();
            this.increaseCause = new HashMap<>();
            this.increaseCauseExclude = new ArrayList<>();
            List<String> increaseCauseList = plugin.getConfig().getStringList("increase.cause");
            int causeAmount;
            for (String cause : increaseCauseList) {
                String[] parts = cause.split("::");

                if (parts.length != 2) {
                    plugin.getLogger().warning("Invalid cause format: " + cause);
                    continue;
                }
                causeName = parts[0].trim();

                if (!parts[1].trim().equals("EXCLUDE")) {
                    try {
                        causeAmount = Integer.parseInt(parts[1].trim());
                        if (causeAmount < 0) causeAmount = 0;
                    } catch (NumberFormatException e) {
                        plugin.getLogger().warning("Invalid amount in cause: " + cause);
                        continue;
                    }
                    this.increaseCauseName.add(causeName);
                    this.increaseCause.put(causeName, causeAmount);
                } else {
                    this.increaseCauseExclude.add(causeName);
                }
            }
            this.increaseDayEnabled = plugin.getConfig().getBoolean("increase.day.enabled", false);
            this.increaseDayMustDifference = plugin.getConfig().getBoolean("increase.day.must_difference", false);
            this.increaseDayDeActiveIgnoreCause = plugin.getConfig().getBoolean("increase.day.deActiveIgnore", false);
            this.increaseDayDeActiveDecreaseCause = plugin.getConfig().getBoolean("increase.day.deActiveDecrease", false);
            this.increaseDayDeActiveIgnoreDay = plugin.getConfig().getBoolean("increase.day.deActiveIgnoreDay", false);
            this.increaseDayDeActiveDecreaseDay = plugin.getConfig().getBoolean("increase.day.deActiveDecreaseDay", false);
            this.increaseDayType = plugin.getConfig().getString("increase.day.type", "minecraft");
            this.increaseDays = plugin.getConfig().getIntegerList("increase.day.days");
            if (this.increaseDayCause != null) {
                this.increaseDayCause.clear();
            }
            if (this.increaseDayCauseName != null) {
                this.increaseDayCauseName.clear();
            }
            this.increaseDayCauseName = new ArrayList<>();
            this.increaseDayCause = new HashMap<>();
            this.increaseDayCauseExclude = new ArrayList<>();
            List<String> increaseDayCauseList = plugin.getConfig().getStringList("increase.day.cause");
            for (String cause : increaseDayCauseList) {
                String[] parts = cause.split("::");

                if (parts.length != 2) {
                    plugin.getLogger().warning("Invalid cause format: " + cause);
                    continue;
                }
                causeName = parts[0].trim();

                if (!parts[1].trim().equals("EXCLUDE")) {
                    try {
                        causeAmount = Integer.parseInt(parts[1].trim());
                        if (causeAmount < 0) causeAmount = 0;
                    } catch (NumberFormatException e) {
                        plugin.getLogger().warning("Invalid amount in cause: " + cause);
                        continue;
                    }
                    this.increaseDayCauseName.add(causeName);
                    this.increaseDayCause.put(causeName, causeAmount);
                } else {
                    this.increaseDayCauseExclude.add(causeName);
                }
            }

            // Decrease settings
            this.decreaseEnabled = plugin.getConfig().getBoolean("decrease.enabled", false);
            this.decreaseMustDifference = plugin.getConfig().getBoolean("decrease.must_difference", false);
            this.decreaseDebt = plugin.getConfig().getBoolean("decrease.debt", false);
            if (this.decreaseCause != null) {
                this.decreaseCause.clear();
            }
            if (this.decreaseCauseName != null) {
                this.decreaseCauseName.clear();
            }
            this.decreaseCauseName = new ArrayList<>();
            this.decreaseCause = new HashMap<>();
            this.decreaseCauseExclude = new ArrayList<>();
            List<String> decreaseCauseList = plugin.getConfig().getStringList("decrease.cause");
            for (String cause : decreaseCauseList) {
                String[] parts = cause.split("::");

                if (parts.length != 2) {
                    plugin.getLogger().warning("Invalid cause format: " + cause);
                }
                causeName = parts[0];

                if (!parts[1].trim().equals("EXCLUDE")) {
                    try {
                        causeAmount = Integer.parseInt(parts[1]);
                        if (causeAmount < 0) causeAmount = 0;
                    } catch (NumberFormatException e) {
                        plugin.getLogger().warning("Invalid amount in cause: " + cause);
                        continue;
                    }
                    this.decreaseCauseName.add(causeName);
                    this.decreaseCause.put(causeName, causeAmount);
                } else {
                    this.decreaseCauseExclude.add(causeName);
                }
            }
            this.decreaseDayEnabled = plugin.getConfig().getBoolean("decrease.day.enabled", false);
            this.decreaseDayMustDifference = plugin.getConfig().getBoolean("decrease.day.must_difference", false);
            this.decreaseDayDeActiveIgnoreCause = plugin.getConfig().getBoolean("decrease.day.deActiveIgnore", false);
            this.decreaseDayDeActiveIncreaseCause = plugin.getConfig().getBoolean("decrease.day.deActiveIncrease", false);
            this.decreaseDayDeActiveIgnoreDay = plugin.getConfig().getBoolean("decrease.day.deActiveIgnoreDay", false);
            this.decreaseDayDeActiveIncreaseDay = plugin.getConfig().getBoolean("decrease.day.deActiveIncreaseDay", false);
            this.decreaseDayType = plugin.getConfig().getString("decrease.day.type", "minecraft");
            this.decreaseDays = plugin.getConfig().getIntegerList("decrease.day.days");
            if (this.decreaseDayCause != null) {
                this.decreaseDayCause.clear();
            }
            if (this.decreaseDayCauseName != null) {
                this.decreaseDayCauseName.clear();
            }
            this.decreaseDayCauseName = new ArrayList<>();
            this.decreaseDayCause = new HashMap<>();
            this.decreaseDayCauseExclude = new ArrayList<>();
            List<String> decreaseDayCauseList = plugin.getConfig().getStringList("decrease.day.cause");
            for (String cause : decreaseDayCauseList) {
                String[] parts = cause.split("::");

                if (parts.length != 2){
                    plugin.getLogger().warning("Invalid cause format: " + cause);
                    continue;
                }
                causeName = parts[0];

                if (!parts[1].trim().equals("EXCLUDE")) {
                    try {
                        causeAmount = Integer.parseInt(parts[1]);
                        if (causeAmount < 0) causeAmount = 0;
                    } catch (NumberFormatException e) {
                        plugin.getLogger().warning("Invalid amount in cause: " + cause);
                        continue;
                    }
                    this.decreaseDayCauseName.add(causeName);
                    this.decreaseDayCause.put(causeName, causeAmount);
                } else {
                    this.decreaseDayCauseExclude.add(causeName);
                }
            }

            // Season settings
            this.seasonEnabled = plugin.getConfig().getBoolean("season.enabled", false);
            this.seasonType = plugin.getConfig().getString("season.type", "real");
            this.seasonDay = plugin.getConfig().getInt("season.day", 30);
            this.resetWorldDay = plugin.getConfig().getBoolean("season.resetWorldDay", false);

            // Permission settings
            this.permissionAllPlayer.put("reload", plugin.getConfig().getBoolean("permissionsAllPlayer.reload", false));
            this.permissionAllPlayer.put("setConfig", plugin.getConfig().getBoolean("permissionsAllPlayer.setConfig", false));
            this.permissionAllPlayer.put("setMaxHealth", plugin.getConfig().getBoolean("permissionsAllPlayer.setMaxHealth", false));
            this.permissionAllPlayer.put("viewHealth", plugin.getConfig().getBoolean("permissionsAllPlayer.viewHealth", false));
            this.permissionAllPlayer.put("viewDeathData", plugin.getConfig().getBoolean("permissionsAllPlayer.viewDeathData", true));
            this.permissionAllPlayer.put("viewDebtData", plugin.getConfig().getBoolean("permissionsAllPlayer.viewDebtData", true));
            this.permissionAllPlayer.put("resetHealth", plugin.getConfig().getBoolean("permissionsAllPlayer.resetHealth", false));
            this.permissionAllPlayer.put("matchHealth", plugin.getConfig().getBoolean("permissionsAllPlayer.matchHealth", false));
            this.permissionAllPlayer.put("removeDeathData", plugin.getConfig().getBoolean("permissionsAllPlayer.removeDeathData", false));
            this.permissionAllPlayer.put("removeDebtData", plugin.getConfig().getBoolean("permissionsAllPlayer.removeDebtData", false));
            this.permissionAllPlayer.put("transferHealth", plugin.getConfig().getBoolean("permissionsAllPlayer.transferHealth", true));
            this.permissionAllPlayer.put("withdrawHealth", plugin.getConfig().getBoolean("permissionsAllPlayer.withdrawHealth", true));
            this.permissionAllPlayer.put("help", plugin.getConfig().getBoolean("permissionsAllPlayer.help", true));

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
            this.notificationPlayerDecreaseSameWay = Objects.requireNonNull(plugin.getConfig().getString("notifications.player.decreaseSameWay")).replace("&", "§");

            this.notificationPlayerDebtPaidOff = Objects.requireNonNull(plugin.getConfig().getString("notifications.player.debtPaidOff")).replace("&", "§");
            this.notificationPlayerDebtReduced = Objects.requireNonNull(plugin.getConfig().getString("notifications.player.debtReduced")).replace("&", "§");
            this.notificationPlayerDebtAdded = Objects.requireNonNull(plugin.getConfig().getString("notifications.player.debtAdded")).replace("&", "§");

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

            this.cachedConfigPaths = null;

            validateConfig();
        } catch (Exception e) {
            Bukkit.getServer().getConsoleSender().sendMessage("§cFailed to load config:§a " + e.getMessage());
        }
    }

    private void validateConfig() throws Exception {
        if (checkDayPeriod <= 0) throw new Exception("checkDayPeriod should be greater than 0:§e " + checkDayPeriod);
        if (!invalidWorlds.isEmpty()) throw new Exception("The following worlds are configured but don't exist:§e " + String.join(", ", invalidWorlds));
        if (!tickErrorWorlds.isEmpty()) throw new Exception("The following worlds are configured but have an invalid tick rate:§e " + String.join(", ", tickErrorWorlds));

        if (hpStart <= 0) throw new Exception("hp.start should be greater than 0:§e " + hpStart);
        if (hpMaxEnabled && hpMaxAmount <= 0) throw new Exception("hp.maxHP.amount should be greater than 0:§e " + hpMaxAmount);
        if (hpMinEnabled && hpMinAmount <= 0) throw new Exception("hp.minHP.amount should be greater than 0:§e " + hpMinAmount);
        if ((hpMinEnabled && hpStart < hpMinAmount) || (hpMaxEnabled && hpStart > hpMaxAmount)) throw new Exception("hp.start should be between hp.minHP.amount and hp.maxHP.amount:§e " + hpStart);
        if (!hpMinEnabled && hpMinBanTime <= 0) throw new Exception("hp.minHP.banTime should be greater than 0:§e " + hpMinBanTime);

        if (healthItemHealthPerItem <= 0) throw new Exception("healthItem.healthPerItem should be greater than 0:§e " + healthItemHealthPerItem);

        if (!priority.contains("IGNORE") && !priority.contains("INCREASE") && !priority.contains("DECREASE")) throw new Exception("priority should contain IGNORE, INCREASE or DECREASE:§e " + priority);

        if (ignoredEnabled && ignoredCause.isEmpty()) throw new Exception("ignore.cause should not be empty.");
        if (ignoredDayEnabled && !ignoredDayType.equals("real") && !ignoredDayType.equals("minecraft")) throw new Exception("ignore.day.type should be real or minecraft:§e " + ignoredDayType);
        if (ignoredDayEnabled && ignoredDays.isEmpty()) throw new Exception("ignore.day.days should not be empty.");
        if (ignoredDayEnabled && ignoredDayCause.isEmpty()) throw new Exception("ignore.day.cause should not be empty.");

        if (increaseEnabled && increaseCause.isEmpty()) throw new Exception("increase.cause should not be empty.");
        if (increaseDayEnabled && !increaseDayType.equals("real") && !increaseDayType.equals("minecraft")) throw new Exception("increase.day.type should be real or minecraft:§e " + increaseDayType);
        if (increaseDayEnabled && increaseDays.isEmpty()) throw new Exception("increase.day.days should not be empty.");
        if (increaseDayEnabled && increaseDayCause.isEmpty()) throw new Exception("increase.day.cause should not be empty.");

        if (decreaseEnabled && decreaseCause.isEmpty()) throw new Exception("decrease.cause should not be empty.");
        if (decreaseDayEnabled && !decreaseDayType.equals("real") && !decreaseDayType.equals("minecraft")) throw new Exception("decrease.day.type should be real or minecraft:§e " + decreaseDayType);
        if (decreaseDayEnabled && decreaseDays.isEmpty()) throw new Exception("decrease.day.days should not be empty.");
        if (decreaseDayEnabled && decreaseDayCause.isEmpty()) throw new Exception("decrease.day.cause should not be empty.");

        if (ignoredDayDeActiveIncreaseCause && increaseDayDeActiveIgnoreCause) throw new Exception("ignore.day.deActiveIncrease and increase.day.deActiveIgnore should not be true at the same time.");
        if (ignoredDayDeActiveDecreaseCause && decreaseDayDeActiveIgnoreCause) throw new Exception("ignore.day.deActiveDecrease and decrease.day.deActiveIgnore should not be true at the same time.");
        if (increaseDayDeActiveDecreaseCause && decreaseDayDeActiveIncreaseCause) throw new Exception("increase.day.deActiveDecrease and decrease.day.deActiveIncrease should not be true at the same time.");

        if (ignoredDayDeActiveIncreaseDay && increaseDayDeActiveIgnoreDay) throw new Exception("ignore.day.deActiveIncreaseDay and increase.day.deActiveIgnoreDay should not be true at the same time.");
        if (ignoredDayDeActiveDecreaseDay && decreaseDayDeActiveIgnoreDay) throw new Exception("ignore.day.deActiveDecreaseDay and decrease.day.deActiveIgnoreDay should not be true at the same time.");
        if (increaseDayDeActiveDecreaseDay && decreaseDayDeActiveIncreaseDay) throw new Exception("increase.day.deActiveDecreaseDay and decrease.day.deActiveIncreaseDay should not be true at the same time.");

        if (resetWorldDay && !seasonEnabled) throw new Exception("season.enabled should be true when resetWorldDay is true:§e " + false);

        /*
        if (ignoredEnabled && !ignoredMustDifference) {
            Set<String> intersectionIgnore = new HashSet<>(ignoredCause);
            intersectionIgnore.retainAll(ignoredDayCause);
            intersectionIgnore.retainAll(increaseCauseName);
            intersectionIgnore.retainAll(increaseDayCauseName);
            intersectionIgnore.retainAll(decreaseCauseName);
            intersectionIgnore.retainAll(increaseDayCauseName);
            if (!intersectionIgnore.isEmpty()) {
                throw new Exception("ignore.cause list should not have common elements with ignore.day.cause, increase.cause, increase.day.cause, decrease.cause, and decrease.day.cause list when ignore.must_difference is false:§e " + String.join(", ", intersectionIgnore));
            }
        }

        if (ignoredDayEnabled && !ignoredDayMustDifference) {
            Set<String> intersectionIgnoreDay = new HashSet<>(ignoredDayCause);
            intersectionIgnoreDay.retainAll(ignoredCause);
            intersectionIgnoreDay.retainAll(increaseCauseName);
            intersectionIgnoreDay.retainAll(increaseDayCauseName);
            intersectionIgnoreDay.retainAll(decreaseCauseName);
            intersectionIgnoreDay.retainAll(decreaseDayCauseName);
            if (!intersectionIgnoreDay.isEmpty()) {
                throw new Exception("ignore.day.cause list should not have common elements with ignore.cause, increase.cause, increase.day.cause, decrease.cause, and decrease.day.cause list when ignore.day.must_difference is false:§e " + String.join(", ", intersectionIgnoreDay));
            }
        }

        if (increaseEnabled && !increaseMustDifference) {
            Set<String> intersectionIncrease = new HashSet<>(increaseCauseName);
            intersectionIncrease.retainAll(ignoredCause);
            intersectionIncrease.retainAll(ignoredDayCause);
            intersectionIncrease.retainAll(increaseDayCauseName);
            intersectionIncrease.retainAll(decreaseCauseName);
            intersectionIncrease.retainAll(decreaseDayCauseName);
            if (!intersectionIncrease.isEmpty()) {
                throw new Exception("increase.cause list should not have common elements with ignore.cause, ignore.day.cause, increase.day.cause, decrease.cause, and decrease.day.cause list when increase.must_difference is false:§e " + String.join(", ", intersectionIncrease));
            }
        }

        if (increaseDayEnabled && !increaseDayMustDifference) {
            Set<String> intersectionIncreaseDay = new HashSet<>(increaseDayCauseName);
            intersectionIncreaseDay.retainAll(ignoredCause);
            intersectionIncreaseDay.retainAll(ignoredDayCause);
            intersectionIncreaseDay.retainAll(increaseCauseName);
            intersectionIncreaseDay.retainAll(decreaseCauseName);
            intersectionIncreaseDay.retainAll(decreaseDayCauseName);
            if (!intersectionIncreaseDay.isEmpty()) {
                throw new Exception("increase.day.cause list should not have common elements with ignore.cause, ignore.day.cause, increase.cause, decrease.cause, and decrease.day.cause list when increase.day.must_difference is false:§e " + String.join(", ", intersectionIncreaseDay));
            }
        }

        if (decreaseEnabled && !decreaseMustDifference) {
            Set<String> intersectionDecrease = new HashSet<>(decreaseCauseName);
            intersectionDecrease.retainAll(ignoredCause);
            intersectionDecrease.retainAll(ignoredDayCause);
            intersectionDecrease.retainAll(increaseCauseName);
            intersectionDecrease.retainAll(increaseDayCauseName);
            intersectionDecrease.retainAll(decreaseDayCauseName);
            if (!intersectionDecrease.isEmpty()) {
                throw new Exception("decrease.cause list should not have common elements with ignore.cause, ignore.day.cause, increase.cause, increase.day.cause, and decrease.day.cause list when decrease.must_difference is false:§e " + String.join(", ", intersectionDecrease));
            }
        }

        if (decreaseDayEnabled && !decreaseDayMustDifference) {
            Set<String> intersectionDecreaseDay = new HashSet<>(decreaseDayCauseName);
            intersectionDecreaseDay.retainAll(ignoredCause);
            intersectionDecreaseDay.retainAll(ignoredDayCause);
            intersectionDecreaseDay.retainAll(increaseCauseName);
            intersectionDecreaseDay.retainAll(increaseDayCauseName);
            intersectionDecreaseDay.retainAll(decreaseCauseName);
            if (!intersectionDecreaseDay.isEmpty()) {
                throw new Exception("decrease.day.cause list should not have common elements with ignore.cause, ignore.day.cause, increase.cause, increase.day.cause, and decrease.cause list when decrease.day.must_difference is false:§e " + String.join(", ", intersectionDecreaseDay));
            }
        }

        if (ignoredDayEnabled) {
            Set<Integer> intersectionIgnoredDays = new HashSet<>(ignoredDays);
            intersectionIgnoredDays.retainAll(increaseDays);
            intersectionIgnoredDays.retainAll(decreaseDays);
            if (!intersectionIgnoredDays.isEmpty()) {
                throw new Exception("ignore.day.days should not have common elements with increase.day.days or decrease.day.days:§e " + String.join(", ", intersectionIgnoredDays.stream().map(String::valueOf).toArray(String[]::new)));
            }
        }

        if (increaseDayEnabled) {
            Set<Integer> intersectionIncreaseDays = new HashSet<>(increaseDays);
            intersectionIncreaseDays.retainAll(ignoredDays);
            intersectionIncreaseDays.retainAll(decreaseDays);
            if (!intersectionIncreaseDays.isEmpty()) {
                throw new Exception("increase.day.days should not have common elements with ignore.day.days or decrease.day.days:§e " + String.join(", ", intersectionIncreaseDays.stream().map(String::valueOf).toArray(String[]::new)));
            }
        }

        if (decreaseDayEnabled) {
            Set<Integer> intersectionDecreaseDays = new HashSet<>(decreaseDays);
            intersectionDecreaseDays.retainAll(ignoredDays);
            intersectionDecreaseDays.retainAll(increaseDays);
            if (!intersectionDecreaseDays.isEmpty()) {
                throw new Exception("decrease.day.days should not have common elements with ignore.day.days or increase.day.days:§e " + String.join(", ", intersectionDecreaseDays.stream().map(String::valueOf).toArray(String[]::new)));
            }
        }
        */
    }

    public int getConfigVersion() {
        return configVersion;
    }

    public int getFirstTimeSetup() {
        return firstTimeSetup;
    }

    public void setFirstTimeSetup(int firstTimeSetup) {
        plugin.getConfig().set("firstTimeSetup", firstTimeSetup);
        this.firstTimeSetup = firstTimeSetup;
        plugin.saveConfig();
        plugin.reloadConfig();
        loadConfig();
    }

    public long getCheckDayPeriod() {
        return checkDayPeriod;
    }

    /**
     * Get list of configured World objects
     * @return List of World objects that are both configured and exist on the server
     */
    public List<World> getConfiguredWorlds() {
        return new ArrayList<>(worldsList.keySet());
    }

    /**
     * Get ticks per day for certain worlds
     * @param worldName Name of the world
     * @return Number of ticks per day, or 0 if the world is not found
     */
    public long getTicksPerDay(World worldName) {
        return worldsList.get(worldName);
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
    public boolean isDropHealthItem() {
        return dropHealthItem;
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
    public int getHPAfterBan() {
        return hpAfterBan;
    }

    // Check if max hp is enabled and more than max config
    public boolean isMaxHPEnabledAndMoreThanMax(double newMaxHealth) {
        return (hpMaxEnabled && newMaxHealth > hpMaxAmount);
    }
    // Check if min hp is enabled and less than min config
    public boolean isMinHPEnabledAndLessThanMin(double newMaxHealth) {
        return (hpMinEnabled && newMaxHealth < hpMinAmount);
    }
    // Check if min hp is disabled and less or same than 0
    public boolean isMinHPDisabledAndSameOrLessThanZero(double newMaxHealth) {
        return (!hpMinEnabled && newMaxHealth <= 0);
    }

    public String getHealthItemMaterial() {
        return healthItemMaterial;
    }
    public String getHealthItemName() {
        return healthItemName;
    }
    public List<String> getHealthItemLore() {
        return healthItemLore;
    }
    public double getHealthItemHealthPerItem() {
        return healthItemHealthPerItem;
    }
    public List<String> getHealthItemCannotToCraft() {
        return healthItemCannotToCraft;
    }
    public boolean isItemCannotCraftUsingHealthItem(String item) {
        return healthItemCannotToCraft.contains(item);
    }

    public List<String> getPriority() {
        return priority;
    }

    public boolean isIgnoredEnabled() {
        return ignoredEnabled;
    }
    public boolean isIgnoredMustDifference() {
        return ignoredMustDifference;
    }
    public List<String> getIgnoredCause() {
        return ignoredCause;
    }
    public List<String> getIgnoredCauseExclude() {
        return ignoredCauseExclude;
    }
    public boolean isIgnoredDayEnabled() {
        return ignoredDayEnabled;
    }
    public boolean isIgnoredDayMustDifference() {
        return ignoredDayMustDifference;
    }
    public boolean isIgnoredDayDeActiveIncreaseCause() {
        return ignoredDayDeActiveIncreaseCause;
    }
    public boolean isIgnoredDayDeActiveDecreaseCause() {
        return ignoredDayDeActiveDecreaseCause;
    }
    public boolean isIgnoredDayDeActiveIncreaseDay() {
        return ignoredDayDeActiveIncreaseDay;
    }
    public boolean isIgnoredDayDeActiveDecreaseDay() {
        return ignoredDayDeActiveDecreaseDay;
    }
    public String getIgnoredDayType() {
        return ignoredDayType;
    }
    public List<Integer> getIgnoredDays() {
        return ignoredDays;
    }
    public List<String> getIgnoredDayCause() {
        return ignoredDayCause;
    }
    public List<String> getIgnoredDayCauseExclude() {
        return ignoredDayCauseExclude;
    }

    public boolean isIncreaseEnabled() {
        return increaseEnabled;
    }
    public boolean isIncreaseMustDifference() {
        return increaseMustDifference;
    }
    public Map<String, Integer> getIncreaseCause() {
        return increaseCause;
    }
    public boolean isIncreaseCauseValid(String cause) {
        return increaseCause.containsKey(cause) && !increaseCauseExclude.contains(cause);
    }
    public List<String> getIncreaseCauseName() {
        return increaseCauseName;
    }
    public List<String> getIncreaseCauseExclude() {
        return increaseCauseExclude;
    }
    public Integer getIncreaseCauseAmount(String cause) {
        return increaseCause.get(cause);
    }
    public boolean isIncreaseDayEnabled() {
        return increaseDayEnabled;
    }
    public boolean isIncreaseDayMustDifference() {
        return increaseDayMustDifference;
    }
    public boolean isIncreaseDayDeActiveIgnoredCause() {
        return increaseDayDeActiveIgnoreCause;
    }
    public boolean isIncreaseDayDeActiveDecreaseCause() {
        return increaseDayDeActiveDecreaseCause;
    }
    public boolean isIncreaseDayDeActiveIgnoredDay() {
        return increaseDayDeActiveIgnoreDay;
    }
    public boolean isIncreaseDayDeActiveDecreaseDay() {
        return increaseDayDeActiveDecreaseDay;
    }
    public String getIncreaseDayType() {
        return increaseDayType;
    }
    public List<Integer> getIncreaseDays() {
        return increaseDays;
    }
    public Map<String, Integer> getIncreaseDayCause() {
        return increaseDayCause;
    }
    public boolean isIncreaseDayCauseValid(String cause) {
        return increaseDayCause.containsKey(cause) && !increaseDayCauseExclude.contains(cause);
    }
    public List<String> getIncreaseDayCauseExclude() {
        return increaseDayCauseExclude;
    }
    public List<String> getIncreaseDayCauseName() {
        return increaseDayCauseName;
    }
    public Integer getIncreaseDayCauseAmount(String cause) {
        return increaseDayCause.get(cause);
    }

    public boolean isDecreaseEnabled() {
        return decreaseEnabled;
    }
    public boolean isDecreaseMustDifference() {
        return decreaseMustDifference;
    }
    public boolean isDecreaseDebt() {
        return decreaseDebt;
    }
    public Map<String, Integer> getDecreaseCause() {
        return decreaseCause;
    }
    public boolean isDecreaseCauseValid(String cause) {
        return decreaseCause.containsKey(cause) && !decreaseCauseExclude.contains(cause);
    }
    public List<String> getDecreaseCauseName() {
        return decreaseCauseName;
    }
    public List<String> getDecreaseCauseExclude() {
        return decreaseCauseExclude;
    }
    public Integer getDecreaseCauseAmount(String cause) {
        return decreaseCause.get(cause);
    }
    public boolean isDecreaseDayEnabled() {
        return decreaseDayEnabled;
    }
    public boolean isDecreaseDayMustDifference() {
        return decreaseDayMustDifference;
    }
    public boolean isDecreaseDayDeActiveIgnoredCause() {
        return decreaseDayDeActiveIgnoreCause;
    }
    public boolean isDecreaseDayDeActiveIncreaseCause() {
        return decreaseDayDeActiveIncreaseCause;
    }
    public boolean isDecreaseDayDeActiveIgnoredDay() {
        return decreaseDayDeActiveIgnoreDay;
    }
    public boolean isDecreaseDayDeActiveIncreaseDay() {
        return decreaseDayDeActiveIncreaseDay;
    }
    public String getDecreaseDayType() {
        return decreaseDayType;
    }
    public List<Integer> getDecreaseDays() {
        return decreaseDays;
    }
    public Map<String, Integer> getDecreaseDayCause() {
        return decreaseDayCause;
    }
    public boolean isDecreaseDayCauseValid(String cause) {
        return decreaseDayCause.containsKey(cause) && !decreaseDayCauseExclude.contains(cause);
    }
    public List<String> getDecreaseDayCauseExclude() {
        return decreaseDayCauseExclude;
    }
    public List<String> getDecreaseDayCauseName() {
        return decreaseDayCauseName;
    }
    public Integer getDecreaseDayCauseAmount(String cause) {
        return decreaseDayCause.get(cause);
    }

    // Check if death cause is ignored
    public boolean isIgnoredCause(String deathCause) {
        return (ignoredEnabled && (ignoredCause.contains("ALL") || ignoredCause.contains(deathCause)) && !ignoredCauseExclude.contains(deathCause));
    }

    // Check if death cause is increase
    public boolean isIncreaseCause(String deathCause) {
        return (increaseEnabled && (isIncreaseCauseValid("ALL") || isIncreaseCauseValid(deathCause)) && !increaseCauseExclude.contains(deathCause));
    }

    // Check if death cause is decrease
    public boolean isDecreaseCause(String deathCause) {
        return (decreaseEnabled && (isDecreaseCauseValid("ALL") || isDecreaseCauseValid(deathCause)) && !decreaseCauseExclude.contains(deathCause));
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
    public boolean isResetWorldDay() {
        return resetWorldDay;
    }

    public boolean canUse(CommandSender sender, String perm) {
        if (!permissionAllPlayer.containsKey(perm)) {
            plugin.getLogger().warning("Unknown permission key used in canUse(): " + perm);
        }
        if (permissionAllPlayer.getOrDefault(perm, false)) return true;
        return sender.hasPermission(PERM_PREFIX + perm) || sender.hasPermission(PERM_PREFIX + "admin");
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
    public String getNotificationPlayerDecreaseSameWay() {
        return notificationPlayerDecreaseSameWay;
    }

    public String getNotificationPlayerDebtPaidOff() {
        return notificationPlayerDebtPaidOff;
    }
    public String getNotificationPlayerDebtReduced() {
        return notificationPlayerDebtReduced;
    }
    public String getNotificationPlayerDebtAdded() {
        return notificationPlayerDebtAdded;
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

    public List<String> getAllConfigPaths() {
        if (this.cachedConfigPaths == null) {
            this.cachedConfigPaths = getConfigPaths(plugin.getConfig().getValues(true), "");
        }
        return this.cachedConfigPaths;
    }

    @SuppressWarnings("unchecked")
    private List<String> getConfigPaths(Map<String, Object> map, String prefix) {
        List<String> paths = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            if (val instanceof Map<?, ?> subMap) {
                paths.addAll(getConfigPaths((Map<String, Object>) subMap, prefix + key + "."));
            } else {
                if (!(plugin.getConfig().isConfigurationSection(prefix + key))) {
                    paths.add(prefix + key);
                }
            }
        }
        return paths;
    }

    public boolean isValidUUID(String str) {
        try {
            UUID.fromString(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}