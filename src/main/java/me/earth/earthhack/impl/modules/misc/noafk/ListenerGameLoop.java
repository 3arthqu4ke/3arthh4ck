package me.earth.earthhack.impl.modules.misc.noafk;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.util.EnumHand;

final class ListenerGameLoop extends ModuleListener<NoAFK, GameLoopEvent>
{
    public ListenerGameLoop(NoAFK module)
    {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event)
    {
        if (mc.player != null && Managers.NCP.passed(module.lagTime.getValue()))
        {
            if (module.rotate.getValue())
            {
                mc.player.rotationYaw += 0.003;
            }

            if (module.swing.getValue() && module.swing_timer.passed(2000))
            {
                mc.player.swingArm(EnumHand.MAIN_HAND);
                module.swing_timer.reset();
            }
        }
    }

}
