package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.impl.core.ducks.util.IContainer;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketKeepAlive;

/**
 * We manage ping from this side, because it allows us to send
 * clearly identified Packets (id == -1337). Everytime a CPacketKeepAlive
 * is sent we also sync the transactionId for mc.player.openContainer.
 */
final class ListenerTick extends ModuleListener<PingBypassModule, TickEvent>
{
    public ListenerTick(PingBypassModule module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (module.timer.passed(module.pings.getValue() * 1000))
        {
            NetHandlerPlayClient connection = mc.getConnection();
            if (connection != null)
            {
                module.startTime = System.currentTimeMillis();
                module.handled = false;

                if (module.isOld())
                {
                    CPacketClickWindow container = new CPacketClickWindow(1, -1337, 1, ClickType.PICKUP, ItemStack.EMPTY, ((IContainer) mc.player.openContainer).getTransactionID());
                    CPacketKeepAlive alive = new CPacketKeepAlive(-1337);
                    connection.sendPacket(container);
                    connection.sendPacket(alive);
                }
                else
                {
                    // it was just convenient to use the old protocol where the client sends KeepAlives to get the ping
                    CPacketKeepAlive alive = new CPacketKeepAlive(-1337);
                    connection.sendPacket(alive);
                }

                module.timer.reset();
            }
        }
    }

}
