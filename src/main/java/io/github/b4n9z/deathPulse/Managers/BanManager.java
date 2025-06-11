package io.github.b4n9z.deathPulse.Managers;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;

import java.util.Date;
import java.util.Objects;

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
            banList.addBan(playerProfile, plugin.getConfigManager().getNotificationPlayerBanReason(), banTime, null);
        } else {
            banEntry.setExpiration(banTime);
        }

        plugin.sendColoredMessageToConsole(plugin.getConfigManager().getNotificationConsoleBanReason().replace("{name}", player.getName()));
        player.kickPlayer(plugin.getConfigManager().getNotificationPlayerKicked());
    }

    public void banPlayerPermanently(Player player) {
        BanList<PlayerProfile> banList = Bukkit.getServer().getBanList(BanList.Type.PROFILE);
        PlayerProfile playerProfile = player.getPlayerProfile();
        BanEntry<PlayerProfile> banEntry = banList.getBanEntry(playerProfile);

        if (banEntry == null) {
            banList.addBan(playerProfile, plugin.getConfigManager().getNotificationPlayerBanReason(), (Date) null, null);
        } else {
            banEntry.setExpiration(null);
        }

        plugin.sendColoredMessageToConsole(plugin.getConfigManager().getNotificationConsoleBanReason().replace("{name}", player.getName()));
        player.kickPlayer(plugin.getConfigManager().getNotificationPlayerKicked());
    }

    public void banOfflinePlayer(OfflinePlayer player, long durationInMillis) {
        Date banTime = new Date(System.currentTimeMillis() + durationInMillis);
        BanList<PlayerProfile> banList = Bukkit.getServer().getBanList(BanList.Type.PROFILE);
        PlayerProfile playerProfile = player.getPlayerProfile();
        BanEntry<PlayerProfile> banEntry = banList.getBanEntry(playerProfile);

        if (banEntry == null) {
            banList.addBan(playerProfile, plugin.getConfigManager().getNotificationPlayerBanReason(), banTime, null);
        } else {
            banEntry.setExpiration(banTime);
        }

        plugin.sendColoredMessageToConsole(plugin.getConfigManager().getNotificationConsoleBanReason().replace("{name}", Objects.requireNonNull(player.getName(), "One Player")));
    }

    public void banOfflinePlayerPermanently(OfflinePlayer player) {
        BanList<PlayerProfile> banList = Bukkit.getServer().getBanList(BanList.Type.PROFILE);
        PlayerProfile playerProfile = player.getPlayerProfile();
        BanEntry<PlayerProfile> banEntry = banList.getBanEntry(playerProfile);

        if (banEntry == null) {
            banList.addBan(playerProfile, plugin.getConfigManager().getNotificationPlayerBanReason(), (Date) null, null);
        } else {
            banEntry.setExpiration(null);
        }

        plugin.sendColoredMessageToConsole(plugin.getConfigManager().getNotificationConsoleBanReason().replace("{name}", Objects.requireNonNull(player.getName(), "One Player")));
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