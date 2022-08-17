package me.earth.earthhack.impl.modules.movement.tickshift;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.network.NoMotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

public class TickShift extends Module
{
    private final Setting<Float> timer = register(new NumberSetting<>("Timer", 2.0f, 0.1f, 100.0f));
    private final Setting<Integer> packets = register(new NumberSetting<>("Packets", 20, 0, 1000));
    private final Setting<Boolean> sneaking = register(new BooleanSetting("Sneaking", false));
    private final Setting<Boolean> cancelGround = register(new BooleanSetting("CancelGround", false));
    private final Setting<Boolean> cancelRotations = register(new BooleanSetting("CancelRotation", false));
    private final Setting<Boolean> fullResetOnPacket = register(new BooleanSetting("FullResetOnPacket", false));
    private final Setting<Integer> lagTime = register(new NumberSetting<>("LagTime", 1000, 0, 10_000));

    private int ticks;

    public TickShift()
    {
        super("TickShift", Category.Movement);
        this.listeners.add(new LambdaListener<>(TickEvent.class, e ->
        {
            if (mc.player == null || mc.world == null || !Managers.NCP.passed(lagTime.getValue()))
            {
                reset();
            }
            else if (ticks <= 0 || MovementUtil.noMovementKeys() || !sneaking.getValue() && mc.player.isSneaking())
            {
                Managers.TIMER.setTimer(1.0f);
            }
        }));
        this.listeners.add(new LambdaListener<>(NoMotionUpdateEvent.class, e ->
        {
            Managers.TIMER.setTimer(1.0f);
            int maxPackets = packets.getValue();
            ticks = ticks >= maxPackets ? maxPackets : ticks + 1;
        }));
        this.listeners.addAll(new CPacketPlayerListener(-10_000)
        {
            @Override
            protected void onPacket(PacketEvent.Send<CPacketPlayer> event)
            {
                if (cancelGround.getValue())
                {
                    event.setCancelled(true);
                }
                else
                {
                    onPacketEvent(event, false);
                }
            }

            @Override
            protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event)
            {
                onPacketEvent(event, true);
            }

            @Override
            protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event)
            {
                if (cancelRotations.getValue()
                        && (cancelGround.getValue()
                        || event.getPacket().isOnGround() == Managers.POSITION.isOnGround()))
                {
                    event.setCancelled(true);
                }
                else
                {
                    onPacketEvent(event, false);
                }
            }

            @Override
            protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event)
            {
                onPacketEvent(event, true);
            }
        }.getListeners());
        this.listeners.add(new LambdaListener<>(WorldClientEvent.Load.class, e -> reset()));
        this.listeners.add(new ReceiveListener<>(SPacketPlayerPosLook.class, e -> reset()));
    }

    @Override
    public String getDisplayInfo()
    {
        return ticks + "";
    }

    @Override
    protected void onEnable()
    {
        reset();
    }

    @Override
    protected void onDisable()
    {
        reset();
    }

    private void onPacketEvent(PacketEvent.Send<? extends CPacketPlayer> event, boolean moving)
    {
        if (event.isCancelled())
        {
            return;
        }

        if (moving && !MovementUtil.noMovementKeys() && (sneaking.getValue() || !mc.player.isSneaking()))
        {
            Managers.TIMER.setTimer(timer.getValue());
        }

        ticks = ticks <= 0 ? 0 : ticks - 1;
        if (fullResetOnPacket.getValue() && Managers.TIMER.getSpeed() == 1.0f) {
            ticks = 0;
        }
    }

    private void reset()
    {
        Managers.TIMER.setTimer(1.0f);
        ticks = 0;
    }

}
