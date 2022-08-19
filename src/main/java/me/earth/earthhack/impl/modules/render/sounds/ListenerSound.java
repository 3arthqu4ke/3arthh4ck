package me.earth.earthhack.impl.modules.render.sounds;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.sounds.util.SoundPosition;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

final class ListenerSound extends
        ModuleListener<Sounds, PacketEvent.Receive<SPacketSoundEffect>>
{
    public ListenerSound(Sounds module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                SPacketSoundEffect.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSoundEffect> event)
    {
        SPacketSoundEffect packet = event.getPacket();
        if (module.thunder.getValue()
            && packet.getCategory() == SoundCategory.WEATHER
            && packet.getSound() == SoundEvents.ENTITY_LIGHTNING_THUNDER)
        {
            double x = packet.getX() - mc.player.posX;
            double z = packet.getZ() - mc.player.posZ;
            double yaw = MathHelper.wrapDegrees(
                            Math.toDegrees(Math.atan2(x, z) - 90.0));

            module.log("Lightning: " + mc.player.posX
                        + "-x, " + mc.player.posZ
                        + "-z, " + yaw + "-angle.");
        }

        boolean cancelled = event.isCancelled();
        if (module.client.getValue()
            || !module.packet.getValue()
            || cancelled && !module.cancelled.getValue())
        {
            return;
        }

        ResourceLocation location = packet.getSound().getSoundName();
        SoundEventAccessor access = mc.getSoundHandler().getAccessor(location);
        ITextComponent c = access == null ? null : access.getSubtitle();
        if (c != null
                && module.isValid(c.getUnformattedComponentText())
            || c == null && module.isValid(location.toString()))
        {
            String s = c != null ? c.getUnformattedComponentText()
                                 : location.toString();
            module.sounds.put(
                new SoundPosition(packet.getX(),
                                  packet.getY(),
                                  packet.getZ(),
                                  (cancelled ? "Cancelled: " : "") + s),
                System.currentTimeMillis());
        }
    }

}
