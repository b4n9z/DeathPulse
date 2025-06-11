package io.github.b4n9z.deathPulse;

import io.github.b4n9z.deathPulse.Listeners.*;
import io.github.b4n9z.deathPulse.Managers.*;
import io.github.b4n9z.deathPulse.Commands.*;
import io.github.b4n9z.deathPulse.bStats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class DeathPulse extends JavaPlugin implements CommandExecutor {
    private DeathDataManager deathDataManager;
    private ConfigManager configManager;
    private BanManager banManager;
    private DayManager dayManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigManager();
        loadBanManager();
        loadDayManager();
        dayManager.start();
        loadDeathDataManager();
        registerEvents();
        registerCommands();
        loadMetrics(23923);
        getLogger().info("DeathPulse plugin enabled!");
    }

    @Override
    public void onDisable() {
        destroyDayManager();
        getLogger().info("DeathPulse plugin disabled!");
    }

    public void loadMetrics(int pluginId) {
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));
    }
    
    public DeathDataManager getDeathDataManager() {
        if (deathDataManager == null) {
            throw new IllegalStateException("DeathDataManager not initialized");
        }
        return deathDataManager;
    }

    public ConfigManager getConfigManager() {
        if (configManager == null) {
            throw new IllegalStateException("ConfigManager not initialized");
        }
        return configManager;
    }

    public BanManager getBanManager() {
        if (banManager == null) {
            throw new IllegalStateException("BanManager not initialized");
        }
        return banManager;
    }

    public DayManager getDayManager() {
        if (dayManager == null) {
            throw new IllegalStateException("DayWarningManager not initialized");
        }
        return dayManager;
    }

    public void loadConfigManager() {
        configManager = new ConfigManager(this);
    }

    public void loadDeathDataManager() {
        deathDataManager = new DeathDataManager(this);
    }

    public void loadBanManager() {
        banManager = new BanManager(this);
    }

    public void loadDayManager() {
        dayManager = new DayManager(this);
        dayManager.firstTimeSetup();
    }

    public void destroyDayManager() {
        if (dayManager == null) return;
        dayManager.stop();
    }

    private void registerEvents() {
        // Register Events
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }

    private void registerCommands() {
        // Register Command
        CommandExecutor mainCommand = new MainCommand(this);
        Objects.requireNonNull(this.getCommand("DeathPulse")).setExecutor(mainCommand);
        Objects.requireNonNull(this.getCommand("dp")).setExecutor(mainCommand);
        // Register Completer
        TabCompleter mainCommandCompleter = new MainCommandCompleter(this);
        Objects.requireNonNull(this.getCommand("DeathPulse")).setTabCompleter(mainCommandCompleter);
        Objects.requireNonNull(this.getCommand("dp")).setTabCompleter(mainCommandCompleter);
    }

    public void sendColoredMessageToConsole(String message) {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        console.sendMessage(message);
    }
}
