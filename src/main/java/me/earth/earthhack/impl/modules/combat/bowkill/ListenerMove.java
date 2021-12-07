package me.earth.earthhack.impl.modules.combat.bowkill;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.item.ItemBow;

final class ListenerMove extends ModuleListener<BowKiller, MoveEvent>
{
    public ListenerMove(BowKiller module)
    {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event)
    {
        if (!mc.player.collidedVertically)
            return;
        if (module.staticS.getValue()
            && mc.player.getActiveItemStack().getItem() instanceof ItemBow && module.blockUnder)
        {
            mc.player.setVelocity(0, 0, 0);
            event.setX(0);
            event.setY(0);
            event.setZ(0);
        }
    }

}
