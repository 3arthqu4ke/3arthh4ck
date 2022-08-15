package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.network.play.client.CPacketPlayer;

// TODO: Test!
final class ListenerCPacket extends CPacketPlayerListener implements Globals {
    private final PingBypassModule module;

    public ListenerCPacket(PingBypassModule module) {
        super(Integer.MIN_VALUE + 1);
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
        onEvent(event);
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event) {
        onEvent(event);
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event) {
        onEvent(event);
    }

    @Override
    protected void onPositionRotation(
        PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
        // NOP
    }

    private void onEvent(PacketEvent.Send<? extends CPacketPlayer> event) {
        if (module.isEnabled()
            && !module.isOld()
            && module.fixRotations.getValue()
            && !event.isCancelled()) {
            event.setCancelled(true);
            // TODO: this packet could get cancelled, send with noEvent?
            NetworkUtil.send(
                new CPacketPlayer.PositionRotation(
                event.getPacket().getX(Managers.POSITION.getX()),
                event.getPacket().getY(Managers.POSITION.getY()),
                event.getPacket().getZ(Managers.POSITION.getZ()),
                event.getPacket().getYaw(Managers.ROTATION.getServerYaw()),
                event.getPacket().getPitch(Managers.ROTATION.getServerPitch()),
                event.getPacket().isOnGround()
            ));
        }
    }

}
