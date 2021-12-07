package me.earth.earthhack.impl.modules.render.lagometer;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.text.ChatIDs;
import net.minecraft.client.gui.ScaledResolution;

final class ListenerTick extends ModuleListener<LagOMeter, TickEvent>
{
    public ListenerTick(LagOMeter module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (module.lagTime.getValue() || module.response.getValue())
        {
            module.resolution = new ScaledResolution(mc);
        }

        if (!module.chat.getValue())
        {
            if (module.sent)
            {
                Managers.CHAT.deleteMessage("Lag-O-Meter-Res", ChatIDs.MODULE);
                Managers.CHAT.deleteMessage("Lag-O-Meter-Lag", ChatIDs.MODULE);
                module.sent = false;
            }

            return;
        }

        boolean sent = module.sent;
        module.sent = false;
        if (module.respondingMessage != null)
        {
            module.sent = true;
            Managers.CHAT.sendDeleteMessage("<" + module.getDisplayName() + "> "
                                            + module.respondingMessage + ".",
                    "Lag-O-Meter-Res",
                    ChatIDs.MODULE);
        }

        if (module.lagMessage != null)
        {
            module.sent = true;
            Managers.CHAT.sendDeleteMessage("<" + module.getDisplayName() + "> "
                                             + module.lagMessage + ".",
                    "Lag-O-Meter-Lag",
                    ChatIDs.MODULE);
        }

        if (sent && !module.sent)
        {
            Managers.CHAT.deleteMessage("Lag-O-Meter-Res", ChatIDs.MODULE);
            Managers.CHAT.deleteMessage("Lag-O-Meter-Lag", ChatIDs.MODULE);
        }
    }

}
