package me.earth.earthhack.impl.modules.player.scaffold;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerMove extends ModuleListener<Scaffold, MoveEvent>
{
    public ListenerMove(Scaffold module)
    {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event)
    {
        if (mc.player.onGround)
        {
            event.setSneaking(!(module.down.getValue()
                    && mc.gameSettings.keyBindSneak.isKeyDown()
                    && !mc.gameSettings.keyBindJump.isKeyDown()));
        }
    }

}
