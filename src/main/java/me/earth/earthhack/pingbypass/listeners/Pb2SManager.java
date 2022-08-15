package me.earth.earthhack.pingbypass.listeners;

import com.google.common.collect.Sets;
import io.netty.util.internal.ConcurrentSet;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.util.Set;

public class Pb2SManager extends SubscriberImpl implements Globals {
    private final ThreadLocal<Boolean> allow = ThreadLocal.withInitial(() -> Boolean.FALSE);
    private final Set<Packet<?>> authorized = new ConcurrentSet<>();
    private final Set<Class<?>> blackList = Sets.newHashSet(
        // TODO: option to switch, pingbypass is now allowed to move
        CPacketPlayer.class, CPacketPlayer.Position.class,
        CPacketPlayer.PositionRotation.class, CPacketPlayer.Rotation.class,
        CPacketConfirmTeleport.class, CPacketInput.class, CPacketVehicleMove.class,
        CPacketSteerBoat.class, CPacketClientSettings.class, CPacketClientStatus.class,
        CPacketPlayerAbilities.class, CPacketEntityAction.class, CPacketSeenAdvancements.class, //?
        CPacketCloseWindow.class
        // TODO: CPacketEntityAction?
        // TODO: Some CustomPayloads?
    );

    public Pb2SManager() {
        allow.set(false);
        this.listeners.add(new LambdaListener<>(PacketEvent.Send.class, Integer.MAX_VALUE - 1, event -> {
            if (isUnAuthorized(event.getPacket()) && PingBypass.isConnected() && blackList.contains(event.getPacket().getClass()) && noThreadLocalFlag()) {
                event.setCancelled(true);
            }
        }));

        this.listeners.add(new LambdaListener<>(TickEvent.class, event -> {
            if (!event.isSafe()) {
                authorized.clear();
            }
        }));
    }

    public boolean isUnAuthorized(Packet<?> packet) {
        return !authorized.remove(packet);
    }

    public boolean noThreadLocalFlag() {
        return !allow.get();
    }

    public void allowAllOnThisThread(boolean allow) {
        this.allow.set(allow);
    }

    public void authorize(Packet<?> packet) {
        if (PingBypass.isConnected()) {
            authorized.add(packet);
        }
    }

}
