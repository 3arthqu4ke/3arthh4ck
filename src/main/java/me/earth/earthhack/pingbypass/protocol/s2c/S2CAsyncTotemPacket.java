package me.earth.earthhack.pingbypass.protocol.s2c;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import me.earth.earthhack.pingbypass.protocol.S2CPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

/**
 * Prevents Offhand - Async totem from being marked as desync.
 */
public class S2CAsyncTotemPacket extends S2CPacket implements Globals {
    private int slot;

    public S2CAsyncTotemPacket() {
        super(ProtocolIds.S2C_ASYNC_TOTEM);
    }

    public S2CAsyncTotemPacket(int slot) {
        super(ProtocolIds.S2C_ASYNC_TOTEM);
        this.slot = slot;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.slot = buf.readVarInt();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.slot);
    }

    @Override
    public void execute(NetworkManager networkManager) {
        mc.addScheduledTask(Locks.wrap(
            Locks.WINDOW_CLICK_LOCK,
            () -> InventoryUtil.put(slot, ItemStack.EMPTY)));
    }

}
