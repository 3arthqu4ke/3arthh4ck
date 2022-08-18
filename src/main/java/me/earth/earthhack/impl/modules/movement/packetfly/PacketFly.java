package me.earth.earthhack.impl.modules.movement.packetfly;

import io.netty.util.internal.ConcurrentSet;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Mode;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Phase;
import me.earth.earthhack.impl.modules.movement.packetfly.util.TimeVec;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Type;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SNoRotation;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PacketFly extends Module
{
    protected final Setting<Mode> mode        =
            register(new EnumSetting<>("Mode", Mode.Factor));
    protected final Setting<Float> factor     =
            register(new NumberSetting<>("Factor", 1.0f, 0.0f, 10.0f));
    protected final Setting<Phase> phase      =
            register(new EnumSetting<>("Phase", Phase.Full));
    protected final Setting<Type> type        =
            register(new EnumSetting<>("Type", Type.Up));
    protected final Setting<Boolean> antiKick =
            register(new BooleanSetting("AntiKick", true));
    protected final Setting<Boolean> answer   =
            register(new BooleanSetting("Answer", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> bbOffset   =
            register(new BooleanSetting("BB-Offset", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> invalidY =
            register(new NumberSetting<>("Invalid-Offset", 1337, 0, 1337));
    protected final Setting<Integer> invalids =
            register(new NumberSetting<>("Invalids", 1, 0, 10))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> sendTeleport   =
            register(new NumberSetting<>("Teleport", 1, 0, 10))
                .setComplexity(Complexity.Expert);
    protected final Setting<Double> concealY    =
            register(new NumberSetting<>("C-Y", 0.0, -256.0, 256.0))
                .setComplexity(Complexity.Expert);
    protected final Setting<Double> conceal    =
            register(new NumberSetting<>("C-Multiplier", 1.0, 0.0, 2.0))
                .setComplexity(Complexity.Expert);
    protected final Setting<Double> ySpeed    =
            register(new NumberSetting<>("Y-Multiplier", 1.0, 0.0, 2.0))
                .setComplexity(Complexity.Expert);
    protected final Setting<Double> xzSpeed =
            register(new NumberSetting<>("X/Z-Multiplier", 1.0, 0.0, 2.0))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> elytra =
            register(new BooleanSetting("Elytra", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> xzJitter =
            register(new BooleanSetting("Jitter-XZ", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> yJitter =
            register(new BooleanSetting("Jitter-Y", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> setPos =
            register(new BooleanSetting("Set-Pos", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> zeroSpeed =
            register(new BooleanSetting("Zero-Speed", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> zeroY =
            register(new BooleanSetting("Zero-Y", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> fixPosition =
            register(new BooleanSetting("FixPosition", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> zeroTeleport =
            register(new BooleanSetting("Zero-Teleport", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> zoomer   =
            register(new NumberSetting<>("Zoomies", 3, 0, 10))
                .setComplexity(Complexity.Expert);

    protected final Map<Integer, TimeVec> posLooks = new ConcurrentHashMap<>();
    protected final Set<Packet<?>> playerPackets = new ConcurrentSet<>();
    protected final AtomicInteger teleportID = new AtomicInteger();
    protected Vec3d vecDelServer;
    protected int packetCounter;
    protected boolean zoomies;
    protected float lastFactor;
    protected int ticks;
    protected int zoomTimer = 0;

    public PacketFly()
    {
        super("PacketFly", Category.Movement);
        this.listeners.add(new ListenerOverlay(this));
        this.listeners.add(new ListenerBlockPush(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.addAll(new ListenerCPacket(this).getListeners());
        this.mode.addObserver(e -> {
            if (e.getValue() == Mode.Compatibility) {
                this.clearValues();
            }
        });
        this.setData(new PacketFlyData(this));
    }

    @Override
    protected void onEnable()
    {
        clearValues();
        if (mc.player == null)
        {
            this.disable();
        }

        // teleportID.set(Managers.POSITION.getTeleportID());
    }

    @Override
    public String getDisplayInfo()
    {
        return mode.getValue().toString();
    }

    protected void clearValues()
    {
        lastFactor = 1.0f;
        packetCounter = 0;
        teleportID.set(0);
        playerPackets.clear();
        posLooks.clear();
        vecDelServer = null;
    }

    protected void onPacketSend(PacketEvent<? extends CPacketPlayer> event)
    {
        if (mode.getValue() != Mode.Compatibility
            && !playerPackets.remove(event.getPacket()))
        {
            event.setCancelled(true);
        }
    }

    protected boolean isPlayerCollisionBoundingBoxEmpty()
    {
        double o = bbOffset.getValue() ? -0.0625 : 0;
        return !mc.world
                  .getCollisionBoxes(mc.player,
                                     mc.player
                                       .getEntityBoundingBox()
                                       .expand(o, o, o))
                  .isEmpty();
    }

    protected boolean checkPackets(int amount)
    {
        if (++this.packetCounter >= amount)
        {
            this.packetCounter = 0;
            return true;
        }

        return false;
    }

    protected void sendPackets(double x, double y, double z, boolean confirm)
    {
        Vec3d offset = new Vec3d(x, y, z);
        Vec3d vec = mc.player.getPositionVector().add(offset);
        vecDelServer = vec;
        Vec3d oOB = type.getValue().createOutOfBounds(vec, invalidY.getValue());

        if (PingBypassModule.isNewPbActive()) {
            mc.player.connection.sendPacket(new C2SNoRotation(
                vec.x, vec.y, vec.z, mc.player.onGround));
        } else {
            sendCPacket(PacketUtil.position(vec.x, vec.y, vec.z));
        }

        double lastX = Managers.POSITION.getX();
        double lastY = Managers.POSITION.getY();
        double lastZ = Managers.POSITION.getZ();
        boolean last = Managers.POSITION.isOnGround();

        if (!mc.isSingleplayer())
        {
            for (int i = 0; i < invalids.getValue(); i++)
            {
                sendCPacket(PacketUtil.position(oOB.x, oOB.y, oOB.z));
                oOB = type.getValue().createOutOfBounds(oOB, invalidY.getValue());
            }
        }

        if (fixPosition.getValue())
        {
            Managers.POSITION.set(lastX, lastY, lastZ);
            Managers.POSITION.setOnGround(last);
        }

        if (confirm && (zeroTeleport.getValue() || teleportID.get() != 0))
        {
            for (int i = 0; i < sendTeleport.getValue(); i++)
            {
                sendConfirmTeleport(vec);
            }
        }

        if (elytra.getValue())
        {
            PacketUtil.sendAction(CPacketEntityAction.Action.START_FALL_FLYING);
        }
    }

    protected void sendConfirmTeleport(Vec3d vec)
    {
        int id = teleportID.incrementAndGet();
        PacketUtil.teleport(id);
        posLooks.put(id, new TimeVec(vec));
    }

    protected void sendCPacket(Packet<?> packet)
    {
        playerPackets.add(packet);
        mc.player.connection.sendPacket(packet);
    }

}
