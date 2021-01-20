package net.kunmc.lab;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class FanListener implements Listener {

    final int MAX_DISTANCE = 7;
    final Vector X_AXIS_VECTOR = new Vector(0.2D, 0D,0D);
    final Vector Y_AXIS_VECTOR = new Vector(0D, 0.35D, 0D);
    final Vector Z_AXIS_VECTOR = new Vector(0D, 0D,0.2D);

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        boolean levitate;
        if(player.getGameMode() == GameMode.SPECTATOR || player.isFlying()){
            return;
        }

        checkVerticalAxis(player);
        checkHorizontalAxis(player,"X","positive");
        checkHorizontalAxis(player,"X","negative");
        checkHorizontalAxis(player,"Z","positive");
        checkHorizontalAxis(player,"Z","negative");

    }

    /**
     * 足元のブロックをチェックして、扇風機なら飛ぶ
     * @param player プレイヤー
     * @return
     */
    public void checkVerticalAxis(Player player){
        Location loc = player.getLocation();

        boolean isYTrapDoor = false;
        Block footBlock;
        for (int i = 0; i < MAX_DISTANCE; i++){
            footBlock = loc.getBlock();
            loc.setY(loc.getY() - 1);
            //トラップドア探し
            if(footBlock.getType().equals(Material.IRON_TRAPDOOR)){
                TrapDoor trapDoor = (TrapDoor) footBlock.getBlockData();
                if(trapDoor.isOpen() == false && trapDoor.getHalf().equals(Bisected.Half.BOTTOM)){
                    isYTrapDoor = true;
                }
                break;
            }

            //空気の場合はいったん無視
            if(footBlock.getType().equals(Material.AIR) || footBlock.getType().equals(Material.CAVE_AIR)
                    || footBlock.getType().equals(Material.VOID_AIR) ){
                continue;
            }
            //間に空気以外のブロックがあるときは中断
            if(!footBlock.getType().equals(Material.IRON_TRAPDOOR)){
                break;
            }
        }
        if(isYTrapDoor == false){
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
        Double yVec = 0D;
        //スニークしている場合は降りていく
        if(player.isSneaking()){
            yVec = -0.7D;
        }
        Vector toVec = new Vector(curVec.getX(),yVec,curVec.getZ());

        player.setVelocity(toVec);
        player.setVelocity(player.getVelocity().add(Y_AXIS_VECTOR));
        player.setFallDistance(0F);
    }

    public void checkHorizontalAxis(Player player, String axis,String direction){
        boolean isHorizontalTrapDoor = false;
        Location loc = player.getLocation();
        Block HorizontalBlock = loc.getBlock();
        for (int i = 0; i < MAX_DISTANCE; i++){
            HorizontalBlock = loc.getBlock();
            loc = getHorizontalLocale(loc,axis,direction);

            //トラップドア探し
            if(HorizontalBlock.getType().equals(Material.IRON_TRAPDOOR)){
                TrapDoor trapDoor = (TrapDoor) HorizontalBlock.getBlockData();
                if(trapDoor.isOpen() == true){
                    isHorizontalTrapDoor = true;
                }
                break;
            }

            //空気の場合はいったん無視
            if(HorizontalBlock.getType().equals(Material.AIR) || HorizontalBlock.getType().equals(Material.CAVE_AIR)
                    || HorizontalBlock.getType().equals(Material.VOID_AIR) ){
                continue;
            }
            //間に空気以外のブロックがあるときは中断
            if(!HorizontalBlock.getType().equals(Material.IRON_TRAPDOOR)){
                break;
            }
        }
        if(isHorizontalTrapDoor == false){
            return;
        }
        //トラップドアの先のブロック
        Block nb = loc.getBlock();
        if(!nb.getType().equals(Material.DISPENSER) ){
            return;
        }
        Dispenser dispenser = (Dispenser)nb.getBlockData();
        TrapDoor trapDoor = (TrapDoor) HorizontalBlock.getBlockData();
        if(dispenser.getFacing() != trapDoor.getFacing()){
            return;
        }
        Vector curVec = player.getVelocity();
        Vector toVec = calcHorizontalVelocity(player,curVec,axis,direction);
        player.setVelocity(toVec);

    }

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

    /**
     * 水平の速度を求める
     * @param curVec 現在のVelocity
     * @param axis 軸 X or Z
     * @param direction 方向 正 or 負
     * @return 計算後の速度
     */
    public Vector calcHorizontalVelocity(Player player,Vector curVec, String axis, String direction){
        if (player.isSneaking()){
            return curVec;
        }
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
