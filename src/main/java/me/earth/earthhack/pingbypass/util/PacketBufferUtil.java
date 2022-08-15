package me.earth.earthhack.pingbypass.util;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;

public class PacketBufferUtil {
    public static AxisAlignedBB readBB(PacketBuffer buf) {
        double minX = buf.readDouble();
        double minY = buf.readDouble();
        double minZ = buf.readDouble();
        double maxX = buf.readDouble();
        double maxY = buf.readDouble();
        double maxZ = buf.readDouble();
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static void writeBB(AxisAlignedBB bb, PacketBuffer buf) {
        buf.writeDouble(bb.minX);
        buf.writeDouble(bb.minY);
        buf.writeDouble(bb.minZ);
        buf.writeDouble(bb.maxX);
        buf.writeDouble(bb.maxY);
        buf.writeDouble(bb.maxZ);
    }

}
