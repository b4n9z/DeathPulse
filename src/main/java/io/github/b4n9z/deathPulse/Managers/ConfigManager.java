package io.github.b4n9z.deathPulse.Managers;

import org.bukkit.plugin.Plugin;

import java.util.List;

public class ConfigManager {
    private final Plugin plugin;
    private int hpStart;
    private int gainedPerDeath;
    private boolean gainedMaxEnabled;
    private int gainedMaxAmount;
    private boolean decreaseEnabled;
    private int decreasePerDeath;
    private int decreaseMin;
    private boolean deathMustDifference;
    private List<String> deathIgnored;
    private List<String> decreaseCause;
    private String deathMessagePlayerGained;
    private String deathMessagePlayerIfSameWay;
    private String deathMessagePlayerIgnored;
    private String deathMessagePlayerDecrease;
    private String deathMessagePlayerMaxHealth;
    private String deathMessageLogServerGained;
    private String deathMessageLogServerDecrease;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        this.hpStart = plugin.getConfig().getInt("HP.start", 20);
        this.gainedPerDeath = plugin.getConfig().getInt("HP.gained.per_death", 2);
        this.gainedMaxEnabled = plugin.getConfig().getBoolean("HP.gained.max.enabled", false);
        this.gainedMaxAmount = plugin.getConfig().getInt("HP.gained.max.amount", 114);
        this.decreaseEnabled = plugin.getConfig().getBoolean("HP.decrease.enabled", false);
        this.decreasePerDeath = plugin.getConfig().getInt("HP.decrease.per_death", 2);
        this.decreaseMin = plugin.getConfig().getInt("HP.decrease.min", 2);
        this.deathMustDifference = plugin.getConfig().getBoolean("death.must_difference", true);
        this.deathIgnored = plugin.getConfig().getStringList("death.ignored");
        this.decreaseCause = plugin.getConfig().getStringList("death.decrease");
        this.deathMessagePlayerGained = plugin.getConfig().getString("notifications.death_message.player.gained");
        this.deathMessagePlayerIfSameWay = plugin.getConfig().getString("notifications.death_message.player.ifSameWay");
        this.deathMessagePlayerIgnored = plugin.getConfig().getString("notifications.death_message.player.ignored");
        this.deathMessagePlayerDecrease = plugin.getConfig().getString("notifications.death_message.player.decrease");
        this.deathMessagePlayerMaxHealth = plugin.getConfig().getString("notifications.death_message.player.maxHealth");
        this.deathMessageLogServerGained = plugin.getConfig().getString("notifications.death_message.logServer.gained");
        this.deathMessageLogServerDecrease = plugin.getConfig().getString("notifications.death_message.logServer.decrease");
    }

    // HP settings
    public int getHpStart() {
        return hpStart;
    }

    public int getGainedPerDeath() {
        return gainedPerDeath;
    }

    public boolean isGainedMaxEnabled() {
        return gainedMaxEnabled;
    }

    public int getGainedMaxAmount() {
        return gainedMaxAmount;
    }

    public boolean isDecreaseEnabled() {
        return decreaseEnabled;
    }

    public int getDecreasePerDeath() {
        return decreasePerDeath;
    }

    public int getDecreaseMin() {
        return decreaseMin;
    }

    // Death settings
    public boolean isDeathMustDifference() {
        return deathMustDifference;
    }

    public List<String> getDeathIgnored() {
        return deathIgnored;
    }

    public List<String> getDecreaseCause() {
        return decreaseCause;
    }


    // Notification settings
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

    public String getDeathMessageLogServerGained() {
        return deathMessageLogServerGained;
    }

    public String getDeathMessageLogServerDecrease() {
        return deathMessageLogServerDecrease;
    }
}