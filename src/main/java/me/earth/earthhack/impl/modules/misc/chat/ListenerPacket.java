package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.chat.util.LoggerMode;
import net.minecraft.network.play.server.SPacketChat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

final class ListenerPacket extends
        ModuleListener<Chat, PacketEvent.Receive<SPacketChat>>
{
    private static final Logger LOGGER = LogManager.getLogger();

    public ListenerPacket(Chat module)
    {
        super(module, PacketEvent.Receive.class, SPacketChat.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketChat> event)
    {
        if (module.log.getValue() == LoggerMode.Async)
        {
            LOGGER.info("[CHAT] {}", event.getPacket()
                                          .getChatComponent()
                                          .getUnformattedText()
                                          .replaceAll("\r", "\\\\r")
                                          .replaceAll("\n", "\\\\n"));
        }
    }

}
