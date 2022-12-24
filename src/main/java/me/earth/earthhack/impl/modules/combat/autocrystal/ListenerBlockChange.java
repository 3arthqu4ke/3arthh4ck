package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;

final class ListenerBlockChange extends ModuleListener<AutoCrystal,
        PacketEvent.Receive<SPacketBlockChange>>
{
    public ListenerBlockChange(AutoCrystal module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event)
    {
        if ((module.multiThread.getValue() || module.mainThreadThreads.getValue())
                && module.blockChangeThread.getValue())
        {
            SPacketBlockChange packet = event.getPacket();
            if (packet.getBlockState().getBlock() == Blocks.AIR
                    && mc.player.getDistanceSq(packet.getBlockPosition()) < 40)
            {
                event.addPostEvent(() ->
                {
                    if (mc.world != null
                        && mc.player != null
                        && HelperUtil.validChange(packet.getBlockPosition(),
                                                  mc.world.playerEntities))
                    {
                        module.threadHelper.startThread();
                    }
                });
            }
        }
    }

}
