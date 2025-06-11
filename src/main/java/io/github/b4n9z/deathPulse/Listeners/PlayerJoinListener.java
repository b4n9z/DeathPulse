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
            HealthManager.setMaxHealth(plugin.getConfigManager().getHPStart(), player);
            HealthManager.healPlayer(player);
        }

        boolean status = false;
        for (String deathType: plugin.getConfigManager().getPriority()) {
            if (deathType.equalsIgnoreCase("IGNORE")) {
                status = plugin.getDayManager().processDayTypePerPlayer(player, "ignored");
            } else if (deathType.equalsIgnoreCase("INCREASE")) {
                status = plugin.getDayManager().processDayTypePerPlayer(player, "increase");
            } else if (deathType.equalsIgnoreCase("DECREASE")) {
                status = plugin.getDayManager().processDayTypePerPlayer(player, "decrease");
            }

            if (status) break;
        }

        plugin.getDayManager().processDayTypePerPlayer(player, "season");
    }
}