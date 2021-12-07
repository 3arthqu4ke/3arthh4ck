package me.earth.earthhack.impl.modules.player.timer;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.player.timer.mode.TimerMode;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerPlayerPackets extends CPacketPlayerListener
        implements Globals
{
    private final Timer timer;

    public ListenerPlayerPackets(Timer timer)
    {
        this.timer = timer;
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event)
    {
        onEvent(event);
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event)
    {
        if (!Managers.POSITION.isBlocking())
        {
            onEvent(event);
        }
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event)
    {
        if (!Managers.ROTATION.isBlocking())
        {
            onEvent(event);
        }
    }

    @Override
    protected void onPositionRotation
            (PacketEvent.Send<CPacketPlayer.PositionRotation> event)
    {
        if (!Managers.ROTATION.isBlocking() && !Managers.POSITION.isBlocking())
        {
            onEvent(event);
        }
    }

    private void onEvent(PacketEvent<?> event)
    {
        if (timer.mode.getValue() == TimerMode.Blink
                && Managers.NCP.passed(timer.lagTime.getValue()))
        {
            if (timer.packets != 0
                    && timer.letThrough.getValue() != 0
                    && timer.packets % timer.letThrough.getValue() == 0)
            {
                timer.packets++;
                return;
            }

            if (MovementUtil.noMovementKeys()
                    && mc.player.motionX < 0.001
                    && mc.player.motionY < 0.001
                    && mc.player.motionZ < 0.001)
            {
                event.setCancelled(true);
                timer.pSpeed = 1.0f;
                timer.packets++;
                return;
            }
            else if (timer.packets > timer.offset.getValue()
                        && timer.sent < timer.maxPackets.getValue())
            {
                timer.pSpeed = timer.speed.getValue();
                timer.packets--;
                timer.sent++;
                return;
            }
        }

        timer.pSpeed  = 1.0f;
        timer.sent    = 0;
        timer.packets = 0;
    }

}
