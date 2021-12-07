package me.earth.earthhack.impl.modules.render.tracers;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class ListenerTick extends ModuleListener<Tracers, TickEvent>
{
    public ListenerTick(Tracers module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (event.isSafe())
        {
            List<Entity> sorted = new ArrayList<>(mc.world.loadedEntityList);

            try
            {
                sorted.sort(Comparator.comparingDouble(entity -> mc.player.getDistanceSq(entity)));
            }
            catch (IllegalStateException ignored)
            {
                // there's not really a way to fix this. except maybe:
                // TODO: cache distanceSq on mainthread?
                // I'm p sure the contract of the sort method is violated
                // by the Packets module which sets the entities on a different
                // thread. That way the distance of an entity can change while
                // the list is being sorted.
                // this crash seems to be relatively rare tho
            }

            module.sorted = sorted;
        }
    }

}
