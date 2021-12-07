package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.impl.event.events.render.ChatEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.chat.util.LoggerMode;

final class ListenerChatLog extends ModuleListener<Chat, ChatEvent.Log>
{
    public ListenerChatLog(Chat module)
    {
        super(module, ChatEvent.Log.class);
    }

    @Override
    public void invoke(ChatEvent.Log event)
    {
       if (module.log.getValue() != LoggerMode.Normal)
       {
           event.setCancelled(true);
       }
    }

}
