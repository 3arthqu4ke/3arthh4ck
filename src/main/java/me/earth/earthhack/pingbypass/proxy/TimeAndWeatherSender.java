package me.earth.earthhack.pingbypass.proxy;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketWorldBorder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.border.WorldBorder;

/**
 * {@link net.minecraft.server.management.PlayerList#updateTimeAndWeatherForPlayer(EntityPlayerMP, WorldServer)}
 */
public class TimeAndWeatherSender
{
    public static void updateTimeAndWeatherForPlayer(NetworkManager manager, World worldIn)
    {
        WorldBorder worldborder = worldIn.getWorldBorder();
        manager.sendPacket(new SPacketWorldBorder(worldborder, SPacketWorldBorder.Action.INITIALIZE));
        manager.sendPacket(new SPacketTimeUpdate(worldIn.getTotalWorldTime(), worldIn.getWorldTime(), worldIn.getGameRules().getBoolean("doDaylightCycle")));
        BlockPos blockpos = worldIn.getSpawnPoint();
        manager.sendPacket(new SPacketSpawnPosition(blockpos));

        if (worldIn.isRaining())
        {
            manager.sendPacket(new SPacketChangeGameState(1, 0.0F));
            manager.sendPacket(new SPacketChangeGameState(7, worldIn.getRainStrength(1.0F)));
            manager.sendPacket(new SPacketChangeGameState(8, worldIn.getThunderStrength(1.0F)));
        }
    }

}
