package me.earth.earthhack.pingbypass.util;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntityPlayerSP;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.modules.client.nospoof.NoSpoof;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;

public class MotionUpdateHelper implements Globals {
    private static final SettingCache
        <Boolean, BooleanSetting, Management> SET_POS =
        Caches.getSetting(Management.class, BooleanSetting.class, "PB-SetPos", true);

    public static void makeMotionUpdate(boolean spoofRotations) {
        makeMotionUpdate(
            Managers.POSITION.getX(),
            Managers.POSITION.getY(),
            Managers.POSITION.getZ(),
            Managers.ROTATION.getServerYaw(),
            Managers.ROTATION.getServerPitch(),
            Managers.POSITION.isOnGround(),
            spoofRotations
        );
    }

    public static void makeMotionUpdate(double x, double y, double z, float yaw,
                                        float pitch, boolean onGround,
                                        boolean spoofRotations) {
        Bus.EVENT_BUS.post(new UpdateEvent(true));
        if (mc.player.isRiding()) {
            MotionUpdateEvent.Riding riding = new MotionUpdateEvent.Riding(
                Stage.PRE,
                x,
                y,
                z,
                yaw,
                pitch,
                onGround,
                PingBypass.PACKET_INPUT.getStrafeSpeed(),
                PingBypass.PACKET_INPUT.getForwardSpeed(),
                PingBypass.PACKET_INPUT.isJumping(),
                PingBypass.PACKET_INPUT.isSneaking());
            riding.setPingBypass(true);
            // made this configurable because I havent really tested it, but
            // it should always be on, since our position should always
            // get set between Update and UpdateWalkingPlayer
            if (SET_POS.getValue()) {
                setPosition(x, y, z, yaw, pitch, onGround, spoofRotations, riding);
            }

            Bus.EVENT_BUS.post(riding);
            if (riding.isCancelled()) {
                return;
            }

            // TODO: do we need to set the position of the ridden entity?
            //  we already do with the C2SRiddenEntityPosition but???
            setPosition(x, y, z, yaw, pitch, onGround, spoofRotations, riding);
            PingBypass.sendToActualServer(new CPacketPlayer.Rotation(riding.getRotationYaw(), riding.getRotationPitch(), riding.isOnGround()));
            PingBypass.sendToActualServer(new CPacketInput(riding.getMoveStrafing(), riding.getMoveForward(), riding.getJump(), riding.getSneak()));
            // we don't send the Vehicle move but just let the vehicle move from the client through
            MotionUpdateEvent.Riding post = new MotionUpdateEvent.Riding(Stage.POST, riding);
            post.setPingBypass(true);
            Bus.EVENT_BUS.post(post);
            return;
        }

        MotionUpdateEvent event = new MotionUpdateEvent(
            Stage.PRE, x, y, z, yaw, pitch, onGround);
        event.setPingBypass(true);
        // made this configurable because I havent really tested it, but
        // it should always be on, since our position should always
        // get set between Update and UpdateWalkingPlayer
        if (SET_POS.getValue()) {
            setPosition(x, y, z, yaw, pitch, onGround, spoofRotations, event);
        }

        Bus.EVENT_BUS.post(event);
        if (!event.isCancelled()) {
            setPosition(x, y, z, yaw, pitch, onGround, spoofRotations, event);
            PingBypass.PACKET_MANAGER.allowAllOnThisThread(true);
            try {
                ((IEntityPlayerSP) mc.player).invokeUpdateWalkingPlayer();
            } finally {
                PingBypass.PACKET_MANAGER.allowAllOnThisThread(false);
            }

            MotionUpdateEvent post = new MotionUpdateEvent(Stage.POST, event);
            // TODO: check if this gets posted when cancelled?
            post.setCancelled(event.isCancelled());
            post.setPingBypass(true);
            Bus.EVENT_BUS.postReversed(post, null);
        }
    }

    private static void setPosition(double x, double y, double z, float yaw,
                                    float pitch, boolean onGround,
                                    boolean spoofRotations,
                                    MotionUpdateEvent event) {
        mc.player.setPositionAndRotation(
            NoSpoof.noPosition() ? x : event.getX(),
            NoSpoof.noPosition() ? y : event.getY(),
            NoSpoof.noPosition() ? z : event.getZ(),
            NoSpoof.noRotation() || !spoofRotations ?  yaw : event.getYaw(),
            NoSpoof.noRotation() || !spoofRotations ? pitch : event.getPitch());
        mc.player.onGround = NoSpoof.noGround() ? onGround : event.isOnGround();
    }

}
