package me.earth.earthhack.impl.modules.movement.anchor;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.ducks.world.IChunk;
import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.modules.movement.longjump.LongJump;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.movement.reversestep.ReverseStep;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.modules.movement.step.Step;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.AirHoleFinder;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.Hole;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.HoleManager;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.SimpleHoleManager;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;

public class Anchor extends Module
{
    private static final ModuleCache<ReverseStep> REVERSE_STEP = Caches.getModule(ReverseStep.class);
    private static final ModuleCache<PacketFly> PACKET_FLY = Caches.getModule(PacketFly.class);
    private static final ModuleCache<BlockLag> BLOCK_LAG = Caches.getModule(BlockLag.class);
    private static final ModuleCache<LongJump> LONGJUMP = Caches.getModule(LongJump.class);
    private static final ModuleCache<Speed> SPEED = Caches.getModule(Speed.class);
    private static final ModuleCache<Step> STEP = Caches.getModule(Step.class);
    private static final SettingCache<SpeedMode, EnumSetting<SpeedMode>, Speed> SPEED_MODE =
            Caches.getSetting(Speed.class, Setting.class, "Mode", SpeedMode.Instant);

    private final Setting<InputMode> inputMode = register(new EnumSetting<>("Input-Mode", InputMode.Keys));
    private final Setting<Float> pitch = register(new NumberSetting<>("Pitch", 90f, -90f, 90f));
    private final Setting<Integer> delay = register(new NumberSetting<>("Delay", 400, 0, 5000));
    private final Setting<Mode> yMode = register(new EnumSetting<>("Y-Mode", Mode.Off));
    private final Setting<Double> y = register(new NumberSetting<>("Y-Speed", 0.0, -10.0, 10.0));
    private final Setting<Mode> xzMode = register(new EnumSetting<>("XZ-Mode", Mode.Constant));
    private final Setting<Double> xz = register(new NumberSetting<>("XZ-Speed", 0.2, 0.0, 10.0));
    private final Setting<Double> yOffset = register(new NumberSetting<>("Y-Offset", 1.0, 0.0, 1.0));
    private final Setting<Integer> lagTime = register(new NumberSetting<>("Lag-Time", 1000, 0, 10_000));
    private final Setting<Boolean> sneaking = register(new BooleanSetting("Sneaking", false));
    private final Setting<Boolean> withSpeed = register(new BooleanSetting("UseWithSpeed", false));
    private final Setting<Boolean> withSpeedInstant = register(new BooleanSetting("UseWithSpeedInstant", true));
    private final Setting<Boolean> withStep = register(new BooleanSetting("UseWithStep", false));
    private final Setting<Boolean> withRStep = register(new BooleanSetting("UseWithReverseStep", true));
    private final Setting<Boolean> movingTowardsCheck = register(new BooleanSetting("MovingTowardsCheck", false));
    private final Setting<Boolean> movingTowardsWithoutKeys = register(new BooleanSetting("MovingTowardsWithoutKeys", false));
    private final Setting<Boolean> holeCheck = register(new BooleanSetting("HoleCheck", true)).setComplexity(Complexity.Expert);
    private final Setting<Boolean> oldCheck = register(new BooleanSetting("HoleCheck", false)).setComplexity(Complexity.Expert);
    private final Setting<Boolean> filterByY = register(new BooleanSetting("FilterByY", true)).setComplexity(Complexity.Expert);

    // TODO: higher distance for 2x1 and 2x2
    // TODO: check if we are going to get pulled through a wall

    private final HoleManager holeManager = new SimpleHoleManager();
    private final AirHoleFinder holeFinder = new AirHoleFinder(holeManager);
    private final StopWatch timer = new StopWatch();

    public Anchor()
    {
        super("Anchor", Category.Movement);
        // TODO: tracer?
        this.listeners.add(new LambdaListener<>(MoveEvent.class, event ->
        {
            if (mc.player.isSpectator()
                || !Managers.NCP.passed(lagTime.getValue())
                || !sneaking.getValue() && mc.player.isSneaking())
            {
                return;
            }

            holeManager.reset();
            BlockPos pos = mc.player.getPosition();
            holeFinder.setChunk((IChunk) mc.world.getChunk(pos));
            holeFinder.setMaxX(pos.getX() + 1);
            holeFinder.setMinX(pos.getX() - 1);
            holeFinder.setMaxY(pos.getY());
            // TODO: what about 2 block deep holes?
            holeFinder.setMinY(pos.getY() - 1);
            holeFinder.setMaxZ(pos.getZ() + 1);
            holeFinder.setMinZ(pos.getZ() - 1);
            holeFinder.calcHoles();

            Hole hole = holeManager.getHoles()
                                   .values()
                                   .stream()
                                   .filter(h -> !filterByY.getValue() || h.getY() < mc.player.posY)
                                   .filter(h -> !movingTowardsCheck.getValue()
                                       || !((movingTowardsWithoutKeys.getValue()
                                                || !MovementUtil.noMovementKeys())
                                                    && !isMovingTowards(h, event)))
                                   .min(Comparator.comparingDouble(this::getDistance))
                                   .orElse(null);
            if (hole == null)
            {
                return;
            }

            if (oldCheck.getValue() && Math.ceil(mc.player.posY) == hole.getY()
                || holeCheck.getValue() && isInHole())
            {
                timer.reset();
                return;
            }

            // checks come here because we want to have the timer always reset while we are in a hole.
            if (!withSpeed.getValue()
                && SPEED.isEnabled() && (!withSpeedInstant.getValue() || SPEED_MODE.getValue() != SpeedMode.Instant)
                || !withStep.getValue() && STEP.isEnabled()
                || !withRStep.getValue() && REVERSE_STEP.isEnabled()
                || PACKET_FLY.isEnabled()
                || BLOCK_LAG.isEnabled()
                || LONGJUMP.isEnabled()
                || inputMode.getValue() == InputMode.Keys && MovementUtil.noMovementKeys()
                || inputMode.getValue() == InputMode.NoKeys && !MovementUtil.noMovementKeys()
                || !timer.passed(delay.getValue())
                || mc.player.rotationPitch > pitch.getValue()
                || holeCheck.getValue() && isInHole())
            {
                return;
            }

            double x = (hole.getX() + (hole.getMaxX() - hole.getX()) / 2.0) - mc.player.posX;
            double z = (hole.getZ() + (hole.getMaxZ() - hole.getZ()) / 2.0) - mc.player.posZ;
            double distance = Math.sqrt(x * x + z * z);
            event.setY(modify(yMode.getValue(), event.getY(), y.getValue()));
            if (distance == 0.0)
            {
                event.setX(0.0);
                event.setY(0.0);
                return;
            }

            // TODO: also take slowness into account?
            double pull_factor = xz.getValue() / distance;
            event.setX(modify(xzMode.getValue(), event.getX(), x * pull_factor));
            event.setZ(modify(xzMode.getValue(), event.getZ(), z * pull_factor));
        }));

        SimpleData data = new SimpleData(this, "Makes you stop over holes.");
        data.register(withSpeed, "Whether this module should be active while Speed is on.");
        data.register(withStep, "Whether this module should be active while Step is on.");
        data.register(pitch, "This module is only active while you are looking down further than this value.");
        data.register(delay, "When to start using this module again after you've been in a hole.");
        data.register(yMode, "-Factor will multiply your vertical speed with the Y-Speed setting." +
                "\n-Constant will set your vertical speed to the Y-Speed setting." +
                "\n-Add will add the Y-Speed setting to your vertical speed." +
                "\n-Off won't change your speed in the vertical direction.");
        data.register(y, "Speed in the vertical direction, configurable by Y-Mode.");
        data.register(xzMode, "-Factor will multiply your horizontal speed with the XZ-Speed setting." +
                "\n-Constant will set your horizontal speed to the XZ-Speed setting." +
                "\n-Add will add the XZ-Speed setting to your horizontal speed." +
                "\n-Off won't change your speed in the horizontal direction.");
        data.register(xz, "Speed in the horizontal direction, configurable by XZ-Mode.");
        data.register(yOffset, "Offset to the bottom of the hole when calculating distance.");
        data.register(withSpeedInstant, "Exception to UseWithSpeed for Speed Mode - Instant.");
        data.register(withRStep, "Whether to use this module together with ReverseStep.");
        data.register(inputMode, "-Always: module is always active.\n" +
            "-NoKeys: module is only active while you are not pressing any movement keys.\n" +
            "-Keys: module is only active while you are pressing movement keys.");
        data.register(movingTowardsCheck, "Checks if you are moving towards the hole.");
        this.setData(data);
    }

    private boolean isInHole() {
        return holeManager
            .getHoles()
            .values()
            .stream()
            .anyMatch(h -> h.contains(mc.player.posX, mc.player.posY, mc.player.posZ));
    }

    private double modify(Mode mode, double value, double setting)
    {
        switch (mode)
        {
            case Factor:
                return value * setting;
            case Constant:
                return setting;
            case Add:
                return value + setting;
            case Off:
            default:
                return value;
        }
    }

    private double getDistance(Hole hole)
    {
        double holeX = hole.getX() + (hole.getMaxX() - hole.getX()) / 2.0;
        double holeY = hole.getY() + yOffset.getValue();
        double holeZ = hole.getZ() + (hole.getMaxZ() - hole.getZ()) / 2.0;
        return mc.player.getDistanceSq(holeX, holeY, holeZ);
    }

    private boolean isMovingTowards(Hole hole, MoveEvent event)
    {
        double holeX = hole.getX() + (hole.getMaxX() - hole.getX()) / 2.0;
        double holeY = hole.getY();
        double holeZ = hole.getZ() + (hole.getMaxZ() - hole.getZ()) / 2.0;
        double distance = mc.player.getDistanceSq(holeX, holeY, holeZ);
        double nextDistance = mc.player
            .getPositionVector()
            .add(new Vec3d(event.getX(), event.getY(), event.getZ())
                     .normalize()
                     .scale(Math.sqrt(distance)))
            .squareDistanceTo(holeX, holeY, holeZ);

        return distance >= nextDistance;
    }

    public enum Mode
    {
        Factor,
        Constant,
        Add,
        Off
    }

    public enum InputMode
    {
        Always,
        NoKeys,
        Keys
    }

}
