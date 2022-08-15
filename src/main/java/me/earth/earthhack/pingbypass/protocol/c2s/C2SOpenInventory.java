package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class C2SOpenInventory extends C2SPacket implements Globals {
    private boolean open;

    public C2SOpenInventory() {
        super(ProtocolIds.C2S_OPEN_INVENTORY);
    }

    public C2SOpenInventory(boolean open) {
        super(ProtocolIds.C2S_OPEN_INVENTORY);
        this.open = open;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.open = buf.readBoolean();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeBoolean(this.open);
    }

    @Override
    public void execute(NetworkManager networkManager) throws IOException {
        mc.addScheduledTask(() -> {
            if (mc.player != null) {
                if (open) {
                    Earthhack.getLogger().info("Displaying Inventory!");
                    mc.displayGuiScreen(new GuiInventory(mc.player));
                } else if (mc.currentScreen instanceof GuiInventory
                    && ((GuiInventory) mc.currentScreen).inventorySlots
                        == mc.player.inventoryContainer) {
                    Earthhack.getLogger().info("Closing Inventory!");
                    mc.displayGuiScreen(null);
                }
            }
        });
    }

}
