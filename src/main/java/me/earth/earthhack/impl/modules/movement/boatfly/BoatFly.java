package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketVehicleMove;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Type;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;

public class BoatFly extends Module
{
    protected final Setting<Boolean> bypass =
            register(new BooleanSetting("Bypass", false));
    protected final Setting<Boolean> postBypass =
            register(new BooleanSetting("PostBypass", false));
    protected final Setting<Integer> ticks =
            register(new NumberSetting<>("Ticks", 2, 0, 20));
    protected final Setting<Integer> packets =
            register(new NumberSetting<>("Packets", 1, 1, 20));
    protected final Setting<Boolean> noVehicleMove =
            register(new BooleanSetting("NoVehicleMove", false));
    protected final Setting<Boolean> noSteer =
            register(new BooleanSetting("NoSteer", false));
    protected final Setting<Boolean> noPosUpdate =
            register(new BooleanSetting("NoPosUpdate", false));
    protected final Setting<Boolean> noForceRotate =
            register(new BooleanSetting("NoForceRotate", false));
    protected final Setting<Boolean> remount =
            register(new BooleanSetting("Remount", false));
    protected final Setting<Boolean> remountPackets =
            register(new BooleanSetting("RemountPackets", false));
    protected final Setting<Boolean> noForceBoatMove =
            register(new BooleanSetting("NoForceBoatMove", false));
    protected final Setting<Boolean> invalid =
            register(new BooleanSetting("Invalid", false));
    protected final Setting<Boolean> boatInvalid =
            register(new BooleanSetting("BoatInvalid", false));
    protected final Setting<Type> invalidMode =
            register(new EnumSetting<>("BoatInvalid", Type.Up));
    protected final Setting<Integer> invalidTicks =
            register(new NumberSetting<>("InvalidTicks", 1, 0, 10));
    protected final Setting<Double> upSpeed =
            register(new NumberSetting<>("Up-Speed", 2.0, 0.0, 10.0));
    protected final Setting<Double> downSpeed =
            register(new NumberSetting<>("Down-Speed", 2.0, 0.0, 10.0));
    protected final Setting<Float> glide =
            register(new NumberSetting<>("Glide", 0.0001f, 0.0f, 0.2f));
    protected final Setting<Boolean> fixYaw =
            register(new BooleanSetting("Yaw", false));
    protected final Setting<Bind> downBind =
            register(new BindSetting("Down-Bind"));
    protected final Setting<Boolean> schedule =
            register(new BooleanSetting("Schedule", false));

    protected int tickCount = 0;
    protected int invalidTickCount = 0;
    protected Set<Packet<?>> packetSet = new HashSet<>();

    public BoatFly()
    {
        super("BoatFly", Category.Movement);
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerDismount(this));
        this.listeners.add(new ListenerPlayerPosLook(this));
        this.listeners.add(new ListenerServerVehicleMove(this));
        this.listeners.add(new ListenerSteer(this));
        this.listeners.add(new ListenerVehicleMove(this));
        this.listeners.add(new ListenerPostVehicleMove(this));
        this.listeners.add(new ListenerEntityLook(this));
        this.listeners.add(new ListenerEntityLookMove(this));
        this.listeners.add(new ListenerEntityRelativeMove(this));
        this.listeners.add(new ListenerEntityTeleport(this));
        this.listeners.addAll(new ListenerCPackets(this).getListeners());

        SimpleData data = new SimpleData(this, "Fly while riding entities.");
        data.register(bypass,
                "Bypasses NCP BoatFly patch.");
        data.register(postBypass,
                "Sends interact packets after vehicle move packets.");
        data.register(ticks,
                "Ticks to wait between sending interact packets.");
        data.register(packets,
                "Number of interact packets to send.");
        data.register(noVehicleMove,
                "Cancels SPacketMoveVehicle, allowing for smoother flight.");
        data.register(noSteer,
                "Cancels CPacketSteerBoat, bypassing some patches.");
        data.register(noPosUpdate,
                "Does not update the player's position along with the boat's" +
                " (Cancels CPacketPlayer).");
        data.register(noForceRotate,
                "Prevents the server from forcing your rotations.");
        data.register(remount,
                "Automatically remounts the boat after being removed.");
        data.register(remountPackets,
                "Sends extra packets after being dismounted.");
        data.register(upSpeed,
                "Speed to fly upwards with.");
        data.register(downSpeed,
                "Speed to fly downwards with.");
        data.register(glide,
                "Glides down with this speed.");
        data.register(fixYaw,
                "Makes the boat rotate with you.");
        data.register(noForceBoatMove,
                "Prevents the server from forcing your entity " +
                        "to move or rotate.");
        this.setData(data);
    }

    public double getGlideSpeed()
    {
        return glide.getValue();
    }

    protected void sendPackets(Entity riding)
    {
        mc.player.connection.sendPacket(
                new CPacketUseEntity(riding, EnumHand.MAIN_HAND));
        mc.player.connection.sendPacket(
                new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        if (invalid.getValue() && invalidTickCount++ >= invalidTicks.getValue())
        {
            Vec3d playerVec = invalidMode.getValue().createOutOfBounds(mc.player.getPositionVector(), 1337);
            PacketUtil.doPosition(playerVec.x, playerVec.y, playerVec.z, false);
            if (boatInvalid.getValue() && mc.player.getRidingEntity() != null)
            {
                CPacketVehicleMove packet = new CPacketVehicleMove();
                Vec3d vec = invalidMode.getValue().createOutOfBounds(mc.player.getRidingEntity().getPositionVector(), 1337);
                ((ICPacketVehicleMove) packet).setY(vec.y);
                ((ICPacketVehicleMove) packet).setX(vec.x);
                ((ICPacketVehicleMove) packet).setZ(vec.z);
                packetSet.add(packet);
                NetworkUtil.sendPacketNoEvent(packet);
            }
            invalidTickCount = 0;
        }
    }

}
