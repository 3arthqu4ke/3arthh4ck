package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.listeners.AntiSelfConnectHelper;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.Arrays;
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
                Earthhack.getLogger().warn("PingBypass|Enable on server!");
                PacketBuffer buf = event.getPacket().getBufferData();
                byte[] current = AntiSelfConnectHelper.getCurrent();
                if (current != null && buf.readableBytes() == 16) {
                    byte[] bytes = new byte[16];
                    buf.readBytes(bytes);
                    if (Arrays.equals(current, bytes)) {
                        ITextComponent reason = new TextComponentString(
                            "Cant connect PingBypass server to itself!");
                        Earthhack.getLogger().error(reason.getFormattedText());
                        event.getNetworkManager().closeChannel(reason);
                        mc.addScheduledTask(() -> mc.displayGuiScreen(
                            new GuiDisconnected(new GuiMainMenu(),
                                                "connect.failed", reason)));
                        return;
                    }
                } else {
                    Earthhack.getLogger().warn(
                        "PingBypass|Enable didnt have the correct amount of bytes!");
                }
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
