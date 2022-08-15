package me.earth.earthhack.pingbypass.event;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

public class PbPacketEvent {
    private PbPacketEvent() {

    }

    public static class S2C<T extends Packet<? extends INetHandler>>
        extends PacketEvent.Send<T>
    {
        public S2C(T packet)
        {
            super(packet);
        }
    }

    public static class S2CNoEvent<T extends Packet<? extends INetHandler>>
        extends PacketEvent.NoEvent<T>
    {
        public S2CNoEvent(T packet, boolean post)
        {
            super(packet, post);
        }
    }

    public static class C2S<T extends Packet<? extends INetHandler>>
        extends PacketEvent.Receive<T>
    {
        public C2S(T packet, NetworkManager networkManager)
        {
            super(packet, networkManager);
        }
    }

    public static class C2SPost<T extends Packet<? extends INetHandler>> extends PacketEvent.Post<T>
    {
        public C2SPost(T packet)
        {
            super(packet);
        }
    }

}
