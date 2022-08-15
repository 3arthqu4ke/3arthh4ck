package me.earth.earthhack.pingbypass.protocol.s2c;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.autoconfig.AutoConfig;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import me.earth.earthhack.pingbypass.protocol.S2CPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class S2CActualServerPacket extends S2CPacket implements Globals {
    private static final ModuleCache<AutoConfig> CONFIG =
        Caches.getModule(AutoConfig.class);
    private static final ModuleCache<PingBypassModule> PINGBYPASS =
        Caches.getModule(PingBypassModule.class);

    private String ip;

    public S2CActualServerPacket() {
        super(ProtocolIds.S2C_ACTUAL_IP);
    }

    public S2CActualServerPacket(String ip) {
        super(ProtocolIds.S2C_ACTUAL_IP);
        this.ip = ip;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.ip = buf.readString(Short.MAX_VALUE);
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeString(ip);
    }

    @Override
    public void execute(NetworkManager networkManager) {
        mc.addScheduledTask(() -> {
            if (CONFIG.isEnabled() && PINGBYPASS.isPresent() && ip != null) {
                PINGBYPASS.get().shouldDisconnect = false;
                CONFIG.get().onConnect(ip);
                PINGBYPASS.get().shouldDisconnect = false;
                PINGBYPASS.enable();
                PINGBYPASS.get().shouldDisconnect = true;
            }
        });
    }

}
