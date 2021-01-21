package net.kunmc.lab;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

public class FanListener implements Listener {

    FanUtil util = new FanUtil();

    /**
     * 動いた時の処理
     * @param e PlayerMoveEvent
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        boolean isSneaking = player.isSneaking();
        doAction(player,isSneaking);
    }

    /**
     * スニーク時の処理
     * @param e PlayerToggleSneakEvent
     */
    @EventHandler
    public void onToggleSneaking(PlayerToggleSneakEvent e){
        Player player = e.getPlayer();
        //直前のスニーク状態が取得されるので反転
        boolean isSneaking = !player.isSneaking();
        doAction(player,isSneaking);
    }

    /**
     * 実際の処理への橋渡し
     * @param player プレイヤー
     * @param isSneaking スニークしているか
     */
    public void doAction(Player player,boolean isSneaking){
        if(player.getGameMode() == GameMode.SPECTATOR || player.isFlying() || !FanUtil.enable){
            return;
        }

        checkVerticalAxis(player);
        if(!isSneaking){
            checkHorizontalAxis(player,"X","positive");
            checkHorizontalAxis(player,"X","negative");
            checkHorizontalAxis(player,"Z","positive");
            checkHorizontalAxis(player,"Z","negative");
        }
    }

    /**
     * 足元のブロックをチェックして、扇風機なら飛ぶ
     * @param player プレイヤー
     */
    public void checkVerticalAxis(Player player){
        Location loc = player.getLocation();

        boolean isYTrapDoor = false;
        Block footBlock;
        for (int distance = 0; distance <= FanUtil.MAX_DISTANCE; distance++){
            footBlock = loc.getBlock();
            loc.setY(loc.getY() - 1);
            //トラップドア探し
            if(util.isTrapDoor(footBlock,distance)){
                if(util.isAvailableVerticalTrapDoor(footBlock)){
                    isYTrapDoor = true;
                }
                break;
            }

            //空気の場合はいったん無視
            if(footBlock.getType().equals(Material.AIR) || footBlock.getType().equals(Material.CAVE_AIR)
                    || footBlock.getType().equals(Material.VOID_AIR)){
                continue;
            }
            //間に空気以外のブロックがあるときは中断
            if(!footBlock.getType().equals(Material.IRON_TRAPDOOR)){
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
        if(player.isSneaking()){
            yVec = -0.7D;
        }
        Vector toVec = new Vector(curVec.getX(),yVec,curVec.getZ());

        player.setVelocity(toVec);
        player.setVelocity(util.calcVerticalVelocity(player.getVelocity()));
        player.setFallDistance(0F);
    }

    /**
     * 水平方向のブロックをチェックして、扇風機なら飛ぶ
     * @param player プレイヤー
     * @param axis 軸 X or Z
     * @param direction 方向 positive or negative
     */
    public void checkHorizontalAxis(Player player, String axis,String direction){
        boolean isHorizontalTrapDoor = false;
        Location loc = player.getLocation();
        Block HorizontalBlock = loc.getBlock();
        for (int distance = 0; distance < FanUtil.MAX_DISTANCE; distance++){
            HorizontalBlock = loc.getBlock();
            loc = util.getHorizontalLocale(loc,axis,direction);

            //トラップドア探し
            if(util.isTrapDoor(HorizontalBlock,distance)){
                if(util.isAvailableHorizontalTrapDoor(HorizontalBlock)){
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
        if(!isHorizontalTrapDoor){
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
        Vector toVec = util.calcHorizontalVelocity(curVec,axis,direction);
        player.setVelocity(toVec);
        player.setFallDistance(0F);
    }
}
