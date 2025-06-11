package io.github.b4n9z.deathPulse.Managers;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DayManager {
    private final DeathPulse plugin;
    private int taskId = -1;
    private final Map<UUID, Set<String>> warnedDays = new HashMap<>();

    public DayManager(DeathPulse plugin) {
        this.plugin = plugin;
    }

    public void start() {
        // Cancel existing task if running
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }

        // Schedule new task to check every 5 minutes
        taskId = new BukkitRunnable() {
            @Override
            public void run() {
                checkAndWarnPlayers();
            }
        }.runTaskTimer(plugin, 0, 20 * 60).getTaskId();  // 1 minutes interval
    }

    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        warnedDays.clear();  // Clear memory when plugin is disabled
    }

    private void checkAndWarnPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            checkAndWarnPlayer(player);
        }
    }

    private void checkAndWarnPlayer(Player player) {
        // Check for each day type
        if (plugin.getConfigManager().isIncreaseEnabled() && plugin.getConfigManager().isIncreaseDayEnabled() && isMultipleDay(player, "increase")) {
            checkAndWarnForDayType(player, "increase");
        }
        if (plugin.getConfigManager().isDecreaseEnabled() && plugin.getConfigManager().isDecreaseDayEnabled() && isMultipleDay(player, "decrease")) {
            checkAndWarnForDayType(player, "decrease");
        }
        if (plugin.getConfigManager().isIgnoredEnabled() && plugin.getConfigManager().isIgnoredDayEnabled() && isMultipleDay(player, "ignored")) {
            checkAndWarnForDayType(player, "ignored");
        }
        if (plugin.getConfigManager().isSeasonEnabled() && isMultipleDay(player, "season")) {
            checkAndWarnForDayType(player, "season");
        }
    }

    private void checkAndWarnForDayType(Player player, String type) {
        World world = player.getWorld();
        int currentDay = getCurrentDay(world, type);
        String dayKey = type + "_" + currentDay;

        // Skip if already warned for this day and type
        if (hasBeenWarned(player.getUniqueId(), dayKey)) {
            return;
        }

        if (isMultipleDay(player, type)) {
            String message = getWarningMessage(type);
            if (message != null) {
                player.sendMessage(message);
                player.sendTitle("Day §c" + currentDay + "!§f", message, 10, 100, 20);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
                markAsWarned(player.getUniqueId(), dayKey);
            }
        }
    }

    private boolean hasBeenWarned(UUID playerId, String dayKey) {
        return warnedDays.getOrDefault(playerId, Collections.emptySet()).contains(dayKey);
    }

    private void markAsWarned(UUID playerId, String dayKey) {
        warnedDays.computeIfAbsent(playerId, k -> new HashSet<>()).add(dayKey);
    }

    private String getWarningMessage(String type) {
        return switch (type.toLowerCase()) {
            case "increase" -> plugin.getConfigManager().getIncreaseDayWarning();
            case "decrease" -> plugin.getConfigManager().getDecreaseDayWarning();
            case "ignored" -> plugin.getConfigManager().getIgnoredDayWarning();
            case "season" -> plugin.getConfigManager().getSeasonChangeWarning();
            default -> null;
        };
    }

    public int getSeason(Player player, String type) {
        World world = player.getWorld();
        int currentDay = getCurrentDay(world, type);
        List<Integer> days = getDaysForType(type);
        int season = 1;
        if (isMultipleDay(player, type)) {
            for (int day : days) {
                return (currentDay / day)+1;
            }
        }
        return season;
    }

    public int getCurrentDay(World world, String type) {
        String dayType = switch (type) {
            case "increase" -> plugin.getConfigManager().getIncreaseDayType();
            case "ignored" -> plugin.getConfigManager().getIgnoredDayType();
            case "decrease" -> plugin.getConfigManager().getDecreaseDayType();
            default -> plugin.getConfigManager().getSeasonType();
        };

        if ("real".equalsIgnoreCase(dayType)) {
            long currentTimeMillis = System.currentTimeMillis();
            return (int) (currentTimeMillis / (1000 * 60 * 60 * 24)) - plugin.getConfigManager().getFirstTimeSetup();
        } else {
            return (int) (world.getFullTime() / 24000);
        }
    }

    public boolean isMultipleDay(Player player, String type) {
        World world = player.getWorld();
        int currentDay = getCurrentDay(world, type);
        List<Integer> days = getDaysForType(type);

        for (int day : days) {
            if (currentDay % day == 0 && currentDay != 0) {
                return true;
            }
        }
        return false;
    }

    private List<Integer> getDaysForType(String type) {
        List<Integer> seasonDayList = new ArrayList<>();
        seasonDayList.add(plugin.getConfigManager().getSeasonDay());
        return switch (type.toLowerCase()) {
            case "increase" -> plugin.getConfigManager().getIncreaseDays();
            case "ignored" -> plugin.getConfigManager().getIgnoredDays();
            case "decrease" -> plugin.getConfigManager().getDecreaseDays();
            default -> seasonDayList;
        };
    }

    public void firstTimeSetup() {
        if (plugin.getConfigManager().getFirstTimeSetup() != 0) return;
        long daysFirst = (System.currentTimeMillis() / (1000 * 60 * 60 * 24))-1;
        plugin.getConfigManager().setFirstTimeSetup((int) daysFirst);
    }
}