package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

final class ListenerBlockMulti extends ModuleListener<AntiSurround,
        PacketEvent.Post<SPacketMultiBlockChange>>
{
    public ListenerBlockMulti(AntiSurround module)
    {
        super(module, PacketEvent.Post.class, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Post<SPacketMultiBlockChange> event)
    {
        if (!module.async.getValue()
            || module.active.get()
            || mc.player == null
            || module.holdingCheck())
        {
            return;
        }

        for (SPacketMultiBlockChange.BlockUpdateData pos
                : event.getPacket().getChangedBlocks())
        {
            if (pos.getBlockState().getMaterial().isReplaceable()
                    && module.onBlockBreak(pos.getPos(),
                                           Managers.ENTITIES.getPlayers(),
                                           Managers.ENTITIES.getEntities()))
            {
                break;
            }
        }
    }

}
