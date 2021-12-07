package me.earth.earthhack.impl.modules.combat.aimbot;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.item.ItemBow;

final class ListenerGameLoop extends ModuleListener<AimBot, GameLoopEvent>
{
    public ListenerGameLoop(AimBot module)
    {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event)
    {
        if (module.target != null
            && !module.silent.getValue()
            && mc.player.getActiveItemStack().getItem() instanceof ItemBow)
        {
            mc.player.rotationYaw   = module.yaw;
            mc.player.rotationPitch = module.pitch;
        }
    }

}
