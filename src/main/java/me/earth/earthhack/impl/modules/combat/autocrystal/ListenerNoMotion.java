package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.NoMotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotationThread;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerNoMotion
        extends ModuleListener<AutoCrystal, NoMotionUpdateEvent>
{
    private float forward = 0.004f;

    public ListenerNoMotion(AutoCrystal module)
    {
        super(module, NoMotionUpdateEvent.class);
    }

    @Override
    public void invoke(NoMotionUpdateEvent event)
    {
        if (module.multiThread.getValue()
                && !module.isSpoofing
                && module.rotate.getValue() != ACRotate.None
                && module.rotationThread.getValue() == RotationThread.Cancel)
        {
            forward = -forward;
            float yaw   = Managers.ROTATION.getServerYaw() + forward;
            float pitch = Managers.ROTATION.getServerPitch() + forward;

            module.rotationCanceller.onPacket(
                new PacketEvent.Send<>(
                    new CPacketPlayer.Rotation(
                        yaw, pitch, Managers.POSITION.isOnGround())));
        }
        else
        {
            module.rotationCanceller.reset();
        }
    }

}
