package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketHeldItemChange;

/**
 * Some servers block certain packets, especially
 * CPacketUseEntity for around 10 ticks (~ 500 ms) after you
 * switched your mainhand slot. If you attack during this time you
 * might flag the anticheat. This class manages the time that
 * passed after the last switch.
 */
public class SwitchManager extends SubscriberImpl
{
    private final StopWatch timer = new StopWatch();
    private volatile int last_slot;

    public SwitchManager()
    {
        this.listeners.add(
            new EventListener<PacketEvent.Post<CPacketHeldItemChange>>
                (PacketEvent.Post.class, CPacketHeldItemChange.class)
        {
            @Override
            public void invoke(PacketEvent.Post<CPacketHeldItemChange> event)
            {
                timer.reset();
                last_slot = event.getPacket().getSlotId();
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Receive<SPacketHeldItemChange>>
                (PacketEvent.Receive.class, SPacketHeldItemChange.class)
        {
            @Override
            public void invoke(PacketEvent.Receive<SPacketHeldItemChange> event)
            {
                last_slot = event.getPacket().getHeldItemHotbarIndex();
            }
        });
    }

    /**
     * @return the time in ms that passed since the last
     *         {@link CPacketHeldItemChange} has been send.
     */
    public long getLastSwitch()
    {
        return timer.getTime();
    }

    /**
     * @return the last slot reported to the server.
     */
    public int getSlot()
    {
        return last_slot;
    }

}
