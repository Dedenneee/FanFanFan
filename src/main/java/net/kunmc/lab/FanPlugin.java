package net.kunmc.lab;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;

public final class FanPlugin extends JavaPlugin {

    public static FileConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        config = getConfig();

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
        FanUtil.MAX_DISTANCE = config.getInt("power.limit");
        getServer().getPluginManager().registerEvents(new FanListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        if (args.length < 1){
            sender.sendMessage("引数が足りません");
            return false;
        }
        if(cmd.getName().equalsIgnoreCase("fan")){
            if(args[0].equalsIgnoreCase("on")){
                FanUtil.enable = true;
                return true;
            }else if(args[0].equalsIgnoreCase("off")){
                FanUtil.enable = false;
                return true;
            }
        }
        return false;
    }
}
