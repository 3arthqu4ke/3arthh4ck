package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.client.CPacketEntityAction;

/**
 * Manages the last {@link CPacketEntityAction} packets sent to the server.
 * TODO: serverSneakState and serverSprintState in UpdateWalkingPlayer?
 */
public class ActionManager extends SubscriberImpl
{
    private volatile boolean sneaking;
    private volatile boolean sprinting;

    public ActionManager()
    {
        this.listeners.add(
            new EventListener<PacketEvent.Post<CPacketEntityAction>>
                    (PacketEvent.Post.class, CPacketEntityAction.class)
        {
            @Override
            public void invoke(PacketEvent.Post<CPacketEntityAction> event)
            {
                switch (event.getPacket().getAction())
                {
                    case START_SPRINTING:
                        sprinting = true;
                        break;
                    case STOP_SPRINTING:
                        sprinting = false;
                        break;
                    case START_SNEAKING:
                        sneaking = true;
                        break;
                    case STOP_SNEAKING:
                        sneaking = false;
                        break;
                    default:
                }
            }
        });
    }

    /**
     * @return <tt>true</tt> if we are sprinting on the server.
     */
    public boolean isSprinting()
    {
        return sprinting;
    }

    /**
     * @return <tt>true</tt> if we are sneaking on the server.
     */
    public boolean isSneaking()
    {
        return sneaking;
    }

}
