package me.earth.earthhack.pingbypass.protocol.s2c;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import me.earth.earthhack.pingbypass.protocol.S2CPacket;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SPasswordPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

public class S2CPasswordPacket extends S2CPacket implements Globals {
    private static final ModuleCache<PingBypassModule> MODULE =
        Caches.getModule(PingBypassModule.class);

    public S2CPasswordPacket() {
        super(ProtocolIds.S2C_PASSWORD);
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) {
        // NOP this password just requests the password
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) {
        // NOP this password just requests the password
    }

    @Override
    public void execute(NetworkManager networkManager) {
        String password = MODULE.get().password.getValue();
        networkManager.sendPacket(new C2SPasswordPacket(
            password == null ? "null" : password));
    }

}
