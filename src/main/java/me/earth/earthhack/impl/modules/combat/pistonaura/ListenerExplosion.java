package me.earth.earthhack.impl.modules.combat.pistonaura;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.math.BlockPos;

final class ListenerExplosion extends
            ModuleListener<PistonAura, PacketEvent.Receive<SPacketExplosion>>
{
    public ListenerExplosion(PistonAura module)
    {
        super(module, PacketEvent.Receive.class, SPacketExplosion.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketExplosion> event)
    {
        if (!module.explosions.getValue())
        {
            return;
        }

        mc.addScheduledTask(() ->
        {
            if (module.current != null)
            {
                SPacketExplosion packet = event.getPacket();
                BlockPos pos =
                    new BlockPos(packet.getX(), packet.getY(), packet.getZ());

                if (pos.equals(module.current.getStartPos().up())
                        || pos.equals(module.current.getCrystalPos().up()))
                {
                    module.current.setValid(false);
                    return;
                }

                for (BlockPos affected : packet.getAffectedBlockPositions())
                {
                    if (affected.equals(module.current.getPistonPos())
                            || affected.equals(module.current.getRedstonePos()))
                    {
                        module.current.setValid(false);
                        break;
                    }
                }
            }
        });
    }

}
