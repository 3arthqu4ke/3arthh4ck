package me.earth.earthhack.impl.modules.misc.noafk;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.network.play.server.SPacketChat;

final class ListenerChat extends
        ModuleListener<NoAFK, PacketEvent.Receive<SPacketChat>>
{
    public ListenerChat(NoAFK module)
    {
        super(module, PacketEvent.Receive.class, SPacketChat.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketChat> event)
    {
        if (module.autoReply.getValue())
        {
            String m = event.getPacket().getChatComponent().getFormattedText();
            if ((m.contains(module.color.getValue().getColor())
                    || module.color.getValue() == TextColor.Reset)
                        && m.contains(module.indicator.getValue()))
            {
                mc.player.sendChatMessage(module.reply.getValue()
                        + module.message.getValue());
            }
        }
    }

}
