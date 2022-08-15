package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class C2SPostKeyPacket extends C2SPacket implements Globals {
    public C2SPostKeyPacket() {
        super(ProtocolIds.C2S_POST_KEYBOARD);
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {

    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {

    }

    @Override
    public void execute(NetworkManager networkManager) throws IOException {
        mc.addScheduledTask(() -> Bus.EVENT_BUS.post(new KeyboardEvent.Post()));
    }

}
