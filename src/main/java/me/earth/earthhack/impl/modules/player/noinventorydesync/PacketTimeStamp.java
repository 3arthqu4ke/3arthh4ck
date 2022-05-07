package me.earth.earthhack.impl.modules.player.noinventorydesync;

import me.earth.earthhack.impl.modules.combat.autocrystal.util.TimeStamp;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public class PacketTimeStamp<T extends INetHandler> extends TimeStamp
{
    private final Packet<T> packet;

    public PacketTimeStamp(Packet<T> packet)
    {
        this.packet = packet;
    }

    public Packet<T> getPacket()
    {
        return packet;
    }

}
