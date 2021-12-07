package me.earth.earthhack.impl.modules.misc.tracker;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.network.play.server.SPacketChat;

final class ListenerChat extends
        ModuleListener<Tracker, PacketEvent.Receive<SPacketChat>>
{
    public ListenerChat(Tracker module)
    {
        super(module, PacketEvent.Receive.class, SPacketChat.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketChat> event)
    {
        if (module.autoEnable.getValue()
                && !module.awaiting
                && !module.isEnabled())
        {
            String s = event.getPacket().getChatComponent().getFormattedText();
            if (!s.contains("<") // must be a message by the server
                    && (s.contains("has accepted your duel request")
                        || s.contains("Accepted the duel request from")))
            {
                Scheduler.getInstance().scheduleAsynchronously(() ->
                {
                    ModuleUtil.sendMessage(module,
                        TextColor.LIGHT_PURPLE
                            + "Duel accepted. Tracker will enable in "
                            + TextColor.WHITE
                            + "5.0"
                            + TextColor.LIGHT_PURPLE
                            + " seconds!");

                    module.timer.reset();
                    module.awaiting = true;
                });
            }
        }
    }

}
