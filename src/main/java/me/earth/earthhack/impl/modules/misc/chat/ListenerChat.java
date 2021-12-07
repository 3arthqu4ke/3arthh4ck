package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.impl.core.ducks.gui.IGuiNewChat;
import me.earth.earthhack.impl.event.events.render.ChatEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

// TODO: Way too complicated for what we want to do...
final class ListenerChat extends ModuleListener<Chat, ChatEvent.Send>
{
    public ListenerChat(Chat module)
    {
        super(module, ChatEvent.Send.class);
    }

    @Override
    public void invoke(ChatEvent.Send event)
    {
        if (module.noScroll.getValue() && mc.ingameGUI != null)
        {
            IGuiNewChat chat = (IGuiNewChat) mc.ingameGUI.getChatGUI();
            if (chat.getScrollPos() != 0)
            {
                module.events.add(event);
                module.cleared = false;
                event.setCancelled(true);
            }
        }
    }

}
