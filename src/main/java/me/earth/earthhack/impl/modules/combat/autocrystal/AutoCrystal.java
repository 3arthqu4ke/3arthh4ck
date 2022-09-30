package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.client.settings.SettingsModule;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.*;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.*;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalTimeStamp;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.RotationFunction;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.player.suicide.SuicideAutoCrystal;
import me.earth.earthhack.impl.util.helpers.blocks.modes.PlaceSwing;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.DiscreteTimer;
import me.earth.earthhack.impl.util.math.GuardTimer;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.SafeRunnable;
import me.earth.earthhack.impl.util.thread.ThreadUtil;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.input.Mouse;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// TODO: with newVerEntities we can actually mine a block,
//  place on top of it, break the crystal after we broke the block,
//  that way we can place the crystal where the block was immediately
//  since the first crystal blew up the item that drops
// TODO: If we can be the last person to spawn any entity on the last tick
//  and first persons to spawn a crystal on the next server tick,
//  our crystal will be the first to spawn
//  next tick, while the other will be the last to spawn last tick,
//  so the last spawned crystal will have the highest id probably.
//  Which would allow for a really good id prediction of the next tick.
//  Only worth if we have really low ms and we can receive packets between
//  the ticks and the world doesnt have many players.
// TODO: more mine stuff!
// TODO: SmartRange for OBBY!!!
public class AutoCrystal extends Module
{
    public static final PositionHistoryHelper POSITION_HISTORY =
        new PositionHistoryHelper();

    static {
        Bus.EVENT_BUS.subscribe(POSITION_HISTORY);
    }

    private static final ScheduledExecutorService EXECUTOR =
            ThreadUtil.newDaemonScheduledExecutor("AutoCrystal");
    private static final ModuleCache<PingBypassModule> PINGBYPASS =
            Caches.getModule(PingBypassModule.class);
    private static final ModuleCache<PacketFly> PACKET_FLY =
            Caches.getModule(PacketFly.class);
    private static final AtomicBoolean ATOMIC_STARTED =
            new AtomicBoolean();
    private static boolean started;

    protected final Setting<ACPages> pages =
            register(new EnumSetting<>("Page", ACPages.Place))
                .setComplexity(Complexity.Medium);

    /* ---------------- Place Settings -------------- */
    protected final Setting<Boolean> place =
            register(new BooleanSetting("Place", true));
    protected final Setting<Target> targetMode =
            register(new EnumSetting<>("Target", Target.Closest))
                .setComplexity(Complexity.Medium);
    protected final Setting<Float> placeRange =
            register(new NumberSetting<>("PlaceRange", 6.0f, 0.0f, 6.0f));
    protected final Setting<Float> placeTrace =
            register(new NumberSetting<>("PlaceTrace", 6.0f, 0.0f, 6.0f))
                .setComplexity(Complexity.Expert);
    public final Setting<Float> minDamage =
            register(new NumberSetting<>("MinDamage", 6.0f, 0.1f, 20.0f));
    protected final Setting<Integer> placeDelay =
            register(new NumberSetting<>("PlaceDelay", 25, 0, 500));
    protected final Setting<Float> maxSelfPlace =
            register(new NumberSetting<>("MaxSelfPlace", 9.0f, 0.0f, 20.0f))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> multiPlace =
            register(new NumberSetting<>("MultiPlace", 1, 1, 5))
                .setComplexity(Complexity.Medium);
    protected final Setting<Float> slowPlaceDmg =
            register(new NumberSetting<>("SlowPlace", 4.0f, 0.1f, 20.0f))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> slowPlaceDelay =
            register(new NumberSetting<>("SlowPlaceDelay", 500, 0, 500))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> override =
            register(new BooleanSetting("OverridePlace", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> newVer =
            register(new BooleanSetting("1.13+", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> newVerEntities =
            register(new BooleanSetting("1.13-Entities", false))
                .setComplexity(Complexity.Medium);
    public final Setting<SwingTime> placeSwing =
            register(new EnumSetting<>("PlaceSwing", SwingTime.Post))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> smartTrace =
            register(new BooleanSetting("Smart-Trace", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> placeRangeEyes =
            register(new BooleanSetting("PlaceRangeEyes", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> placeRangeCenter =
            register(new BooleanSetting("PlaceRangeCenter", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Double> traceWidth =
            register(new NumberSetting<>("TraceWidth", -1.0, -1.0, 1.0))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> fallbackTrace =
            register(new BooleanSetting("Fallback-Trace", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> rayTraceBypass =
            register(new BooleanSetting("RayTraceBypass", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> forceBypass =
            register(new BooleanSetting("ForceBypass", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> rayBypassFacePlace =
            register(new BooleanSetting("RayBypassFacePlace", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> rayBypassFallback =
            register(new BooleanSetting("RayBypassFallback", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> bypassTicks =
            register(new NumberSetting<>("BypassTicks", 10, 0, 20))
                .setComplexity(Complexity.Expert);
    protected final Setting<Float> rbYaw =
            register(new NumberSetting<>("RB-Yaw", 180.0f, 0.0f, 180.0f))
                .setComplexity(Complexity.Expert);
    protected final Setting<Float> rbPitch =
            register(new NumberSetting<>("RB-Pitch", 90.0f, 0.0f, 90.0f))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> bypassRotationTime =
            register(new NumberSetting<>("RayBypassRotationTime", 500, 0, 1000))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> ignoreNonFull =
            register(new BooleanSetting("IgnoreNonFull", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> efficientPlacements =
            register(new BooleanSetting("EfficientPlacements", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> simulatePlace =
            register(new NumberSetting<>("Simulate-Place", 0, 0, 10))
                .setComplexity(Complexity.Medium);

    /* ---------------- Break Settings -------------- */
    protected final Setting<Attack> attackMode = // TODO: Calc isnt implemented yet!
            register(new EnumSetting<>("Attack", Attack.Crystal));
    protected final Setting<Boolean> attack =
            register(new BooleanSetting("Break", true));
    protected final Setting<Float> breakRange =
            register(new NumberSetting<>("BreakRange", 6.0f, 0.0f, 6.0f));
    protected final Setting<Integer> breakDelay =
            register(new NumberSetting<>("BreakDelay", 25, 0, 500));
    protected final Setting<Float> breakTrace =
            register(new NumberSetting<>("BreakTrace", 3.0f, 0.0f, 6.0f))
                .setComplexity(Complexity.Medium);
    protected final Setting<Float> minBreakDamage =
            register(new NumberSetting<>("MinBreakDmg", 0.5f, 0.0f, 20.0f))
                .setComplexity(Complexity.Medium);
    protected final Setting<Float> maxSelfBreak =
            register(new NumberSetting<>("MaxSelfBreak", 10.0f, 0.0f, 20.0f))
                .setComplexity(Complexity.Medium);
    protected final Setting<Float> slowBreakDamage =
            register(new NumberSetting<>("SlowBreak", 3.0f, 0.1f, 20.0f))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> slowBreakDelay =
            register(new NumberSetting<>("SlowBreakDelay", 500, 0, 500))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> instant =
            register(new BooleanSetting("Instant", false));
    protected final Setting<Boolean> asyncCalc =
            register(new BooleanSetting("Async-Calc", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> alwaysCalc =
            register(new BooleanSetting("Always-Calc", false))
                .setComplexity(Complexity.Medium);

    protected final Setting<Boolean> ncpRange =
            register(new BooleanSetting("NCP-Range", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<SmartRange> placeBreakRange =
            register(new EnumSetting<>("SmartRange", SmartRange.None))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> smartTicks =
            register(new NumberSetting<>("SmartRange-Ticks", 0, 0, 20))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> negativeTicks =
            register(new NumberSetting<>("Negative-Ticks", 0, 0, 20))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> smartBreakTrace =
            register(new BooleanSetting("SmartBreakTrace", true))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> negativeBreakTrace =
            register(new BooleanSetting("NegativeBreakTrace", true))
                .setComplexity(Complexity.Expert);

    protected final Setting<Integer> packets =
            register(new NumberSetting<>("Packets", 1, 1, 5))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> overrideBreak =
            register(new BooleanSetting("OverrideBreak", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<AntiWeakness> antiWeakness =
            register(new EnumSetting<>("AntiWeakness", AntiWeakness.None))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> instantAntiWeak =
            register(new BooleanSetting("AW-Instant", true))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> efficient =
            register(new BooleanSetting("Efficient", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> manually =
            register(new BooleanSetting("Manually", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> manualDelay =
            register(new NumberSetting<>("ManualDelay", 500, 0, 500))
                .setComplexity(Complexity.Expert);
    protected final Setting<SwingTime> breakSwing =
            register(new EnumSetting<>("BreakSwing", SwingTime.Post))
                .setComplexity(Complexity.Expert);

    /* --------------- Rotations -------------- */
    protected final Setting<ACRotate> rotate =
            register(new EnumSetting<>("Rotate", ACRotate.None));
    protected final Setting<RotateMode> rotateMode =
            register(new EnumSetting<>("Rotate-Mode", RotateMode.Normal))
                .setComplexity(Complexity.Medium);
    protected final Setting<Float> smoothSpeed =
            register(new NumberSetting<>("Smooth-Speed", 0.5f, 0.1f, 2.0f))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> endRotations =
            register(new NumberSetting<>("End-Rotations", 250, 0, 1000))
                .setComplexity(Complexity.Expert);
    protected final Setting<Float> angle =
            register(new NumberSetting<>("Break-Angle", 180.0f, 0.1f, 180.0f))
                .setComplexity(Complexity.Medium);
    protected final Setting<Float> placeAngle =
            register(new NumberSetting<>("Place-Angle", 180.0f, 0.1f, 180.0f))
                .setComplexity(Complexity.Medium);
    protected final Setting<Float> height =
            register(new NumberSetting<>("Height", 0.05f, 0.0f, 1.0f))
                .setComplexity(Complexity.Expert);
    protected final Setting<Double> placeHeight =
            register(new NumberSetting<>("Place-Height", 1.0, 0.0, 1.0))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> rotationTicks =
            register(new NumberSetting<>("Rotations-Existed", 0, 0, 500))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> focusRotations =
            register(new BooleanSetting("Focus-Rotations", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> focusAngleCalc =
            register(new BooleanSetting("FocusRotationCompare", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Double> focusExponent =
            register(new NumberSetting<>("FocusExponent", 0.0, 0.0, 10.0))
                .setComplexity(Complexity.Expert);
    protected final Setting<Double> focusDiff =
            register(new NumberSetting<>("FocusDiff", 0.0, 0.0, 180.0))
                .setComplexity(Complexity.Expert);
    protected final Setting<Double> rotationExponent =
            register(new NumberSetting<>("RotationExponent", 0.0, 0.0, 10.0))
                .setComplexity(Complexity.Expert);
    protected final Setting<Double> minRotDiff =
            register(new NumberSetting<>("MinRotationDiff", 0.0, 0.0, 180.0))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> existed =
            register(new NumberSetting<>("Existed", 0, 0, 500))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> pingExisted =
            register(new BooleanSetting("Ping-Existed", false))
                .setComplexity(Complexity.Medium);

    /* ---------------- Misc Settings -------------- */
    protected final Setting<Float> targetRange =
            register(new NumberSetting<>("TargetRange", 20.0f, 0.1f, 20.0f));
    protected final Setting<Float> pbTrace =
            register(new NumberSetting<>("CombinedTrace", 3.0f, 0.0f, 6.0f))
                .setComplexity(Complexity.Medium);
    protected final Setting<Float> range =
            register(new NumberSetting<>("Range", 12.0f, 0.1f, 20.0f));
    protected final Setting<Boolean> suicide =
            register(new BooleanSetting("Suicide", false));
    protected final Setting<Boolean> shield =
            register(new BooleanSetting("Shield", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> shieldCount =
            register(new NumberSetting<>("ShieldCount", 1, 1, 5))
                .setComplexity(Complexity.Expert);
    protected final Setting<Float> shieldMinDamage =
            register(new NumberSetting<>("ShieldMinDamage", 6.0f, 0.0f, 20.0f))
                .setComplexity(Complexity.Expert);
    protected final Setting<Float> shieldSelfDamage =
            register(new NumberSetting<>("ShieldSelfDamage", 2.0f, 0.0f, 20.0f))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> shieldDelay =
            register(new NumberSetting<>("ShieldPlaceDelay", 50, 0, 5000))
                .setComplexity(Complexity.Expert);
    protected final Setting<Float> shieldRange =
            register(new NumberSetting<>("ShieldRange", 10.0f, 0.0f, 20.0f))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> shieldPrioritizeHealth =
            register(new BooleanSetting("Shield-PrioritizeHealth", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> multiTask =
            register(new BooleanSetting("MultiTask", true))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> multiPlaceCalc =
            register(new BooleanSetting("MultiPlace-Calc", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> multiPlaceMinDmg =
            register(new BooleanSetting("MultiPlace-MinDmg", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> countDeadCrystals =
            register(new BooleanSetting("CountDeadCrystals", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> countDeathTime =
            register(new BooleanSetting("CountWithinDeathTime", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> yCalc =
            register(new BooleanSetting("Y-Calc", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> dangerSpeed =
            register(new BooleanSetting("Danger-Speed", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Float> dangerHealth =
            register(new NumberSetting<>("Danger-Health", 0.0f, 0.0f, 36.0f))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> cooldown =
            register(new NumberSetting<>("CoolDown", 500, 0, 10_000));
    protected final Setting<Integer> placeCoolDown =
            register(new NumberSetting<>("PlaceCooldown", 0, 0, 10_000))
                .setComplexity(Complexity.Medium);
    protected final Setting<AntiFriendPop> antiFriendPop =
            register(new EnumSetting<>("AntiFriendPop", AntiFriendPop.None));
    protected final Setting<Boolean> antiFeetPlace =
            register(new BooleanSetting("AntiFeetPlace", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> feetBuffer =
            register(new NumberSetting<>("FeetBuffer", 5, 0, 50))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> stopWhenEating =
            register(new BooleanSetting("StopWhenEating", false));
    protected final Setting<Boolean> stopWhenMining =
            register(new BooleanSetting("StopWhenMining", false));
    protected final Setting<Boolean> dangerFacePlace =
            register(new BooleanSetting("Danger-FacePlace", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> motionCalc =
            register(new BooleanSetting("Motion-Calc", false))
                .setComplexity(Complexity.Expert);

    /* ---------------- FacePlace and ArmorPlace -------------- */
    protected final Setting<Boolean> holdFacePlace =
            register(new BooleanSetting("HoldFacePlace", false));
    protected final Setting<Float> facePlace =
            register(new NumberSetting<>("FacePlace", 10.0f, 0.0f, 36.0f));
    protected final Setting<Float> minFaceDmg =
            register(new NumberSetting<>("Min-FP", 2.0f, 0.0f, 5.0f))
                .setComplexity(Complexity.Medium);
    protected final Setting<Float> armorPlace =
            register(new NumberSetting<>("ArmorPlace", 5.0f, 0.0f, 100.0f))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> pickAxeHold =
            register(new BooleanSetting("PickAxe-Hold", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> antiNaked =
            register(new BooleanSetting("AntiNaked", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> fallBack =
            register(new BooleanSetting("FallBack", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Float> fallBackDiff =
            register(new NumberSetting<>("Fallback-Difference", 10.0f, 0.0f, 16.0f))
                .setComplexity(Complexity.Expert);
    protected final Setting<Float> fallBackDmg =
            register(new NumberSetting<>("FallBackDmg", 3.0f, 0.0f, 6.0f))
                .setComplexity(Complexity.Expert);

    /* ---------------- Switch, Swing and PingBypass -------------- */
    protected final Setting<AutoSwitch> autoSwitch =
            register(new EnumSetting<>("AutoSwitch", AutoSwitch.Bind));
    protected final Setting<Boolean> mainHand =
            register(new BooleanSetting("MainHand", false));
    protected final Setting<Bind> switchBind =
            register(new BindSetting("SwitchBind", Bind.none()));
    protected final Setting<Boolean> switchBack =
            register(new BooleanSetting("SwitchBack", true));
    protected final Setting<Boolean> useAsOffhand =
            register(new BooleanSetting("UseAsOffHandBind", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> instantOffhand =
            register(new BooleanSetting("Instant-Offhand", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> pingBypass =
            register(new BooleanSetting("Old-PingBypass", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> switchMessage =
            register(new BooleanSetting("Switch-Message", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<SwingType> swing =
            register(new EnumSetting<>("BreakHand", SwingType.MainHand))
                .setComplexity(Complexity.Expert);
    protected final Setting<SwingType> placeHand =
            register(new EnumSetting<>("PlaceHand", SwingType.MainHand))
                .setComplexity(Complexity.Expert);
    protected final Setting<CooldownBypass> cooldownBypass =
            register(new EnumSetting<>("CooldownBypass", CooldownBypass.None))
                .setComplexity(Complexity.Medium);
    protected final Setting<CooldownBypass> obsidianBypass =
            register(new EnumSetting<>("ObsidianBypass", CooldownBypass.None))
                .setComplexity(Complexity.Medium);
    protected final Setting<CooldownBypass> antiWeaknessBypass =
            register(new EnumSetting<>("AntiWeaknessBypass", CooldownBypass.None))
                .setComplexity(Complexity.Medium);
    protected final Setting<CooldownBypass> mineBypass =
            register(new EnumSetting<>("MineBypass", CooldownBypass.None))
                .setComplexity(Complexity.Medium);
    protected final Setting<SwingType> obbyHand =
            register(new EnumSetting<>("ObbyHand", SwingType.MainHand))
                .setComplexity(Complexity.Expert);

    /* ---------------- Render Settings -------------- */
    protected final Setting<Boolean> render =
            register(new BooleanSetting("Render", true))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> renderTime =
            register(new NumberSetting<>("Render-Time", 600, 0, 5000))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> box =
            register(new BooleanSetting("Draw-Box", true))
                .setComplexity(Complexity.Medium);
    protected final Setting<Color> boxColor =
            register(new ColorSetting("Box", new Color(255, 255, 255, 120)));
    protected final Setting<Color> outLine =
            register(new ColorSetting("Outline", new Color(255, 255, 255, 240)));
    protected final Setting<Color> indicatorColor =
            register(new ColorSetting("IndicatorColor", new Color(190, 5, 5, 255)))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> fade =
            register(new BooleanSetting("Fade", true))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> fadeComp =
            register(new BooleanSetting("Fade-Compatibility", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> fadeTime =
            register(new NumberSetting<>("Fade-Time", 1000, 0, 5000))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> realtime =
            register(new BooleanSetting("Realtime", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> slide =
            register(new BooleanSetting("Slide", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> smoothSlide =
            register(new BooleanSetting("SmoothenSlide", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Double> slideTime =
            register(new NumberSetting<>("Slide-Time", 250.0, 1.0, 1000.0))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> zoom =
            register(new BooleanSetting("Zoom", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Double> zoomTime =
            register(new NumberSetting<>("Zoom-Time", 100.0, 1.0, 1000.0))
                .setComplexity(Complexity.Medium);
    protected final Setting<Double> zoomOffset =
            register(new NumberSetting<>("Zoom-Offset", -0.5, -1.0, 1.0))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> multiZoom =
            register(new BooleanSetting("Multi-Zoom", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> renderExtrapolation =
            register(new BooleanSetting("RenderExtrapolation", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<RenderDamagePos> renderDamage =
            register(new EnumSetting<>("DamageRender", RenderDamagePos.None))
                .setComplexity(Complexity.Medium);
    protected final Setting<RenderDamage> renderMode =
            register(new EnumSetting<>("DamageMode", RenderDamage.Normal))
                .setComplexity(Complexity.Medium);

    /* ---------------- SetDead Settings -------------- */
    protected final Setting<Boolean> setDead =
            register(new BooleanSetting("SetDead", false));
    protected final Setting<Boolean> instantSetDead =
            register(new BooleanSetting("Instant-Dead", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> pseudoSetDead =
            register(new BooleanSetting("Pseudo-Dead", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> simulateExplosion =
            register(new BooleanSetting("SimulateExplosion", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> soundRemove =
            register(new BooleanSetting("SoundRemove", true))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> useSafeDeathTime =
            register(new BooleanSetting("UseSafeDeathTime", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> safeDeathTime =
            register(new NumberSetting<>("Safe-Death-Time", 0, 0, 500))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> deathTime =
            register(new NumberSetting<>("Death-Time", 0, 0, 500))
                .setComplexity(Complexity.Medium);

    /* ---------------- Obsidian Settings -------------- */
    protected final Setting<Boolean> obsidian =
            register(new BooleanSetting("Obsidian", false));
    protected final Setting<Boolean> basePlaceOnly =
            register(new BooleanSetting("BasePlaceOnly", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> obbySwitch =
            register(new BooleanSetting("Obby-Switch", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> obbyDelay =
            register(new NumberSetting<>("ObbyDelay", 500, 0, 5000))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> obbyCalc =
            register(new NumberSetting<>("ObbyCalc", 500, 0, 5000))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> helpingBlocks =
            register(new NumberSetting<>("HelpingBlocks", 1, 0, 5))
                .setComplexity(Complexity.Medium);
    protected final Setting<Float> obbyMinDmg =
            register(new NumberSetting<>("Obby-MinDamage", 7.0f, 0.1f, 36.0f));
    protected final Setting<Boolean> terrainCalc =
            register(new BooleanSetting("TerrainCalc", true));
    protected final Setting<Boolean> obbySafety =
            register(new BooleanSetting("ObbySafety", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<RayTraceMode> obbyTrace =
            register(new EnumSetting<>("Obby-Raytrace", RayTraceMode.Fast))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> obbyTerrain =
            register(new BooleanSetting("Obby-Terrain", true))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> obbyPreSelf =
            register(new BooleanSetting("Obby-PreSelf", true))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> fastObby =
            register(new NumberSetting<>("Fast-Obby", 0, 0, 3))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> maxDiff =
            register(new NumberSetting<>("Max-Difference", 1, 0, 5))
                .setComplexity(Complexity.Expert);
    protected final Setting<Double> maxDmgDiff =
            register(new NumberSetting<>("Max-DamageDiff", 0.0, 0.0, 10.0))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> setState =
            register(new BooleanSetting("Client-Blocks", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<PlaceSwing> obbySwing =
            register(new EnumSetting<>("Obby-Swing", PlaceSwing.Once))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> obbyFallback = // TODO: not yet implemented
            register(new BooleanSetting("Obby-Fallback", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Rotate> obbyRotate =
            register(new EnumSetting<>("Obby-Rotate", Rotate.None));

    /* ---------------- Liquids Settings -------------- */
    protected final Setting<Boolean> interact =
            register(new BooleanSetting("Interact", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> inside =
            register(new BooleanSetting("Inside", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> lava =
            register(new BooleanSetting("Lava", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> water =
            register(new BooleanSetting("Water", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> liquidObby =
            register(new BooleanSetting("LiquidObby", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> liquidRayTrace =
            register(new BooleanSetting("LiquidRayTrace", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> liqDelay =
            register(new NumberSetting<>("LiquidDelay", 500, 0, 1000))
                .setComplexity(Complexity.Medium);
    protected final Setting<Rotate> liqRotate =
            register(new EnumSetting<>("LiquidRotate", Rotate.None))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> pickaxeOnly =
            register(new BooleanSetting("PickaxeOnly", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> interruptSpeedmine =
            register(new BooleanSetting("InterruptSpeedmine", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> setAir =
            register(new BooleanSetting("SetAir", true))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> absorb =
            register(new BooleanSetting("Absorb", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> requireOnGround =
            register(new BooleanSetting("RequireOnGround", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> ignoreLavaItems =
            register(new BooleanSetting("IgnoreLavaItems", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> sponges =
            register(new BooleanSetting("Sponges", false))
                .setComplexity(Complexity.Expert);

    /* ---------------- AntiTotem Settings -------------- */
    protected final Setting<Boolean> antiTotem =
            register(new BooleanSetting("AntiTotem", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Float> totemHealth =
            register(new NumberSetting<>("Totem-Health", 1.5f, 0.0f, 10.0f))
                .setComplexity(Complexity.Expert);
    protected final Setting<Float> minTotemOffset =
            register(new NumberSetting<>("Min-Offset", 0.5f, 0.0f, 5.0f))
                .setComplexity(Complexity.Expert);
    protected final Setting<Float> maxTotemOffset =
            register(new NumberSetting<>("Max-Offset", 2.0f, 0.0f, 5.0f))
                .setComplexity(Complexity.Expert);
    protected final Setting<Float> popDamage =
            register(new NumberSetting<>("Pop-Damage", 12.0f, 10.0f, 20.0f))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> totemSync =
            register(new BooleanSetting("TotemSync", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> forceAntiTotem =
            register(new BooleanSetting("Force-AntiTotem", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> forceSlow =
            register(new BooleanSetting("Force-Slow", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> syncForce =
            register(new BooleanSetting("Sync-Force", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> dangerForce =
            register(new BooleanSetting("Danger-Force", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> forcePlaceConfirm =
            register(new NumberSetting<>("Force-Place", 100, 0, 500))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> forceBreakConfirm =
            register(new NumberSetting<>("Force-Break", 100, 0, 500))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> attempts =
            register(new NumberSetting<>("Attempts", 500, 0, 10000))
                .setComplexity(Complexity.Expert);

    /* ---------------- Damage Sync -------------- */
    // TODO: HealthConfirm for DamageSync as well, adjust last damage dealt?
    protected final Setting<Boolean> damageSync =
            register(new BooleanSetting("DamageSync", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> preSynCheck =
            register(new BooleanSetting("Pre-SyncCheck", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> discreteSync =
            register(new BooleanSetting("Discrete-Sync", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> dangerSync =
            register(new BooleanSetting("Danger-Sync", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> placeConfirm =
            register(new NumberSetting<>("Place-Confirm", 250, 0, 500))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> breakConfirm =
            register(new NumberSetting<>("Break-Confirm", 250, 0, 500))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> syncDelay =
            register(new NumberSetting<>("SyncDelay", 500, 0, 500))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> surroundSync = // TODO: Implement this later
            register(new BooleanSetting("SurroundSync", true))
                .setComplexity(Complexity.Expert);

    /* ---------------- Extrapolation Settings -------------- */
    // TODO: make this not suck, keep in mind that
    //  we might not be able to place when target moves in a block!
    public final Setting<Integer> extrapol =
            register(new NumberSetting<>("Extrapolation", 0, 0, 50))
                .setComplexity(Complexity.Medium);
    public final Setting<Integer> bExtrapol =
            register(new NumberSetting<>("Break-Extrapolation", 0, 0, 50))
                .setComplexity(Complexity.Medium);
    public final Setting<Integer> blockExtrapol =
            register(new NumberSetting<>("Block-Extrapolation", 0, 0, 50))
                .setComplexity(Complexity.Medium);
    public final Setting<BlockExtrapolationMode> blockExtraMode =
            register(new EnumSetting<>("BlockExtraMode",
                                       BlockExtrapolationMode.Pessimistic))
                .setComplexity(Complexity.Expert);
    public final Setting<Boolean> doubleExtraCheck =
            register(new BooleanSetting("DoubleExtraCheck", true))
                .setComplexity(Complexity.Expert);

    public final Setting<Boolean> avgPlaceDamage =
            register(new BooleanSetting("AvgPlaceExtra", false))
                .setComplexity(Complexity.Expert);
    public final Setting<Double> placeExtraWeight =
        register(new NumberSetting<>("P-Extra-Weight", 1.0, 0.0, 5.0))
            .setComplexity(Complexity.Medium);
    public final Setting<Double> placeNormalWeight =
        register(new NumberSetting<>("P-Norm-Weight", 1.0, 0.0, 5.0))
            .setComplexity(Complexity.Medium);
    public final Setting<Boolean> avgBreakExtra =
            register(new BooleanSetting("AvgBreakExtra", false))
                .setComplexity(Complexity.Expert);
    public final Setting<Double> breakExtraWeight =
        register(new NumberSetting<>("B-Extra-Weight", 1.0, 0.0, 5.0))
            .setComplexity(Complexity.Medium);
    public final Setting<Double> breakNormalWeight =
        register(new NumberSetting<>("B-Norm-Weight", 1.0, 0.0, 5.0))
            .setComplexity(Complexity.Medium);
    public final Setting<Boolean> gravityExtrapolation =
        register(new BooleanSetting("Extra-Gravity", true))
            .setComplexity(Complexity.Expert);
    public final Setting<Double> gravityFactor =
        register(new NumberSetting<>("Gravity-Factor", 1.0, 0.0, 5.0))
            .setComplexity(Complexity.Expert);
    public final Setting<Double> yPlusFactor =
        register(new NumberSetting<>("Y-Plus-Factor", 1.0, 0.0, 5.0))
            .setComplexity(Complexity.Expert);
    public final Setting<Double> yMinusFactor =
        register(new NumberSetting<>("Y-Minus-Factor", 1.0, 0.0, 5.0))
            .setComplexity(Complexity.Expert);
    public final Setting<Boolean> selfExtrapolation =
            register(new BooleanSetting("SelfExtrapolation", false))
                .setComplexity(Complexity.Medium);

    /* ---------------- Predict Settings -------------- */
    protected final Setting<Boolean> idPredict =
            register(new BooleanSetting("ID-Predict", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> idOffset =
            register(new NumberSetting<>("ID-Offset", 1, 1, 10))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> idDelay =
            register(new NumberSetting<>("ID-Delay", 0, 0, 500))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> idPackets =
            register(new NumberSetting<>("ID-Packets", 1, 1, 10))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> godAntiTotem =
            register(new BooleanSetting("God-AntiTotem", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> holdingCheck =
            register(new BooleanSetting("Holding-Check", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> toolCheck =
            register(new BooleanSetting("Tool-Check", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<PlaceSwing> godSwing =
            register(new EnumSetting<>("God-Swing", PlaceSwing.Once))
                .setComplexity(Complexity.Expert);

    /* ---------------- Efficiency -------------- */
    protected final Setting<PreCalc> preCalc =
            register(new EnumSetting<>("Pre-Calc", PreCalc.None))
                .setComplexity(Complexity.Expert);
    protected final Setting<ExtrapolationType> preCalcExtra =
            register(new EnumSetting<>("PreCalcExtra", ExtrapolationType.Place))
                .setComplexity(Complexity.Expert);
    protected final Setting<Float> preCalcDamage =
            register(new NumberSetting<>("Pre-CalcDamage", 15.0f, 0.0f, 36.0f))
                .setComplexity(Complexity.Expert);

    /* ---------------- MultiThreading -------------- */
    protected final Setting<Boolean> multiThread =
            register(new BooleanSetting("MultiThread", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> smartPost =
            register(new BooleanSetting("Smart-Post", true))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> mainThreadThreads =
            register(new BooleanSetting("MainThreadThreads", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<RotationThread> rotationThread =
            register(new EnumSetting<>("RotationThread", RotationThread.Predict))
                .setComplexity(Complexity.Expert);
    protected final Setting<Float> partial =
            register(new NumberSetting<>("Partial", 0.8f, 0.0f, 1.0f))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> maxCancel =
            register(new NumberSetting<>("MaxCancel", 10, 1, 50))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> timeOut =
            register(new NumberSetting<>("Wait", 2, 1, 10))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> blockDestroyThread =
            register(new BooleanSetting("BlockDestroyThread", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> threadDelay =
            register(new NumberSetting<>("ThreadDelay", 25, 0, 100))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> tickThreshold =
            register(new NumberSetting<>("TickThreshold", 5, 1, 20))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> preSpawn =
            register(new NumberSetting<>("PreSpawn", 3, 1, 20))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> maxEarlyThread =
            register(new NumberSetting<>("MaxEarlyThread", 8, 1, 20))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> pullBasedDelay =
            register(new NumberSetting<>("PullBasedDelay", 0, 0, 1000))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> explosionThread =
            register(new BooleanSetting("ExplosionThread", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> soundThread =
            register(new BooleanSetting("SoundThread", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> entityThread =
            register(new BooleanSetting("EntityThread", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> spawnThread =
            register(new BooleanSetting("SpawnThread", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> spawnThreadWhenAttacked =
            register(new BooleanSetting("SpawnThreadWhenAttacked", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> destroyThread =
            register(new BooleanSetting("DestroyThread", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> serverThread =
            register(new BooleanSetting("ServerThread", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> gameloop =
        register(new BooleanSetting("Gameloop", false))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> asyncServerThread =
            register(new BooleanSetting("AsyncServerThread", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> earlyFeetThread =
            register(new BooleanSetting("EarlyFeetThread", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> lateBreakThread =
            register(new BooleanSetting("LateBreakThread", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> motionThread =
            register(new BooleanSetting("MotionThread", true))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> blockChangeThread =
            register(new BooleanSetting("BlockChangeThread", false))
                .setComplexity(Complexity.Medium);

    /* ---------------- Dev and Debugging -------------- */
    protected final Setting<Integer> priority =
            register(new NumberSetting<>("Priority", 1500, Integer.MIN_VALUE,
                    Integer.MAX_VALUE))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> spectator =
            register(new BooleanSetting("Spectator", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> noPacketFlyRotationChecks =
            register(new BooleanSetting("NoPacketFlyRotationChecks", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> clearPost =
            register(new BooleanSetting("ClearPost", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> sequential =
            register(new BooleanSetting("Sequential", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> seqTime =
            register(new NumberSetting<>("Seq-Time", 250, 0, 1000))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> endSequenceOnSpawn =
            register(new BooleanSetting("EndSequenceOnSpawn", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> endSequenceOnBreak =
            register(new BooleanSetting("EndSequenceOnBreak", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> endSequenceOnExplosion =
            register(new BooleanSetting("EndSequenceOnExplosion", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> antiPlaceFail =
            register(new BooleanSetting("AntiPlaceFail", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> debugAntiPlaceFail =
            register(new BooleanSetting("DebugAntiPlaceFail", false))
                .setComplexity(Complexity.Dev);
    protected final Setting<Boolean> alwaysBomb =
            register(new BooleanSetting("Always-Bomb", false))
                .setComplexity(Complexity.Expert);
    public final Setting<Boolean> useSafetyFactor =
            register(new BooleanSetting("UseSafetyFactor", false))
                .setComplexity(Complexity.Expert);
    public final Setting<Double> selfFactor =
            register(new NumberSetting<>("SelfFactor", 1.0, 0.0, 10.0))
                .setComplexity(Complexity.Expert);
    public final Setting<Double> safetyFactor =
            register(new NumberSetting<>("SafetyFactor", 1.0, 0.0, 10.0))
                .setComplexity(Complexity.Expert);
    public final Setting<Double> compareDiff =
            register(new NumberSetting<>("CompareDiff", 1.0, 0.0, 10.0))
                .setComplexity(Complexity.Expert);
    public final Setting<Boolean> facePlaceCompare =
            register(new BooleanSetting("FacePlaceCompare", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> removeTime =
            register(new NumberSetting<>("Remove-Time", 1000, 0, 2500))
                .setComplexity(Complexity.Expert);

    /* ---------------- Fields -------------- */
    public final Map<BlockPos, CrystalTimeStamp> placed =
            new ConcurrentHashMap<>();
    protected final ListenerSound soundObserver =
            new ListenerSound(this);
    protected final AtomicInteger motionID =
            new AtomicInteger();

    /* ---------------- Timers -------------- */
    protected final DiscreteTimer placeTimer =
            new GuardTimer(1000, 5).reset(placeDelay.getValue());
    protected final DiscreteTimer breakTimer =
            new GuardTimer(1000, 5).reset(breakDelay.getValue());
    protected final StopWatch renderTimer = new StopWatch();
    protected final StopWatch bypassTimer = new StopWatch();
    protected final StopWatch obbyTimer = new StopWatch();
    protected final StopWatch obbyCalcTimer = new StopWatch();
    protected final StopWatch targetTimer = new StopWatch();
    protected final StopWatch cTargetTimer = new StopWatch();
    protected final StopWatch forceTimer = new StopWatch();
    protected final StopWatch liquidTimer = new StopWatch();
    protected final StopWatch shieldTimer = new StopWatch();
    protected final StopWatch slideTimer = new StopWatch();
    protected final StopWatch zoomTimer = new StopWatch();
    protected final StopWatch pullTimer = new StopWatch();

    /* ---------------- States -------------- */
    protected final Queue<Runnable> post = new ConcurrentLinkedQueue<>();
    protected volatile RotationFunction rotation;
    private BlockPos bypassPos;
    public BlockPos bombPos;
    protected EntityPlayer target;
    protected Entity crystal;
    protected Entity focus;
    protected BlockPos renderPos;
    protected BlockPos slidePos;
    public boolean switching;
    protected boolean isSpoofing;
    protected boolean noGod;
    protected String damage;

    /* ---------------- Helpers -------------- */
    protected final ExtrapolationHelper extrapolationHelper =
            new ExtrapolationHelper(this);

    public final HelperSequential sequentialHelper =
            new HelperSequential(this);

    // TODO: static
    protected final IDHelper idHelper =
            new IDHelper(basePlaceOnly);

    protected final HelperLiquids liquidHelper =
            new HelperLiquids(this);

    protected final HelperPlace placeHelper =
            new HelperPlace(this);

    protected final HelperBreak breakHelper =
            new HelperBreak(this);

    protected final HelperObby obbyHelper =
            new HelperObby(this);

    protected final HelperBreakMotion breakHelperMotion =
            new HelperBreakMotion(this);

    protected final AntiTotemHelper antiTotemHelper =
            new AntiTotemHelper(totemHealth);

    protected final WeaknessHelper weaknessHelper =
            new WeaknessHelper(antiWeakness, cooldown);

    protected final RotationCanceller rotationCanceller =
            new RotationCanceller(this, maxCancel);

    public HelperEntityBlocksPlace bbBlockingHelper =
        new HelperEntityBlocksPlace(this);

    protected final ThreadHelper threadHelper =
            new ThreadHelper(this,
                             multiThread,
                             mainThreadThreads,
                             threadDelay,
                             rotationThread,
                             rotate);

    protected final DamageHelper damageHelper =
            new DamageHelper(this,
                             extrapolationHelper,
                             terrainCalc,
                             extrapol,
                             bExtrapol,
                             selfExtrapolation,
                             obbyTerrain);

    protected final DamageSyncHelper damageSyncHelper =
            new DamageSyncHelper(Bus.EVENT_BUS,
                    discreteSync,
                    syncDelay,
                    dangerSync);

    protected final ForceAntiTotemHelper forceHelper =
            new ForceAntiTotemHelper(Bus.EVENT_BUS,
                    discreteSync,
                    syncDelay,
                    forcePlaceConfirm,
                    forceBreakConfirm,
                    dangerForce);

    protected final FakeCrystalRender crystalRender =
            new FakeCrystalRender(simulatePlace);

    public final HelperRotation rotationHelper =
            new HelperRotation(this);

    protected final ServerTimeHelper serverTimeHelper =
            new ServerTimeHelper(this,
                    rotate,
                    placeSwing,
                    antiFeetPlace,
                    newVer,
                    feetBuffer);

    public final HelperRange rangeHelper =
            new HelperRange(this);

    public AutoCrystal() {
        this("AutoCrystal", Category.Combat);
    }

    public AutoCrystal(String name, Category category) {
        super(name, category);
        Bus.EVENT_BUS.subscribe(idHelper);
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerBlockMulti(this));
        this.listeners.add(new ListenerDestroyEntities(this));
        this.listeners.add(new ListenerExplosion(this));
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerKeyboard(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerNoMotion(this));
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.add(new ListenerPostPlace(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerRenderEntities(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerDestroyBlock(this));
        this.listeners.add(new ListenerUseEntity(this));
        this.listeners.addAll(new ListenerCPlayers(this).getListeners());
        this.listeners.addAll(new ListenerEntity(this).getListeners());
        this.listeners.addAll(extrapolationHelper.getListeners());
        this.listeners.addAll(sequentialHelper.getListeners());

        new PageBuilder<>(this, pages)
                .addPage(p -> p == ACPages.Place, place, simulatePlace)
                .addPage(p -> p == ACPages.Break, attackMode, breakSwing)
                .addPage(p -> p == ACPages.Rotate, rotate, pingExisted)
                .addPage(p -> p == ACPages.Misc, targetRange, motionCalc)
                .addPage(p -> p == ACPages.FacePlace, holdFacePlace, fallBackDmg)
                .addPage(p -> p == ACPages.Switch, autoSwitch, obbyHand)
                .addPage(p -> p == ACPages.Render, render, renderMode)
                .addPage(p -> p == ACPages.SetDead, setDead, deathTime)
                .addPage(p -> p == ACPages.Obsidian, obsidian, obbyRotate)
                .addPage(p -> p == ACPages.Liquids, interact, sponges)
                .addPage(p -> p == ACPages.AntiTotem, antiTotem, attempts)
                .addPage(p -> p == ACPages.DamageSync, damageSync, surroundSync)
                .addPage(p -> p == ACPages.Extrapolation, extrapol, selfExtrapolation)
                .addPage(p -> p == ACPages.GodModule, idPredict, godSwing)
                .addPage(p -> p == ACPages.MultiThread, preCalc, blockChangeThread)
                .addPage(p -> p == ACPages.Development, priority, removeTime)
                .register(Visibilities.VISIBILITY_MANAGER);

        boolean start = false;
        for (Setting<?> setting : this.getSettings()) {
            if (setting == this.pages) {
                start = true;
                continue;
            }

            if (start) {
                Visibilities.VISIBILITY_MANAGER.registerVisibility(
                    setting, Visibilities.orComposer(
                        () -> SettingsModule.COMPLEXITY.getValue()
                            == Complexity.Beginner));
            }
        }

        // Need to re-register the listeners for it to take effect
        this.priority.addObserver(e ->
        {
            if (Bus.EVENT_BUS.isSubscribed(this)) {
                Bus.EVENT_BUS.unsubscribe(this);
                Bus.EVENT_BUS.subscribe(this);
            }
        });

        this.setData(new AutoCrystalData(this));
    }

    @Override
    protected void onEnable() {
        reset();
        Managers.SET_DEAD.addObserver(this.soundObserver);
    }

    @Override
    protected void onDisable() {
        Managers.SET_DEAD.removeObserver(this.soundObserver);
        reset();
    }

    @Override
    public String getDisplayInfo() {
        if (switching) {
            return TextColor.GREEN + "Switching";
        }

        EntityPlayer t = getTarget();
        return t == null ? null : t.getName();
    }

    public void setRenderPos(BlockPos pos, float damage) {
        setRenderPos(pos, MathUtil.round(damage, 1) + "");
    }

    public void setRenderPos(BlockPos pos, String text) {
        renderTimer.reset();
        if (pos != null && !pos.equals(slidePos)
            && (!smoothSlide.getValue()
                || slideTimer.passed(slideTime.getValue()))) {
            slidePos = renderPos;
            slideTimer.reset();
        }

        if (pos != null && (multiZoom.getValue() || !pos.equals(renderPos))) {
            zoomTimer.reset();
        }

        this.renderPos = pos;
        this.damage = text;
        this.bypassPos = null;
    }

    public BlockPos getRenderPos() {
        if (renderTimer.passed(renderTime.getValue())) {
            renderPos = null;
            slidePos = null;
        }

        return renderPos;
    }

    /**
     * Sets the Target displayed in the Info and ESP.
     * Will have no effects on who's getting targeted.
     *
     * @param target the target.
     */
    public void setTarget(EntityPlayer target) {
        this.targetTimer.reset();
        this.target = target;
    }

    /**
     * @return the currently targeted player.
     */
    public EntityPlayer getTarget()
    {
        if (targetTimer.passed(600))
        {
            target = null;
        }

        return target;
    }

    public void setCrystal(Entity crystal)
    {
        if (focusRotations.getValue()
                && !rotate.getValue().noRotate(ACRotate.Break))
        {
            focus = crystal;
        }

        this.cTargetTimer.reset();
        this.crystal = crystal;
    }

    /**
     * @return the currently targeted crystal.
     */
    public Entity getCrystal()
    {
        if (cTargetTimer.passed(600))
        {
            crystal = null;
        }

        return crystal;
    }

    /**
     * @return <tt>true</tt> if PingBypass is
     * enabled and the pingBypass setting is on.
     */
    public boolean isPingBypass()
    {
        return pingBypass.getValue() && PINGBYPASS.isEnabled();
    }

    /**
     * @return minDamage used for Calculation.
     * Normally @link CrystalAura#minDamage}.
     */
    public float getMinDamage()
    {
        // We could also check if we are mining webs with our sword.
        return holdFacePlace.getValue()
                && mc.currentScreen == null
                && Mouse.isButtonDown(0)
                && (!(mc.player.getHeldItemMainhand().getItem()
                            instanceof ItemPickaxe)
                    || pickAxeHold.getValue())
                || dangerFacePlace.getValue() && !Managers.SAFETY.isSafe()
                        ? minFaceDmg.getValue()
                        : minDamage.getValue();
    }

    /**
     * Runs all Runnables in {@link AutoCrystal#post}.
     */
    public void runPost()
    {
        CollectionUtil.emptyQueue(post);
    }

    /**
     * Resets all fields and helpers.
     */
    protected void reset()
    {
        target = null;
        crystal = null;
        renderPos = null;
        slidePos = null;
        rotation = null;
        switching = false;
        bypassPos = null;
        post.clear();
        mc.addScheduledTask(crystalRender::clear);

        try
        {
            placed.clear();
            threadHelper.reset();
            rotationCanceller.reset();
            antiTotemHelper.setTarget(null);
            antiTotemHelper.setTargetPos(null);
            idHelper.setUpdated(false);
            idHelper.setHighestID(0);
        }
        catch (Throwable t) // Possible since MultiThread stuff...
        {
            t.printStackTrace();
        }
    }

    protected boolean shouldDanger()
    {
        return dangerSpeed.getValue()
                && (!Managers.SAFETY.isSafe()
                || EntityUtil.getHealth(mc.player) < dangerHealth.getValue());
    }

    /**
     * This guarantees that the Executor is only started once!
     * Could probably also package this as Observers for the
     * 4 settings we check but too much work.
     */
    protected void checkExecutor()
    {
        // we use "started" here cause its faster than the atomic one
        if (!started
            && asyncServerThread.getValue()
            && serverThread.getValue()
            && multiThread.getValue()
            && rotate.getValue() == ACRotate.None)
        {
            synchronized (AutoCrystal.class)
            {
                if (!ATOMIC_STARTED.get()) // check again this time volatile
                {
                    startExecutor();
                    ATOMIC_STARTED.set(true);
                    started = true;
                }
            }
        }
    }

    private void startExecutor()
    {
        // Start Executor
        EXECUTOR.scheduleAtFixedRate(
            (SafeRunnable) this::doExecutorTick, 0, 1, TimeUnit.MILLISECONDS);
    }

    private void doExecutorTick()
    {
        if (this.isEnabled()
                && mc.player != null
                && mc.world != null
                && asyncServerThread.getValue()
                && rotate.getValue() == ACRotate.None
                && serverThread.getValue()
                && multiThread.getValue())
        {
            if (Managers.TICK.valid(
                    Managers.TICK.getTickTimeAdjusted(),
                    Managers.TICK.normalize(Managers.TICK.getSpawnTime()
                            - tickThreshold.getValue()),
                    Managers.TICK.normalize(Managers.TICK.getSpawnTime()
                            - preSpawn.getValue())))
            {
                if (!earlyFeetThread.getValue())
                {
                    threadHelper.startThread();
                }
                else if (lateBreakThread.getValue())
                {
                    threadHelper.startThread(true, false);
                }
            }
            else
            {
                EntityPlayer closest = EntityUtil.getClosestEnemy();
                if (closest != null
                    && BlockUtil.isSemiSafe(closest, true, newVer.getValue())
                    && BlockUtil.canBeFeetPlaced(closest, true,
                                                 newVer.getValue())
                    && earlyFeetThread.getValue()
                    && Managers.TICK.valid(Managers.TICK.getTickTimeAdjusted(),
                                           0, maxEarlyThread.getValue()))
                {
                    threadHelper.startThread(false, true);
                }
            }
        }
    }

    public boolean isNotCheckingRotations() {
        return noPacketFlyRotationChecks.getValue()
            && (PingBypass.PACKET_SERVICE.isPacketFlying()
                || PACKET_FLY.isEnabled());
    }

    /**
     * {@link SuicideAutoCrystal}
     */
    public boolean isSuicideModule() {
        return false;
    }

    public BlockPos getBypassPos() {
        if (bypassTimer.passed(bypassRotationTime.getValue())
            || !forceBypass.getValue()
            || !rayTraceBypass.getValue()) {
            bypassPos = null;
        }

        return bypassPos;
    }

    public void setBypassPos(BlockPos pos) {
        bypassTimer.reset();
        this.bypassPos = pos;
    }

    public boolean isEating() {
        ItemStack stack = mc.player.getActiveItemStack();
        return mc.player.isHandActive()
            && !stack.isEmpty()
            && stack.getItem().getItemUseAction(stack) == EnumAction.EAT;
    }

    public boolean isMining() {
        return mc.playerController.getIsHittingBlock();
    }

    public boolean isOutsidePlaceRange(BlockPos pos) {
        EntityPlayer player = RotationUtil.getRotationPlayer();
        double x = player.posX;
        double y = player.posY + (placeRangeEyes.getValue() ? player.getEyeHeight() : 0);
        double z = player.posZ;
        double distance = placeRangeCenter.getValue() ? pos.distanceSqToCenter(x, y, z) : pos.distanceSq(x, y, z);
        return distance >= MathUtil.square(placeRange.getValue());
    }

    public int getDeathTime() {
        if (useSafeDeathTime.getValue() && Managers.SAFETY.isSafe()) {
            return safeDeathTime.getValue();
        }

        return deathTime.getValue();
    }

}
