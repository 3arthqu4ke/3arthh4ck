package me.earth.earthhack.pingbypass.listeners;

import io.netty.util.internal.ConcurrentSet;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.event.listeners.PostSendListener;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.event.PbDisconnectEvent;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CWindowClick;
import net.minecraft.network.play.client.CPacketClickWindow;

import java.util.Set;

public class PbWindowClickService extends SubscriberImpl implements Globals {
    private final Set<CPacketClickWindow> authorized = new ConcurrentSet<>();

    public PbWindowClickService() {
        this.listeners.add(new PostSendListener<>(CPacketClickWindow.class, e -> {
            if (!authorized.remove(e.getPacket())) {
                PingBypass.sendPacket(new S2CWindowClick(e.getPacket()));
            }
        }));
        this.listeners.add(new LambdaListener<>(TickEvent.class, e -> {
            if (!e.isSafe()) {
                authorized.clear();
            }
        }));
        this.listeners.add(new LambdaListener<>(PbDisconnectEvent.class,
                                                e -> authorized.clear()));
    }

    public void authorize(CPacketClickWindow packet) {
        authorized.add(packet);
    }

}
