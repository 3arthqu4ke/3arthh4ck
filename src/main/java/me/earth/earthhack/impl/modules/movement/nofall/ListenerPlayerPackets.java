package me.earth.earthhack.impl.modules.movement.nofall;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerPlayerPackets extends CPacketPlayerListener
        implements Globals
{
    public final NoFall module;

    public ListenerPlayerPackets(NoFall module)
    {
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event)
    {
        onPacket(event.getPacket());
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event)
    {
        onPacket(event.getPacket());
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event)
    {
        onPacket(event.getPacket());
    }

    @Override
    protected void onPositionRotation
            (PacketEvent.Send<CPacketPlayer.PositionRotation> event)
    {
        onPacket(event.getPacket());
    }

    private void onPacket(CPacketPlayer packet)
    {
        switch(module.mode.getValue())
        {
            case Packet:
                if (mc.player.fallDistance > 3.0F)
                {
                    ((ICPacketPlayer) packet).setOnGround(true);
                    return;
                }
                break;
            case Anti:
                if (mc.player.fallDistance > 3.0F)
                {
                    ((ICPacketPlayer) packet).setY(
                        mc.player.posY + 0.10000000149011612);
                    return;
                }
                break;
            case AAC:
                if (mc.player.fallDistance > 3.0F)
                {
                    mc.player.onGround = true;
                    mc.player.capabilities.isFlying = true;
                    mc.player.capabilities.allowFlying = true;
                    ((ICPacketPlayer) packet).setOnGround(false);
                    mc.player.velocityChanged = true;
                    mc.player.capabilities.isFlying = false;
                    mc.player.jump();
                }
                break;
        }
    }

}
