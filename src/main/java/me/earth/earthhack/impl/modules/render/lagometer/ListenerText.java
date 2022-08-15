package me.earth.earthhack.impl.modules.render.lagometer;

import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.text.TextColor;

final class ListenerText extends ModuleListener<LagOMeter, Render2DEvent>
{
    public ListenerText(LagOMeter module)
    {
        super(module, Render2DEvent.class);
    }

    @Override
    public void invoke(Render2DEvent event)
    {
        if (!module.response.getValue() && !module.lagTime.getValue()
            || mc.player == null)
        {
            return;
        }

        module.lagMessage = null;
        module.respondingMessage = null;
        if (module.response.getValue()
                && Managers.SERVER.lastResponse()
                    > module.responseTime.getValue())
        {
            module.respondingMessage = TextColor.RED
                    + "Server not responding. ("
                    + MathUtil.round(Managers.SERVER.lastResponse() / 1000.0, 1)
                    + ")";
        }

        if (module.lagTime.getValue())
        {
            long time = module.chatTime.getValue()
                        - (System.currentTimeMillis()
                            - Managers.NCP.getTimeStamp());
            if (time >= 0)
            {
                module.lagMessage = TextColor.RED + "Server lagged you back ("
                        + MathUtil.round(time / 1000.0, 1) + ")";
            }
        }

        String toRender = module.respondingMessage;
        if (toRender == null)
        {
            toRender = module.lagMessage;
        }

        if (toRender == null || !module.render.getValue())
        {
            return;
        }

        Managers.TEXT.drawString(
                toRender,
                (module.resolution.getScaledWidth() / 2.0f)
                    - (Managers.TEXT.getStringWidth(toRender) / 2.0f) + 2,
                20,
                0xffffffff,
                true);
    }

}
