package io.github.b4n9z.deathPulse.Managers;

import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class HealthManager{
    public static void setMaxHealth(double decimal, Player player){
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(decimal);
    }
    public static double getMaxHealth(Player player){
        return player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
    }
    public static void setOfflinePlayerMaxHealth(double decimal, OfflinePlayer player){
        player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(decimal);
    }
    public static void healPlayer(Player player){
        player.setHealth(getMaxHealth(player));
    }
    public static double getHealth(Player player){
        return player.getHealth();
    }
}