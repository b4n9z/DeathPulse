package io.github.b4n9z.deathPulse.Listeners;

import io.github.b4n9z.deathPulse.DeathPulse;
import io.github.b4n9z.deathPulse.Managers.HealthManager;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.profile.PlayerProfile;

import java.util.Date;
import java.util.UUID;

public class PlayerDeathListener implements Listener {
    private final DeathPulse plugin;

    public PlayerDeathListener(DeathPulse plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        BanEntry entry = null;
        if (player == null) return;
        UUID playerUUID = player.getUniqueId();
        String deathCause = event.getDamageSource().getDamageType().getTranslationKey();

        if (plugin.getConfigManager().getDeathIgnored().contains(deathCause)) {
            String msgPlayer = plugin.getConfigManager().getDeathMessagePlayerIgnored()
                    .replace("&","§")
                    .replace("{cause}",deathCause);
            player.sendMessage(msgPlayer);
            return;
        }

        if (plugin.getConfigManager().isDecreaseEnabled() && plugin.getConfigManager().getDecreaseCause().contains(deathCause)) {
            double newMaxHealth = HealthManager.getMaxHealth(player) - plugin.getConfigManager().getDecreasePerDeath();
            plugin.getDeathDataManager().logDeath(playerUUID, deathCause);
            if(plugin.getConfigManager().isDecreaseMinEnabled() && newMaxHealth < plugin.getConfigManager().getDecreaseMinAmount()){
                newMaxHealth = plugin.getConfigManager().getDecreaseMinAmount();
            } else if(!plugin.getConfigManager().isDecreaseMinEnabled() && newMaxHealth <= 0){
                newMaxHealth = 0;
                long durationInMillis = (long) plugin.getConfigManager().getDecreaseBanTime() * 60 * 60 * 1000;
                Date banTime = new Date(System.currentTimeMillis() + durationInMillis);
                BanList<PlayerProfile> banList = Bukkit.getServer().getBanList(BanList.Type.PROFILE);
                PlayerProfile playerProfile = player.getPlayerProfile();
                BanEntry<PlayerProfile> banEntry = banList.getBanEntry(playerProfile);
                if (banEntry == null) {
                    banList.addBan(playerProfile, "You have been banned due to low health", banTime, null);
                } else {
                    banEntry.setExpiration(banTime);
                }
                player.kickPlayer("You have been banned due to low health");
            }
            HealthManager.setMaxHealth(newMaxHealth, player);
            String msgPlayer = plugin.getConfigManager().getDeathMessagePlayerDecrease()
                    .replace("&","§")
                    .replace("{decrease}",String.valueOf(plugin.getConfigManager().getDecreasePerDeath()))
                    .replace("{cause}",deathCause);
            String msgServer = plugin.getConfigManager().getDeathMessageLogServerDecrease()
                    .replace("&","§")
                    .replace("{name}",player.getName())
                    .replace("{decrease}",String.valueOf(plugin.getConfigManager().getDecreasePerDeath()))
                    .replace("{cause}",deathCause);
            player.sendMessage(msgPlayer);
            plugin.getLogger().info(msgServer);
            return;
        }

        double currentMaxHealth = HealthManager.getMaxHealth(player);
        double newMaxHealth = currentMaxHealth + plugin.getConfigManager().getGainedPerDeath();
        String msgPlayer = plugin.getConfigManager().getDeathMessagePlayerGained()
                .replace("&", "§")
                .replace("{gain}", String.valueOf(plugin.getConfigManager().getGainedPerDeath()))
                .replace("{cause}", deathCause);
        String msgServer = plugin.getConfigManager().getDeathMessageLogServerGained()
                .replace("&", "§")
                .replace("{name}", player.getName())
                .replace("{gain}", String.valueOf(plugin.getConfigManager().getGainedPerDeath()))
                .replace("{cause}", deathCause);

        if (!plugin.getConfigManager().isGainedMaxEnabled() || newMaxHealth <= plugin.getConfigManager().getGainedMaxAmount()) {
            boolean isNewDeathType = plugin.getDeathDataManager().logDeath(playerUUID, deathCause);

            if (isNewDeathType) {
                HealthManager.setMaxHealth(newMaxHealth, player);
                player.sendMessage(msgPlayer);
                plugin.getLogger().info(msgServer);
            } else if (!plugin.getConfigManager().isDeathMustDifference()) {
                HealthManager.setMaxHealth(newMaxHealth, player);
                player.sendMessage(msgPlayer);
                plugin.getLogger().info(msgServer);
            } else {
                String sameWayDeath = plugin.getConfigManager().getDeathMessagePlayerIfSameWay()
                        .replace("&", "§");
                player.sendMessage(sameWayDeath);
            }
        } else {
            String msgPlayerIfMaxHealth = plugin.getConfigManager().getDeathMessagePlayerMaxHealth()
                    .replace("&", "§");
            player.sendMessage(msgPlayerIfMaxHealth);
        }
    }
}
