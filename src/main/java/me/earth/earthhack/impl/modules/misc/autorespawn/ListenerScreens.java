package me.earth.earthhack.impl.modules.misc.autorespawn;

import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.GuiGameOver;

final class ListenerScreens extends
        ModuleListener<AutoRespawn, GuiScreenEvent<GuiGameOver>>
{
    public ListenerScreens(AutoRespawn module)
    {
        super(module, GuiScreenEvent.class, GuiGameOver.class);
    }

    @Override
    public void invoke(GuiScreenEvent<GuiGameOver> event)
    {
        if (mc.player != null)
        {
            if (module.coords.getValue())
            {
                ChatUtil.sendMessage(TextColor.RED
                                + "You died at "
                                + TextColor.WHITE
                                + MathUtil.round(mc.player.posX, 2)
                                + TextColor.RED
                                + "x, "
                                + TextColor.WHITE
                                + MathUtil.round(mc.player.posY, 2)
                                + TextColor.RED
                                + "y, "
                                + TextColor.WHITE
                                + MathUtil.round(mc.player.posZ, 2)
                                + TextColor.RED
                                + "z.");
            }

            mc.player.respawnPlayer();
            event.setCancelled(true);
        }
    }

}
