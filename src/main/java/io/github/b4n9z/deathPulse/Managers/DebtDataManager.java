package io.github.b4n9z.deathPulse.Managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class DebtDataManager {
    private final Plugin plugin;
    private final File dataFolder;
    private final Map<UUID, Integer> playerDebtCache = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public DebtDataManager(Plugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "debt_data");
        if (!dataFolder.exists()) {
            if (!dataFolder.mkdirs()) {
                throw new RuntimeException("Failed to create data directory: " + dataFolder.getAbsolutePath());
            }
        }
    }

    private File getPlayerFile(UUID playerUUID) {
        return new File(dataFolder, playerUUID.toString() + ".dat");
    }

    /** Load debt from file (if not exist → 0). */
    public int loadDebt(UUID playerUUID) {
        lock.lock();
        try {
            if (playerDebtCache.containsKey(playerUUID)) {
                return playerDebtCache.get(playerUUID);
            }

            File file = getPlayerFile(playerUUID);
            if (!file.exists()) {
                playerDebtCache.put(playerUUID, 0);
                return 0;
            }

            try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
                int debt = dis.readInt();
                playerDebtCache.put(playerUUID, debt);
                return debt;
            } catch (IOException e) {
                Bukkit.getLogger().severe("Failed to load debt data for " + playerUUID);
                return 0;
            }
        } finally {
            lock.unlock();
        }
    }

    /** Save debt to file in async. */
    private void saveDebt(UUID playerUUID, int debt) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            lock.lock();
            try {
                File file = getPlayerFile(playerUUID);
                try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
                    dos.writeInt(debt);
                } catch (IOException e) {
                    Bukkit.getLogger().severe("Failed to save debt data for " + playerUUID);
                }
            } finally {
                lock.unlock();
            }
        });
    }

    /** Set new debt and save immediately. */
    public void setDebt(UUID playerUUID, int debt) {
        lock.lock();
        try {
            playerDebtCache.put(playerUUID, debt);
        } finally {
            lock.unlock();
        }
        saveDebt(playerUUID, debt);
    }

    /** Get the value from the cache (if it doesn't exist, load it first). */
    public int getDebt(UUID playerUUID) {
        lock.lock();
        try {
            Integer debt = playerDebtCache.get(playerUUID);
            if (debt == null) {
                debt = loadDebt(playerUUID); // loadDebt
            }
            return debt;
        } finally {
            lock.unlock();
        }
    }

    /** Add debt value. */
    public void addDebt(UUID playerUUID, int amount) {
        if (amount <= 0) return;
        int newDebt = getDebt(playerUUID) + amount;
        setDebt(playerUUID, newDebt);
    }

    /** Reduce debt value. */
    public int reduceDebt(UUID playerUUID, int amount) {
        int currentDebt = getDebt(playerUUID);
        int newDebt = currentDebt - amount;

        if (newDebt < 0) {
            // calculate leftover
            int leftover = -newDebt;
            setDebt(playerUUID, 0);
            return leftover;
        } else {
            setDebt(playerUUID, newDebt);
            return 0;
        }
    }

    /** Remove player file. */
    public boolean removeDebtData(UUID playerUUID) {
        if (lock.tryLock()) {
            try {
                playerDebtCache.remove(playerUUID);
                File file = getPlayerFile(playerUUID);
                if (file.exists()) {
                    return file.delete();
                }
                return true;
            } finally {
                lock.unlock();
            }
        } else {
            return false;
        }
    }

    /** Remove all file debt. */
    public boolean removeAllDebtData() {
        if (lock.tryLock()) {
            try {
                playerDebtCache.clear();
                File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".dat"));
                if (files != null) {
                    int count = files.length;
                    int deleted = 0;
                    for (File file : files) {
                        if (file.delete()) {
                            deleted++;
                        }
                    }
                    return deleted == count; // Failed to delete all files
                }
                return true;
            } finally {
                lock.unlock();
            }
        } else {
            return false;
        }
    }
}
