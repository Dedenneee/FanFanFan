package net.kunmc.lab;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.util.Vector;

import java.util.Map;

public class FanUtil {

    public static int max_distance;
    public static double fall_velocity;
    public static Map<String,Velocity> velocityMap;
    public static Map<String, Integer> rangeMap;

    /**
     * 距離によって有効なトラップドアの判定
     * @param block ブロック
     * @param distance ブロックとの距離
     * @return トラップドアか否か
     */
    public boolean isTrapDoor(Block block,int distance){
        boolean isTrapDoor = false;

        if(block.getType().equals(Material.IRON_TRAPDOOR)
                && distance <= rangeMap.get("iron")){
            isTrapDoor = true;
        }else if(block.getType().equals(Material.ACACIA_TRAPDOOR)
                && distance <= rangeMap.get("acacia")){
            isTrapDoor = true;
        }else if(block.getType().equals(Material.OAK_TRAPDOOR)
                && distance <= rangeMap.get("oak")){
            isTrapDoor = true;
        }else if(block.getType().equals(Material.JUNGLE_TRAPDOOR)
                && distance <= rangeMap.get("jungle")){
            isTrapDoor = true;
        }else if(block.getType().equals(Material.BIRCH_TRAPDOOR)
                && distance <= rangeMap.get("birch")){
            isTrapDoor = true;
        }else if(block.getType().equals(Material.DARK_OAK_TRAPDOOR)
                && distance <= rangeMap.get("dark_oak")){
            isTrapDoor = true;
        }else if(block.getType().equals(Material.SPRUCE_TRAPDOOR)
                && distance <= rangeMap.get("spruce")){
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

    public  Vector calcVerticalVelocity(Vector curVec,Block verticalBlock){
        if(verticalBlock.getType().equals(Material.IRON_TRAPDOOR)){
            curVec = curVec.add(new Vector(0D,velocityMap.get("iron").getVerticalVelocity(),0D));
        }else if(verticalBlock.getType().equals(Material.ACACIA_TRAPDOOR)){
            curVec = curVec.add(new Vector(0D,velocityMap.get("acacia").getVerticalVelocity(),0D));
        }else if(verticalBlock.getType().equals(Material.OAK_TRAPDOOR)){
            curVec = curVec.add(new Vector(0D,velocityMap.get("oak").getVerticalVelocity(),0D));
        }else if(verticalBlock.getType().equals(Material.JUNGLE_TRAPDOOR)){
            curVec = curVec.add(new Vector(0D,velocityMap.get("jungle").getVerticalVelocity(),0D));
        }else if(verticalBlock.getType().equals(Material.BIRCH_TRAPDOOR)){
            curVec = curVec.add(new Vector(0D,velocityMap.get("birch").getVerticalVelocity(),0D));
        }else if(verticalBlock.getType().equals(Material.DARK_OAK_TRAPDOOR)){
            curVec = curVec.add(new Vector(0D,velocityMap.get("dark_oak").getVerticalVelocity(),0D));
        }else if(verticalBlock.getType().equals(Material.SPRUCE_TRAPDOOR)){
            curVec = curVec.add(new Vector(0D,velocityMap.get("spruce").getVerticalVelocity(),0D));
        }
        return curVec;
    }

    /**
     * 水平の速度を求める
     * @param curVec 現在のVelocity
     * @param axis 軸 X or Z
     * @param direction 方向 positive or negative
     * @return 計算後の速度
     */
    public Vector calcHorizontalVelocity(Vector curVec, String axis, String direction,Block horizontalBlock){
        double velocity = 0D;
        switch (axis){
            case "X":
                velocity = getHorizontalBlockVelocity(horizontalBlock);
                if (direction.equals("positive")){
                    curVec.add(new Vector((velocity * -1)+curVec.getX() * -2,0D,0D));
                }else if(direction.equals("negative")){
                    curVec.add(new Vector(velocity+ curVec.getX() * -2,0D,0D));
                }
                break;
            case "Z":
                velocity = getHorizontalBlockVelocity(horizontalBlock);
                if (direction.equals("positive")){
                    curVec.add(new Vector(0D,0D, (velocity * -1)+curVec.getZ() * -2));

                }else if(direction.equals("negative")){
                    curVec.add(new Vector(0D,0D, velocity+curVec.getZ() * -2));
                }
                break;
            default:
                break;
        }
        return curVec;
    }

    /**
     * トラップドアの種類ごとの速度を返す
     * @param horizontalBlock トラップドア
     * @return 種類ごとの速度
     */
    public double getHorizontalBlockVelocity(Block horizontalBlock){
        double velocity = 0D;
        if(horizontalBlock.getType().equals(Material.IRON_TRAPDOOR)){
            velocity = velocityMap.get("iron").getHorizontalVelocity();
        }else if(horizontalBlock.getType().equals(Material.ACACIA_TRAPDOOR)){
            velocity = velocityMap.get("acacia").getHorizontalVelocity();
        }else if(horizontalBlock.getType().equals(Material.OAK_TRAPDOOR)){
            velocity = velocityMap.get("oak").getHorizontalVelocity();
        }else if(horizontalBlock.getType().equals(Material.JUNGLE_TRAPDOOR)){
            velocity = velocityMap.get("jungle").getHorizontalVelocity();
        }else if(horizontalBlock.getType().equals(Material.BIRCH_TRAPDOOR)){
            velocity = velocityMap.get("birch").getHorizontalVelocity();
        }else if(horizontalBlock.getType().equals(Material.DARK_OAK_TRAPDOOR)){
            velocity = velocityMap.get("dark_oak").getHorizontalVelocity();
        }else if(horizontalBlock.getType().equals(Material.SPRUCE_TRAPDOOR)) {
            velocity = velocityMap.get("spruce").getHorizontalVelocity();
        }
        return velocity;
    }
}
