package me.earth.earthhack.impl.modules.misc.autoreconnect;

import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.client.gui.GuiDisconnected;

final class ListenerScreen extends
        ModuleListener<AutoReconnect, GuiScreenEvent<GuiDisconnected>>
{
    public ListenerScreen(AutoReconnect module)
    {
        super(module, GuiScreenEvent.class, -1000, GuiDisconnected.class);
    }

    @Override
    public void invoke(GuiScreenEvent<GuiDisconnected> event)
    {
        if (!event.isCancelled() && !PingBypass.isConnected()) {
            if (module.connected) {
                module.setConnected(false);
            } else {
                module.onGuiDisconnected(event.getScreen());
                event.setCancelled(true);
            }
        }
    }

}
