package me.earth.earthhack.pingbypass.listeners;

import com.google.common.collect.Sets;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.status.server.SPacketPong;
import net.minecraft.network.status.server.SPacketServerInfo;

import java.util.Set;

public class PbReceiveListener extends EventListener<PacketEvent.Receive<?>> {
    private final Set<Class<?>> blacklist = Sets.newHashSet(
        SPacketServerInfo.class, SPacketPong.class,
        SPacketEncryptionRequest.class, SPacketLoginSuccess.class,
        SPacketEnableCompression.class, SPacketKeepAlive.class,
        SPacketDisconnect.class,
        net.minecraft.network.login.server.SPacketDisconnect.class
    );

    public PbReceiveListener() {
        super(PacketEvent.Receive.class, Integer.MIN_VALUE);
        try {
            blacklist.add(Class.forName("net.minecraftforge.fml.common.network.internal.FMLProxyPacket"));
        } catch (ClassNotFoundException ignored) {

        }
    }

    @Override
    public void invoke(PacketEvent.Receive<?> event) {
        if (!event.isPingBypassCancelled()
            && !blacklist.contains(event.getPacket().getClass())) {
            // TODO: locking will take a long time when joining the world,
            //  could we instead queue the packets we receive?
            Locks.acquire(Locks.PINGBYPASS_PACKET_LOCK,
                          () -> PingBypass.sendPacket(event.getPacket()));
        }
    }

}
