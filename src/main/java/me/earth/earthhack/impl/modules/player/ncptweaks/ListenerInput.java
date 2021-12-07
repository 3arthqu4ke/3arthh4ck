package me.earth.earthhack.impl.modules.player.ncptweaks;

import me.earth.earthhack.impl.event.events.movement.MovementInputEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.item.ItemFood;

final class ListenerInput extends ModuleListener<NCPTweaks, MovementInputEvent>
{
    public ListenerInput(NCPTweaks module)
    {
        super(module, MovementInputEvent.class);
    }

    @Override
    public void invoke(MovementInputEvent event)
    {
        if (module.sneakEat.getValue())
        {
            if (mc.gameSettings.keyBindUseItem.isKeyDown()
                && mc.player.getActiveItemStack().getItem()
                    instanceof ItemFood)
            {
                event.getInput().sneak = true;
                if (module.stopSpeed.getValue())
                {
                    module.speedStopped = true;
                }

                return;
            }
        }

        module.speedStopped = false;
    }

}
