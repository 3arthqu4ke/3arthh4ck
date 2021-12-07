package me.earth.earthhack.impl.modules.player.mcp;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;

final class ListenerMotion extends
        ModuleListener<MiddleClickPearl, MotionUpdateEvent>
{
    public ListenerMotion(MiddleClickPearl module)
    {
        super(module, MotionUpdateEvent.class, Integer.MIN_VALUE);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (module.runnable == null)
        {
            return;
        }

        if (event.getStage() == Stage.PRE)
        {
            event.setYaw(mc.player.rotationYaw);
            event.setPitch(mc.player.rotationPitch);
            Managers.ROTATION.setBlocking(true);
        }
        else
        {
            module.runnable.run();
            module.runnable = null;
            Managers.ROTATION.setBlocking(false);
        }
    }

}
