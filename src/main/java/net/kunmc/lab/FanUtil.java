package net.kunmc.lab;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.util.Vector;

import java.util.Map;

public class FanUtil {

    public static int MAX_DISTANCE;
    public static  boolean enable = false;
    final Vector X_AXIS_VECTOR = new Vector(0.2D, 0D,0D);
    final Vector Y_AXIS_VECTOR = new Vector(0D, 0.35D, 0D);
    final Vector Z_AXIS_VECTOR = new Vector(0D, 0D,0.2D);
    public static Map<String, Integer> powerMap;

    /**
     * 距離によって有効なトラップドアの判定
     * @param block ブロック
     * @param distance ブロックとの距離
     * @return トラップドアか否か
     */
    public boolean isTrapDoor(Block block,int distance){
        boolean isTrapDoor = false;

        if(block.getType().equals(Material.IRON_TRAPDOOR)){
            isTrapDoor = true;
        }else if(block.getType().equals(Material.ACACIA_TRAPDOOR)
                && distance <= powerMap.get("ACACIA")){
            isTrapDoor = true;
        }else if(block.getType().equals(Material.OAK_TRAPDOOR)
                && distance <= powerMap.get("OAK")){
            isTrapDoor = true;
        }else if(block.getType().equals(Material.JUNGLE_TRAPDOOR)
                && distance <= powerMap.get("JUNGLE")){
            isTrapDoor = true;
        }else if(block.getType().equals(Material.BIRCH_TRAPDOOR)
                && distance <= powerMap.get("BIRCH")){
            isTrapDoor = true;
        }else if(block.getType().equals(Material.DARK_OAK_TRAPDOOR)
                && distance <= powerMap.get("DARK_OAK")){
            isTrapDoor = true;
        }else if(block.getType().equals(Material.SPRUCE_TRAPDOOR)
                && distance <= powerMap.get("SPRUCE")){
            isTrapDoor = true;
        }

        return isTrapDoor;
    }

    /**
     * 垂直軸で有効なトラップドアかどうか判定
     * @param block トラップドア
     * @return 有効かどうか
     */
    public boolean isAvailableVerticalTrapDoor(Block block){
        boolean isAvailable = false;
        TrapDoor trapDoor = (TrapDoor) block.getBlockData();
        if(!trapDoor.isOpen() && trapDoor.getHalf().equals(Bisected.Half.BOTTOM)){
            isAvailable = true;
        }
        return isAvailable;
    }

    /**
     * 水平軸で有効なトラップドアかどうか判定
     * @param block　トラップドア
     * @return 有効かどうか
     */
    public boolean isAvailableHorizontalTrapDoor(Block block){
        boolean isAvailable = false;
        TrapDoor trapDoor = (TrapDoor) block.getBlockData();
        if(trapDoor.isOpen()){
            isAvailable = true;
        }
        return isAvailable;
    }

    /**
     * 水平方向へのブロック探索
     * @param loc 場所
     * @param axis 軸 X or Z
     * @param direction 方向 positive or negative
     * @return 位置
     */
    public Location getHorizontalLocale(Location loc,String axis,String direction){
        switch (axis){
            case "X":
                if (direction.equals("positive")){
                    loc.setX(loc.getX() + 1);
                }else if(direction.equals("negative")){
                    loc.setX(loc.getX() - 1);
                }
                break;
            case "Z":
                if (direction.equals("positive")){
                    loc.setZ(loc.getZ() + 1);
                }else if(direction.equals("negative")){
                    loc.setZ(loc.getZ() - 1);
                }
                break;
            default:
                break;
        }
        return loc;
    }

    public  Vector calcVerticalVelocity(Vector curVec){
        curVec = curVec.add(Y_AXIS_VECTOR);
        return curVec;
    }

    /**
     * 水平の速度を求める
     * @param curVec 現在のVelocity
     * @param axis 軸 X or Z
     * @param direction 方向 positive or negative
     * @return 計算後の速度
     */
    public Vector calcHorizontalVelocity(Vector curVec, String axis, String direction){

        switch (axis){
            case "X":
                if (direction.equals("positive")){
                    curVec.add(new Vector(X_AXIS_VECTOR.getX() * -1,0D,0D));
                }else if(direction.equals("negative")){
                    curVec.add(X_AXIS_VECTOR);
                }
                break;
            case "Z":
                if (direction.equals("positive")){
                    curVec.add(new Vector(0D,0D,Z_AXIS_VECTOR.getZ() * -1));
                }else if(direction.equals("negative")){
                    curVec.add(Z_AXIS_VECTOR);
                }
                break;
            default:
                break;
        }
        return curVec;
    }
}
