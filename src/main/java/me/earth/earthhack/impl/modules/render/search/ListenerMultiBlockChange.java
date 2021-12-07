package me.earth.earthhack.impl.modules.render.search;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

final class ListenerMultiBlockChange extends
        ModuleListener<Search, PacketEvent.Receive<SPacketMultiBlockChange>>
{
    public ListenerMultiBlockChange(Search module)
    {
        super(module, PacketEvent.Receive.class, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event)
    {
        if (module.remove.getValue())
        {
            for (SPacketMultiBlockChange.BlockUpdateData data :
                    event.getPacket().getChangedBlocks())
            {
                IBlockState state = data.getBlockState();
                if (state.getMaterial() == Material.AIR
                        || !module.isValid(state.getBlock().getLocalizedName()))
                {
                    module.toRender.remove(data.getPos());
                }
            }
        }
    }

}
