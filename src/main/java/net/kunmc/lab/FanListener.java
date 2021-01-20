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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import static org.bukkit.Bukkit.getLogger;

public class FanListener implements Listener {

    final int IRON_DISTANCE = 7;
    final Vector Y_AXIS_VECTOR = new Vector(0D, 0.35D, 0D);

    @EventHandler
    public void onPJoin(PlayerJoinEvent e){

        e.setJoinMessage("ようこそ"+e.getPlayer().getDisplayName()+"さん");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        boolean levitate;
        if(player.getGameMode() == GameMode.SPECTATOR || player.isFlying()){
            return;
        }

        checkVerticalAxis(player);

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
        for (int i = 0; i < IRON_DISTANCE; i++){
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
        getLogger().info("飛ぶ");
    }

    public boolean checkHorizontalAxis(Location playerLoc){
        boolean isHorizontalFan = false;

        return isHorizontalFan;
    }

    public boolean checkFanFace(Location trapLoc){
        boolean isFacing = false;

        return isFacing;
    }
}
