package me.earth.earthhack.pingbypass.protocol.s2c;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.util.IContainer;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import me.earth.earthhack.pingbypass.protocol.S2CPacket;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketClickWindow;

import java.io.IOException;

public class S2CWindowClick extends S2CPacket implements Globals {
    private int windowId;
    private int slotId;
    private int mouseButton;
    private short actionNumber;
    private ItemStack clickedItem = ItemStack.EMPTY;
    private ClickType type;

    public S2CWindowClick() {
        super(ProtocolIds.S2C_WINDOW_CLICK);
    }

    public S2CWindowClick(CPacketClickWindow packet) {
        super(ProtocolIds.S2C_WINDOW_CLICK);
        this.windowId = packet.getWindowId();
        this.slotId = packet.getSlotId();
        this.mouseButton = packet.getUsedButton();
        this.actionNumber = packet.getActionNumber();
        this.clickedItem = packet.getClickedItem();
        this.type = packet.getClickType();
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.windowId = buf.readByte();
        this.slotId = buf.readShort();
        this.mouseButton = buf.readByte();
        this.actionNumber = buf.readShort();
        this.type = buf.readEnumValue(ClickType.class);
        this.clickedItem = buf.readItemStack();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeByte(this.windowId);
        buf.writeShort(this.slotId);
        buf.writeByte(this.mouseButton);
        buf.writeShort(this.actionNumber);
        buf.writeEnumValue(this.type);
        buf.writeItemStack(this.clickedItem);
        //net.minecraftforge.common.util.PacketUtil.writeItemStackFromClientToServer(buf, this.clickedItem);
    }

    @Override
    public void execute(NetworkManager networkManager) {
        mc.addScheduledTask(() -> {
            if (mc.player != null && mc.player.openContainer.windowId == windowId) {
                try {
                    ((IContainer) mc.player.openContainer).setTransactionID(actionNumber);
                    ItemStack itemstack = mc.player.openContainer.slotClick(slotId, mouseButton, type, mc.player);
                    if (!ItemStack.areItemStacksEqualUsingNBTShareTag(itemstack, clickedItem)) {
                        ChatUtil.sendMessage("<" + TextColor.DARK_RED + "PingBypass" + TextColor.WHITE + "> Inventory desync in slot " + slotId + "!");
                        //Earthhack.getLogger().warn("Inventory desync in slot: " + slotId + "!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
