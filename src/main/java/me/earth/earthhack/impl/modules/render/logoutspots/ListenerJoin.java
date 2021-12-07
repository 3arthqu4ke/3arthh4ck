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
import net.minecraft.util.math.Vec3d;

final class ListenerJoin extends ModuleListener<LogoutSpots, ConnectionEvent.Join>
{
    public ListenerJoin(LogoutSpots module)
    {
        super(module, ConnectionEvent.Join.class);
    }

    @Override
    public void invoke(ConnectionEvent.Join event)
    {
        if (event.getName().equals(mc.getSession().getProfile().getName()))
        {
            return;
        }

        LogoutSpot spot = module.spots.remove(event.getUuid());
        if (module.message.getValue() != MessageMode.None)
        {
            String text;
            if (spot != null)
            {
                Vec3d pos = spot.rounded();
                text = TextColor.YELLOW
                        + event.getName()
                        + TextColor.RED
                        + " is back at: "
                        + pos.x + "x, "
                        + pos.y + "y, "
                        + pos.z + "z!";
            }
            else
            {
                EntityPlayer player = event.getPlayer();
                if (player != null)
                {
                    text = TextColor.YELLOW + player.getName() + TextColor.GREEN + " just joined at: %sx, %sy, %sz!";
                    text = String.format(text, MathUtil.round(player.posX, 1),
                                               MathUtil.round(player.posY, 1),
                                               MathUtil.round(player.posZ, 1));
                }
                else if (module.message.getValue() != MessageMode.Render)
                {
                    text = TextColor.YELLOW + event.getName() + TextColor.GREEN + " just joined.";
                }
                else
                {
                    return;
                }
            }

            Managers.CHAT.sendDeleteMessageScheduled(text, event.getUuid().toString(), ChatIDs.MODULE);
        }
    }

}
