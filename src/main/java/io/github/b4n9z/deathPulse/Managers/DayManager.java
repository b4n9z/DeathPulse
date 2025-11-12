package io.github.b4n9z.deathPulse.Managers;

import io.github.b4n9z.deathPulse.DeathPulse;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DayManager {
    private final DeathPulse plugin;
    private int taskId = -1;
    private final Map<UUID, Set<String>> warnedDays = new HashMap<>();
    private final Set<String> lastCheckedDay = ConcurrentHashMap.newKeySet();
    private final Map<String, Boolean> dayTypeCache = Collections.synchronizedMap(new LinkedHashMap<>(100, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
            return size() > 100;
        }
    });


    public DayManager(DeathPulse plugin) {
        this.plugin = plugin;
    }

    public void start() {
        // Cancel existing task if running
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }

        // Run the task asynchronously to prevent server lag
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::checkAndWarnPlayers, 0, 20 * plugin.getConfigManager().getCheckDayPeriod()).getTaskId();
    }

    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        // Clear memory when plugin is disabled
        warnedDays.clear();
        lastCheckedDay.clear();
        dayTypeCache.clear();
    }

    private void checkAndWarnPlayers() {
        // Process each world in a separate task to prevent blocking
        for (World world : plugin.getConfigManager().getConfiguredWorlds()) {
            Bukkit.getScheduler().runTask(plugin, () -> processWorld(world));
        }
    }

    private void processWorld(World world) {
        String worldName = world.getName();

        int realDay = getCurrentDay(world, "real"); // Default to real day check
        int gameDay = getCurrentDay(world, "minecraft"); // Default to minecraft day check

        String dayKey = worldName + "_g" + gameDay + "_r" + realDay;

        // Skip if we've already checked this day
        if (lastCheckedDay.contains(dayKey)) {
            return;
        }

        if (lastCheckedDay.size() > 1000) lastCheckedDay.clear();

        lastCheckedDay.add(dayKey);

        // Process each day type
        boolean status = false;
        for (String deathType: plugin.getConfigManager().getPriority()) {
            if (deathType.equalsIgnoreCase("IGNORE")) {
                status = processDayType(world, "ignored");
            } else if (deathType.equalsIgnoreCase("INCREASE")) {
                status = processDayType(world, "increase");
            } else if (deathType.equalsIgnoreCase("DECREASE")) {
                status = processDayType(world, "decrease");
            }

            if (status) break;
        }

        processDayType(world, "season");
    }

    private boolean processDayType(World world, String type) {
        if (world == null || !isDayTypeEnabled(type) || !isMultipleDay(world, type)) {
            return false;
        }

        boolean isIgnoredDay = isMultipleDay(world, "ignored");
        boolean isIncreaseDay = isMultipleDay(world, "increase");
        boolean isDecreaseDay = isMultipleDay(world, "decrease");

        if (shouldSkipDayType(type, isIgnoredDay, isIncreaseDay, isDecreaseDay)) return false;

        if (type.equals("season") && plugin.getConfigManager().isResetWorldDay()) {
            long daysFirst = (System.currentTimeMillis() / (1000 * 60 * 60 * 24))-1;
            plugin.getConfigManager().setFirstTimeSetup((int) daysFirst);

            world.setTime(0);
        }

        String message = getWarningMessage(type);
        if (message == null || message.isEmpty()) {
            return false;
        }

        String dayKey = type + "_" + getCurrentDay(world, getTypeDays(type));

        // Process players in batches
        List<Player> players = new ArrayList<>(world.getPlayers());
        int batchSize = 100; // Process 100 players per tick
        for (int i = 0; i < players.size(); i += batchSize) {
            final int start = i;
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (int j = start; j < Math.min(start + batchSize, players.size()); j++) {
                    Player player = players.get(j);
                    if (!hasBeenWarned(player.getUniqueId(), dayKey)) {
                        player.sendMessage(message);
                        player.sendTitle("§eDay §c" + getCurrentDay(world, getTypeDays(type)) + "§f!","", 10, 70, 20);
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
                        markAsWarned(player.getUniqueId(), dayKey);
                    }
                }
            });
        }

        return true;
    }

    public boolean processDayTypePerPlayer(Player player, String type) {
        World world = player.getWorld();
        if (isDayTypeEnabled(type) && isMultipleDay(world, type)) {
            boolean isIgnoredDay = isMultipleDay(world, "ignored");
            boolean isIncreaseDay = isMultipleDay(world, "increase");
            boolean isDecreaseDay = isMultipleDay(world, "decrease");

            if (shouldSkipDayType(type, isIgnoredDay, isIncreaseDay, isDecreaseDay)) return false;

            String message = getWarningMessage(type);
            if (message == null || message.isEmpty()) {
                return false;
            }

            String dayKey = type + "_" + getCurrentDay(world, getTypeDays(type));
            if (hasBeenWarned(player.getUniqueId(), dayKey)) {
                return false;
            }

            // Process One player
            player.sendMessage(message);
            player.sendTitle("§eDay §c" + getCurrentDay(world, getTypeDays(type)) + "§f!","", 10, 70, 20);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
            markAsWarned(player.getUniqueId(), dayKey);
            return true;
        }
        return false;
    }

    private boolean isDayTypeEnabled(String type) {
        return switch (type.toLowerCase()) {
            case "ignored" -> plugin.getConfigManager().isIgnoredDayEnabled();
            case "increase" -> plugin.getConfigManager().isIncreaseDayEnabled();
            case "decrease" -> plugin.getConfigManager().isDecreaseDayEnabled();
            case "season" -> plugin.getConfigManager().isSeasonEnabled();
            default -> false;
        };
    }

    private boolean hasBeenWarned(UUID playerId, String dayKey) {
        return warnedDays.getOrDefault(playerId, Collections.emptySet()).contains(dayKey);
    }

    private void markAsWarned(UUID playerId, String dayKey) {
        warnedDays.computeIfAbsent(playerId, k -> new HashSet<>()).add(dayKey);
    }

    private String getWarningMessage(String type) {
        return switch (type.toLowerCase()) {
            case "ignored" -> plugin.getConfigManager().getIgnoredDayWarning();
            case "increase" -> plugin.getConfigManager().getIncreaseDayWarning();
            case "decrease" -> plugin.getConfigManager().getDecreaseDayWarning();
            case "season" -> plugin.getConfigManager().getSeasonChangeWarning();
            default -> null;
        };
    }

    public int getSeason(World world, String type) {
        int currentDay = getCurrentDay(world, getTypeDays(type));
        List<Integer> days = getDaysForType(type);
        int season = 1;
        if (isMultipleDay(world, type)) {
            for (int day : days) {
                return (currentDay / day)+1;
            }
        }
        return season;
    }

    public int getCurrentDay(World world, String dayType) {
        if ("real".equalsIgnoreCase(dayType)) {
            long currentTimeMillis = System.currentTimeMillis();
            return (int) (currentTimeMillis / (1000 * 60 * 60 * 24)) - plugin.getConfigManager().getFirstTimeSetup();
        } else {
            return (int) (world.getFullTime() / plugin.getConfigManager().getTicksPerDay(world));
        }
    }

    public boolean isMultipleDay(World world, String type) {
        int currentDay = getCurrentDay(world, getTypeDays(type));
        String cacheKey = world.getName() + "_" + type + "_" + currentDay;

        // Check cache first
        if (dayTypeCache.containsKey(cacheKey)) {
            return dayTypeCache.get(cacheKey);
        }

        List<Integer> days = getDaysForType(type);
        boolean result = false;

        for (int day : days) {
            if (currentDay % day == 0 && currentDay != 0) {
                result = true;
                break;
            }
        }
        dayTypeCache.put(cacheKey, result);

        return result;
    }

    public String getTypeDays(String type) {
        return switch (type.toLowerCase()) {
            case "ignored" -> plugin.getConfigManager().getIgnoredDayType();
            case "increase" -> plugin.getConfigManager().getIncreaseDayType();
            case "decrease" -> plugin.getConfigManager().getDecreaseDayType();
            default -> plugin.getConfigManager().getSeasonType();
        };
    }

    private List<Integer> getDaysForType(String type) {
        List<Integer> seasonDayList = new ArrayList<>();
        seasonDayList.add(plugin.getConfigManager().getSeasonDay());
        return switch (type.toLowerCase()) {
            case "ignored" -> plugin.getConfigManager().getIgnoredDays();
            case "increase" -> plugin.getConfigManager().getIncreaseDays();
            case "decrease" -> plugin.getConfigManager().getDecreaseDays();
            default -> seasonDayList;
        };
    }

    public void firstTimeSetup() {
        if (plugin.getConfigManager().getFirstTimeSetup() != 0) return;
        long daysFirst = (System.currentTimeMillis() / (1000 * 60 * 60 * 24))-1;
        plugin.getConfigManager().setFirstTimeSetup((int) daysFirst);
    }

    private boolean shouldSkipDayType(String current, boolean ignored, boolean increase, boolean decrease) {
        return switch (current.toLowerCase()) {
            case "ignored" -> (increase && plugin.getConfigManager().isIncreaseDayDeActiveIgnoredDay()) ||
                    (decrease && plugin.getConfigManager().isDecreaseDayDeActiveIgnoredDay());
            case "increase" -> (ignored && plugin.getConfigManager().isIgnoredDayDeActiveIncreaseDay()) ||
                    (decrease && plugin.getConfigManager().isDecreaseDayDeActiveIncreaseDay());
            case "decrease" -> (ignored && plugin.getConfigManager().isIgnoredDayDeActiveDecreaseDay()) ||
                    (increase && plugin.getConfigManager().isIncreaseDayDeActiveDecreaseDay());
            default -> false;
        };
    }

    public void clearLastCheckedDay(){
        lastCheckedDay.clear();
    }
}