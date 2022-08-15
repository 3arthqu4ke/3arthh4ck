package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.event.PlayerEvent;
import me.earth.earthhack.impl.managers.client.event.PlayerEventType;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class C2SFriendPacket extends C2SPacket {
    private static final Logger LOGGER = LogManager.getLogger(C2SFriendPacket.class);

    private PlayerEventType type;
    private String name;
    private UUID uuid;

    public C2SFriendPacket() {
        super(ProtocolIds.C2S_FRIEND);
    }

    public C2SFriendPacket(PlayerEvent event) {
        super(ProtocolIds.C2S_FRIEND);
        this.name = event.getName();
        this.type = event.getType();
        this.uuid = event.getUuid();
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) {
        try {
            this.name = buf.readString(Short.MAX_VALUE);
            this.uuid = UUID.fromString(buf.readString(Short.MAX_VALUE));
            this.type = buf.readEnumValue(PlayerEventType.class);
        } catch (Exception e) {
            LOGGER.error(e);
            throw e;
        }
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) {
        try {
            buf.writeString(this.name);
            buf.writeString(this.uuid == null ? UUID.randomUUID().toString() : this.uuid.toString());
            buf.writeEnumValue(type);
        } catch (Exception e) {
            LOGGER.error(e);
            throw e;
        }
    }

    @Override
    public void execute(NetworkManager networkManager) {
        switch (type) {
            case ADD:
                Managers.FRIENDS.add(name, uuid);
                break;
            case DEL:
                Managers.FRIENDS.remove(name);
                break;
            default:
        }
    }

}
