package net.kunmc.lab;

import net.kunmc.lab.util.FanUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class EntityAction {
    FanUtil util = new FanUtil();

    /**
     * 足元のブロックをチェックして、扇風機なら飛ぶ
     * @param entity エンティティ
     */
    public void checkVerticalAxis(Entity entity){
        Location loc = entity.getLocation();

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
            if(!util.checkEmptyBlock(verticalBlock)){
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
        Vector curVec = entity.getVelocity();
        double yVec = 0D;
        //スニークしている場合は降りていく
        boolean isSneaking = false;
        if (entity instanceof Player){
            isSneaking = ((Player) entity).isSneaking();
        }
        if(isSneaking){
            yVec = -0.6D;
            entity.setVelocity(new Vector(curVec.getX(),yVec,curVec.getZ()));
        }else{
            entity.setVelocity(new Vector(curVec.getX(),yVec,curVec.getZ()));
            entity.setVelocity(util.calcVerticalVelocity(entity.getVelocity(),verticalBlock));
        }
        entity.setFallDistance(0F);
    }

    /**
     * 水平方向のブロックをチェックして、扇風機なら飛ぶ
     * @param entity プレイヤー
     * @param axis 軸 X or Z
     * @param direction 方向 positive or negative
     */
    public void checkHorizontalAxis(Entity entity, String axis,String direction) {
        boolean isHorizontalTrapDoor = false;

        Location loc = entity.getLocation();
        Block horizontalBlock = loc.getBlock();
        for(int yAxis = 0;yAxis<=1;yAxis++) {
            loc = entity.getLocation();
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
                if (!util.checkEmptyBlock(horizontalBlock)) {
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
        Vector curVec = entity.getVelocity();
        entity.setVelocity(util.calcHorizontalVelocity(curVec,axis,direction,horizontalBlock));
    }
}
