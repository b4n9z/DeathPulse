package io.github.b4n9z.deathPulse.Managers;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;

import java.util.Date;

public class BanManager {
    private final DeathPulse plugin;

    public BanManager(DeathPulse plugin) {
        this.plugin = plugin;
    }

    public void banPlayer(Player player, long durationInMillis) {
        Date banTime = new Date(System.currentTimeMillis() + durationInMillis);
        BanList<PlayerProfile> banList = Bukkit.getServer().getBanList(BanList.Type.PROFILE);
        PlayerProfile playerProfile = player.getPlayerProfile();
        BanEntry<PlayerProfile> banEntry = banList.getBanEntry(playerProfile);

        if (banEntry == null) {
            banList.addBan(playerProfile, plugin.getConfigManager().getDeathMessagePlayerBanReason(), banTime, null);
        } else {
            banEntry.setExpiration(banTime);
        }

        plugin.getLogger().info(plugin.getConfigManager().getDeathMessageLogServerBanReason());
        player.kickPlayer(plugin.getConfigManager().getDeathMessagePlayerKicked().replace("&", "§"));
    }

    public void banPlayerPermanently(Player player) {
        BanList<PlayerProfile> banList = Bukkit.getServer().getBanList(BanList.Type.PROFILE);
        PlayerProfile playerProfile = player.getPlayerProfile();
        BanEntry<PlayerProfile> banEntry = banList.getBanEntry(playerProfile);

        if (banEntry == null) {
            banList.addBan(playerProfile, plugin.getConfigManager().getDeathMessagePlayerBanReason(), (Date) null, null);
        } else {
            banEntry.setExpiration(null);
        }

        plugin.getLogger().info(plugin.getConfigManager().getDeathMessageLogServerBanReason());
        player.kickPlayer(plugin.getConfigManager().getDeathMessagePlayerKicked().replace("&", "§"));
    }

    public void banOfflinePlayer(OfflinePlayer player, long durationInMillis) {
        Date banTime = new Date(System.currentTimeMillis() + durationInMillis);
        BanList<PlayerProfile> banList = Bukkit.getServer().getBanList(BanList.Type.PROFILE);
        PlayerProfile playerProfile = player.getPlayerProfile();
        BanEntry<PlayerProfile> banEntry = banList.getBanEntry(playerProfile);

        if (banEntry == null) {
            banList.addBan(playerProfile, plugin.getConfigManager().getDeathMessagePlayerBanReason(), banTime, null);
        } else {
            banEntry.setExpiration(banTime);
        }

        plugin.getLogger().info(plugin.getConfigManager().getDeathMessageLogServerBanReason());
    }

    public void banOfflinePlayerPermanently(OfflinePlayer player) {
        BanList<PlayerProfile> banList = Bukkit.getServer().getBanList(BanList.Type.PROFILE);
        PlayerProfile playerProfile = player.getPlayerProfile();
        BanEntry<PlayerProfile> banEntry = banList.getBanEntry(playerProfile);

        if (banEntry == null) {
            banList.addBan(playerProfile, plugin.getConfigManager().getDeathMessagePlayerBanReason(), (Date) null, null);
        } else {
            banEntry.setExpiration(null);
        }

        plugin.getLogger().info(plugin.getConfigManager().getDeathMessageLogServerBanReason());
    }

    public void unbanPlayer(OfflinePlayer player) {
        BanList<PlayerProfile> banList = Bukkit.getServer().getBanList(BanList.Type.PROFILE);
        PlayerProfile playerProfile = player.getPlayerProfile();
        banList.pardon(playerProfile);
    }

    public boolean isPlayerBanned(OfflinePlayer player) {
        BanList<PlayerProfile> banList = Bukkit.getServer().getBanList(BanList.Type.PROFILE);
        PlayerProfile playerProfile = player.getPlayerProfile();
        return banList.isBanned(playerProfile);
    }
}