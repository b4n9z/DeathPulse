package io.github.b4n9z.deathPulse.Managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class DeathDataManager {
    private final Plugin plugin;
    private final Gson gson = new Gson();
    private final File dataFolder;
    private final Map<UUID, Set<String>> playerDeathCache = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public DeathDataManager(Plugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "death_data");
        if (!dataFolder.exists()) dataFolder.mkdirs();
    }

    public boolean logDeath(UUID playerUUID, String deathCause) {
        Set<String> deathCauses = playerDeathCache.computeIfAbsent(playerUUID, this::loadPlayerDeaths);
        boolean isNewDeath = deathCauses.add(deathCause);
        savePlayerDeaths(playerUUID, deathCauses);
        return isNewDeath;
    }

    public Set<String> loadPlayerDeaths(UUID playerUUID) {
        lock.lock();
        try {
            if (playerDeathCache.containsKey(playerUUID)) {
                return playerDeathCache.get(playerUUID);
            }
            File file = new File(dataFolder, playerUUID.toString() + ".json");
            if (!file.exists()) return new HashSet<>();

            try (FileReader reader = new FileReader(file)) {
                Type type = new TypeToken<Set<String>>() {}.getType();
                return gson.fromJson(reader, type);
            } catch (IOException e) {
                Bukkit.getLogger().severe("Failed to load death data for " + playerUUID);
                return new HashSet<>();
            }
        } finally {
            lock.unlock();
        }
    }

    private void savePlayerDeaths(UUID playerUUID, Set<String> deathCauses) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            lock.lock();
            try {
                File file = new File(dataFolder, playerUUID.toString() + ".json");

                try (FileWriter writer = new FileWriter(file)) {
                    gson.toJson(deathCauses, writer);
                } catch (IOException e) {
                    Bukkit.getLogger().severe("Failed to save death data for " + playerUUID);
                }
            } finally {
                lock.unlock();
            }
        });
    }

    public boolean removePlayerDeathData(UUID playerUUID) {
        if (lock.tryLock()) {
            try {
                playerDeathCache.remove(playerUUID);
                File file = new File(dataFolder, playerUUID.toString() + ".json");
                if (file.exists()) {
                    file.delete();
                }
                return true; // Successful deletion
            } finally {
                lock.unlock();
            }
        } else {
            return false; // Another operation is in progress
        }
    }

    public boolean removeAllDeathData() {
        if (lock.tryLock()) {
            try {
                playerDeathCache.clear();
                File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".json"));
                if (files != null) {
                    for (File file : files) {
                        file.delete();
                    }
                }
                return true; // Successful deletion
            } finally {
                lock.unlock();
            }
        } else {
            return false; // Another operation is in progress
        }
    }
}