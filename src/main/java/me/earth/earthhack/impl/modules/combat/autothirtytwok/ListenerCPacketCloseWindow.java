package me.earth.earthhack.impl.modules.combat.autothirtytwok;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.client.CPacketCloseWindow;

final class ListenerCPacketCloseWindow extends ModuleListener<Auto32k, PacketEvent.Send<CPacketCloseWindow>> {

    public ListenerCPacketCloseWindow(Auto32k module) {
        super(module, PacketEvent.Send.class, CPacketCloseWindow.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketCloseWindow> event) {
        module.onCPacketCloseWindow(event);
    }

}
