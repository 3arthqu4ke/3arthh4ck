package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CConfirmTransaction;
import net.minecraft.network.play.server.SPacketConfirmTransaction;

public class PbTransactionListener extends EventListener<PacketEvent.Receive<SPacketConfirmTransaction>>
    implements Globals {
    public PbTransactionListener() {
        super(PacketEvent.Receive.class, SPacketConfirmTransaction.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketConfirmTransaction> event) {
        event.setPingBypassCancelled(true);
        PingBypass.sendPacket(new S2CConfirmTransaction(event.getPacket()));
    }

}
