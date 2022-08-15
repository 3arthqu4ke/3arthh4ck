package me.earth.earthhack.impl.modules.combat.legswitch;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.minecraft.combat.util.SoundObserver;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.legswitch.modes.LegAutoSwitch;
import me.earth.earthhack.impl.util.helpers.addable.ListType;
import me.earth.earthhack.impl.util.helpers.addable.RemovingItemAddingModule;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.helpers.blocks.modes.PlaceSwing;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.DiscreteTimer;
import me.earth.earthhack.impl.util.math.GuardTimer;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.path.BasePath;
import me.earth.earthhack.impl.util.math.path.PathFinder;
import me.earth.earthhack.impl.util.math.path.Pathable;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

import java.util.List;

// TODO: AutoMine compatibility
public class LegSwitch extends RemovingItemAddingModule
{
    private static final ModuleCache<AutoCrystal> AUTO_CRYSTAL =
            Caches.getModule(AutoCrystal.class);

    protected final Setting<LegAutoSwitch> autoSwitch =
        register(new EnumSetting<>("AutoSwitch", LegAutoSwitch.None));
    protected final Setting<Integer> delay =
        register(new NumberSetting<>("Delay", 500, 0, 500));
    protected final Setting<Boolean> closest =
        register(new BooleanSetting("Closest", true));
    protected final Setting<ACRotate> rotate =
        register(new EnumSetting<>("Rotate", ACRotate.None));
    protected final Setting<Float> minDamage =
        register(new NumberSetting<>("MinDamage", 7.0f, 0.0f, 36.0f));
    protected final Setting<Float> maxSelfDamage =
        register(new NumberSetting<>("MaxSelfDamage", 4.0f, 0.0f, 36.0f));
    protected final Setting<Float> placeRange =
        register(new NumberSetting<>("PlaceRange", 6.0f, 0.0f, 6.0f));
    protected final Setting<Float> placeTrace =
        register(new NumberSetting<>("PlaceTrace", 6.0f, 0.0f, 6.0f));
    protected final Setting<Float> breakRange =
        register(new NumberSetting<>("BreakRange", 6.0f, 0.0f, 6.0f));
    protected final Setting<Float> breakTrace =
        register(new NumberSetting<>("BreakTrace", 3.0f, 0.0f, 6.0f));
    protected final Setting<Float> combinedTrace =
        register(new NumberSetting<>("CombinedTrace", 3.0f, 0.0f, 6.0f));
    protected final Setting<Boolean> instant =
        register(new BooleanSetting("Instant", true));
    protected final Setting<Boolean> setDead =
        register(new BooleanSetting("SetDead", false));
    protected final Setting<Boolean> requireMid =
        register(new BooleanSetting("Mid", false));
    protected final Setting<Boolean> soundRemove =
        register(new BooleanSetting("SoundRemove", true));
    protected final Setting<Boolean> antiWeakness =
        register(new BooleanSetting("AntiWeakness", false));
    protected final Setting<Boolean> soundStart =
        register(new BooleanSetting("SoundStart", false));
    protected final Setting<Boolean> newVer =
        register(new BooleanSetting("1.13+", false));
    protected final Setting<Boolean> newVerEntities =
        register(new BooleanSetting("1.13-Entities", false));
    protected final Setting<Boolean> rotationPacket =
        register(new BooleanSetting("Rotation-Packet", false));
    protected final Setting<Integer> coolDown =
        register(new NumberSetting<>("CoolDown", 0, 0, 500));
    protected final Setting<Float> targetRange =
        register(new NumberSetting<>("Target-Range", 10.0f, 0.0f, 20.0f));
    protected final Setting<Boolean> breakBlock =
        register(new BooleanSetting("BlockStart", false));
    protected final Setting<Boolean> obsidian =
        register(new BooleanSetting("Obsidian", false));
    protected final Setting<Integer> helpingBlocks =
        register(new NumberSetting<>("HelpingBlocks", 1, 1, 10));
    protected final Setting<RayTraceMode> smartRay =
        register(new EnumSetting<>("Raytrace", RayTraceMode.Fast));
    protected final Setting<Rotate> obbyRotate =
        register(new EnumSetting<>("Obby-Rotate", Rotate.None));
    protected final Setting<Boolean> normalRotate =
        register(new BooleanSetting("NormalRotate", false));
    protected final Setting<Boolean> setBlockState =
        register(new BooleanSetting("SetBlockState", false));
    protected final Setting<PlaceSwing> obbySwing =
        register(new EnumSetting<>("ObbySwing", PlaceSwing.Once));

    protected final SoundObserver observer = new ListenerSound(this);
    protected final DiscreteTimer timer = new GuardTimer(500);
    protected LegConstellation constellation;
    protected volatile boolean active;
    protected BlockPos targetPos;
    protected float[] bRotations;
    protected float[] rotations;
    protected Runnable post;
    protected int slot;

    public LegSwitch()
    {
        super("LegSwitch", Category.Combat,
                s -> "Black/Whitelist LegSwitch" +
                        " from being active while you hold "
                            + s.getName() + ".");
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerBlockMulti(this));
        this.listeners.add(new ListenerBlockBreak(this));
        this.listType.setValue(ListType.BlackList);
        this.setData(new LegSwitchData(this));
    }

    @Override
    public void onEnable()
    {
        Managers.SET_DEAD.addObserver(observer);
    }

    @Override
    public void onDisable()
    {
        Managers.SET_DEAD.removeObserver(observer);
        active = false;
        constellation = null;
    }

    @Override
    public String getDisplayInfo()
    {
        return constellation == null || !active
                ? null
                : constellation.player.getName();
    }

    public boolean isActive()
    {
        return this.isEnabled() && active;
    }

    protected void startCalculation()
    {
        startCalculation(mc.world);
    }

    protected void startCalculation(IBlockAccess access)
    {
        if (!isStackValid(mc.player.getHeldItemOffhand())
                && !isStackValid(mc.player.getHeldItemMainhand()))
        {
            this.active = false;
            return;
        }

        if (this.constellation == null
                || !this.constellation.isValid(this, mc.player, access))
        {
            this.constellation = ConstellationFactory.create(this,
                    mc.world.playerEntities);

            if (this.constellation != null
                    && !obsidian.getValue()
                    && (this.constellation.firstNeedsObby
                        || this.constellation.secondNeedsObby))
            {
                this.constellation = null;
            }
        }

        if (this.constellation == null)
        {
            this.active = false;
        }

        this.active = true;
        this.prepare();
        this.execute();
    }

    protected boolean isValid(BlockPos pos,
                              IBlockState state,
                              List<EntityPlayer> players)
    {
        if (state.getBlock() != Blocks.AIR || players == null)
        {
            return false;
        }

        for (EntityPlayer player : players)
        {
            if (player != null
                && !Managers.FRIENDS.contains(player)
                && player.getDistanceSq(pos) < 4)
            {
                return true;
            }
        }

        return false;
    }

    protected void prepare()
    {
        if (!timer.passed(delay.getValue()))
        {
            return;
        }

        int weakSlot = -1;
        if (!DamageUtil.canBreakWeakness(true))
        {
            if (!antiWeakness.getValue()
                || coolDown.getValue() != 0
                || (weakSlot = DamageUtil.findAntiWeakness()) == -1)
            {
                return;
            }
        }

        if (constellation == null)
        {
            targetPos = null;
            return;
        }

        Entity crystal = null;
        for (Entity entity : mc.world.loadedEntityList)
        {
            if (entity instanceof EntityEnderCrystal
                    && !entity.isDead
                    && entity.getEntityBoundingBox()
                             .intersects(
                            new AxisAlignedBB(constellation.targetPos)))
            {
                crystal = entity;
                break;
            }
        }

        targetPos = constellation.firstPos;
        // use this variable to determine which boolean
        // in the constellation to set to false/true
        boolean firstNeedsObby = true;
        BlockPos obbyPos = constellation.firstNeedsObby
                                ? constellation.firstPos
                                : null;
        if (crystal != null)
        {
            if (Managers.SWITCH.getLastSwitch() < coolDown.getValue())
            {
                return;
            }

            if (crystal.getPosition().down().equals(constellation.firstPos))
            {
                obbyPos = constellation.secondNeedsObby
                                                   ? constellation.secondPos
                                                   : null;
                targetPos = constellation.secondPos;
                firstNeedsObby = false;
            }

            bRotations = RotationUtil.getRotations(crystal);
        }

        int obbySlot = -1;
        Pathable path = null;
        if (obbyPos != null)
        {
            obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
            if (obbySlot == -1)
            {
                return;
            }

            path = new BasePath(RotationUtil.getRotationPlayer(),
                                obbyPos,
                                helpingBlocks.getValue());

            boolean newVersion = newVer.getValue();
            BlockPos[] blacklist = new BlockPos[newVersion ? 4 : 6];
            blacklist[0] = constellation.playerPos;
            blacklist[1] = constellation.secondPos.up();
            blacklist[2] = constellation.firstPos.up();
            blacklist[3] = constellation.targetPos;
            if (!newVersion)
            {
                blacklist[4] = constellation.secondPos.up(2);
                blacklist[5] = constellation.firstPos.up(2);
            }

            PathFinder.findPath(
                path,
                placeRange.getValue(),
                mc.world.loadedEntityList,
                smartRay.getValue(),
                ObbyModule.HELPER,
                Blocks.OBSIDIAN.getDefaultState(),
                PathFinder.CHECK,
                blacklist);

            if (!path.isValid()
                || path.getPath().length > 1
                    && normalRotate.getValue()
                    && obbyRotate.getValue() == Rotate.Normal)
            {
                constellation.invalid = true;
                return;
            }
        }

        RayTraceResult result;
        assert targetPos != null;
        if (path != null)
        {
            rotations = path.getPath()[0].getRotations();
        }
        else
        {
            rotations = RotationUtil.getRotationsToTopMiddle(targetPos.up());
        }

        if (!rotate.getValue().noRotate(ACRotate.Place))
        {
            float[] theRotations = rotations;
            IBlockAccess access = mc.world;
            if (path != null)
            {
                // last block placed
                Ray last = path.getPath()[path.getPath().length - 1];
                theRotations = last.getRotations();
                BlockStateHelper helper = new BlockStateHelper();
                helper.addBlockState(last.getPos().offset(last.getFacing()),
                                     Blocks.OBSIDIAN.getDefaultState());
                access = helper;
            }

            BlockPos thePos = targetPos.up();
            result = RotationUtil.rayTraceWithYP(
                    thePos, access, theRotations[0], theRotations[1],
                    (b, p) -> p.equals(thePos));
        }
        else
        {
            result = new RayTraceResult(new Vec3d(0.5,1.0,0.5), EnumFacing.UP);
            rotations = null;
        }

        Entity finalCrystal = crystal;
        // fine, but place might be scheduled to post
        // which will make it wait quite a while?
        if (rotationPacket.getValue()
                && rotations != null
                && bRotations != null
                && finalCrystal != null)
        {
            int finalWeakSlot = weakSlot;
            Runnable runnable = () ->
            {
                mc.player.connection.sendPacket(
                        new CPacketPlayer.Rotation(
                                bRotations[0],
                                bRotations[1],
                                mc.player.onGround));

                int lastSlot = mc.player.inventory.currentItem;
                if (finalWeakSlot != -1)
                {
                    InventoryUtil.switchTo(finalWeakSlot);
                }

                mc.player.connection.sendPacket(
                        new CPacketUseEntity(finalCrystal));
                mc.player.connection.sendPacket(
                        new CPacketAnimation(EnumHand.MAIN_HAND));
                bRotations = null;

                InventoryUtil.switchTo(lastSlot);
            };

            if (finalWeakSlot != -1)
            {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, runnable);
            }
            else
            {
                runnable.run();
            }

            if (setDead.getValue())
            {
                Managers.SET_DEAD.setDead(finalCrystal);
            }
        }

        if (rotations == null)
        {
            rotations = bRotations;
        }

        Pathable finalPath = path;
        int finalObbySlot = obbySlot;
        boolean finalFirstNeedsObby = firstNeedsObby;
        LegConstellation finalConstellation = constellation;
        post = Locks.wrap(Locks.PLACE_SWITCH_LOCK, () ->
        {
            int slot = -1;
            int lastSlot = mc.player.inventory.currentItem;
            if (!InventoryUtil.isHolding(Items.END_CRYSTAL))
            {
                slot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);
                if (autoSwitch.getValue() == LegAutoSwitch.None || slot == -1)
                {
                    active = false;
                    return;
                }
            }

            EnumHand hand = mc.player.getHeldItemMainhand().getItem()
                                == Items.END_CRYSTAL || slot != -2
                    ? EnumHand.MAIN_HAND
                    : EnumHand.OFF_HAND;

            if (bRotations != null && finalCrystal != null)
            {
                mc.player.connection.sendPacket(
                        new CPacketUseEntity(finalCrystal));
                mc.player.connection.sendPacket(
                        new CPacketAnimation(EnumHand.MAIN_HAND));
            }

            if (finalPath != null)
            {
                InventoryUtil.switchTo(finalObbySlot);
                for (int i = 0; i < finalPath.getPath().length; i++)
                {
                    Ray ray = finalPath.getPath()[i];
                    if (i != 0 && obbyRotate.getValue() == Rotate.Packet)
                    {
                        Managers.ROTATION.setBlocking(true);
                        float[] r = ray.getRotations();
                        PingBypass.sendToActualServer(
                                PacketUtil.rotation(r[0], r[1],
                                        mc.player.onGround));
                        Managers.ROTATION.setBlocking(false);
                    }

                    float[] f = RayTraceUtil.hitVecToPlaceVec(
                            ray.getPos(), ray.getResult().hitVec);

                    mc.player.connection.sendPacket(
                        new CPacketPlayerTryUseItemOnBlock(
                            ray.getPos(),
                            ray.getFacing(),
                            hand,
                            f[0],
                            f[1],
                            f[2]));

                    if (setBlockState.getValue())
                    {
                        mc.addScheduledTask(() ->
                        {
                            if (mc.world != null)
                            {
                                finalConstellation.states.put(
                                        ray.getPos().offset(ray.getFacing()),
                                        Blocks.OBSIDIAN.getDefaultState());

                                mc.world.setBlockState(
                                        ray.getPos().offset(ray.getFacing()),
                                        Blocks.OBSIDIAN.getDefaultState());
                            }
                        });
                    }

                    if (obbySwing.getValue() == PlaceSwing.Always)
                    {
                        Swing.Packet.swing(hand);
                    }
                }

                Ray ray = finalPath.getPath()[finalPath.getPath().length - 1];
                BlockPos last = ray.getPos().offset(ray.getFacing());
                Managers.BLOCKS.addCallback(last, s ->
                {
                    if (s.getBlock() == Blocks.OBSIDIAN)
                    {
                        if (finalFirstNeedsObby)
                        {
                            finalConstellation.firstNeedsObby = false;
                        }
                        else
                        {
                            finalConstellation.secondNeedsObby = false;
                        }
                    }

                    finalConstellation.states.put(last, s);
                });

                if (obbySwing.getValue() == PlaceSwing.Once)
                {
                    Swing.Packet.swing(hand);
                }
            }

            if (slot != -1)
            {
                InventoryUtil.switchTo(slot);
            }
            else
            {
                InventoryUtil.syncItem();
            }

            CPacketPlayerTryUseItemOnBlock place =
                    new CPacketPlayerTryUseItemOnBlock(targetPos,
                            result.sideHit,
                            hand,
                            (float) result.hitVec.x,
                            (float) result.hitVec.y,
                            (float) result.hitVec.z);

            CPacketAnimation animation = new CPacketAnimation(hand);

            mc.player.connection.sendPacket(place);
            mc.player.connection.sendPacket(animation);

            if (slot != -1 && autoSwitch.getValue() != LegAutoSwitch.Keep)
            {
                InventoryUtil.switchTo(lastSlot);
            }

            AUTO_CRYSTAL.computeIfPresent(a ->
                a.setRenderPos(targetPos, "LS"));

            if (setDead.getValue() && finalCrystal != null)
            {
                Managers.SET_DEAD.setDead(finalCrystal);
            }
        });

        timer.reset(delay.getValue());
        if (rotate.getValue().noRotate(ACRotate.Place))
        {
            execute();
        }
    }

    protected void execute()
    {
        if (post != null)
        {
            active = true;
            post.run();
        }

        post = null;
        bRotations = null;
        rotations = null;
    }

    protected boolean checkPos(BlockPos pos)
    {
        if (BlockUtil.getDistanceSq(pos)
                <= MathUtil.square(placeRange.getValue())
            && mc.player.getDistanceSq(pos) >
                MathUtil.square(placeTrace.getValue())
            && !RayTraceUtil.raytracePlaceCheck(mc.player, pos))
        {
            return false;
        }

        BlockPos up   = pos.up();
        BlockPos upUp = up.up();
        if (mc.world.getBlockState(up).getBlock() != Blocks.AIR
            || !newVer.getValue()
                && mc.world.getBlockState(upUp).getBlock() != Blocks.AIR
            || !BlockUtil.checkEntityList(up, true, null)
            || newVerEntities.getValue()
                && !BlockUtil.checkEntityList(upUp, true, null))
        {
            return false;
        }

        if (BlockUtil.getDistanceSq(pos)
                <= MathUtil.square(combinedTrace.getValue()))
        {
            return true;
        }

        return RayTraceUtil.canBeSeen(
                new Vec3d(pos.getX() + 0.5,
                          pos.getY() + 2.7,
                          pos.getZ() + 0.5),
                mc.player);
    }

}
