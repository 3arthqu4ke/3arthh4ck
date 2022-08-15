package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketPlayer;

import java.io.IOException;

public class C2SActualPos extends C2SPacket implements Globals {
    private static final SettingCache
        <Double, NumberSetting<Double>, Management> RANGE =
        Caches.getSetting(Management.class, NumberSetting.class,
                          "PB-Position-Range", 5.0);
    private static final ModuleCache<PacketFly> PACKET_FLY =
        Caches.getModule(PacketFly.class);

    private boolean packetFly;
    private double x;
    private double y;
    private double z;

    public C2SActualPos() {
        super(ProtocolIds.C2S_ACTUAL_POS);
    }

    public C2SActualPos(double x, double y, double z) {
        super(ProtocolIds.C2S_ACTUAL_POS);
        this.x = x;
        this.y = y;
        this.z = z;
        this.packetFly = PACKET_FLY.isEnabled();
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.packetFly = buf.readBoolean();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeBoolean(packetFly);
    }

    @Override
    public void execute(NetworkManager networkManager) throws IOException {
        mc.addScheduledTask(() -> {
            PingBypass.PACKET_SERVICE.setPacketFlying(packetFly);
            PingBypass.PACKET_SERVICE.setActualPos(this);
        });
    }

    public boolean isValid(CPacketPlayer packet) {
        double pX = packet.getX(this.x) - this.x;
        double pY = packet.getY(this.y) - this.y;
        double pZ = packet.getZ(this.z) - this.z;
        return pX * pX + pY * pY + pZ * pZ <= MathUtil.square(RANGE.getValue());
    }

}
