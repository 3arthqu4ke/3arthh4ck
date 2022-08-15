package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.event.events.misc.DamageBlockEvent;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;

public class C2SDamageBlockPacket extends C2SPacket implements Globals {
    private BlockPos pos;
    private EnumFacing facing;
    private float damage;
    private int delay;

    public C2SDamageBlockPacket() {
        super(ProtocolIds.C2S_DAMAGE_BLOCK);
    }

    public C2SDamageBlockPacket(BlockPos pos, EnumFacing facing, float damage,
                                int delay) {
        super(ProtocolIds.C2S_DAMAGE_BLOCK);
        this.pos = pos;
        this.facing = facing;
        this.damage = damage;
        this.delay = delay;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.pos = buf.readBlockPos();
        this.facing = buf.readEnumValue(EnumFacing.class);
        this.damage = buf.readFloat();
        this.delay = buf.readVarInt();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeBlockPos(pos);
        buf.writeEnumValue(facing);
        buf.writeFloat(damage);
        buf.writeVarInt(delay);
    }

    @Override
    public void execute(NetworkManager networkManager) throws IOException {
        mc.addScheduledTask(() -> {
            if (mc.player != null && mc.playerController != null) {
                DamageBlockEvent event = new DamageBlockEvent(pos, facing, damage, delay);
                Bus.EVENT_BUS.post(event);
                ((IPlayerControllerMP) mc.playerController).setCurBlockDamageMP(event.getDamage());
                ((IPlayerControllerMP) mc.playerController).setBlockHitDelay(event.getDelay());
            }
        });
    }

}
