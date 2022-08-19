package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.concurrent.TimeUnit;

public class ListenerEnablePingBypass extends ModuleListener<PingBypassModule, PacketEvent.Receive<SPacketCustomPayload>>
{
    private long lastRequest = 0;

    public ListenerEnablePingBypass(PingBypassModule module)
    {
        super(module, PacketEvent.Receive.class, SPacketCustomPayload.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketCustomPayload> event)
    {
        // TODO: make it so servers cant troll us
        if ("PingBypass|Enable".equals(event.getPacket().getChannelName()))
        {
            if (PingBypass.isServer()) {
                ITextComponent reason = new TextComponentString(
                    "PingBypass server should not connect" +
                        " to another PingBypass server!");
                Earthhack.getLogger().error(reason.getFormattedText());
                event.getNetworkManager().closeChannel(reason);
                mc.addScheduledTask(() -> mc.displayGuiScreen(
                    new GuiDisconnected(new GuiMainMenu(),
                                        "connect.failed", reason)));
                return;
            }

            event.setCancelled(true);
            if (module.allowEnable.getValue() && !module.isEnabled()
                && TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - lastRequest) > 5)
            {
                lastRequest = System.nanoTime();
                module.protocol.setValue(PbProtocol.New, false);
                module.shouldDisconnect = false;
                module.enable();
                module.shouldDisconnect = true;
            }
        }
    }

}
