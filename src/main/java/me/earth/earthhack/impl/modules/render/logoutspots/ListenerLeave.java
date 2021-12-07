package me.earth.earthhack.impl.modules.render.logoutspots;

import me.earth.earthhack.impl.event.events.network.ConnectionEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.logoutspots.mode.MessageMode;
import me.earth.earthhack.impl.modules.render.logoutspots.util.LogoutSpot;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.player.EntityPlayer;

final class ListenerLeave extends ModuleListener<LogoutSpots, ConnectionEvent.Leave>
{
    public ListenerLeave(LogoutSpots module)
    {
        super(module, ConnectionEvent.Leave.class);
    }

    @Override
    public void invoke(ConnectionEvent.Leave event)
    {
        EntityPlayer player = event.getPlayer();
        if (module.message.getValue() != MessageMode.None)
        {
            String text = null;
            if (player != null)
            {
                text = String.format(TextColor.YELLOW
                                        + player.getName()
                                        + TextColor.RED
                                        + " just logged out, at: %sx, %sy, %sz.",
                                            MathUtil.round(player.posX, 1),
                                            MathUtil.round(player.posY, 1),
                                            MathUtil.round(player.posZ, 1));
            }
            else if (module.message.getValue() != MessageMode.Render)
            {
                text = TextColor.YELLOW + event.getName() + TextColor.RED + " just logged out.";
            }

            if (text != null)
            {
                Managers.CHAT.sendDeleteMessageScheduled(text, event.getUuid().toString(), ChatIDs.MODULE);
            }
        }

        if (player != null && (module.friends.getValue() || !Managers.FRIENDS.contains(player)))
        {
            LogoutSpot spot = new LogoutSpot(player);
            module.spots.put(player.getUniqueID(), spot);
        }
    }

}
