package me.earth.earthhack.impl.modules.client.server.protocol;

import me.earth.earthhack.impl.modules.client.server.api.IConnectionManager;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Separate class cause we don't want to
 * use this for command-line servers/clients.
 */
public class ProtocolPlayUtil
{
    public static byte[] velocityAndPosition(EntityPlayer player)
    {
        double x  = player.posX;
        double y  = player.posY;
        double z  = player.posZ;
        double dX = player.motionX;
        double dY = player.motionY;
        double dZ = player.motionZ;

        byte[] packets = new byte[64];
        ByteBuffer buf = ByteBuffer.wrap(packets);
        buf.putInt(Protocol.POSITION)
                .putInt(24).putDouble(x).putDouble(y).putDouble(z)
                .putInt(Protocol.VELOCITY)
                .putInt(24).putDouble(dX).putDouble(dY).putDouble(dZ);

        return packets;
    }

    public static void sendVelocityAndPosition(IConnectionManager manager,
                                               EntityPlayer player)
    {
        try
        {
            manager.send(velocityAndPosition(player));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
