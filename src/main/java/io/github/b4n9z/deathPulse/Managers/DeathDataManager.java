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

public class DeathDataManager {
    private final Plugin plugin;
    private final Gson gson = new Gson();
    private final File dataFolder;
    private final Map<UUID, Set<String>> playerDeathCache = new HashMap<>();

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

    private Set<String> loadPlayerDeaths(UUID playerUUID) {
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
    }

    private void savePlayerDeaths(UUID playerUUID, Set<String> deathCauses) {
        File file = new File(dataFolder, playerUUID.toString() + ".json");

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(deathCauses, writer);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save death data for " + playerUUID);
        }
    }
}