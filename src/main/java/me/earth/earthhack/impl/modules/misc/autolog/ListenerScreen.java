package me.earth.earthhack.impl.modules.misc.autolog;

import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.autolog.util.LogScreen;
import net.minecraft.client.gui.GuiDisconnected;

final class ListenerScreen
        extends ModuleListener<AutoLog, GuiScreenEvent<GuiDisconnected>>
{
    public ListenerScreen(AutoLog module)
    {
        super(module, GuiScreenEvent.class, GuiDisconnected.class);
    }

    @Override
    public void invoke(GuiScreenEvent<GuiDisconnected> event)
    {
        if (module.awaitScreen)
        {
            module.awaitScreen = false;
            mc.displayGuiScreen(
                    new LogScreen(module, module.message, module.serverData));
            event.setCancelled(true);
        }
    }

}
