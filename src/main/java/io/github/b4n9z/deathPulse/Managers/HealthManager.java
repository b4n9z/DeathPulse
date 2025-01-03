package io.github.b4n9z.deathPulse.Managers;

import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class HealthManager{
    private static final Attribute MAX_HEALTH_ATTRIBUTE;

    static {
        Attribute attribute;
        try {
            // Try to load GENERIC_MAX_HEALTH
            attribute = (Attribute) Attribute.class.getField("GENERIC_MAX_HEALTH").get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Fallback to MAX_HEALTH
            try {
                attribute = (Attribute) Attribute.class.getField("MAX_HEALTH").get(null);
            } catch (IllegalAccessException | NoSuchFieldException ex) {
                throw new RuntimeException(ex);
            }
        }
        MAX_HEALTH_ATTRIBUTE = attribute;
    }

    public static void setMaxHealth(double decimal, Player player){
        player.getAttribute(MAX_HEALTH_ATTRIBUTE).setBaseValue(decimal);
    }
    public static double getMaxHealth(Player player){
        return player.getAttribute(MAX_HEALTH_ATTRIBUTE).getBaseValue();
    }
    public static void setOfflinePlayerMaxHealth(double decimal, OfflinePlayer player){
        player.getPlayer().getAttribute(MAX_HEALTH_ATTRIBUTE).setBaseValue(decimal);
    }
    public static void healPlayer(Player player){
        player.setHealth(getMaxHealth(player));
    }
    public static double getHealth(Player player){
        return player.getHealth();
    }
}