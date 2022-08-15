package me.earth.earthhack.pingbypass.protocol.s2c;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import me.earth.earthhack.pingbypass.protocol.S2CPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketConfirmTransaction;

/**
 * Since we don't send {@link SPacketConfirmTransaction} to the client this
 * allows us to still inform the client about ConfirmTransactions.
 */
public class S2CConfirmTransaction extends S2CPacket implements Globals {
    private int windowId;
    private short actionNumber;
    private boolean accepted;

    public S2CConfirmTransaction() {
        super(ProtocolIds.S2C_CONFIRM_TRANSACTION);
    }

    public S2CConfirmTransaction(SPacketConfirmTransaction packet) {
        super(ProtocolIds.S2C_CONFIRM_TRANSACTION);
        this.windowId = packet.getWindowId();
        this.actionNumber = packet.getActionNumber();
        this.accepted = packet.wasAccepted();
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) {
        this.windowId = buf.readUnsignedByte();
        this.actionNumber = buf.readShort();
        this.accepted = buf.readBoolean();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) {
        buf.writeByte(this.windowId);
        buf.writeShort(this.actionNumber);
        buf.writeBoolean(this.accepted);
    }

    public int getWindowId() {
        return windowId;
    }

    public short getActionNumber() {
        return actionNumber;
    }

    public boolean wasAccepted() {
        return accepted;
    }

}
