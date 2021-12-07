package me.earth.earthhack.impl.modules.render.norender;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

final class ListenerTick extends ModuleListener<NoRender, TickEvent>
{
    private boolean previous;

    public ListenerTick(NoRender module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        boolean shouldUpdate = module.items.getValue();
        if (event.isSafe())
        {
            if (previous != shouldUpdate)
            {
                if (shouldUpdate)
                {
                    for (Entity entity : mc.world.loadedEntityList)
                    {
                        if (entity instanceof EntityItem && !entity.isDead)
                        {
                            Managers.SET_DEAD
                                    .setDeadCustom(entity, Long.MAX_VALUE);
                            module.ids.add(entity.getEntityId());
                        }
                    }
                }
                else
                {
                    module.ids.forEach(Managers.SET_DEAD::revive);
                    module.ids.clear();
                }
            }
        }
        else
        {
            module.ids.forEach(Managers.SET_DEAD::confirmKill);
            module.ids.clear();
        }

        previous = shouldUpdate;
    }

}
