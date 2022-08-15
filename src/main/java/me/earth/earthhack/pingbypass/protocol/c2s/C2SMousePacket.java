package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.keyboard.MouseEvent;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class C2SMousePacket extends C2SPacket implements Globals {
    private int key;
    private boolean state;

    public C2SMousePacket() {
        super(ProtocolIds.C2S_MOUSE);
    }

    public C2SMousePacket(MouseEvent event) {
        super(ProtocolIds.C2S_MOUSE);
        this.key = event.getButton();
        this.state = event.getState();
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.key = buf.readVarInt();
        this.state = buf.readBoolean();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.key);
        buf.writeBoolean(this.state);
    }

    @Override
    public void execute(NetworkManager networkManager) throws IOException {
        mc.addScheduledTask(() -> Bus.EVENT_BUS.post(
            new MouseEvent(key, state)));
    }

}
