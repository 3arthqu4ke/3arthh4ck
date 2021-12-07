package me.earth.earthhack.impl.modules.render.sounds;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.sounds.util.CustomSound;
import me.earth.earthhack.impl.modules.render.sounds.util.SoundPosition;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;

import java.awt.*;
import java.util.Map;

final class ListenerRender extends ModuleListener<Sounds, Render3DEvent>
{
    public ListenerRender(Sounds module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        if (!module.render.getValue())
        {
            return;
        }

        for (Map.Entry<SoundPosition, Long> e : module.sounds.entrySet())
        {
            SoundPosition p = e.getKey();
            int color = module.color.getRGB();
            if (p instanceof CustomSound)
            {
                color = Color.HSBtoRGB(Managers.COLOR.getHueByPosition(
                                            p.getY()),
                                       1.0f,
                                       1.0f);
            }

            if (module.fade.getValue())
            {
                int alpha = color >>> 24;
                double t = System.currentTimeMillis() - e.getValue();
                double factor = (1.0 - (t / module.remove.getValue()));
                if (factor <= 0.0)
                {
                    continue;
                }

                alpha = MathUtil.clamp((int) (alpha * factor), 0, 255) << 24;
                color = (color & 0x00ffffff) | alpha;
            }

            double x = p.getX() - Interpolation.getRenderPosX();
            double y = p.getY() - Interpolation.getRenderPosY();
            double z = p.getZ() - Interpolation.getRenderPosZ();
            String c = TextColor.CUSTOM + String.format("%08X", color);
            RenderUtil.drawNametag(c + p.getName(), x, y, z,
                module.scale.getValue(), 0xffffffff, module.rect.getValue());
        }
    }

}
