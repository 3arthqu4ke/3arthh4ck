package me.earth.earthhack.impl.modules.render.lagometer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

final class ListenerPosLook extends
        ModuleListener<LagOMeter, PacketEvent.Receive<SPacketPlayerPosLook>>
{
    public ListenerPosLook(LagOMeter module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MAX_VALUE,
                SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event)
    {
        module.teleported.set(false);
        Entity player = RotationUtil.getRotationPlayer();
        if (player == null)
        {
            return;
        }

        SPacketPlayerPosLook packet = event.getPacket();
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        float yaw = packet.getYaw();
        float pitch = packet.getPitch();

        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X))
        {
            x += player.posX;
        }

        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y))
        {
            y += player.posY;
        }

        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z))
        {
            z += player.posZ;
        }

        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X_ROT))
        {
            pitch += player.rotationPitch;
        }

        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y_ROT))
        {
            yaw += player.rotationYaw;
        }

        module.x = x;
        module.y = y;
        module.z = z;
        module.yaw = yaw;
        module.pitch = pitch;
    }

}
