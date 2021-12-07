package me.earth.earthhack.impl.modules.client.media;

import me.earth.earthhack.impl.event.events.render.ChatEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerClearChat extends ModuleListener<Media, ChatEvent.Clear>
{
    public ListenerClearChat(Media module)
    {
        super(module, ChatEvent.Clear.class);
    }

    @Override
    public void invoke(ChatEvent.Clear event)
    {
        module.cache.clear();
    }

}
