package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

final class ListenerBlockMulti extends ModuleListener<AutoCrystal,
        PacketEvent.Receive<SPacketMultiBlockChange>>
{
    public ListenerBlockMulti(AutoCrystal module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event)
    {
        if ((module.multiThread.getValue() || module.mainThreadThreads.getValue())
                && module.blockChangeThread.getValue())
        {
            SPacketMultiBlockChange packet = event.getPacket();
            event.addPostEvent(() ->
            {
                if (mc.world == null || mc.player == null)
                {
                    return;
                }

                for (SPacketMultiBlockChange.BlockUpdateData data :
                        packet.getChangedBlocks())
                {
                    if (data.getBlockState().getMaterial() == Material.AIR
                            && HelperUtil.validChange(data.getPos(),
                                                      mc.world.playerEntities))
                    {
                        module.threadHelper.startThread();
                        break;
                    }
                }
            });
        }
    }

}
