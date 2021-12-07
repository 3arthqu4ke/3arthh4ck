package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.RotationFunction;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.network.play.client.CPacketPlayer;

public class RotationCanceller implements Globals
{
    private static final ModuleCache<PacketFly> PACKETFLY =
            Caches.getModule(PacketFly.class);

    private final StopWatch timer = new StopWatch();
    private final Setting<Integer> maxCancel;
    private final AutoCrystal module;

    private volatile CPacketPlayer last;

    public RotationCanceller(AutoCrystal module, Setting<Integer> maxCancel)
    {
        this.module = module;
        this.maxCancel = maxCancel;
    }

    /**
     * Sends the last cancelled packet if
     * the timer passed the MaxCancel time.
     */
    public void onGameLoop()
    {
        if (last != null && timer.passed(maxCancel.getValue()))
        {
            sendLast();
        }
    }

    /**
     * Cancels the Event and stores the packet if possible.
     * Also sends the last cancelled Packet if it hasn't been
     * sent yet.
     *
     * @param event the packetEvent.
     */
    public synchronized void onPacket(
            PacketEvent.Send<? extends CPacketPlayer> event)
    {
        if (event.isCancelled() || PACKETFLY.isEnabled())
        {
            return;
        }

        reset(); // Send last Packet if it hasn't been yet
        if (Managers.ROTATION.isBlocking())
        {
            return;
        }

        event.setCancelled(true);
        last = event.getPacket();
        timer.reset();
    }

    /**
     * Sets the Rotations of the last Packet and sends it,
     * if it has been cancelled.
     *
     * @param function the RotationFunction setting the packet.
     * @return <tt>true</tt> if Rotations have been set.
     */
    public synchronized boolean setRotations(RotationFunction function)
    {
        if (last == null)
        {
            return false;
        }

        double x = last.getX(Managers.POSITION.getX());
        double y = last.getX(Managers.POSITION.getY());
        double z = last.getX(Managers.POSITION.getZ());
        float yaw   = Managers.ROTATION.getServerYaw();
        float pitch = Managers.ROTATION.getServerPitch();
        boolean onGround = last.isOnGround();

        ICPacketPlayer accessor = (ICPacketPlayer) last;
        float[] r = function.apply(x, y, z, yaw, pitch);
        if (r[0] - yaw == 0.0 || r[1] - pitch == 0.0)
        {
            if (!accessor.isRotating()
                && !accessor.isMoving()
                && onGround == Managers.POSITION.isOnGround())
            {
                last = null;
                return true;
            }

            sendLast();
            return true;
        }

        if (accessor.isRotating())
        {
            accessor.setYaw(r[0]);
            accessor.setPitch(r[1]);
            sendLast();
        }
        else if (accessor.isMoving())
        {
            last = PacketUtil.positionRotation(x, y, z, r[0], r[1], onGround);
            sendLast();
        }
        else
        {
            last = PacketUtil.rotation(r[0], r[1], onGround);
            sendLast();
        }

        return true;
    }

    /**
     * Sends the last Packet if it has been cancelled.
     */
    public void reset()
    {
        if (last != null && mc.player != null)
        {
            sendLast();
        }
    }

    /**
     * Drops the current packet. It won't be send.
     */
    public synchronized void drop()
    {
        last = null;
    }

    private synchronized void sendLast()
    {
        CPacketPlayer packet = last;
        if (packet != null && mc.player != null)
        {
            NetworkUtil.sendPacketNoEvent(packet);
            module.runPost();
        }

        last = null;
    }

}
