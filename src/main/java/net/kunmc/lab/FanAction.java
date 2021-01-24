package net.kunmc.lab;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FanAction extends BukkitRunnable {
    FanUtil util = new FanUtil();

    @Override
    public void run(){
        Bukkit.getServer().getOnlinePlayers().forEach(player ->{
            if(player.getGameMode() == GameMode.SPECTATOR || player.isFlying() ){
                return;
            }
            boolean isSneaking = player.isSneaking();
            checkVerticalAxis(player,isSneaking);
            if(!isSneaking){
                checkHorizontalAxis(player,"X","positive");
                checkHorizontalAxis(player,"X","negative");
                checkHorizontalAxis(player,"Z","positive");
                checkHorizontalAxis(player,"Z","negative");
            }
        });

    }

    /**
     * 足元のブロックをチェックして、扇風機なら飛ぶ
     * @param player プレイヤー
     */
    public void checkVerticalAxis(Player player,boolean isSneaking){
        Location loc = player.getLocation();

        boolean isYTrapDoor = false;
        Block verticalBlock = loc.getBlock();
        for (int distance = 0; distance <= FanUtil.max_distance; distance++){
            verticalBlock = loc.getBlock();
            loc.setY(loc.getY() - 1);
            //トラップドア探し
            if(util.isTrapDoor(verticalBlock,distance)){
                if(util.isAvailableVerticalTrapDoor(verticalBlock)){
                    isYTrapDoor = true;
                }
                break;
            }

            //空気の場合はいったん無視
            if(!(verticalBlock.getType().equals(Material.AIR) || verticalBlock.getType().equals(Material.CAVE_AIR)
                    || verticalBlock.getType().equals(Material.VOID_AIR))){
                break;
            }
        }
        if(!isYTrapDoor){
            return;
        }

        //トラップドアの下のブロック
        Block ub = loc.getBlock();
        if(!ub.getType().equals(Material.DISPENSER) ){
            return;
        }
        Dispenser dispenser = (Dispenser)ub.getBlockData();
        if(dispenser.getFacing() != BlockFace.UP){
            return;
        }
        Vector curVec = player.getVelocity();
        double yVec = 0D;

        //スニークしている場合は降りていく
        if(isSneaking){
            yVec = -0.6D;
            player.setVelocity(new Vector(curVec.getX(),yVec,curVec.getZ()));
        }else{
            player.setVelocity(new Vector(curVec.getX(),yVec,curVec.getZ()));
            player.setVelocity(util.calcVerticalVelocity(player.getVelocity(),verticalBlock));
        }
        player.setFallDistance(0F);
    }

    /**
     * 水平方向のブロックをチェックして、扇風機なら飛ぶ
     * @param player プレイヤー
     * @param axis 軸 X or Z
     * @param direction 方向 positive or negative
     */
    public void checkHorizontalAxis(Player player, String axis,String direction) {
        boolean isHorizontalTrapDoor = false;

        Location loc = player.getLocation();
        Block horizontalBlock = loc.getBlock();
        for(int yAxis = 0;yAxis<=1;yAxis++) {
            loc = player.getLocation();
            loc.setY(loc.getY() + yAxis);
            for (int distance = 0; distance <= FanUtil.max_distance + 1; distance++) {
                horizontalBlock = loc.getBlock();
                loc = util.getHorizontalLocale(loc, axis, direction);
                //トラップドア探し
                if (util.isTrapDoor(horizontalBlock, distance)) {
                    if (util.isAvailableHorizontalTrapDoor(horizontalBlock)) {
                        isHorizontalTrapDoor = true;
                    }
                    break;
                }

                //空気の場合はいったん無視
                if (!(horizontalBlock.getType().equals(Material.AIR) || horizontalBlock.getType().equals(Material.CAVE_AIR)
                        || horizontalBlock.getType().equals(Material.VOID_AIR))) {
                    break;
                }
            }
            if(isHorizontalTrapDoor){
                break;
            }
        }


        if(!isHorizontalTrapDoor){
            return;
        }
        //トラップドアの先のブロック
        Block nb = loc.getBlock();
        if(!nb.getType().equals(Material.DISPENSER) ){
            return;
        }
        Dispenser dispenser = (Dispenser)nb.getBlockData();
        TrapDoor trapDoor = (TrapDoor) horizontalBlock.getBlockData();
        if(dispenser.getFacing() != trapDoor.getFacing()){
            return;
        }
        Vector curVec = player.getVelocity();
        Vector toVec = util.calcHorizontalVelocity(curVec,axis,direction,horizontalBlock);
        player.setVelocity(toVec);
        player.setFallDistance(0F);
    }
}
