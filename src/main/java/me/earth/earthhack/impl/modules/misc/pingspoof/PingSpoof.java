package me.earth.earthhack.impl.modules.misc.pingspoof;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.client.ShutDownEvent;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.thread.ThreadUtil;
import net.minecraft.network.Packet;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Sometime do it for ConfirmTeleport as well but doesn't matter rn
public class PingSpoof extends Module
{
    private final ScheduledExecutorService service;

    protected final Setting<Integer> delay =
            register(new NumberSetting<>("Delay", 100, 1, 5000));
    protected final Setting<Boolean> keepAlive =
            register(new BooleanSetting("KeepAlive", true));
    protected final Setting<Boolean> transactions =
            register(new BooleanSetting("Transactions", false));
    protected final Setting<Boolean> resources =
            register(new BooleanSetting("Resources", false));

    /** The Packet Queue, needed to preserve the order of the packets. */
    protected final Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    /** Stores the transactionIDs. */
    protected final Set<Short> transactionIDs = new HashSet<>();

    public PingSpoof()
    {
        super("PingSpoof", Category.Misc);
        service = ThreadUtil.newDaemonScheduledExecutor("PingSpoof");
        Bus.EVENT_BUS.register(new EventListener<ShutDownEvent>
                (ShutDownEvent.class)
        {
            @Override
            public void invoke(ShutDownEvent event)
            {
                service.shutdown();
            }
        });

        this.listeners.add(new ListenerKeepAlive(this));
        this.listeners.add(new ListenerLogout(this));
        this.listeners.add(new ListenerTransaction(this));
        this.listeners.add(new ListenerClickWindow(this));
        this.listeners.add(new ListenerResources(this));
        this.setData(new PingSpoofData(this));
    }

    @Override
    protected void onDisable()
    {
        clearPackets(true);
    }

    public int getDelay()
    {
        return delay.getValue();
    }

    protected void clearPackets(boolean send)
    {
        transactionIDs.clear();
        CollectionUtil.emptyQueue(packets, packet ->
        {
            if (send)
            {
                NetworkUtil.sendPacketNoEvent(packet);
            }
        });
    }

    protected void onPacket(Packet<?> packet)
    {
        packets.add(packet);
        service.schedule(() ->
        {
            if (mc.player != null)
            {
                Packet<?> p = packets.poll();
                if (p != null)
                {
                    NetworkUtil.sendPacketNoEvent(p);
                }
            }
        }, delay.getValue(), TimeUnit.MILLISECONDS);
    }

}
