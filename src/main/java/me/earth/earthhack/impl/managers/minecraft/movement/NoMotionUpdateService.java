package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.NoMotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SNoMotionUpdateEvent;
import net.minecraft.network.play.client.CPacketPlayer;

/**
 * Posts a {@link NoMotionUpdateEvent} Event,
 * if onUpdateWalkingPlayer is called,
 * but no CPacketPlayer is sent.
 */
public class NoMotionUpdateService extends SubscriberImpl implements Globals
{
    private boolean awaiting;

    public NoMotionUpdateService()
    {
        this.listeners.add(new EventListener<MotionUpdateEvent>(
                MotionUpdateEvent.class, Integer.MIN_VALUE)
        {
            @Override
            public void invoke(MotionUpdateEvent event)
            {
                if (event.isCancelled())
                {
                    return;
                }

                if (event.getStage() == Stage.PRE)
                {
                    setAwaiting(true);
                }
                else
                {
                    if (isAwaiting())
                    {
                        NoMotionUpdateEvent noMotionUpdate =
                            new NoMotionUpdateEvent();
                        Bus.EVENT_BUS.post(noMotionUpdate);
                        if (!noMotionUpdate.isCancelled()
                            && PingBypassModule.CACHE.isEnabled()
                            && !PingBypassModule.CACHE.get().isOld()
                            && mc.player != null) {
                            mc.player.connection.sendPacket(
                                new C2SNoMotionUpdateEvent());
                        }
                    }

                    setAwaiting(false);
                }
            }
        });
        this.listeners.addAll(new CPacketPlayerListener()
        {
            @Override
            protected void onPacket
                    (PacketEvent.Send<CPacketPlayer> event)
            {
                setAwaiting(false);
            }

            @Override
            protected void onPosition
                    (PacketEvent.Send<CPacketPlayer.Position> event)
            {
                setAwaiting(false);
            }

            @Override
            protected void onRotation
                    (PacketEvent.Send<CPacketPlayer.Rotation> event)
            {
                setAwaiting(false);
            }

            @Override
            protected void onPositionRotation
                    (PacketEvent.Send<CPacketPlayer.PositionRotation> event)
            {
                setAwaiting(false);
            }
        }.getListeners());
    }

    public void setAwaiting(boolean awaiting)
    {
        this.awaiting = awaiting;
    }

    public boolean isAwaiting()
    {
        return awaiting;
    }

}
