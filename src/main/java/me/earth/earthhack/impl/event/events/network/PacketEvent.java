package me.earth.earthhack.impl.event.events.network;

import me.earth.earthhack.api.event.events.Event;
import me.earth.earthhack.impl.util.thread.SafeRunnable;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A cancellable Event,
 * wrapping a packet.
 *
 * @param <T> the type of packet.
 */
public class PacketEvent<T extends Packet<? extends INetHandler>> extends Event
{
    private boolean isPingBypassCancelled;
    private final T packet;

    protected PacketEvent(T packet)
    {
        this.packet = packet;
    }

    public T getPacket()
    {
        return packet;
    }

    public boolean isPingBypassCancelled()
    {
        return isPingBypassCancelled;
    }

    public void setPingBypassCancelled(boolean pingBypassCancelled)
    {
        isPingBypassCancelled = pingBypassCancelled;
    }

    /**
     * An object of this sub class gets created when
     * a packet is sent.
     *
     * @param <T> the type of packet sent.
     */
    public static class Send<T extends Packet<? extends INetHandler>>
            extends PacketEvent<T>
    {
        public Send(T packet)
        {
            super(packet);
        }
    }

    public static class NoEvent<T extends Packet<? extends INetHandler>>
            extends PacketEvent<T>
    {
        private final boolean post;

        public NoEvent(T packet, boolean post)
        {
            super(packet);
            this.post = post;
        }

        public boolean hasPost()
        {
            return post;
        }
    }

    /**
     * An object of this class gets created when
     * a packet is received.
     * <p>
     * There is no "Post" event for Receiving packets, instead u
     * can use the {@link Receive#addPostEvent(SafeRunnable)} method
     * to add a Runnable that will be scheduled after the packet
     * has been processed.
     * <p>
     * Posting of this event should happen within the Try and Catch block
     * of the NetworkManagers "channelRead0" method. Because of that you
     * can also throw a ThreadQuickExitException if you don't want any
     * listeners after yours to process this event.
     *
     * @param <T> the type of packet received.
     */
    public static class Receive<T extends Packet<? extends INetHandler>>
            extends PacketEvent<T>
    {
        // Could use a PostRunnable, with "shouldScheduleIfCancelled"
        private final Deque<Runnable> postEvents = new ArrayDeque<>();
        private final NetworkManager networkManager;

        public Receive(T packet, NetworkManager networkManager)
        {
            super(packet);
            this.networkManager = networkManager;
        }

        /**
         * @param runnable will be scheduled after the event has been
         *                 processed and hasn't been cancelled.
         */
        public void addPostEvent(SafeRunnable runnable)
        {
            postEvents.add(runnable);
        }

        /**
         * @return all PostEvents for this event.
         */
        public Deque<Runnable> getPostEvents()
        {
            return postEvents;
        }

        public NetworkManager getNetworkManager()
        {
            return networkManager;
        }
    }

    /**
     * Exists only for Sending, not required for Receiving.
     * Objects of this class will be created after a packet has
     * successfully been sent.
     *
     * @param <T> the type of packet sent.
     */
    public static class Post<T extends Packet<? extends INetHandler>>
            extends PacketEvent<T>
    {
        public Post(T packet)
        {
            super(packet);
        }
    }

}
