package me.earth.earthhack.impl.modules.combat.killaura;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerRiding extends ModuleListener<KillAura, MotionUpdateEvent.Riding>
{
    public ListenerRiding(KillAura module)
    {
        super(module, MotionUpdateEvent.Riding.class);
    }

    @Override
    public void invoke(MotionUpdateEvent.Riding event)
    {
        if (event.getStage() == Stage.PRE)
        {
            ListenerMotion.pre(module,
                               event,
                               module.ridingTeleports.getValue());
        }
        else
        {
            ListenerMotion.post(module);
        }
    }

}
