package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;

import java.util.List;

final class ListenerSound extends
        ModuleListener<Packets, PacketEvent.Receive<SPacketSoundEffect>>
{
    public ListenerSound(Packets module)
    {
        super(module, PacketEvent.Receive.class, SPacketSoundEffect.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSoundEffect> event)
    {
        if (module.fastSetDead.getValue() && mc.player != null)
        {
            SPacketSoundEffect packet = event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS
                    && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE)
            {
                List<Entity> entities = Managers.ENTITIES.getEntities();
                if (entities != null)
                {
                    Managers.SET_DEAD.removeCrystals(
                            new Vec3d(packet.getX(),
                                      packet.getY(),
                                      packet.getZ()),
                            11.0f,
                            entities);
                }
            }
        }
    }

}
