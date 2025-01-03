package io.github.b4n9z.deathPulse.Listeners;

import io.github.b4n9z.deathPulse.DeathPulse;
import io.github.b4n9z.deathPulse.Managers.HealthManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final DeathPulse plugin;

    public PlayerJoinListener (DeathPulse plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            HealthManager.setMaxHealth(plugin.getConfigManager().getHpStart(), player);
            HealthManager.healPlayer(player);
        }
    }
}