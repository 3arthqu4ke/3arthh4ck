package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages the legitimacy of windowClicks against NCP.
 * Also manages the time that passed since the last
 * LagBack via {@link SPacketPlayerPosLook}.
 */
public class NCPManager extends SubscriberImpl implements Globals
{
    private final AtomicLong lagTimer  = new AtomicLong();
    private final StopWatch clickTimer = new StopWatch();
    private boolean endedSprint;
    private boolean endedSneak;
    private boolean windowClicks;
    private boolean strict;

    /** Constructs a new NCPManager. */
    public NCPManager()
    {
        this.listeners.add(
            new EventListener<PacketEvent.Receive<SPacketPlayerPosLook>>
                (PacketEvent.Receive.class,
                        Integer.MAX_VALUE,
                        SPacketPlayerPosLook.class)
        {
            @Override
            public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event)
            {
                lagTimer.set(System.currentTimeMillis());
            }
        });
        this.listeners.add(
            new EventListener<WorldClientEvent.Load>
                    (WorldClientEvent.Load.class)
        {
            @Override
            public void invoke(WorldClientEvent.Load event)
            {
                endedSneak   = false;
                endedSprint  = false;
                windowClicks = false;
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Send<CPacketClickWindow>>
                (PacketEvent.Send.class, -1000, CPacketClickWindow.class)
        {
            @Override
            public void invoke(PacketEvent.Send<CPacketClickWindow> event)
            {
                if (!isStrict() || event.isCancelled())
                {
                    return;
                }

                if (mc.player.isActiveItemStackBlocking())
                {
                    Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                        mc.playerController.onStoppedUsingItem(mc.player));
                }

                if (Managers.ACTION.isSneaking())
                {
                    endedSneak = true;
                    mc.player.connection.sendPacket(
                            new CPacketEntityAction(mc.player,
                                    CPacketEntityAction.Action.STOP_SNEAKING));
                }

                if (Managers.ACTION.isSprinting())
                {
                    endedSprint = true;
                    mc.player.connection.sendPacket(
                            new CPacketEntityAction(mc.player,
                                    CPacketEntityAction.Action.STOP_SPRINTING));
                }
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Post<CPacketClickWindow>>
                (PacketEvent.Post.class, -1000, CPacketClickWindow.class)
        {
            @Override
            public void invoke(PacketEvent.Post<CPacketClickWindow> event)
            {
                clickTimer.reset();
                if (!windowClicks && isStrict())
                {
                    release();
                }
            }
        });
    }

    public StopWatch getClickTimer()
    {
        return clickTimer;
    }

    /**
     * @return <tt>true</tt> if NCP-Strict is active.
     */
    public boolean isStrict()
    {
        return strict;
    }

    /**
     * @param strict set NCP-Strict to this value.
     */
    public void setStrict(boolean strict)
    {
        if (this.strict && !strict)
        {
            releaseMultiClick();
        }

        this.strict = strict;
    }

    /**
     * Marks that NCP-Strict should expect multiple
     * WindowClicks in a short period of time and
     * not spam packets. Always call
     * {@link NCPManager#releaseMultiClick()}
     * afterwards.
     */
    public void startMultiClick()
    {
        this.windowClicks = true;
    }

    /**
     * Call after {@link NCPManager#startMultiClick()}, to
     * end a streak of multiple windowClicks and send the
     * packets.
     */
    public void releaseMultiClick()
    {
        this.windowClicks = false;
        release();
    }

    /**
     * Returns <tt>true</tt> if more time than the given delay in
     * milliseconds passed since the last {@link SPacketPlayerPosLook}
     * arrived at our client.
     *
     * @param ms the delay in ms to check.
     */
    public boolean passed(int ms)
    {
        return System.currentTimeMillis() - lagTimer.get() >= ms;
    }

    /**
     * @return the {@link System#currentTimeMillis()} of the last lag.
     */
    public long getTimeStamp()
    {
        return lagTimer.get();
    }

    /**
     * Resets the LagTimer. {@link NCPManager#passed(int)} is affected.
     */
    public void reset()
    {
        lagTimer.set(System.currentTimeMillis());
    }

    /**
     * Called after a windowClick, sends a SneakPacket
     * if we stopped sneaking for the windowClick and
     * a SprintPacket if we stopped sprinting.
     */
    private void release()
    {
        if (endedSneak)
        {
            endedSneak = false;
            mc.player.connection.sendPacket(new CPacketEntityAction(
                    mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }

        if (endedSprint)
        {
            endedSprint = false;
            mc.player.connection.sendPacket(new CPacketEntityAction(
                    mc.player, CPacketEntityAction.Action.START_SPRINTING));
        }
    }

}
