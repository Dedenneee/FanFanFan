package net.kunmc.lab;

import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

public final class FanPlugin extends JavaPlugin {

    public static FileConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();
        config = getConfig();

        FanUtil.xAxisVector = new Vector(config.getDouble("velocity.x"),0D,0D);
        FanUtil.yAxisVector = new Vector(0D,config.getDouble("velocity.y"),0D);
        FanUtil.zAxisVector = new Vector(0D,0D,config.getDouble("velocity.z"));
        FanUtil.fall_velocity = config.getDouble("velocity.fall");
        FanUtil.powerMap = new HashMap<String, Integer>(){
            {
                put("IRON", config.getInt("power.iron"));
                put("ACACIA", config.getInt("power.acacia"));
                put("OAK", config.getInt("power.oak"));
                put("JUNGLE", config.getInt("power.jungle"));
                put("BIRCH", config.getInt("power.birch"));
                put("DARK_OAK", config.getInt("power.dark_oak"));
                put("SPRUCE", config.getInt("power.spruce"));
            }
        };
        FanUtil.max_distance = config.getInt("power.limit");
        getLogger().info("扇風機プラグインがオンになりました");
        new FanAction().runTaskTimer(this,0L,1L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("扇風機プラグインがオフになりました");
    }


}
