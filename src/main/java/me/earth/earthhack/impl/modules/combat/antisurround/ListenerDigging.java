package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.impl.core.ducks.network.ICPacketPlayerDigging;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.antisurround.util.AntiSurroundFunction;
import net.minecraft.network.play.client.CPacketPlayerDigging;

final class ListenerDigging extends
        ModuleListener<AntiSurround, PacketEvent.Send<CPacketPlayerDigging>>
{
    private final AntiSurroundFunction function;

    public ListenerDigging(AntiSurround module)
    {
        super(module,
                PacketEvent.Send.class,
                -1000,
                CPacketPlayerDigging.class);
        this.function = new PreCrystalFunction(module);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerDigging> event)
    {
        if (event.isCancelled()
            || event.getPacket().getAction()
                    != CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK
            || module.holdingCheck()
            || !module.preCrystal.getValue()
            || !((ICPacketPlayerDigging) event.getPacket())
                                              .isClientSideBreaking())
        {
            return;
        }

        module.onBlockBreak(
                event.getPacket().getPosition(),
                Managers.ENTITIES.getPlayersAsync(),
                Managers.ENTITIES.getEntitiesAsync(),
                function);
    }

}
