package me.earth.earthhack.pingbypass.event;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.text.ITextComponent;

public class PbDisconnectEvent extends DisconnectEvent {
    public PbDisconnectEvent(ITextComponent component, NetworkManager manager) {
        super(component, manager);
    }

}
