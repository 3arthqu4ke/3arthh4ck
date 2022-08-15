package me.earth.earthhack.pingbypass.protocol.s2c;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.render.ColorUtil;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import me.earth.earthhack.pingbypass.protocol.S2CPacket;
import me.earth.earthhack.pingbypass.util.PacketBufferUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.io.IOException;

public class S2CRenderPacket extends S2CPacket implements Globals {
    private AxisAlignedBB bb;
    private Color outLine;
    private Color color;

    public S2CRenderPacket() {
        super(ProtocolIds.S2C_RENDER);
    }

    public S2CRenderPacket(BlockPos pos, Color outline, Color color) {
        this(new AxisAlignedBB(pos), outline, color);
    }

    public S2CRenderPacket(AxisAlignedBB bb, Color outline, Color color) {
        super(ProtocolIds.S2C_RENDER);
        this.bb = bb;
        this.outLine = outline;
        this.color = color;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        bb = PacketBufferUtil.readBB(buf);
        outLine = ColorUtil.fromARGB(buf.readVarInt());
        color = ColorUtil.fromARGB(buf.readVarInt());
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        PacketBufferUtil.writeBB(bb, buf);
        buf.writeVarInt(ColorUtil.toARGB(outLine));
        buf.writeVarInt(ColorUtil.toARGB(color));
    }

    @Override
    public void execute(NetworkManager networkManager) {
        mc.addScheduledTask(() -> PingBypass.RENDER.addRender(this));
    }

    public AxisAlignedBB getBb() {
        return bb;
    }

    public Color getOutLine() {
        return outLine;
    }

    public Color getColor() {
        return color;
    }

}
