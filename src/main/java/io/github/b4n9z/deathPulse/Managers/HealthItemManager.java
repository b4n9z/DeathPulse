package io.github.b4n9z.deathPulse.Managers;

import io.github.b4n9z.deathPulse.DeathPulse;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HealthItemManager {
    private final DeathPulse plugin;
    private ItemStack healthItem;

    public HealthItemManager(DeathPulse plugin) {
        this.plugin = plugin;
        createHealthItem();
    }

    private void createHealthItem() {
        ItemStack item = new ItemStack(Material.valueOf(plugin.getConfigManager().getHealthItemMaterial()));
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName(
            ChatColor.translateAlternateColorCodes(
                '&',
                plugin.getConfigManager().getHealthItemName())
        );

        List<String> lore = plugin.getConfigManager().getHealthItemLore().stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
        meta.setLore(lore);

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON);
        try {
            meta.setEnchantmentGlintOverride(true);
        } catch (IllegalArgumentException ignored) {
            // Ignored if Minecraft version is too old
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(new NamespacedKey(plugin, "healthItem"), PersistentDataType.BYTE, (byte) 1);

        item.setItemMeta(meta);
        this.healthItem = item;
    }

    public boolean isHealthItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        PersistentDataContainer pdc = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer();
        return pdc.has(new NamespacedKey(plugin, "healthItem"), PersistentDataType.BYTE);
    }

    public ItemStack getHealthItem() {
        return healthItem.clone();
    }

    public int getHealthPerItem() {
        return (int) plugin.getConfigManager().getHealthItemHealthPerItem();
    }
}
