package me.earth.earthhack.impl.modules.combat.bomber;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.bomber.enums.CrystalBomberMode;
import me.earth.earthhack.impl.modules.combat.bomber.enums.CrystalBomberStage;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.stream.Collectors;

public class CrystalBomber extends Module {

    protected final Setting<CrystalBomberMode> mode =
            register(new EnumSetting<>("Mode", CrystalBomberMode.Normal));
    protected final Setting<Float> range =
            register(new NumberSetting<>("Range", 6.0f, 0.1f, 6.0f));
    protected final Setting<Float> toggleAt =
            register(new NumberSetting<>("ToggleAt", 8.0f, 0.1f, 20.0f));
    protected final Setting<Float> enemyRange =
            register(new NumberSetting<>("EnemyRange", 6.0f, 0.1f, 16.0f));
    protected final Setting<Integer> delay =
            register(new NumberSetting<>("Delay", 0, 0, 500));
    protected final Setting<Integer> cooldown =
            register(new NumberSetting<>("Cooldown", 0, 0, 500));
    protected final Setting<Boolean> rotate =
            register(new BooleanSetting("Rotate", false));
    protected final Setting<Boolean> reCheckCrystal =
            register(new BooleanSetting("ReCheckCrystal", false));
    protected final Setting<Boolean> airCheck =
            register(new BooleanSetting("AirCheck", false));
    protected final Setting<Boolean> smartSneak =
            register(new BooleanSetting("Smart-Sneak", true));
    protected final Setting<Boolean> bypass =
            register(new BooleanSetting("Bypass", false));

    private static final ModuleCache<Speedmine> SPEEDMINE =
            Caches.getModule(Speedmine.class);

    private static EntityPlayer target;
    private Vec3d lastTargetPos;
    private BlockPos targetPos;
    private int lastSlot;
    private boolean hasHit;
    public boolean rotating = false;
    private float yaw = 0.0f;
    private float targetYaw = 0.0f;
    private float pitch = 0.0f;
    private int rotationPacketsSpoofed = 0;
    private boolean offhand;

    private final StopWatch timer = new StopWatch();
    private final StopWatch delayTimer = new StopWatch();
    private final StopWatch cooldownTimer = new StopWatch();

    private CrystalBomberStage stage = CrystalBomberStage.FirstHit;
    private boolean firstHit = false;

    public CrystalBomber() {
        super("CrystalBomber", Category.Combat);
        this.listeners.add(new ListenerMotion(this));
    }

    @Override
    protected void onEnable() {
        targetPos = null;
        lastTargetPos = null;
        target = null;
        stage = CrystalBomberStage.FirstHit;
        timer.reset();
        delayTimer.reset();
        cooldownTimer.reset();
        /*if (fullOffhand.getValue()) {
            Speedmine.getInstance().pausing = true;
        }*/
    }

    protected void doCrystalBomber(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE) {
            updateTarget();
            if (target != null) {
                if (targetPos != null) {
                    lastTargetPos = new Vec3d(targetPos);
                }
                targetPos = PositionUtil.getPosition(target).up().up();
                if (lastTargetPos != null && !lastTargetPos.equals(new Vec3d(targetPos))) {
                    stage = CrystalBomberStage.FirstHit;
                    firstHit = true;
                }
                if (delayTimer.passed(delay.getValue())) {
                    if (reCheckCrystal.getValue()) recheckCrystal();
                    switch (stage) {
                        case FirstHit:
                            if (mc.world.getBlockState(targetPos).getBlock() != Blocks.AIR && MineUtil.canBreak(targetPos)) {
                                rotateToPos(targetPos, event);
                                break;
                            }
                        case Crystal:
                            if (mc.world.getBlockState(targetPos).getBlock() == Blocks.OBSIDIAN) {
                                if (BlockUtil.canPlaceCrystal(targetPos, false, false)) {
                                    rotateToPos(targetPos, event);
                                    break;
                                }
                            } else {
                                stage = CrystalBomberStage.PlaceObsidian;
                                delayTimer.reset();
                                break;
                            }
                            break;
                        case Pickaxe:
                            if (firstHit) {
                                if (isValidForMining()) {
                                    rotateToPos(targetPos, event);
                                    break;
                                }
                            } else {
                                rotateToPos(targetPos, event);
                                break;
                            }
                            break;
                        case Explode:
                            List<Entity> crystals = mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(targetPos.up()));
                            if (!crystals.isEmpty()) {
                                if ((mc.world.getBlockState(targetPos).getBlock() == Blocks.AIR || !airCheck.getValue()) && cooldownTimer.passed(cooldown.getValue())) {
                                    EntityEnderCrystal crystal = (EntityEnderCrystal) crystals.get(0);
                                    if (crystal != null) {
                                        rotateTo(crystal, event);
                                        break;
                                    }
                                }
                            } else {
                                if (reCheckCrystal.getValue()) {
                                    stage = CrystalBomberStage.Crystal;
                                    delayTimer.reset();
                                    break;
                                }
                            }
                        case PlaceObsidian:
                            int obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
                            boolean offhand = mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock) mc.player.getHeldItemOffhand().getItem()).getBlock() == Blocks.OBSIDIAN;
                            if (obbySlot != -1 || offhand) {
                                if (BlockUtil.isReplaceable(targetPos)) {
                                    if (mc.player.getDistanceSq(targetPos) <= MathUtil.square(range.getValue())) {
                                        rotateToPos(targetPos, event);
                                        break;
                                    }
                                } else if (mc.world.getBlockState(targetPos).getBlock() == Blocks.OBSIDIAN) {
                                    if (mode.getValue() == CrystalBomberMode.Instant) {
                                        stage = CrystalBomberStage.Crystal;
                                    } else {
                                        stage = CrystalBomberStage.FirstHit;
                                    }
                                    break;
                                }
                            }
                    }
                }
            }
        } else if (event.getStage() == Stage.POST) {
            updateTarget();
            if (target != null) {
                if (delayTimer.passed(delay.getValue())) {
                    switch (stage) {
                        case FirstHit:
                            if (mc.world.getBlockState(targetPos).getBlock() != Blocks.AIR /*|| hitAir.getValue()) && BlockUtil.canBreakNoAir(targetPos)*/) {
                                if (SPEEDMINE.get().getPos() == null || !(new Vec3d(SPEEDMINE.get().getPos()).equals(new Vec3d(targetPos)))) {
                                    mc.playerController.onPlayerDamageBlock(targetPos, mc.player.getHorizontalFacing().getOpposite());
                                } else if (new Vec3d(SPEEDMINE.get().getPos()).equals(new Vec3d(targetPos)) && (SPEEDMINE.get().getMode() == MineMode.Instant || SPEEDMINE.get().getMode() == MineMode.Civ)) {
                                    stage = CrystalBomberStage.Crystal;
                                    delayTimer.reset();
                                    timer.reset();
                                    firstHit = false;
                                    break;
                                }
                                stage = CrystalBomberStage.Crystal;
                                delayTimer.reset();
                                timer.reset();
                                firstHit = true;
                                break;
                            }
                        case Crystal:
                            /*if (offhandSwitch.getValue()) {
                                doOffhandSwitch();
                            }*/
                            int crystalSlot = getCrsytalSlot();
                            offhand = mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
                            if (!offhand /*&& !offhandSwitch.getValue() || !isValidForOffhand()*/) {
                                lastSlot = mc.player.inventory.currentItem;
                                if (crystalSlot != -1) {
                                    if (bypass.getValue()) {
                                        InventoryUtil.switchToBypass(crystalSlot);
                                    } else {
                                        InventoryUtil.switchTo(crystalSlot);
                                    }
                                }
                            }
                            if (offhand || mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
                                if (/*BlockUtil.rayTracePlaceCheck(targetPos, true) && mc.player.getDistanceSq(targetPos) <= MathUtil.square(range.getValue()) || */mc.player.getDistanceSq(targetPos) <= MathUtil.square(range.getValue())) {
                                    placeCrystalOnBlock(targetPos, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, false);
                                }
                            }
                            /*if (!offhand && !offhandSwitch.getValue() && switchBack.getValue() || !isValidForOffhand()) {
                                mc.player.inventory.currentItem = lastSlot;
                                mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                            }*/
                            stage = CrystalBomberStage.Pickaxe;
                            delayTimer.reset();
                            break;
                        case Pickaxe:
                            if (firstHit) {
                                if (isValidForMining()) {
                                    int pickSlot = getPickSlot();
                                    int lastSlot = mc.player.inventory.currentItem;
                                    if (pickSlot != -1) {
                                        if (bypass.getValue()) {
                                            InventoryUtil.switchToBypass(pickSlot);
                                        } else {
                                            InventoryUtil.switchTo(pickSlot);
                                        }
                                        SPEEDMINE.get().forceSend();
                                        stage = CrystalBomberStage.Explode;
                                        if (bypass.getValue()) {
                                            InventoryUtil.switchToBypass(pickSlot);
                                        } else {
                                            InventoryUtil.switchTo(lastSlot);
                                        }
                                        delayTimer.reset();
                                        cooldownTimer.reset();
                                        firstHit = false;
                                        break;
                                    }
                                }
                            } else {
                                int pickSlot = getPickSlot();
                                int lastSlot = mc.player.inventory.currentItem;
                                if (pickSlot != -1) {
                                    if (bypass.getValue()) {
                                        InventoryUtil.switchToBypass(pickSlot);
                                    } else {
                                        InventoryUtil.switchTo(pickSlot);
                                    }
                                    SPEEDMINE.get().forceSend();
                                    stage = CrystalBomberStage.Explode;
                                    delayTimer.reset();
                                    cooldownTimer.reset();
                                    if (bypass.getValue()) {
                                        InventoryUtil.switchToBypass(lastSlot);
                                    } else {
                                        InventoryUtil.switchTo(lastSlot);
                                    }
                                    break;
                                }
                            }
                        case Explode:
                            List<Entity> crystals = mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(targetPos.up()));
                            if (cooldownTimer.passed(cooldown.getValue())) {
                                if (!crystals.isEmpty() && (mc.world.getBlockState(targetPos).getBlock() == Blocks.AIR || !airCheck.getValue())) {
                                    EntityEnderCrystal crystal = (EntityEnderCrystal) crystals.get(0);
                                    if (crystal != null) {
                                        // rotateTo(crystal);
                                        rotating = false;
                                        if (/*EntityUtil.rayTraceHitCheck(crystal, true) && mc.player.getDistanceSq(crystal) <= MathUtil.square(range.getValue()) || */mc.player.getDistanceSq(crystal) <= MathUtil.square(range.getValue())) {
                                            attackEntity(crystal, true, true);
                                            stage = CrystalBomberStage.PlaceObsidian;
                                            delayTimer.reset();
                                            break;
                                        }
                                    } else {
                                        if (reCheckCrystal.getValue()) {
                                            stage = CrystalBomberStage.Crystal;
                                            delayTimer.reset();
                                            break;
                                        }
                                    }
                                } else {
                                    if (reCheckCrystal.getValue()) {
                                        stage = CrystalBomberStage.Crystal;
                                        delayTimer.reset();
                                        break;
                                    }
                                }
                            } else {
                                stage = CrystalBomberStage.Explode;
                                break;
                            }
                        case PlaceObsidian:
                            int obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
                            /*if (offhandSwitch.getValue()) {
                                doOffhandObby();
                            }*/
                            boolean offhand = mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock) mc.player.getHeldItemOffhand().getItem()).getBlock() == Blocks.OBSIDIAN;
                            if (obbySlot != -1 || offhand) {
                                if (BlockUtil.isReplaceable(targetPos) || BlockUtil.isAir(targetPos)) {
                                    /*if (!offhand || !isValidForOffhand()) {
                                        mc.player.inventory.currentItem = obbySlot;
                                        mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                                    }*/
                                    if (mc.player.getDistanceSq(targetPos) <= MathUtil.square(range.getValue())) {
                                        // rotateToPos(targetPos);
                                        EnumFacing facing = BlockUtil.getFacing(targetPos);
                                        if (facing != null) {
                                            float[] rotations = RotationUtil.getRotations(targetPos.offset(facing), facing.getOpposite());
                                            placeBlock(targetPos.offset(facing), facing.getOpposite(), rotations, obbySlot);
                                        }
                                    }
                                    if (mode.getValue() == CrystalBomberMode.Instant) {
                                        stage = CrystalBomberStage.Crystal;
                                    } else {
                                        stage = CrystalBomberStage.FirstHit;
                                    }
                                    delayTimer.reset();
                                    break;
                                } else if (mc.world.getBlockState(targetPos).getBlock() == Blocks.OBSIDIAN) {
                                    if (mode.getValue() == CrystalBomberMode.Instant) {
                                        stage = CrystalBomberStage.Crystal;
                                    } else {
                                        stage = CrystalBomberStage.FirstHit;
                                    }
                                    delayTimer.reset();
                                    break;
                                }
                            }
                    }
                }
            }
        }
    }

    private void updateTarget() {
        List<EntityPlayer> players = mc.world.playerEntities.stream()
                .filter(entity -> mc.player.getDistanceSq(entity) <= MathUtil.square(enemyRange.getValue()))
                .filter(entity -> !Managers.FRIENDS.contains(entity))
                .collect(Collectors.toList());
        EntityPlayer currentPlayer = null;
        for (EntityPlayer player : players) {
            if (player == mc.player) continue;
            if (currentPlayer == null) currentPlayer = player;
            if (mc.player.getDistanceSq(player) < mc.player.getDistanceSq(currentPlayer)) currentPlayer = player;
        }
        target = currentPlayer;
    }

    private int getPickSlot() {
        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.DIAMOND_PICKAXE) {
                return (i);
            }
        }
        return -1;
    }

    private int getCrsytalSlot() {
        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.END_CRYSTAL) {
                return (i);
            }
        }
        return -1;
    }

    private void rotateTo(Entity entity, MotionUpdateEvent event) {
        if (rotate.getValue()) {
            final float[] angle = RotationUtil.getRotations(entity);
            event.setYaw(angle[0]);
            event.setPitch(angle[1]);
        }
    }

    private void rotateToPos(BlockPos pos, MotionUpdateEvent event) {
        if (rotate.getValue()) {
            final float[] angle = RotationUtil.getRotationsToTopMiddle(pos);
            event.setYaw(angle[0]);
            event.setPitch(angle[1]);
        }
    }

    private void recheckCrystal() {
        if (mc.world.getBlockState(targetPos).getBlock() == Blocks.OBSIDIAN && mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(targetPos.up())).isEmpty() && stage != CrystalBomberStage.FirstHit) {
            stage = CrystalBomberStage.Crystal;
        }
    }

    public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand, boolean swing, boolean exactHand) {
        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX() + .5, pos.getY() - .5d, pos.getZ() + .5));
        EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0, 0, 0));
        if (swing) {
            mc.player.connection.sendPacket(new CPacketAnimation(exactHand ? hand : EnumHand.MAIN_HAND));
        }
    }

    public static void attackEntity(Entity entity, boolean packet, boolean swingArm) {
        if (packet) {
            mc.player.connection.sendPacket(new CPacketUseEntity(entity));
        } else {
            mc.playerController.attackEntity(mc.player, entity);
        }

        if(swingArm) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    public boolean isValidForMining() {
        int pickSlot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE);
        if (pickSlot == -1) return false;
        return SPEEDMINE.get().damages[pickSlot] >= SPEEDMINE.get().limit.getValue();
    }

    /**
     * Places a block on the given position
     * and also tries to lag us back into the block.
     *
     * @param on the position to place on.
     * @param facing the offset.
     */
    protected void placeBlock(BlockPos on, EnumFacing facing, float[] rotations, int slot)
    {
        if (rotations != null)
        {
            RayTraceResult result =
                    RayTraceUtil.getRayTraceResult(rotations[0], rotations[1]);

            float[] f = RayTraceUtil.hitVecToPlaceVec(on, result.hitVec);
            int lastSlot = mc.player.inventory.currentItem;
            boolean sneaking = smartSneak.getValue()
                    && !SpecialBlocks.shouldSneak(on, true);

            if (bypass.getValue()) {
                InventoryUtil.switchToBypass(slot);
            } else {
                InventoryUtil.switchTo(slot);
            }
            if (!sneaking)
            {
                mc.player.connection.sendPacket(
                        new CPacketEntityAction(mc.player,
                                CPacketEntityAction.Action.START_SNEAKING));
            }

            mc.player.connection.sendPacket(
                    new CPacketPlayerTryUseItemOnBlock(on,
                            facing,
                            InventoryUtil.getHand(slot),
                            f[0],
                            f[1],
                            f[2]));
            mc.player.connection.sendPacket(
                    new CPacketAnimation(InventoryUtil.getHand(slot)));

            if (!sneaking)
            {
                mc.player.connection.sendPacket(
                        new CPacketEntityAction(mc.player,
                                CPacketEntityAction.Action.STOP_SNEAKING));
            }

            if (mc.player.inventory.getStackInSlot(InventoryUtil.hotbarToInventory(lastSlot)).getItem() != Items.DIAMOND_PICKAXE) {
                if (bypass.getValue()) {
                    InventoryUtil.switchToBypass(lastSlot);
                } else {
                    InventoryUtil.switchTo(lastSlot);
                }
            }
        }
    }

}
