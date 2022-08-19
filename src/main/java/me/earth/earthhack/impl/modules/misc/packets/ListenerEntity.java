package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.core.mixins.network.server.ISPacketEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.SPacketEntityListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketEntity;

final class ListenerEntity extends SPacketEntityListener
{
    private final Packets packets;

    public ListenerEntity(Packets packets)
    {
        super(Integer.MIN_VALUE + 1);
        this.packets = packets;
    }

    @Override
    protected void onPacket
            (PacketEvent.Receive<SPacketEntity> event)
    {
        onEvent(event);
    }

    @Override
    protected void onPosition
            (PacketEvent.Receive<SPacketEntity.S15PacketEntityRelMove> event)
    {
        onEvent(event);
    }

    @Override
    protected void onRotation
            (PacketEvent.Receive<SPacketEntity.S16PacketEntityLook> event)
    {
        onEvent(event);
    }

    @Override
    protected void onPositionRotation
            (PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove> event)
    {
        onEvent(event);
    }

    private void onEvent(PacketEvent.Receive<? extends SPacketEntity> event)
    {
        if (event.isCancelled() || !packets.fastEntities.getValue())
        {
            return;
        }

        SPacketEntity packet = event.getPacket();
        Entity e = Managers.ENTITIES
                           .getEntity(((ISPacketEntity) packet).getEntityId());
        if (e == null)
        {
            return;
        }

        event.setCancelled(true);
        long oldServerPosX = e.serverPosX;
        long oldServerPosY = e.serverPosY;
        long oldServerPosZ = e.serverPosZ;
        mc.addScheduledTask(() -> ((IEntity) e).setOldServerPos(
            oldServerPosX, oldServerPosY, oldServerPosZ));

        e.serverPosX += packet.getX();
        e.serverPosY += packet.getY();
        e.serverPosZ += packet.getZ();
        double x = e.serverPosX / 4096.0;
        double y = e.serverPosY / 4096.0;
        double z = e.serverPosZ / 4096.0;

        if (!e.canPassengerSteer())
        {
            float yaw   = packet.isRotating()
                            ? (float)(packet.getYaw() * 360) / 256.0f
                            : e.rotationYaw;
            float pitch = packet.isRotating()
                            ? (float)(packet.getPitch() * 360) / 256.0f
                            : e.rotationPitch;

            e.setPositionAndRotationDirect(x, y, z, yaw, pitch, 3, false);
            e.onGround = packet.getOnGround();
        }
    }

}
