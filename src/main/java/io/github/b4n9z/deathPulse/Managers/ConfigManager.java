package io.github.b4n9z.deathPulse.Managers;

import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigManager {
    private final Plugin plugin;
    private int hpStart;
    private int gainedPerDeath;
    private boolean gainedSpecialDayEnabled;
    private String gainedSpecialDayType;
    private List<Integer> gainedSpecialDays;
    private int gainedSpecialDayAmount;
    private boolean gainedMaxEnabled;
    private int gainedMaxAmount;
    private boolean decreaseEnabled;
    private int decreasePerDeath;
    private boolean decreaseMinEnabled;
    private int decreaseMinAmount;
    private int decreaseBanTime;
    private boolean decreaseDayEnabled;
    private String decreaseDayType;
    private List<Integer> decreaseDays;
    private int decreaseDayAmount;
    private boolean deathMustDifference;
    private List<String> deathIgnored;
    private List<String> decreaseCause;
    private String deathMessagePlayerGained;
    private String deathMessagePlayerIfSameWay;
    private String deathMessagePlayerIgnored;
    private String deathMessagePlayerDecrease;
    private String deathMessagePlayerMaxHealth;
    private String deathMessagePlayerBanReason;
    private String deathMessagePlayerKicked;
    private String deathMessageLogServerGained;
    private String deathMessageLogServerDecrease;
    private String deathMessageLogServerBanReason;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        try {
            this.hpStart = plugin.getConfig().getInt("HP.start", 20);
            this.gainedPerDeath = plugin.getConfig().getInt("HP.gained.per_death", 2);
            this.gainedSpecialDayEnabled = plugin.getConfig().getBoolean("HP.gained.special_day.enabled", false);
            this.gainedSpecialDayType = plugin.getConfig().getString("HP.gained.special_day.type", "minecraft");
            this.gainedSpecialDays = plugin.getConfig().getIntegerList("HP.gained.special_day.days");
            this.gainedSpecialDayAmount = plugin.getConfig().getInt("HP.gained.special_day.amount", 10);
            this.gainedMaxEnabled = plugin.getConfig().getBoolean("HP.gained.max.enabled", false);
            this.gainedMaxAmount = plugin.getConfig().getInt("HP.gained.max.amount", 114);
            this.decreaseEnabled = plugin.getConfig().getBoolean("HP.decrease.enabled", false);
            this.decreasePerDeath = plugin.getConfig().getInt("HP.decrease.per_death", 2);
            this.decreaseMinEnabled = plugin.getConfig().getBoolean("HP.decrease.min.enabled", false);
            this.decreaseMinAmount = plugin.getConfig().getInt("HP.decrease.min.amount", 2);
            this.decreaseBanTime = plugin.getConfig().getInt("HP.decrease.min.banTime", 24);
            this.decreaseDayEnabled = plugin.getConfig().getBoolean("HP.decrease.day.enabled", false);
            this.decreaseDayType = plugin.getConfig().getString("HP.decrease.day.type", "server");
            this.decreaseDays = plugin.getConfig().getIntegerList("HP.decrease.day.days");
            this.decreaseDayAmount = plugin.getConfig().getInt("HP.decrease.day.amount", 10);
            this.deathMustDifference = plugin.getConfig().getBoolean("death.must_difference", true);
            this.deathIgnored = plugin.getConfig().getStringList("death.ignored");
            this.decreaseCause = plugin.getConfig().getStringList("death.decrease");
            this.deathMessagePlayerGained = plugin.getConfig().getString("notifications.death_message.player.gained");
            this.deathMessagePlayerIfSameWay = plugin.getConfig().getString("notifications.death_message.player.ifSameWay");
            this.deathMessagePlayerIgnored = plugin.getConfig().getString("notifications.death_message.player.ignored");
            this.deathMessagePlayerDecrease = plugin.getConfig().getString("notifications.death_message.player.decrease");
            this.deathMessagePlayerMaxHealth = plugin.getConfig().getString("notifications.death_message.player.maxHealth");
            this.deathMessagePlayerBanReason = plugin.getConfig().getString("notifications.death_message.player.banReason");
            this.deathMessagePlayerKicked = plugin.getConfig().getString("notifications.death_message.player.kicked");
            this.deathMessageLogServerGained = plugin.getConfig().getString("notifications.death_message.logServer.gained");
            this.deathMessageLogServerDecrease = plugin.getConfig().getString("notifications.death_message.logServer.decrease");
            this.deathMessageLogServerBanReason = plugin.getConfig().getString("notifications.death_message.logServer.banReason");

            validateConfig();
        } catch (Exception e) {
            plugin.getLogger().warning("§cFailed to load config:§a " + e.getMessage());
        }
    }

    private void validateConfig() throws Exception {
        if (hpStart <= 0) throw new Exception("Invalid HP.start value:§e " + hpStart);
        if (gainedPerDeath <= 0) throw new Exception("Invalid HP.gained.per_death value:§e " + gainedPerDeath);
        if (gainedSpecialDayEnabled && gainedSpecialDays.isEmpty()) throw new Exception("Invalid HP.gained.special_day.type value:§e " + gainedSpecialDayType);
        if (gainedSpecialDayEnabled && gainedSpecialDayAmount < decreasePerDeath) throw new Exception("Invalid HP.gained.special_day.amount value:§e " + gainedSpecialDayAmount);
        if (!gainedSpecialDayType.equals("real") && !gainedSpecialDayType.equals("minecraft")) throw new Exception("Invalid HP.gained.special_day.type value:§e " + gainedSpecialDayType);
        if (gainedMaxEnabled && gainedMaxAmount <= 0) throw new Exception("Invalid HP.gained.max.amount value:§e " + gainedMaxAmount);
        if (decreaseEnabled && decreasePerDeath < 0) throw new Exception("Invalid HP.decrease.per_death value:§e " + decreasePerDeath);
        if (decreaseEnabled && decreaseMinEnabled && decreaseMinAmount < 0) throw new Exception("Invalid HP.decrease.min.amount value:§e " + decreaseMinAmount);
        if (decreaseEnabled && !decreaseMinEnabled && decreaseBanTime < 0) throw new Exception("Invalid HP.decrease.min.banTime value:§e " + decreaseBanTime);
        if (decreaseEnabled && decreaseDayEnabled && decreaseDays.isEmpty()) throw new Exception("Invalid HP.decrease.day.days value:§e " + decreaseDays);
        if (decreaseEnabled && decreaseDayEnabled && decreaseDayAmount < 0) throw new Exception("Invalid HP.decrease.day.amount value:§e " + decreaseDayAmount);
        if (!decreaseDayType.equals("real") && !decreaseDayType.equals("minecraft")) throw new Exception("Invalid HP.decrease.day.type value:§e " + decreaseDayType);

        Set<String> intersection = new HashSet<>(deathIgnored);
        intersection.retainAll(decreaseCause);
        if (!intersection.isEmpty()) {
            throw new Exception("death.ignored and death.decrease lists should not have common elements:§e " + String.join(", ", intersection));
        }

        Set<Integer> SpecialDays = new HashSet<>(gainedSpecialDays);
        SpecialDays.retainAll(decreaseDays);
        if (!SpecialDays.isEmpty()) {
            throw new Exception("gained.special_day.days and decrease.day.days lists should not have common elements:§e " + String.join(", ", SpecialDays.stream().map(String::valueOf).toArray(String[]::new)));
        }
    }

    public int getHpStart() {
        return hpStart;
    }

    public void setHpStart(int hpStart) {
        plugin.getConfig().set("HP.start", hpStart);
        this.hpStart = hpStart;
    }

    public int getGainedPerDeath() {
        return gainedPerDeath;
    }

    public void setGainedPerDeath(int gainedPerDeath) {
        plugin.getConfig().set("HP.gained.per_death", gainedPerDeath);
        this.gainedPerDeath = gainedPerDeath;
    }

    public boolean isGainedSpecialDayEnabled() {
        return gainedSpecialDayEnabled;
    }

    public String getGainedSpecialDayType() {
        return gainedSpecialDayType;
    }

    public List<Integer> getGainedSpecialDays() {
        return gainedSpecialDays;
    }

    public int getGainedSpecialDayAmount() {
        return gainedSpecialDayAmount;
    }

    public boolean isGainedMaxEnabled() {
        return gainedMaxEnabled;
    }

    public void setGainedMaxEnabled(boolean gainedMaxEnabled) {
        plugin.getConfig().set("HP.gained.max.enabled", gainedMaxEnabled);
        this.gainedMaxEnabled = gainedMaxEnabled;
    }

    public int getGainedMaxAmount() {
        return gainedMaxAmount;
    }

    public void setGainedMaxAmount(int gainedMaxAmount) {
        plugin.getConfig().set("HP.gained.max.amount", gainedMaxAmount);
        this.gainedMaxAmount = gainedMaxAmount;
    }

    public boolean isDecreaseEnabled() {
        return decreaseEnabled;
    }

    public void setDecreaseEnabled(boolean decreaseEnabled) {
        plugin.getConfig().set("HP.decrease.enabled", decreaseEnabled);
        this.decreaseEnabled = decreaseEnabled;
    }

    public int getDecreasePerDeath() {
        return decreasePerDeath;
    }

    public void setDecreasePerDeath(int decreasePerDeath) {
        plugin.getConfig().set("HP.decrease.per_death", decreasePerDeath);
        this.decreasePerDeath = decreasePerDeath;
    }

    public boolean isDecreaseMinEnabled() {
        return decreaseMinEnabled;
    }

    public void setDecreaseMinEnabled(boolean decreaseMinEnabled) {
        plugin.getConfig().set("HP.decrease.min.enabled", decreaseMinEnabled);
        this.decreaseMinEnabled = decreaseMinEnabled;
    }

    public int getDecreaseMinAmount() {
        return decreaseMinAmount;
    }

    public void setDecreaseMinAmount(int decreaseMinAmount) {
        plugin.getConfig().set("HP.decrease.min.amount", decreaseMinAmount);
        this.decreaseMinAmount = decreaseMinAmount;
    }

    public int getDecreaseBanTime() {
        return decreaseBanTime;
    }

    public void setDecreaseBanTime(int decreaseBanTime) {
        plugin.getConfig().set("HP.decrease.min.banTime", decreaseBanTime);
        this.decreaseBanTime = decreaseBanTime;
    }

    public boolean isDecreaseDayEnabled() {
        return decreaseDayEnabled;
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

    public boolean isDeathMustDifference() {
        return deathMustDifference;
    }

    public List<String> getDeathIgnored() {
        return deathIgnored;
    }

    public List<String> getDecreaseCause() {
        return decreaseCause;
    }

    public String getDeathMessagePlayerGained() {
        return deathMessagePlayerGained;
    }

    public String getDeathMessagePlayerIfSameWay() {
        return deathMessagePlayerIfSameWay;
    }

    public String getDeathMessagePlayerIgnored() {
        return deathMessagePlayerIgnored;
    }

    public String getDeathMessagePlayerDecrease() {
        return deathMessagePlayerDecrease;
    }

    public String getDeathMessagePlayerMaxHealth() {
        return deathMessagePlayerMaxHealth;
    }

    public String getDeathMessagePlayerBanReason() {
        return deathMessagePlayerBanReason;
    }

    public String getDeathMessagePlayerKicked() {
        return deathMessagePlayerKicked;
    }

    public String getDeathMessageLogServerGained() {
        return deathMessageLogServerGained;
    }

    public String getDeathMessageLogServerDecrease() {
        return deathMessageLogServerDecrease;
    }

    public String getDeathMessageLogServerBanReason() {
        return deathMessageLogServerBanReason;
    }
}