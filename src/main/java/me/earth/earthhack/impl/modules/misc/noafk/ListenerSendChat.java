package me.earth.earthhack.impl.modules.misc.noafk;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.network.play.client.CPacketChatMessage;

final class ListenerSendChat
    extends ModuleListener<NoAFK, PacketEvent.Send<CPacketChatMessage>>
{
    public ListenerSendChat(NoAFK module)
    {
        super(module, PacketEvent.Send.class, CPacketChatMessage.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketChatMessage> event)
    {
        if (module.blockingChatMessages)
        {
            Managers.CHAT.sendDeleteMessage(
                TextColor.RED + "Not sending "
                    + TextColor.WHITE + event.getPacket().getMessage()
                    + TextColor.RED
                    + " to server! Make sure you have baritone installed!",
                "NoAfkBaritone",
                ChatIDs.MODULE);
            event.setCancelled(true);
        }
    }

}

