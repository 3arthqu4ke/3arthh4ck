package me.earth.earthhack.impl.event.events.network;

import net.minecraft.network.NetworkManager;
import net.minecraft.util.text.ITextComponent;

/**
 * Note that this event gets posted asynchronously!
 */
public class DisconnectEvent
{
    private final ITextComponent component;
    private final NetworkManager manager;

    public DisconnectEvent(ITextComponent component, NetworkManager manager)
    {
        this.component = component;
        this.manager = manager;
    }

    public ITextComponent getComponent()
    {
        return component;
    }

    public NetworkManager getManager()
    {
        return manager;
    }

}
