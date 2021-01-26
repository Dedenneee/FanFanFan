package net.kunmc.lab;

import net.kunmc.lab.util.FanUtil;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class FanTask extends BukkitRunnable {
    FanUtil util = new FanUtil();
    EntityAction entityAction = new EntityAction();
    @Override
    public void run(){
        HashMap<Long, Chunk> uniqueChunkMap = new HashMap<>();
        Bukkit.getServer().getOnlinePlayers().forEach(player ->{
            Chunk chunk[] = player.getWorld().getLoadedChunks();
            for (Chunk value : chunk) {
                if (!uniqueChunkMap.containsKey(value.getChunkKey())) {
                    uniqueChunkMap.put(value.getChunkKey(), value);
                }
            }

            if(player.getGameMode() == GameMode.SPECTATOR || player.isFlying() ){
                return;
            }

            boolean isSneaking = player.isSneaking();
            entityAction.checkVerticalAxis(player);
            if(!isSneaking){
                entityAction.checkHorizontalAxis(player,"X","positive");
                entityAction.checkHorizontalAxis(player,"X","negative");
                entityAction.checkHorizontalAxis(player,"Z","positive");
                entityAction.checkHorizontalAxis(player,"Z","negative");
            }

        });
        ArrayList<Entity[]> entitiesList = new ArrayList<>();
        for(Map.Entry<Long,Chunk> entry : uniqueChunkMap.entrySet()){
            entitiesList.add(entry.getValue().getEntities());
        }

        entitiesList.stream()
                .forEach(entities -> Stream.of(entities)
                        .filter(entity -> util.checkMovingEntity(entity))
                        .forEach(entity -> {
                                    entityAction.checkVerticalAxis(entity);
                                    entityAction.checkHorizontalAxis(entity,"X","positive");
                                    entityAction.checkHorizontalAxis(entity,"X","negative");
                                    entityAction.checkHorizontalAxis(entity,"Z","positive");
                                    entityAction.checkHorizontalAxis(entity,"Z","negative");
                        }
                        ));
    }

}
