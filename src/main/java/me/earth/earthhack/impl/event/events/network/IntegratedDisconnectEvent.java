package me.earth.earthhack.impl.event.events.network;

import net.minecraft.network.NetworkManager;
import net.minecraft.util.text.ITextComponent;

public class IntegratedDisconnectEvent extends DisconnectEvent {
    public IntegratedDisconnectEvent(ITextComponent component, NetworkManager manager) {
        super(component, manager);
    }

}
