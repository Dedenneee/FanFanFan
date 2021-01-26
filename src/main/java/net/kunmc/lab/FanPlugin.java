package net.kunmc.lab;

import net.kunmc.lab.bean.Velocity;
import net.kunmc.lab.util.FanUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FanPlugin extends JavaPlugin {

    public static FileConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        config = getConfig();

        FanUtil.fall_velocity = config.getDouble("fall");
        FanUtil.velocityMap = new HashMap<String, Velocity>(){
            {
                put("iron", new Velocity(config.getDouble("iron.velocity.horizontal"),
                        config.getDouble("iron.velocity.vertical")));
                put("acacia", new Velocity(config.getDouble("acacia.velocity.horizontal"),
                        config.getDouble("acacia.velocity.vertical")));
                put("oak", new Velocity(config.getDouble("oak.velocity.horizontal"),
                        config.getDouble("oak.velocity.vertical")));
                put("jungle", new Velocity(config.getDouble("jungle.velocity.horizontal"),
                        config.getDouble("jungle.velocity.vertical")));
                put("birch",  new Velocity(config.getDouble("birch.velocity.horizontal"),
                        config.getDouble("birch.velocity.vertical")));
                put("dark_oak", new Velocity(config.getDouble("dark_oak.velocity.horizontal"),
                        config.getDouble("dark_oak.velocity.vertical")));
                put("spruce", new Velocity(config.getDouble("spruce.velocity.horizontal"),
                        config.getDouble("spruce.velocity.vertical")));
            }
        };
        FanUtil.rangeMap = new HashMap<String, Integer>(){
            {
                put("iron", config.getInt("iron.range"));
                put("acacia", config.getInt("acacia.range"));
                put("oak", config.getInt("oak.range"));
                put("jungle", config.getInt("jungle.range"));
                put("birch", config.getInt("birch.range"));
                put("dark_oak", config.getInt("dark_oak.range"));
                put("spruce", config.getInt("spruce.range"));
            }
        };
        FanUtil.max_distance = config.getInt("limit");
        getLogger().info("扇風機プラグインがオンになりました");
        new FanTask().runTaskTimer(this,0L,1L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("扇風機プラグインがオフになりました");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return Stream.of("horizontal", "vertical", "range")
                        .filter(e -> e.startsWith(args[0]))
                        .collect(Collectors.toList());
            case 2:
                return Stream.of("all","iron", "acacia","oak","jungle","birch","dark_oak","spruce")
                        .filter(e -> e.startsWith(args[1]))
                        .collect(Collectors.toList());
            case 3:
                if(args[0].equalsIgnoreCase("range")){
                    return Collections.singletonList("0");
                }else{
                    return Collections.singletonList("0.00");
                }

        }
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        if(!cmd.getName().equalsIgnoreCase("fanpower")){
            return false;
        }
        if(args.length < 3){
            sender.sendMessage("引数が足りません");
            return false;
        }else if(args.length > 3){
            sender.sendMessage("引数が多すぎます");
            return false;
        }
        String arg1 = args[0];

        if(!(arg1.equalsIgnoreCase("horizontal") || arg1.equalsIgnoreCase("vertical")
                || arg1.equalsIgnoreCase("range"))){
            sender.sendMessage("第1引数には変更したいパラメータを入力してください");
            return false;
        }

        String arg2 = args[1];
        ArrayList<String> materials = new ArrayList<>();
        materials.add("all");
        materials.add("iron");
        materials.add("acacia");
        materials.add("oak");
        materials.add("jungle");
        materials.add("birch");
        materials.add("dark_oak");
        materials.add("spruce");
        if(!materials.contains(arg2)) {
            sender.sendMessage("第2引数にはトラップドアの種類を入力してください");
            return false;
        }

        String arg3 = args[2];
        double power = 0D;
        int range = 0;
        if(arg1.equalsIgnoreCase("range")){
            try {
                range = Integer.parseInt(arg3);
            }catch(NumberFormatException e) {
                sender.sendMessage("第3引数には数値を入力してください");
                return false;
            }
        }else{
            try {
                power = Double.parseDouble(arg3);
            }catch(NumberFormatException e) {
                sender.sendMessage("第3引数には数値を入力してください");
                return false;
            }
        }

        String material = materials.get(materials.indexOf(arg2));
        //水平の強さ変更処理
        if (arg1.equalsIgnoreCase("horizontal") && material.equalsIgnoreCase("all")){
            for (String all : materials){
                if (all.equalsIgnoreCase("all")){
                    continue;
                }
                config.set(all + ".velocity.horizontal",power);
                FanUtil.velocityMap.get(all).setHorizontalVelocity(power);
                saveConfig();
                sender.sendMessage(all + "_TrapDoorの水平方向の力を「"+power+"」に変更しました");

            }
            return true;
        }else if(arg1.equalsIgnoreCase("horizontal") ){
            config.set(material + ".velocity.horizontal",power);
            FanUtil.velocityMap.get(material).setHorizontalVelocity(power);
            saveConfig();
            sender.sendMessage(material + "_TrapDoorの水平方向の力を「"+power+"」に変更しました");
            return true;
        }

        //垂直の強さ変更処理
        if(arg1.equalsIgnoreCase("vertical") && material.equalsIgnoreCase("all")){
            for (String all : materials){
                if (all.equalsIgnoreCase("all")){
                    continue;
                }
                config.set(all + ".velocity.vertical",power);
                FanUtil.velocityMap.get(all).setVerticalVelocity(power);
                saveConfig();
                sender.sendMessage(all + "_TrapDoorの垂直方向の力を「"+power+"」に変更しました");

            }
            return true;
        } else if(arg1.equalsIgnoreCase("vertical")){
            config.set(material + ".velocity.vertical",power);
            FanUtil.velocityMap.get(material).setVerticalVelocity(power);
            saveConfig();
            sender.sendMessage(material + "_TrapDoorの垂直方向の力を「"+power+"」に変更しました");
            return true;
        }

        //有効範囲変更処理
        if(arg1.equalsIgnoreCase("range") && material.equalsIgnoreCase("all")){
            for (String all : materials){
                if (all.equalsIgnoreCase("all")){
                    continue;
                }
                changeRange(sender,all,range);
            }
            return true;
        } else if(arg1.equalsIgnoreCase("range")){
            changeRange(sender,material,range);
            return true;
        }
        return false;
    }

    /**
     * 有効範囲の変更処理
     * @param sender コマンド送信者
     * @param material トラップドアの素材
     * @param range 有効範囲
     */
    public void changeRange(CommandSender sender,String material,Integer range){
        config.set(material + ".range",range);
        FanUtil.rangeMap.replace(material,range);
        if(range > FanUtil.max_distance){
            FanUtil.max_distance = range;
            config.set("limit",range);
            config.set("limit_block",material);
        }else if(config.getString("limit_block").equalsIgnoreCase(material)){
            int max = 0;
            for(Map.Entry<String,Integer> entry : FanUtil.rangeMap.entrySet()){
                if(entry.getValue() > max){
                    max = entry.getValue();
                    config.set("limit",max);
                    config.set("limit_block",entry.getKey());
                }
            }
        }
        saveConfig();
        sender.sendMessage(material + "_TrapDoorの有効範囲を「"+range+"」ブロックに変更しました");
    }
}
