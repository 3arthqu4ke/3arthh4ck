package me.earth.earthhack.impl.modules.player.autotool;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;

final class ListenerUpdate extends ModuleListener<AutoTool, UpdateEvent>
{
    public ListenerUpdate(AutoTool module)
    {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event)
    {
        if (module.set && !mc.gameSettings.keyBindAttack.isKeyDown())
        {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK,
                          () -> InventoryUtil.switchTo(module.lastSlot));
            module.reset();
        }
    }

}
