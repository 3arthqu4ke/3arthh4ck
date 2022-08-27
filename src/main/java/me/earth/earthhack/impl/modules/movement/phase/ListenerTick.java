package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerTick
        extends ModuleListener<Phase, TickEvent>
{
    public ListenerTick(Phase module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        // NOOP
        /*if (module.mode.getValue() == PhaseMode.ConstantiamNew
                && mc.player.collidedHorizontally) {
            PacketUtil.doPosition(mc.player.posX, mc.player.posY, mc.player.posZ, true);
            PacketUtil.doPosition(mc.player.posX, mc.player.posY - 0.05, mc.player.posZ, true);
            PacketUtil.doPosition(mc.player.posX, mc.player.posY, mc.player.posZ, true);
        }*/
    }
}
