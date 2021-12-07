package me.earth.earthhack.impl.modules.render.search;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.server.SPacketBlockChange;

final class ListenerBlockChange extends
        ModuleListener<Search, PacketEvent.Receive<SPacketBlockChange>>
{
    public ListenerBlockChange(Search module)
    {
        super(module, PacketEvent.Receive.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event)
    {
        if (module.remove.getValue())
        {
            IBlockState state = event.getPacket().getBlockState();
            if (state.getMaterial() == Material.AIR
                    || !module.isValid(state.getBlock().getLocalizedName()))
            {
                module.toRender.remove(event.getPacket().getBlockPosition());
            }
        }
    }

}
