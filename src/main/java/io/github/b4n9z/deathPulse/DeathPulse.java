package io.github.b4n9z.deathPulse;

import io.github.b4n9z.deathPulse.Listeners.*;
import io.github.b4n9z.deathPulse.Managers.*;
import io.github.b4n9z.deathPulse.Commands.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathPulse extends JavaPlugin implements CommandExecutor {
    private DeathDataManager deathDataManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigManager();
        registerEventsAndCommands();
        getLogger().info("DeathPulse plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DeathPulse plugin disabled!");
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

    private void registerEventsAndCommands() {
        configManager = new ConfigManager(this); // Load config.yml
        deathDataManager = new DeathDataManager(this);
        //Register Events
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        //Register Command
        CommandExecutor mainCommand = new MainCommand(this);
        this.getCommand("DeathPulse").setExecutor(mainCommand);
        this.getCommand("dp").setExecutor(mainCommand);
        //Register Completer
        this.getCommand("DeathPulse").setTabCompleter(new MainCommandCompleter());
        this.getCommand("dp").setTabCompleter(new MainCommandCompleter());
    }
}
