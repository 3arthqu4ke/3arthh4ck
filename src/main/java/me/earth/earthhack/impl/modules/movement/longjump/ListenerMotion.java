package me.earth.earthhack.impl.modules.movement.longjump;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class ListenerMotion extends ModuleListener<LongJump, MotionUpdateEvent>
{
    public ListenerMotion(LongJump module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE)
        {
            module.distance = MovementUtil.getDistance2D();
        }
    }

}
