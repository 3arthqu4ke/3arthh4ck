package me.earth.earthhack.impl.modules.combat.aimbot;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.item.ItemBow;

final class ListenerMotion extends ModuleListener<AimBot, MotionUpdateEvent>
{
    public ListenerMotion(AimBot module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() != Stage.PRE)
        {
            return;
        }

        if (!(mc.player.getActiveItemStack().getItem() instanceof ItemBow))
        {
            module.target = null;
            return;
        }

        module.target = module.getTarget();
        if (module.target == null)
        {
            return;
        }

    }

}
