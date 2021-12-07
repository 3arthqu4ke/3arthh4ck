package me.earth.earthhack.impl.modules.movement.phase;

import io.netty.util.internal.ConcurrentSet;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.CollisionEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.modules.movement.phase.mode.PhaseMode;
import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

import static net.minecraft.util.math.MathHelper.floor;

public class Phase extends BlockESPModule implements CollisionEvent.Listener
{
    protected final Setting<PhaseMode> mode   =
        register(new EnumSetting<>("Mode", PhaseMode.Sand));
    protected final Setting<Boolean> autoClip =
        register(new BooleanSetting("AutoClip", false));
    protected final Setting<Double> blocks    =
        register(new NumberSetting<>("Blocks", 0.003, 0.001, 10.0));
    protected final Setting<Double> distance  =
        register(new NumberSetting<>("Distance", 0.2, 0.0, 10.0));
    protected final Setting<Double> speed    =
        register(new NumberSetting<>("Speed", 4.0, 0.1, 10.0));
    protected final Setting<Double> constSpeed    =
            register(new NumberSetting<>("ConstSpeed", 1.0, 0.1, 10.0));
    protected final Setting<Boolean> constStrafe =
            register(new BooleanSetting("ConstStrafe", false));
    protected final Setting<Boolean> constTeleport =
            register(new BooleanSetting("ConstTeleport", false));
    protected final Setting<Boolean> sneakCheck =
            register(new BooleanSetting("SneakCheck", false));
    protected final Setting<Boolean> cancel =
        register(new BooleanSetting("Cancel", false));
    protected final Setting<Boolean> limit =
        register(new BooleanSetting("Limit", true));
    protected final Setting<Integer> skipTime    =
        register(new NumberSetting<>("Skip-Time", 150, 0, 1000));
    protected final Setting<Boolean> onlyBlock =
        register(new BooleanSetting("OnlyInBlock", false));
    protected final Setting<Boolean> cancelSneak =
            register(new BooleanSetting("CancelSneak", false));
    protected final Setting<Boolean> autoSneak =
        register(new BooleanSetting("AutoSneak", false));
    protected final Setting<Boolean> autoClick =
        register(new BooleanSetting("AutoClick", false));
    protected final Setting<Integer> clickDelay    =
        register(new NumberSetting<>("Click-Delay", 250, 0, 1000));
    protected final Setting<Boolean> requireClick =
        register(new BooleanSetting("RequireClick", false));
    protected final Setting<Boolean> clickBB =
        register(new BooleanSetting("Click-BB", false));
    protected final Setting<Boolean> requireForward =
        register(new BooleanSetting("RequireForward", false));
    protected final Setting<Boolean> forwardBB =
        register(new BooleanSetting("Forward-BB", false));
    protected final Setting<Boolean> smartClick =
        register(new BooleanSetting("SmartClick", false));
    protected final Setting<Boolean> esp =
        register(new BooleanSetting("ESP", false));

    protected final ListenerCollision listenerCollision;
    protected final Set<Packet<?>> packets = new ConcurrentSet<>();
    protected final StopWatch clickTimer = new StopWatch();
    protected final StopWatch timer = new StopWatch();
    protected BlockPos pos;
    protected int delay;

    public Phase()
    {
        super("Phase", Category.Movement);
        this.listenerCollision = new ListenerCollision(this);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerSuffocation(this));
        this.listeners.add(new ListenerBlockPush(this));
        this.listeners.add(new ListenerInput(this));
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerSneak(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.addAll(new ListenerCPackets(this).getListeners());

        this.unregister(super.color);
        this.unregister(super.outline);
        this.unregister(super.lineWidth);
        this.unregister(super.height);
        super.height.addObserver(e ->
            { e.setValue(1.0f); e.setCancelled(true); });

        this.register(super.color);
        this.register(super.outline);
        this.register(super.lineWidth);

        this.setData(new PhaseData(this));
    }

    @Override
    public String getDisplayInfo()
    {
        return mode.getValue().name();
    }

    @Override
    protected void onEnable()
    {
        delay = 0;
        EntityPlayerSP player = mc.player;
        if (player != null && autoClip.getValue())
        {
            double yawCos =
                    Math.cos(Math.toRadians(player.rotationYaw + 90.0f));
            double yawSin =
                    Math.sin(Math.toRadians(player.rotationYaw + 90.0f));
            player.setPosition(
                    player.posX + 1.0 * blocks.getValue() * yawCos
                            + 0.0 * blocks.getValue() * yawSin,
                    player.posY,
                    player.posZ + (1.0 * blocks.getValue() * yawSin
                            - 0.0 * blocks.getValue() * yawCos));
        }
    }

    @Override
    protected void onDisable()
    {
        if (mc.player != null)
        {
            mc.player.noClip = false;
        }
    }

    @Override
    public void onCollision(CollisionEvent event)
    {
        if (this.isEnabled())
        {
            listenerCollision.invoke(event);
        }
    }

    public boolean isPhasing()
    {
        AxisAlignedBB bb = mc.player.getEntityBoundingBox();
        for (int x = floor(bb.minX); x < floor(bb.maxX) + 1; x++)
        {
            for (int y = floor(bb.minY); y < floor(bb.maxY) + 1; y++)
            {
                for (int z = floor(bb.minZ); z < floor(bb.maxZ) + 1; z++)
                {
                    if (mc.world.getBlockState(new BlockPos(x, y, z))
                                .getMaterial()
                                .blocksMovement())
                    {
                        if (bb.intersects(
                               new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1)))
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    protected void send(Packet<?> packet)
    {
        packets.add(packet);
        mc.player.connection.sendPacket(packet);
    }

    protected void onPacket(PacketEvent.Send<? extends CPacketPlayer> event)
    {
        if (mode.getValue() == PhaseMode.ConstantiamNew
                && !MovementUtil.isMoving()
                && mc.player.posY == mc.player.lastTickPosY)
        {
            event.setCancelled(true);
        }
    }

}
