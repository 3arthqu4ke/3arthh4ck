package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pbteleport.PbTeleport;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class C2SConfirmLagBack extends C2SPacket implements Globals {
    private static final ModuleCache<PbTeleport> PB_TELEPORT =
        Caches.getModule(PbTeleport.class);

    private int id;

    public C2SConfirmLagBack() {
        super(ProtocolIds.C2S_LAG_BACK_CONFIRM);
    }

    public C2SConfirmLagBack(int id) {
        super(ProtocolIds.C2S_LAG_BACK_CONFIRM);
        this.id = id;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.id = buf.readVarInt();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.id);
    }

    @Override
    public void execute(NetworkManager networkManager) {
        if (PB_TELEPORT.isPresent()) {
            PB_TELEPORT.get().onConfirm(this.id);
        }
    }

}
