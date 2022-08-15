package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;

public class C2SClickBlockPacket extends C2SPacket implements Globals {
    private BlockPos pos;
    private EnumFacing facing;

    public C2SClickBlockPacket() {
        super(ProtocolIds.C2S_CLICK_BLOCK);
    }

    public C2SClickBlockPacket(BlockPos pos, EnumFacing facing) {
        super(ProtocolIds.C2S_CLICK_BLOCK);
        this.pos = pos;
        this.facing = facing;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.pos = buf.readBlockPos();
        this.facing = buf.readEnumValue(EnumFacing.class);
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeBlockPos(pos);
        buf.writeEnumValue(facing);
    }

    @Override
    public void execute(NetworkManager networkManager) throws IOException {
        mc.addScheduledTask(() -> {
            if (mc.player != null && mc.playerController != null) {
                Bus.EVENT_BUS.post(new ClickBlockEvent(pos, facing));
            }
        });
    }

}
