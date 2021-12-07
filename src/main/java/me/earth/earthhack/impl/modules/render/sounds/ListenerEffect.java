package me.earth.earthhack.impl.modules.render.sounds;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.sounds.util.CoordLogger;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.util.math.MathHelper;

final class ListenerEffect extends
        ModuleListener<Sounds, PacketEvent.Receive<SPacketEffect>>
{
    public ListenerEffect(Sounds module)
    {
        super(module, PacketEvent.Receive.class, SPacketEffect.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEffect> event)
    {
        SPacketEffect packet = event.getPacket();
        switch (packet.getSoundType())
        {
            case 1038:
                if (module.portal.getValue())
                {
                    module.log("EndPortal: "
                            + packet.getSoundPos().getX() + "-X, "
                            + packet.getSoundPos().getY() + "-Y, "
                            + packet.getSoundPos().getZ() + "-Z.");
                }

                break;
            case 1023:
                if (module.wither.getValue())
                {
                    if (module.coordLogger.getValue() == CoordLogger.Vanilla)
                    {
                        module.log("Wither: "
                            + packet.getSoundPos().getX() + "-X, "
                            + packet.getSoundPos().getY() + "-Y, "
                            + packet.getSoundPos().getZ() + "-Z.");
                    }
                    else
                    {
                        double x = packet.getSoundPos().getX() - mc.player.posX;
                        double z = packet.getSoundPos().getZ() - mc.player.posZ;
                        double yaw = MathHelper.wrapDegrees(
                                Math.toDegrees(Math.atan2(x, z) - 90.0));

                        module.log("Wither: "
                            + mc.player.posX + "-X, "
                            + mc.player.posZ + "-Z, "
                            + yaw + "-Angle.");
                    }
                }

                break;
            case 1028:
                if (module.dragon.getValue())
                {
                    double x = packet.getSoundPos().getX() - mc.player.posX;
                    double z = packet.getSoundPos().getZ() - mc.player.posZ;
                    double yaw = MathHelper.wrapDegrees(
                            Math.toDegrees(Math.atan2(x, z) - 90.0));

                    module.log("Dragon: "
                        + mc.player.posX + "-X, "
                        + mc.player.posZ + "-Z, "
                        + yaw + "-Angle.");
                }

                break;
            default:
        }
    }

}
