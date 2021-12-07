package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.misc.EatEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;
import net.minecraft.item.ItemStack;

final class ListenerEat extends ModuleListener<Announcer, EatEvent>
{
    public ListenerEat(Announcer module)
    {
        super(module, EatEvent.class);
    }

    @Override
    public void invoke(EatEvent event)
    {
        if (module.eat.getValue() && event.getEntity().equals(mc.player))
        {
            ItemStack stack = event.getStack();
            module.addWordAndIncrement(AnnouncementType.Eat,
                                       stack.getDisplayName());
        }
    }

}
