package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotationThread;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerCPlayers extends CPacketPlayerListener
{
    private final AutoCrystal module;

    public ListenerCPlayers(AutoCrystal module)
    {
        this.module = module;
    }

    @Override
    protected void onPacket
            (PacketEvent.Send<CPacketPlayer> event)
    {
        update(event);
    }

    @Override
    protected void onPosition
            (PacketEvent.Send<CPacketPlayer.Position> event)
    {
        update(event);
    }

    @Override
    protected void onRotation
            (PacketEvent.Send<CPacketPlayer.Rotation> event)
    {
        update(event);
    }

    @Override
    protected void onPositionRotation
            (PacketEvent.Send<CPacketPlayer.PositionRotation> event)
    {
        update(event);
    }

    private void update(PacketEvent.Send<? extends CPacketPlayer> event)
    {
        if (module.multiThread.getValue()
            && !module.isSpoofing
            && module.rotate.getValue() != ACRotate.None
            && module.rotationThread.getValue() == RotationThread.Cancel)
        {
            module.rotationCanceller.onPacket(event);
        }
        else
        {
            module.rotationCanceller.reset();
        }
    }

}
