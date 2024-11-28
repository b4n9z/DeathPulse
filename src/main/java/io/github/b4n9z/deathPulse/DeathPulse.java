package io.github.b4n9z.deathPulse;

import io.github.b4n9z.deathPulse.Listeners.*;
import io.github.b4n9z.deathPulse.Managers.*;
import io.github.b4n9z.deathPulse.Commands.*;
import io.github.b4n9z.deathPulse.bStats.Metrics;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathPulse extends JavaPlugin implements CommandExecutor {
    private DeathDataManager deathDataManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigManager();
        loadDeathDataManager();
        registerEvents();
        registerCommands();
        loadMetrics(23923);
        getLogger().info("DeathPulse plugin enabled!");
    }

    @Override
    public void onDisable() {
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

    public void loadConfigManager() {
        configManager = new ConfigManager(this);
    }

    public void loadDeathDataManager() {
        deathDataManager = new DeathDataManager(this);
    }

    private void registerEvents() {
        // Register Events
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }

    private void registerCommands() {
        // Register Command
        CommandExecutor mainCommand = new MainCommand(this);
        this.getCommand("DeathPulse").setExecutor(mainCommand);
        this.getCommand("dp").setExecutor(mainCommand);
        // Register Completer
        this.getCommand("DeathPulse").setTabCompleter(new MainCommandCompleter());
        this.getCommand("dp").setTabCompleter(new MainCommandCompleter());
    }
}
