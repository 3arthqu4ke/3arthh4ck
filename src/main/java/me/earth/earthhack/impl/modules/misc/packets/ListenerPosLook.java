package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.surround.Surround;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;

final class ListenerPosLook extends
        ModuleListener<Packets, PacketEvent.Receive<SPacketPlayerPosLook>>
{
    private static final ModuleCache<PacketFly> PACKET_FLY =
            Caches.getModule(PacketFly.class);
    private static final ModuleCache<Freecam> FREE_CAM =
            Caches.getModule(Freecam.class);
    private static final ModuleCache<Surround> SURROUND =
            Caches.getModule(Surround.class);

    public ListenerPosLook(Packets module)
    {
        super(module,
                PacketEvent.Receive.class,
                -1000,
                SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event)
    {
        if (event.isCancelled()
                || mc.player == null
                || PACKET_FLY.isEnabled()
                || FREE_CAM.isEnabled()
                || !module.fastTeleports.getValue())
        {
            return;
        }

        event.setCancelled(true);
        SPacketPlayerPosLook packet = event.getPacket();

        boolean xFlag   = packet.getFlags()
                                .contains(SPacketPlayerPosLook.EnumFlags.X);
        boolean yFlag   = packet.getFlags()
                                .contains(SPacketPlayerPosLook.EnumFlags.Y);
        boolean zFlag   = packet.getFlags()
                                .contains(SPacketPlayerPosLook.EnumFlags.Z);
        boolean yawFlag = packet.getFlags()
                                .contains(SPacketPlayerPosLook.EnumFlags.Y_ROT);
        boolean pitFlag = packet.getFlags()
                                .contains(SPacketPlayerPosLook.EnumFlags.X_ROT);

        double x  = packet.getX()     + (xFlag   ? mc.player.posX          : 0);
        double y  = packet.getY()     + (yFlag   ? mc.player.posY          : 0);
        double z  = packet.getZ()     + (zFlag   ? mc.player.posZ          : 0);
        float yaw = packet.getYaw()   + (yawFlag ? mc.player.rotationYaw   : 0);
        float pit = packet.getPitch() + (pitFlag ? mc.player.rotationPitch : 0);

        try
        {
            SURROUND.computeIfPresent(s -> s.blockTeleporting = true);
            mc.player.connection.sendPacket(
                new CPacketConfirmTeleport(packet.getTeleportId()));
        }
        finally
        {
            SURROUND.computeIfPresent(s -> s.blockTeleporting = false);
        }

        Managers.ROTATION.setBlocking(true);
        mc.player.connection.sendPacket(
                new CPacketPlayer.PositionRotation(
                        MathHelper.clamp(x, -3.0E7D, 3.0E7D),
                        y,
                        MathHelper.clamp(z, -3.0E7D, 3.0E7D),
                        yaw,
                        pit,
                        false));
        Managers.ROTATION.setBlocking(false);

        if (module.asyncTeleports.getValue())
        {
            execute(x, y, z, yaw, pit, xFlag, yFlag, zFlag);
        }

        mc.addScheduledTask(() ->
                execute(x, y, z, yaw, pit, xFlag, yFlag, zFlag));

        PacketUtil.loadTerrain();
    }

    private void execute(double x,
                         double y,
                         double z,
                         float yaw,
                         float pitch,
                         boolean xFlag,
                         boolean yFlag,
                         boolean zFlag)
    {
        if (!xFlag)
        {
            mc.player.motionX = 0.0;
        }

        if (!yFlag)
        {
            mc.player.motionY = 0.0;
        }

        if (!zFlag)
        {
            mc.player.motionZ = 0.0;
        }

        mc.player.setPositionAndRotation(x, y, z, yaw, pitch);
        if (SURROUND.isEnabled() && SURROUND.get().teleport.getValue())
        {
            SURROUND.get().startPos = SURROUND.get().getPlayerPos();
        }
    }

}
