package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SRiddenEntityPosition;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketInput;

public class CPacketInputService extends SubscriberImpl implements Globals {
    private float strafeSpeed;
    private float forwardSpeed;
    private boolean jumping;
    private boolean sneaking;

    public CPacketInputService() {
        // these listeners are only used on the client
        this.listeners.add(new LambdaListener<>(
            MotionUpdateEvent.Riding.class, Integer.MIN_VALUE, e -> {
            if (e.getStage() == Stage.PRE
                && PingBypassModule.CACHE.isEnabled()
                && !PingBypassModule.CACHE.get().isOld()) {
                mc.player.connection.sendPacket(new CPacketInput(
                    e.getMoveStrafing(), e.getMoveForward(),
                    e.getJump(), e.getSneak()));
                Entity entity = mc.player.getLowestRidingEntity();
                if (entity != mc.player && entity.canPassengerSteer()) {
                    mc.player.connection.sendPacket(new C2SRiddenEntityPosition(
                        entity.getEntityId(), entity.posX, entity.posY, entity.posZ));
                }
            }
        }));
    }

    public void onInput(CPacketInput cPacketInput) {
        this.strafeSpeed = cPacketInput.getStrafeSpeed();
        this.forwardSpeed = cPacketInput.getForwardSpeed();
        this.jumping = cPacketInput.isJumping();
        this.sneaking = cPacketInput.isSneaking();
    }

    public float getStrafeSpeed() {
        return strafeSpeed;
    }

    public float getForwardSpeed() {
        return forwardSpeed;
    }

    public boolean isJumping() {
        return jumping;
    }

    public boolean isSneaking() {
        return sneaking;
    }

}
