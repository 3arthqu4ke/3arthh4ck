package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.tooltips.util.TimeStack;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;

final class ListenerRender2D extends ModuleListener<ToolTips, Render2DEvent>
{
    public ListenerRender2D(ToolTips module)
    {
        super(module, Render2DEvent.class);
    }

    @Override
    public void invoke(Render2DEvent event)
    {
        int x = 1;
        int y = Managers.TEXT.getStringHeightI() + 4;

        for (EntityPlayer player : mc.world.playerEntities)
        {
            if (player != null && !EntityUtil.isDead(player))
            {
                TimeStack stack =
                        module.spiedPlayers.get(player.getName().toLowerCase());
                if (stack != null
                        && (player.getHeldItemMainhand()
                                    .equals(stack.getStack())
                            || System.nanoTime()
                                    - stack.getTime() < 2000000000))
                {
                    if (!module.drawShulkerToolTip(
                            stack.getStack(), x, y, player.getName()))
                    {
                        module.spiedPlayers.remove(
                                player.getName().toLowerCase());
                    }
                    else
                    {
                        y += 79;
                    }
                }
            }
        }
    }

}
