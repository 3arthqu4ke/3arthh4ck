package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerTick extends ModuleListener<AutoCrystal, TickEvent>
{
    public ListenerTick(AutoCrystal module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (event.isSafe())
        {
            module.checkExecutor();
            module.placed.values().removeIf(stamp ->
                System.currentTimeMillis() - stamp.getTimeStamp()
                        > module.removeTime.getValue());

            module.crystalRender.tick();
            if (!module.idHelper.isUpdated())
            {
                module.idHelper.update();
                module.idHelper.setUpdated(true);
            }

            module.weaknessHelper.updateWeakness();
        }
    }

}
