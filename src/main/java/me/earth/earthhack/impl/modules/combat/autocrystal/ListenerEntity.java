package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.core.mixins.network.server.ISPacketEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.event.listeners.SPacketEntityListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotationThread;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityTeleport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class ListenerEntity extends SPacketEntityListener
{
    private final AutoCrystal module;

    public ListenerEntity(AutoCrystal module)
    {
        this.module = module;
        this.listeners.add(
            new ReceiveListener<>(SPacketEntityTeleport.class, e ->
        {
            if (!shouldCalc())
            {
                return;
            }

            EntityPlayer p = getEntity(e.getPacket().getEntityId());
            if (p != null)
            {
                double x = e.getPacket().getX();
                double y = e.getPacket().getY();
                double z = e.getPacket().getZ();
                onEvent(p, x, y, z);
            }
        }));
    }

    protected void onPacket(
            PacketEvent.Receive<SPacketEntity> event) { }

    @Override
    protected void onRotation(
            PacketEvent.Receive<SPacketEntity.S16PacketEntityLook> event) { }

    @Override
    protected void onPosition(
            PacketEvent.Receive<SPacketEntity.S15PacketEntityRelMove> event)
    {
        onEvent(event.getPacket());
    }

    @Override
    protected void onPositionRotation(
            PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove> event)
    {
        onEvent(event.getPacket());
    }

    private void onEvent(SPacketEntity packet)
    {
        if (!shouldCalc())
        {
            return;
        }

        EntityPlayer p = getEntity(((ISPacketEntity) packet).getEntityId());
        if (p == null)
        {
            return;
        }

        double x = (p.serverPosX + packet.getX()) / 4096.0;
        double y = (p.serverPosY + packet.getY()) / 4096.0;
        double z = (p.serverPosZ + packet.getZ()) / 4096.0;

        onEvent(p, x, y, z);
    }

    private void onEvent(EntityPlayer player, double x, double y, double z)
    {
        Entity entity = RotationUtil.getRotationPlayer();
        if (entity != null
            && entity.getDistanceSq(x, y, z)
                < MathUtil.square(module.targetRange.getValue())
            && !Managers.FRIENDS.contains(player))
        {
            boolean enemied = Managers.ENEMIES.contains(player);
            // Scheduling is required since this event might get cancelled.
            Scheduler.getInstance().scheduleAsynchronously(() ->
            {
                if (mc.world == null)
                {
                    return;
                }

                List<EntityPlayer> enemies;
                if (enemied)
                {
                    enemies = new ArrayList<>(1);
                    enemies.add(player);
                }
                else
                {
                    enemies = Collections.emptyList();
                }

                EntityPlayer target = module.targetMode.getValue().getTarget(
                                                 mc.world.playerEntities,
                                                 enemies,
                                                 module.targetRange.getValue());

                if (target == null || target.equals(player))
                {
                    module.threadHelper.startThread();
                }
            });
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean shouldCalc()
    {
        return module.multiThread.getValue()
                && module.entityThread.getValue()
                && (module.rotate.getValue() == ACRotate.None
                 || module.rotationThread.getValue() != RotationThread.Predict);
    }

    private EntityPlayer getEntity(int id)
    {
        List<Entity> entities = Managers.ENTITIES.getEntities();
        if (entities == null)
        {
            return null;
        }

        Entity entity = null;
        for (Entity e : entities)
        {
            if (e != null && e.getEntityId() == id)
            {
                entity = e;
                break;
            }
        }

        if (entity instanceof EntityPlayer)
        {
            return (EntityPlayer) entity;
        }

        return null;
    }

}
