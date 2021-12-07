package me.earth.earthhack.impl.modules.player.blink.mode;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketTabComplete;

public enum PacketMode
{
    All()
    {
        @Override
        public boolean shouldCancel(Packet<?> packet)
        {
            return true;
        }
    },
    CPacketPlayer()
    {
        @Override
        public boolean shouldCancel(Packet<?> packet)
        {
            return packet instanceof CPacketPlayer;
        }
    },
    Filtered()
    {
        @Override
        public boolean shouldCancel(Packet<?> packet)
        {
            return !(packet instanceof CPacketChatMessage
                        || packet instanceof CPacketConfirmTeleport
                        || packet instanceof CPacketKeepAlive
                        || packet instanceof CPacketTabComplete
                        || packet instanceof CPacketClientStatus);
        }
    };

    public abstract boolean shouldCancel(Packet<?> packet);
}
