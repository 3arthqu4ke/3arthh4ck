package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.impl.event.events.network.NoMotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.network.play.client.CPacketPlayer;

// TODO: Test!
final class ListenerNoUpdate
    extends ModuleListener<PingBypassModule, NoMotionUpdateEvent> {
    public ListenerNoUpdate(PingBypassModule module) {
        super(module, NoMotionUpdateEvent.class, Integer.MAX_VALUE);
    }

    @Override
    public void invoke(NoMotionUpdateEvent event) {
        if (module.isEnabled()
            && !module.isOld()
            && module.alwaysUpdate.getValue()
            && !PingBypass.isServer()
            && !event.isCancelled()) {
            event.setCancelled(true);
            // TODO: this could get cancelled, send with noEvent?
            NetworkUtil.send(new CPacketPlayer.PositionRotation(
                Managers.POSITION.getX(),
                Managers.POSITION.getY(),
                Managers.POSITION.getZ(),
                Managers.ROTATION.getServerYaw(),
                Managers.ROTATION.getServerPitch(),
                Managers.POSITION.isOnGround()
            ));
        }
    }

}
