package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.impl.core.ducks.gui.IGuiNewChat;
import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerGameLoop extends ModuleListener<Chat, GameLoopEvent>
{
    public ListenerGameLoop(Chat module)
    {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event)
    {
        if (!module.cleared && mc.ingameGUI != null)
        {
            IGuiNewChat chat = (IGuiNewChat) mc.ingameGUI.getChatGUI();
            if (chat.getScrollPos() == 0)
            {
                module.clearNoScroll();
            }
        }
    }

}
