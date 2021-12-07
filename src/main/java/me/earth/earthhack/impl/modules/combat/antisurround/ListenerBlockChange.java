package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.network.play.server.SPacketBlockChange;

final class ListenerBlockChange extends ModuleListener<AntiSurround,
        PacketEvent.Post<SPacketBlockChange>>
{
    public ListenerBlockChange(AntiSurround module)
    {
        super(module, PacketEvent.Post.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Post<SPacketBlockChange> event)
    {
        if (!module.async.getValue()
            || module.active.get()
            || mc.player == null
            || module.holdingCheck())
        {
            return;
        }

        if (event.getPacket().getBlockState().getMaterial().isReplaceable())
        {
            module.onBlockBreak(event.getPacket().getBlockPosition(),
                                Managers.ENTITIES.getPlayers(),
                                Managers.ENTITIES.getEntities());
        }
    }

}
