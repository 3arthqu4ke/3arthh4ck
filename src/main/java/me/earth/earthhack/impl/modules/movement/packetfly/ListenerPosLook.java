package me.earth.earthhack.impl.modules.movement.packetfly;

import me.earth.earthhack.impl.core.mixins.network.server.ISPacketPlayerPosLook;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Mode;
import me.earth.earthhack.impl.modules.movement.packetfly.util.TimeVec;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;

final class ListenerPosLook extends
        ModuleListener<PacketFly, PacketEvent.Receive<SPacketPlayerPosLook>>
{
    public ListenerPosLook(PacketFly module)
    {
        super(module, PacketEvent.Receive.class, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event)
    {
        if (module.mode.getValue() == Mode.Compatibility)
        {
            return;
        }

        ISPacketPlayerPosLook packet =
                (ISPacketPlayerPosLook) event.getPacket();

        if (mc.player.isEntityAlive()
                && module.mode.getValue() != Mode.Setback
                && module.mode.getValue() != Mode.Slow
                && !(mc.currentScreen instanceof GuiDownloadTerrain)
                && mc.world.isBlockLoaded(new BlockPos(mc.player), false))
        {
            TimeVec vec = module.posLooks.remove(packet.getTeleportId());
            if (vec != null
                    && vec.x == packet.getX()
                    && vec.y == packet.getY()
                    && vec.z == packet.getZ())
            {
                event.setCancelled(true);
                return;
            }
        }

        module.teleportID.set(packet.getTeleportId());

        if (module.answer.getValue())
        {
            event.setCancelled(true);
            mc.addScheduledTask(() ->
                    PacketUtil.handlePosLook(event.getPacket(),
                                             mc.player,
                                             true,
                                             false));
            return;
        }

        packet.setYaw(mc.player.rotationYaw);
        packet.setPitch(mc.player.rotationPitch);
    }

}

