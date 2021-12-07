package me.earth.earthhack.impl.modules.misc.autofish;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.math.Vec3d;

final class ListenerSound extends
        ModuleListener<AutoFish, PacketEvent.Receive<SPacketSoundEffect>>
{
    public ListenerSound(AutoFish module)
    {
        super(module, PacketEvent.Receive.class, SPacketSoundEffect.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSoundEffect> event)
    {
        SPacketSoundEffect packet = event.getPacket();
        if (packet.getSound().equals(SoundEvents.ENTITY_BOBBER_SPLASH))
        {
            EntityFishHook fish = mc.player.fishEntity;
            if (fish != null
                    && mc.player.equals(fish.getAngler())
                    && (module.range.getValue() == 0.0
                        || fish.getPositionVector()
                               .distanceTo(new Vec3d(packet.getX(),
                                packet.getY(),
                                packet.getZ()))
                            <= module.range.getValue()))
            {
                module.splash = true;
            }
        }
    }

}
