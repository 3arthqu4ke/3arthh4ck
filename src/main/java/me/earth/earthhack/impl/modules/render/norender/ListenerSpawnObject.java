package me.earth.earthhack.impl.modules.render.norender;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.play.server.SPacketSpawnObject;

final class ListenerSpawnObject extends
        ModuleListener<NoRender, PacketEvent.Receive<SPacketSpawnObject>>
{
    public ListenerSpawnObject(NoRender module)
    {
        super(module, PacketEvent.Receive.class, -10, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event)
    {
        if (event.isCancelled()
            || !module.items.getValue()
            || event.getPacket().getType() != 2)
        {
            return;
        }

        SPacketSpawnObject p = event.getPacket();
        Entity e = new EntityItem(mc.world, p.getX(), p.getY(), p.getZ());
        EntityTracker.updateServerPosition(e, p.getX(), p.getY(), p.getZ());
        e.rotationPitch = (p.getPitch() * 360) / 256.0f;
        e.rotationYaw   = (p.getYaw() * 360)   / 256.0f;
        Entity[] parts = e.getParts();
        if (parts != null)
        {
            int id = p.getEntityID() - e.getEntityId();
            for (Entity part : parts)
            {
                part.setEntityId(part.getEntityId() + id);
            }
        }

        e.setEntityId(p.getEntityID());
        e.setUniqueId(p.getUniqueId());

        if (p.getData() > 0)
        {
            e.setVelocity(p.getSpeedX() / 8000.0,
                          p.getSpeedY() / 8000.0,
                          p.getSpeedZ() / 8000.0);
        }

        event.setCancelled(true);
        mc.addScheduledTask(() ->
        {
            Managers.SET_DEAD.setDeadCustom(e, Long.MAX_VALUE);
            module.ids.add(p.getEntityID());
        });
    }

}
