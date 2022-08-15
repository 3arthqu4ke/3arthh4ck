package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CGameProfile;
import net.minecraft.network.login.server.SPacketLoginSuccess;

public class PbLoginSuccessService
    extends EventListener<PacketEvent.Receive<SPacketLoginSuccess>> {
    public PbLoginSuccessService() {
        super(PacketEvent.Receive.class,
              Integer.MIN_VALUE,
              SPacketLoginSuccess.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketLoginSuccess> event) {
        if (!event.isPingBypassCancelled()) {
            PingBypass.sendPacket(
                new S2CGameProfile(event.getPacket().getProfile()));
        }
    }

}
