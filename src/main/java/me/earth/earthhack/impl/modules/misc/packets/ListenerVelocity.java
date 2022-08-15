package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.management.Management;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityVelocity;

final class ListenerVelocity extends
        ModuleListener<Packets, PacketEvent.Receive<SPacketEntityVelocity>>
{
    private static final SettingCache
        <Boolean, BooleanSetting, Management> ACTIVE =
        Caches.getSetting(Management.class, BooleanSetting.class, "MotionService", true);

    public ListenerVelocity(Packets module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                SPacketEntityVelocity.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityVelocity> event)
    {
        if (event.isCancelled() || !module.fastVelocity.getValue())
        {
            return;
        }

        SPacketEntityVelocity packet = event.getPacket();
        EntityPlayer player = mc.player;
        if (player == null || player.getEntityId() == packet.getEntityID())
        {
            return;
        }

        Entity entity = Managers.ENTITIES.getEntity(packet.getEntityID());
        if (entity != null && !(ACTIVE.getValue() && entity instanceof EntityPlayer))
        {
            event.setCancelled(module.cancelVelocity.getValue());
            entity.setVelocity(packet.getMotionX() / 8000.0,
                               packet.getMotionY() / 8000.0,
                               packet.getMotionZ() / 8000.0);
        }
    }

}
