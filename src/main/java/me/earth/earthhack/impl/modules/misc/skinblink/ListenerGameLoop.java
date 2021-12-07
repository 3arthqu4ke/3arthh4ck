package me.earth.earthhack.impl.modules.misc.skinblink;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.player.EnumPlayerModelParts;

final class ListenerGameLoop extends ModuleListener<SkinBlink, GameLoopEvent>
{
    public ListenerGameLoop(SkinBlink module)
    {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event)
    {
        if (module.timer.passed(module.delay.getValue()))
        {
            for (EnumPlayerModelParts parts : EnumPlayerModelParts.values())
            {
                mc.gameSettings
                        .setModelPartEnabled(parts,
                                             module.random.getValue()
                                                 ? Math.random() < 0.5
                                                 : !mc.gameSettings
                                                         .getModelParts()
                                                         .contains(parts));
            }

            module.timer.reset();
        }
    }

}
