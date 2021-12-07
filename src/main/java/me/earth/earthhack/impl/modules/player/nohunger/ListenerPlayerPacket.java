package me.earth.earthhack.impl.modules.player.nohunger;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerPlayerPacket
        extends CPacketPlayerListener implements Globals
{
    private final NoHunger module;

    public ListenerPlayerPacket(NoHunger module)
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
        if (module.ground.getValue()
                && module.onGround
                && mc.player.onGround
                && packet.getY(0.0) ==
                 (!((ICPacketPlayer) packet).isMoving() ? 0.0 : mc.player.posY))
        {
            ((ICPacketPlayer) packet).setOnGround(false);
        }

        module.onGround = mc.player.onGround;
    }

}
