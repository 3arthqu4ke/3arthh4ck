package me.earth.earthhack.impl.modules.player.noinventorydesync;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.util.List;
import java.util.Objects;

public class Restore implements Globals
{
    private final List<ItemStack> list;
    private final int windowId;
    private final int id;
    private final int packetIndex;
    private final long timeStamp;
    private final ItemStack drag;

    public Restore(List<ItemStack> list, ItemStack drag, int windowId, int id, int packetIndex)
    {
        this.list = list;
        this.windowId = windowId;
        this.id = id;
        this.packetIndex = packetIndex;
        this.timeStamp = System.currentTimeMillis();
        this.drag = drag;
    }

    public void restore(List<PacketTimeStamp<INetHandlerPlayClient>> packets)
    {
        Container container;
        if (mc.player != null
                && ((container = mc.player.openContainer) != null && container.windowId == windowId || windowId == 0))
        {
            ChatUtil.sendMessage(TextColor.RED + "Restoring Inventory...");
            if (windowId == 0)
            {
                container = mc.player.inventoryContainer;
            }

            for (int i = 0; i < list.size(); i++)
            {
                if (i < container.inventorySlots.size())
                {
                    container.putStackInSlot(i, list.get(i));
                }
            }

            mc.player.inventory.setItemStack(drag);
            for (int i = getPacketIndex(); i < packets.size(); i++)
            {
                packets.get(i).getPacket().processPacket(mc.player.connection);
            }
        }
    }

    public List<ItemStack> getList()
    {
        return list;
    }

    public int getWindowId()
    {
        return windowId;
    }

    public int getId()
    {
        return id;
    }

    public int getPacketIndex()
    {
        return packetIndex;
    }

    public long getTimeStamp()
    {
        return timeStamp;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Restore)) return false;
        Restore restore = (Restore) o;
        return getWindowId() == restore.getWindowId() && getId() == restore.getId();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getWindowId(), getId());
    }

}
