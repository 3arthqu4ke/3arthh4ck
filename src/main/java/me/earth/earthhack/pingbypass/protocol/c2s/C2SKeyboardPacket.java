package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class C2SKeyboardPacket extends C2SPacket implements Globals {
    private boolean eventState;
    private char character;
    private int key;

    public C2SKeyboardPacket() {
        super(ProtocolIds.C2S_KEYBOARD);
    }

    public C2SKeyboardPacket(KeyboardEvent event) {
        super(ProtocolIds.C2S_KEYBOARD);
        this.eventState = event.getEventState();
        this.character = event.getCharacter();
        this.key = event.getKey();
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.eventState = buf.readBoolean();
        this.character = buf.readChar();
        this.key = buf.readVarInt();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeBoolean(this.eventState);
        buf.writeChar(this.character);
        buf.writeVarInt(this.key);
    }

    @Override
    public void execute(NetworkManager networkManager) throws IOException {
        mc.addScheduledTask(() -> Bus.EVENT_BUS.post(
            new KeyboardEvent(eventState, key, character)));
    }

}
