package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class C2SRiddenEntityPosition extends C2SPacket implements Globals {
    private int entityId;
    private double x;
    private double y;
    private double z;

    public C2SRiddenEntityPosition() {
        super(ProtocolIds.C2S_RIDDEN_ENTITY);
    }

    public C2SRiddenEntityPosition(int entityId, double x, double y, double z) {
        super(ProtocolIds.C2S_RIDDEN_ENTITY);
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }

    @Override
    public void execute(NetworkManager networkManager) throws IOException {
        mc.addScheduledTask(() -> {
            if (mc.world != null) {
                Entity entity = mc.world.getEntityByID(entityId);
                if (entity != null) {
                    entity.setPosition(x, y, z);
                }
            }
        });
    }

}
