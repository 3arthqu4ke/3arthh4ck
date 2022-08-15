package me.earth.earthhack.pingbypass.protocol.s2c;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import me.earth.earthhack.pingbypass.protocol.S2CPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;

import java.io.IOException;

public class S2CChatPacket extends S2CPacket implements Globals {
    private ITextComponent chatComponent;
    private ChatType type;
    private int id;

    public S2CChatPacket() {
        super(ProtocolIds.S2C_CHAT);
    }

    public S2CChatPacket(ITextComponent component) {
        this(component, ChatType.SYSTEM);
    }

    public S2CChatPacket(ITextComponent component, ChatType type) {
        this(component, type, 0);
    }

    public S2CChatPacket(ITextComponent component, ChatType type, int id) {
        super(ProtocolIds.S2C_CHAT);
        this.chatComponent = component;
        this.type = type;
        this.id = id;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.chatComponent = buf.readTextComponent();
        this.type = ChatType.byId(buf.readByte());
        this.id = buf.readVarInt();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) {
        buf.writeTextComponent(this.chatComponent);
        buf.writeByte(this.type.getId());
        buf.writeVarInt(this.id);
    }

    @Override
    public void execute(NetworkManager networkManager) {
        mc.addScheduledTask(() -> {
            if (chatComponent != null) {
                ChatUtil.sendComponent(chatComponent, id);
            }
        });
    }

}
