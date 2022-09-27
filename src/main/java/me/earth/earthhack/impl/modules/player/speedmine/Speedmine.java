package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.core.ducks.network.ICPacketPlayerDigging;
import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autotrap.AutoTrap;
import me.earth.earthhack.impl.modules.player.automine.AutoMine;
import me.earth.earthhack.impl.modules.player.speedmine.mode.ESPMode;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.blocks.states.IBlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

import static net.minecraft.network.play.client.CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK;
import static net.minecraft.network.play.client.CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK;

// TODO Tps Sync
// TODO Test around with multiple blocks
// TODO: Rewrite!
public class Speedmine extends Module
{
    private static final ModuleCache<AutoMine> AUTO_MINE =
            Caches.getModule(AutoMine.class);
    private static final ModuleCache<AutoTrap> AUTO_TRAP =
        Caches.getModule(AutoTrap.class);

    protected final Setting<MineMode> mode     =
            register(new EnumSetting<>("Mode", MineMode.Smart));
    protected final Setting<Boolean> noReset   =
            register(new BooleanSetting("Reset", true));
    public final Setting<Float> limit       =
            register(new NumberSetting<>("Damage", 1.0f, 0.0f, 2.0f))
                .setComplexity(Complexity.Medium);
    protected final Setting<Float> range       =
            register(new NumberSetting<>("Range", 7.0f, 0.1f, 100.0f));
    protected final Setting<Boolean> multiTask =
            register(new BooleanSetting("MultiTask", false));
    protected final Setting<Boolean> rotate    =
            register(new BooleanSetting("Rotate", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> event     =
            register(new BooleanSetting("Event", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> display   =
            register(new BooleanSetting("DisplayDamage", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> delay     =
            register(new NumberSetting<>("ClickDelay", 100, 0, 500))
                .setComplexity(Complexity.Medium);
    protected final Setting<ESPMode> esp       =
            register(new EnumSetting<>("ESP", ESPMode.Outline))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> alpha     =
            register(new NumberSetting<>("BlockAlpha", 100, 0, 255))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> outlineA  =
            register(new NumberSetting<>("OutlineAlpha", 100, 0, 255))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> realDelay =
            register(new NumberSetting<>("Delay", 50, 0, 500))
                .setComplexity(Complexity.Medium);
    public final Setting<Boolean> onGround  = // this should be true?
            register(new BooleanSetting("OnGround", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> toAir     =
            register(new BooleanSetting("ToAir", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> swap      =
            register(new BooleanSetting("SilentSwitch", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<CooldownBypass> cooldownBypass =
            register(new EnumSetting<>("CoolDownBypass", CooldownBypass.None))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> requireBreakSlot      =
            register(new BooleanSetting("RequireBreakSlot", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> placeCrystal =
            register(new BooleanSetting("PlaceCrystal", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> breakCrystal =
            register(new BooleanSetting("BreakCrystal", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> breakInstant =
            register(new BooleanSetting("BreakSpawningCrystals", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> offhandPlace =
            register(new BooleanSetting("OffhandPlace", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> offhandSilent =
            register(new BooleanSetting("OffhandSilent", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> megaSilent =
            register(new BooleanSetting("MegaSilent", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> antiAntiSilentSwitch =
            register(new BooleanSetting("AntiAntiSilentSwitch", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> prePlace =
            register(new BooleanSetting("PrePlace", false))
                .setComplexity(Complexity.Medium);
    public final Setting<Float> prePlaceLimit       =
            register(new NumberSetting<>("PrePlaceLimit", 0.95f, 0.0f, 2.0f))
                .setComplexity(Complexity.Medium);
    protected final Setting<Bind> breakBind =
            register(new BindSetting("BreakBind", Bind.none()))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> normal     =
            register(new BooleanSetting("Normal", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> resetAfterPacket =
            register(new BooleanSetting("ResetAfterPacket", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> checkPacket =
            register(new BooleanSetting("CheckPacket", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> swingStop =
            register(new BooleanSetting("Swing-Stop", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> limitRotations =
            register(new BooleanSetting("Limit-Rotations", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> confirm =
            register(new NumberSetting<>("Confirm", 500, 0, 1000))
                .setComplexity(Complexity.Medium);
    protected final Setting<Double> crystalRange =
            register(new NumberSetting<>("CrystalRange", 6.0, 0.0, 10.0))
                .setComplexity(Complexity.Medium);
    protected final Setting<Double> crystalTrace =
            register(new NumberSetting<>("CrystalTrace", 6.0, 0.0, 10.0))
                .setComplexity(Complexity.Expert);
    protected final Setting<Double> crystalBreakTrace =
            register(new NumberSetting<>("CrystalTrace", 3.0, 0.0, 10.0))
                .setComplexity(Complexity.Expert);
    protected final Setting<Double> minDmg =
            register(new NumberSetting<>("MinDamage", 10.0, 0.0, 36.0))
                .setComplexity(Complexity.Medium);
    protected final Setting<Double> maxSelfDmg =
            register(new NumberSetting<>("MaxSelfDamage", 10.0, 0.0, 36.0))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> newVer =
            register(new BooleanSetting("1.13", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> newVerEntities =
            register(new BooleanSetting("1.13-Entities", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> growRender =
            register(new BooleanSetting("GrowRender", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Color> pbColor  =
            register(new ColorSetting("PB-Color", new Color(0, 255, 0, 240)))
                .setComplexity(Complexity.Expert);
    protected final Setting<Color> pbOutline  =
            register(new ColorSetting("PB-Outline", new Color(0, 255, 0, 120)))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> down =
            register(new BooleanSetting("Down", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> tpsSync =
            register(new BooleanSetting("TpsSync", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> abortNextTick =
            register(new BooleanSetting("AbortNextTick", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> cancelNormalPackets =
            register(new BooleanSetting("CancelNormalPackets", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> cancelClick =
            register(new BooleanSetting("CancelClick", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> cancelEvent =
            register(new BooleanSetting("CancelEvent", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> aASSwitchTime =
            register(new NumberSetting<>("AASSwitchTime", 500, 0, 1000))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> resetFastOnAir     =
            register(new BooleanSetting("ResetFastOnAir", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> resetFastOnNonAir     =
            register(new BooleanSetting("ResetFastOnNonAir", false))
                .setComplexity(Complexity.Expert);

    protected final FastHelper fastHelper = new FastHelper(this);
    public final CrystalHelper crystalHelper = new CrystalHelper(this);
    protected final OngroundHistoryHelper ongroundHistoryHelper =
        new OngroundHistoryHelper();

    /**
     * Damage dealt to block for each hotbarSlot.
     */
    public final float[] damages =
            new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    /**
     * A StopWatch to handle ClickDelay.
     */
    protected final StopWatch timer = new StopWatch();
    /**
     * A StopWatch to handle Resetting after sending a Packet.
     */
    protected final StopWatch resetTimer = new StopWatch();
    protected final StopWatch aASSSwitchTimer = new StopWatch();
    /**
     * The Pos we are currently mining.
     */
    protected BlockPos pos;
    /**
     * The facing we hit the current pos int.
     */
    protected EnumFacing facing;
    /**
     * Cached boundingBox for the currentPos.
     */
    protected AxisAlignedBB bb;
    /**
     * Rotations to the current pos.
     */
    protected float[] rotations;
    /**
     * Maximum damage dealt to the current Pos.
     */
    public float maxDamage;
    /**
     * <tt>true</tt> if we sent the STOP_DESTROY packet.
     */
    protected boolean sentPacket;
    /**
     * true if we should send an abort packet
     */
    protected boolean shouldAbort;
    /**
     * true if the module should not send destroy block packets right now
     */
    protected boolean pausing;
    /**
     * timer for delays
     */
    protected final StopWatch delayTimer = new StopWatch();
    /**
     * Packet to send after we limited our rotations.
     */
    protected Packet<?> limitRotationPacket;
    /**
     * Slot for LimitRotations.
     */
    protected int limitRotationSlot = -1;

    public Speedmine()
    {
        super("Speedmine", Category.Player);
        Bus.EVENT_BUS.subscribe(ongroundHistoryHelper);
        this.listeners.add(new ListenerDamage(this));
        this.listeners.add(new ListenerReset(this));
        this.listeners.add(new ListenerClick(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerMultiBlockChange(this));
        this.listeners.add(new ListenerDeath(this));
        this.listeners.add(new ListenerLogout(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerDigging(this));
        this.listeners.add(new ListenerKeyPress(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.setData(new SpeedMineData(this));
    }

    @Override
    protected void onEnable()
    {
        reset();
    }

    @Override
    public String getDisplayInfo()
    {
        if (display.getValue()
            && (mode.getValue() == MineMode.Smart
            || mode.getValue() == MineMode.Fast))
        {
            return (maxDamage >= limit.getValue()
                    ? TextColor.GREEN + MathUtil.round(limit.getValue(), 1)
                    : "" + MathUtil.round(maxDamage, 1));
        }

        return mode.getValue().toString();
    }

    /**
     * Sends an ABORT_DESTROY_BLOCK CPacketPlayerDigging for the
     * current pos and resets the playerController.
     */
    public void abortCurrentPos()
    {
        AUTO_MINE.computeIfPresent(a -> a.addToBlackList(pos));
        mc.player.connection.sendPacket(new CPacketPlayerDigging(
                                CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK,
                                pos,
                                facing));

        ((IPlayerControllerMP) mc.playerController).setIsHittingBlock(false);
        ((IPlayerControllerMP) mc.playerController).setCurBlockDamageMP(0.0f);
        mc.world.sendBlockBreakProgress(this.mc.player.getEntityId(), pos, -1);
        mc.player.resetCooldown();
        reset();
    }

    /**
     * Resets the current pos and all damages dealt to it.
     */
    public void reset()
    {
        pos    = null;
        facing = null;
        bb     = null;
        maxDamage  = 0.0f;
        sentPacket = false;
        limitRotationSlot = -1;
        limitRotationPacket = null;
        fastHelper.reset();
        AUTO_MINE.computeIfPresent(AutoMine::reset);

        for (int i = 0; i < 9; i++)
        {
            damages[i] = 0.0f;
        }
    }

    /**
     * Returns the current mode.
     *
     * @return a MineMode.
     */
    public MineMode getMode()
    {
        return mode.getValue();
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public StopWatch getTimer()
    {
        return timer;
    }

    public float getRange()
    {
        return range.getValue();
    }

    public int getBlockAlpha() {
        return alpha.getValue();
    }

    public int getOutlineAlpha() {
        return outlineA.getValue();
    }

    public boolean isPausing() {
        return pausing;
    }

    public void setPausing(boolean pausing) {
        this.pausing = pausing;
    }

    protected boolean sendStopDestroy(BlockPos pos,
                                      EnumFacing facing,
                                      boolean toAir)
    {
        return sendStopDestroy(pos, facing, toAir, true);
    }

    protected boolean sendStopDestroy(BlockPos pos,
                                      EnumFacing facing,
                                      boolean toAir,
                                      boolean withRotations)
    {
        CPacketPlayerDigging stop  =
                new CPacketPlayerDigging(
                        CPacketPlayerDigging
                            .Action
                            .STOP_DESTROY_BLOCK,
                        pos,
                        facing);

        if (toAir)
        {
            //noinspection ConstantConditions
            ((ICPacketPlayerDigging) stop).setClientSideBreaking(true);
        }

        if (withRotations
                && rotate.getValue()
                && limitRotations.getValue()
                && !RotationUtil.isLegit(pos, facing))
        {
            limitRotationPacket = stop;
            limitRotationSlot = mc.player.inventory.currentItem;
            return false;
        }

        if (event.getValue())
        {
            mc.player.connection.sendPacket(stop);
        }
        else
        {
            NetworkUtil.sendPacketNoEvent(stop, false);
        }

        if (mode.getValue() == MineMode.Fast)
        {
            fastHelper.sendAbortStart(pos, facing);
        }

        onSendPacket();
        return true;
    }

    protected void postSend(boolean toAir)
    {
        if (placeCrystal.getValue())
        {
            AUTO_TRAP.computeIfPresent(autoTrap -> autoTrap.blackList
                .put(pos, System.currentTimeMillis()));
        }

        if (swingStop.getValue())
        {
            Swing.Packet.swing(EnumHand.MAIN_HAND);
        }

        if (toAir)
        {
            mc.playerController.onPlayerDestroyBlock(pos);
        }

        if (breakCrystal.getValue())
        {
            IBlockStateHelper helper = new BlockStateHelper();
            helper.addAir(pos);
            for (Entity crystal : mc.world.loadedEntityList)
            {
                double distance;
                if (crystal instanceof EntityEnderCrystal
                    && !EntityUtil.isDead(crystal)
                    && (distance = mc.player.getDistanceSq(crystal)) < MathUtil.square(crystalRange.getValue())
                    && (mc.player.canEntityBeSeen(crystal) || distance < MathUtil.square(crystalBreakTrace.getValue())))
                {
                    float selfDamage = DamageUtil.calculate(crystal, mc.player, helper);
                    if (selfDamage < EntityUtil.getHealth(mc.player) && selfDamage < maxSelfDmg.getValue())
                    {
                        for (EntityPlayer player : mc.world.playerEntities)
                        {
                            float damage = DamageUtil.calculate(crystal, player, helper);
                            if (damage >= minDmg.getValue())
                            {
                                PacketUtil.attack(crystal);
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (resetAfterPacket.getValue() && mode.getValue() != MineMode.Fast)
        {
            reset();
        }
    }

    public void forceSend()
    {
        if (pos != null)
        {
            if (mode.getValue() == MineMode.Instant)
            {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(
                            STOP_DESTROY_BLOCK, pos, facing));
                sendStopDestroy(pos, facing, false);
                if (mode.getValue() == MineMode.Instant)
                {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(
                            ABORT_DESTROY_BLOCK, pos, facing));
                }
            }
            else if (mode.getValue() == MineMode.Civ)
            {
                sendStopDestroy(pos, facing, false);
            }
        }
    }

    public void tryBreak() {
        int breakSlot;
        if (!pausing && ((breakSlot = findBreakSlot()) != -1 || requireBreakSlot.getValue())) {
            boolean toAir = this.toAir.getValue();
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
            {
                int lastSlot = mc.player.inventory.currentItem;
                if (breakSlot != -1) {
                    cooldownBypass.getValue().switchTo(breakSlot);
                }

                CPacketPlayerDigging packet =
                    new CPacketPlayerDigging(
                        CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                        pos,
                        facing);

                if (toAir)
                {
                    //noinspection ConstantConditions
                    ((ICPacketPlayerDigging) packet)
                            .setClientSideBreaking(true);
                }

                NetworkUtil.sendPacketNoEvent(packet, false);
                if (breakSlot != -1) {
                    cooldownBypass.getValue().switchBack(lastSlot, breakSlot);
                }
            });

            if (toAir)
            {
                mc.playerController.onPlayerDestroyBlock(pos);
            }

            onSendPacket();
        }
    }

    private int findBreakSlot()
    {
        int slot = -1;
        for (int i = 0; i < damages.length; i++)
        {
            if (damages[i] >= limit.getValue()
                    && (slot = i) >= mc.player.inventory.currentItem)
            {
                return slot;
            }
        }

        return slot;
    }

    public void checkReset()
    {
        if (sentPacket
                && resetTimer.passed(confirm.getValue())
                && (mode.getValue() == MineMode.Packet
                    || mode.getValue() == MineMode.Smart))
        {
            reset();
        }
    }

    public void onSendPacket()
    {
        sentPacket = true;
        resetTimer.reset();
    }

    public void updateDamages()
    {
        maxDamage = 0.0f;
        for (int i = 0; i < 9; i++)
        {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            float damage = MineUtil.getDamage(stack, pos, onGround.getValue());
            if (tpsSync.getValue()) {
                damage *= Managers.TPS.getFactor();
            }

            damages[i] = MathUtil.clamp(damages[i] + damage, 0.0f, Float.MAX_VALUE);
            if (damages[i] > maxDamage)
            {
                maxDamage = damages[i];
            }
        }
    }

    public int getFastSlot()
    {
        int fastSlot = -1;
        for (int i = 0; i < damages.length; i++)
        {
            if (damages[i] >= limit.getValue())
            {
                fastSlot = i;
                if (i == mc.player.inventory.currentItem)
                {
                    break;
                }
            }
        }

        return fastSlot;
    }

    public void postCrystalPlace(int fastSlot, int lastSlot, boolean swap)
    {
        if (swap)
        {
            cooldownBypass.getValue().switchTo(fastSlot);
        }

        boolean toAir = this.toAir.getValue();
        InventoryUtil.syncItem();
        if (sendStopDestroy(pos, facing, toAir))
        {
            postSend(toAir);
        }

        if (swap)
        {
            cooldownBypass.getValue().switchBack(lastSlot, fastSlot);
        }
    }

    public boolean prePlaceCheck()
    {
        if (prePlace.getValue() && placeCrystal.getValue())
        {
            for (float damage : damages)
            {
                if (damage >= prePlaceLimit.getValue())
                {
                    return true;
                }
            }
        }

        return false;
    }

}
