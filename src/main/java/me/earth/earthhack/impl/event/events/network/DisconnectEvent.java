package me.earth.earthhack.impl.event.events.network;

import net.minecraft.util.text.ITextComponent;

/**
 * Note that this event gets posted asynchronously!
 */
public class DisconnectEvent
{
    private final ITextComponent component;

    public DisconnectEvent(ITextComponent component)
    {
        this.component = component;
    }

    public ITextComponent getComponent()
    {
        return component;
    }

}
