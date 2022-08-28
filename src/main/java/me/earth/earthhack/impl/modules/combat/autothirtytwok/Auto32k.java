package me.earth.earthhack.impl.modules.combat.autothirtytwok;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingEvent;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.block.*;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// TODO: Rewrite
//TODO: DUPLICATE CODE, Also maybe pass the Dispenserdata around and change it that would be smarter
//TODO: When not rotating or simulating Dispenser placement is chinese. Dispenser placement is chinese in general...
public class Auto32k extends Module {

    private static final ModuleCache<Freecam> FREECAM =
            Caches.getModule(Freecam.class);

    protected Setting<Mode> mode = register(new EnumSetting<>("Mode", Mode.NORMAL));
    protected final Setting<Boolean> swing = register(new BooleanSetting("Swing", false))
        .setComplexity(Complexity.Expert);
    protected final Setting<Integer> delay = register(new NumberSetting<>("Delay/Place", 25, 0, 250))
        .setComplexity(Complexity.Expert);
    protected final Setting<Integer> delayDispenser = register(new NumberSetting<>("Blocks/Place", 1, 1, 8));
    protected final Setting<Integer> blocksPerPlace = register(new NumberSetting<>("Actions/Place", 1, 1, 3));
    protected final Setting<Float> range = register(new NumberSetting<>("PlaceRange", 4.5F, 0.0F, 6.0F));
    protected final Setting<Boolean> raytrace = register(new BooleanSetting("Raytrace", false))
        .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> rotate = register(new BooleanSetting("Rotate", false));
    protected final Setting<Boolean> autoSwitch = register(new BooleanSetting("AutoSwitch", false))
        .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> withBind = register(new BooleanSetting("WithBind", false))
        .setComplexity(Complexity.Medium);
    protected final Setting<Bind> switchBind = register(new BindSetting("SwitchBind", Bind.none()))
        .setComplexity(Complexity.Medium);
    protected final Setting<Double> targetRange = register(new NumberSetting<>("TargetRange", 6.0, 0.0, 20.0))
        .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> extra = register(new BooleanSetting("ExtraRotation", false))
        .setComplexity(Complexity.Medium);
    protected final Setting<PlaceType> placeType = register(new EnumSetting<>("Place", PlaceType.CLOSE))
        .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> freecam = register(new BooleanSetting("Freecam", false))
        .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> onOtherHoppers = register(new BooleanSetting("UseHoppers", false))
        .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> preferObby = register(new BooleanSetting("UseObby", false))
        .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> checkForShulker = register(new BooleanSetting("CheckShulker", true))
        .setComplexity(Complexity.Medium);
    protected final Setting<Integer> checkDelay = register(new NumberSetting<>("CheckDelay", 500, 0, 500))
        .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> drop = register(new BooleanSetting("Drop", false))
        .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> mine = register(new BooleanSetting("Mine", false))
        .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> checkStatus = register(new BooleanSetting("CheckState", true))
        .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> packet = register(new BooleanSetting("Packet", false))
        .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> superPacket = register(new BooleanSetting("DispExtra", false))
        .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> secretClose = register(new BooleanSetting("SecretClose", false))
        .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> closeGui = register(new BooleanSetting("CloseGui", false))
        .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> repeatSwitch = register(new BooleanSetting("SwitchOnFail", true))
        .setComplexity(Complexity.Expert);
    //private final Setting<Boolean> authSneak = register(new Setting("AuthSneak", true));
    protected final Setting<Boolean> simulate = register(new BooleanSetting("Simulate", true))
        .setComplexity(Complexity.Expert);
    protected final Setting<Float> hopperDistance = register(new NumberSetting<>("HopperRange", 8.0F, 0.0F, 20.0F))
        .setComplexity(Complexity.Expert);
    protected final Setting<Integer> trashSlot = register(new NumberSetting<>("32kSlot", 0, 0, 9))
        .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> messages = register(new BooleanSetting("Messages", false))
        .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> antiHopper = register(new BooleanSetting("AntiHopper", false))
        .setComplexity(Complexity.Medium);

    private float yaw;
    private float pitch;
    private boolean spoof;
    public boolean switching;

    private int shulkerSlot = -1;
    private int hopperSlot = -1;

    private BlockPos hopperPos;
    private EntityPlayer target;
    public Step currentStep = Step.PRE;
    private final StopWatch placeTimer = new StopWatch();
    private static Auto32k instance;

    private int obbySlot = -1;
    private int dispenserSlot = -1;
    private int redstoneSlot = -1;
    private DispenserData finalDispenserData;
    private int actionsThisTick = 0;
    private boolean checkedThisTick = false;
    private final StopWatch disableTimer = new StopWatch();
    private boolean shouldDisable;

    public Auto32k() {
        super("Auto32k", Category.Combat);
        instance = this;
        this.listeners.add(new ListenerCPacketCloseWindow(this));
        this.listeners.addAll(new ListenerCPacketPlayer(this).getListeners());
        this.listeners.add(new ListenerGuiOpen(this));
        this.listeners.add(new ListenerKeyPress(this));
        this.listeners.add(new ListenerMotion(this));
        this.setData(new SimpleData(this, "Port of the awful old Phobos Auto32k."));
    }

    public static Auto32k getInstance() {
        if (instance == null) {
            instance = new Auto32k();
        }
        return instance;
    }

    @Override
    public void onEnable() {
        checkedThisTick = false;
        resetFields();
        if (mc.currentScreen instanceof GuiHopper) {
            currentStep = Step.HOPPERGUI;
        }

        if (mode.getValue() == Mode.NORMAL && autoSwitch.getValue() && !withBind.getValue()) {
            switching = true;
        }
    }

    public void onUpdateWalkingPlayer(MotionUpdateEvent event) {
        if (event.getStage() != Stage.PRE) {
            return;
        }

        if (shouldDisable && disableTimer.passed(1000)) {
            shouldDisable = false;
            this.disable();
            return;
        }

        checkedThisTick = false;
        actionsThisTick = 0;
        if (!this.isEnabled() || (mode.getValue() == Mode.NORMAL && autoSwitch.getValue() && !switching)) {
            return;
        }

        if (mode.getValue() == Mode.NORMAL) {
            normal32k();
        } else {
            processDispenser32k();
        }
    }

    protected void onGui(GuiScreenEvent<?> event) {
        if (!this.isEnabled()) {
            return;
        }

        if (!secretClose.getValue() && mc.currentScreen instanceof GuiHopper) {
            if (drop.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD && hopperPos != null) {
                mc.player.dropItem(true);
                if (mine.getValue() && hopperPos != null) {
                    //int currentSlot = mc.player.inventory.currentItem;
                    int pickaxeSlot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE);
                    if (pickaxeSlot != -1) {
                        InventoryUtil.switchTo(pickaxeSlot);
                        if (rotate.getValue()) {
                            rotateToPos(hopperPos.up(), null);
                        }
                        mc.playerController.onPlayerDamageBlock(hopperPos.up(), mc.player.getHorizontalFacing());
                        mc.playerController.onPlayerDamageBlock(hopperPos.up(), mc.player.getHorizontalFacing());
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        //InventoryUtil.switchToHotbarSlot(currentSlot, false);
                    }
                }
            }
            resetFields();
            if (mode.getValue() != Mode.NORMAL) {
                this.disable();
                return;
            }
            if (!autoSwitch.getValue() || mode.getValue() == Mode.DISPENSER) {
                this.disable();
            } else if (!withBind.getValue()) {
                this.disable();
            }
        } else if (event.getScreen() instanceof GuiHopper) {
            currentStep = Step.HOPPERGUI;
        }
    }

    @Override
    public String getDisplayInfo() {
        if (switching) {
            return TextColor.GREEN + "Switch";
        }
        return null;
    }

    protected void onKeyInput(KeyboardEvent event) {
        if (!this.isEnabled()) {
            return;
        }

        if (event.getEventState() && switchBind.getValue().getKey() == event.getKey() && withBind.getValue()) {
            if (switching) {
                resetFields();
                switching = true;
            }
            switching = !switching;
        }
    }

    protected void onSettingChange(SettingEvent<?> event) {
        if (event.getSetting().getContainer() == this) {
            resetFields();
        }
    }

    protected void onCPacketPlayer(CPacketPlayer packet) {
        if (packet instanceof CPacketPlayer.PositionRotation
                || packet instanceof CPacketPlayer.Rotation) {
            if (spoof) {
                ((ICPacketPlayer) packet).setYaw(this.yaw);
                ((ICPacketPlayer) packet).setPitch(this.pitch);
                spoof = false;
            }
        }
    }

    protected void onCPacketCloseWindow(PacketEvent.Send<CPacketCloseWindow> event) {
        if (!secretClose.getValue() && mc.currentScreen instanceof GuiHopper && hopperPos != null) {
            if (drop.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) {
                mc.player.dropItem(true);
                if (mine.getValue()) {
                    //int currentSlot = mc.player.inventory.currentItem;
                    int pickaxeSlot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE);
                    if (pickaxeSlot != -1) {
                        InventoryUtil.switchTo(pickaxeSlot);
                        if (rotate.getValue()) {
                            rotateToPos(hopperPos.up(), null);
                        }
                        mc.playerController.onPlayerDamageBlock(hopperPos.up(), mc.player.getHorizontalFacing());
                        mc.playerController.onPlayerDamageBlock(hopperPos.up(), mc.player.getHorizontalFacing());
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        //InventoryUtil.switchToHotbarSlot(currentSlot, false);
                    }
                }
            }
            resetFields();
            if (!autoSwitch.getValue() || mode.getValue() == Mode.DISPENSER) {
                this.disable();
            } else if (!withBind.getValue()) {
                this.disable();
            }
        } else if (secretClose.getValue() && (!autoSwitch.getValue() || switching || mode.getValue() == Mode.DISPENSER) && currentStep == Step.HOPPERGUI) {
            event.setCancelled(true);
        }
    }


    /*NORMAL 32K:*/


    private void normal32k() {
        if (autoSwitch.getValue()) {
            if (switching) {
                processNormal32k();
            } else {
                resetFields();
            }
        } else {
            processNormal32k();
        }
    }

    private void processNormal32k() {
        if (!this.isEnabled()) {
            return;
        }

        if (placeTimer.passed(delay.getValue())) {
            check();
            switch (currentStep) {
                case PRE:
                    runPreStep();
                    if (currentStep == Step.PRE) {
                        break;
                    }
                case HOPPER:
                    if (currentStep == Step.HOPPER) {
                        checkState();
                        if (currentStep == Step.PRE) {
                            if (checkedThisTick) {
                                processNormal32k();
                            }
                            return;
                        }
                        runHopperStep();
                        if (actionsThisTick >= blocksPerPlace.getValue() && !placeTimer.passed(delay.getValue())) {
                            break;
                        }
                    }
                case SHULKER:
                    checkState();
                    if (currentStep == Step.PRE) {
                        if (checkedThisTick) {
                            processNormal32k();
                        }
                        return;
                    }
                    runShulkerStep();
                    if (actionsThisTick >= blocksPerPlace.getValue() && !placeTimer.passed(delay.getValue())) {
                        break;
                    }
                case CLICKHOPPER:
                    checkState();
                    if (currentStep == Step.PRE) {
                        if (checkedThisTick) {
                            processNormal32k();
                        }
                        return;
                    }
                    runClickHopper();
                case HOPPERGUI:
                    runHopperGuiStep();
                    break;
                default:
                    currentStep = Step.PRE;
                    break;
            }
        }
    }

    private void runPreStep() {
        if (!this.isEnabled()) {
            return;
        }

        PlaceType type = placeType.getValue();

        if (FREECAM.isEnabled() && !freecam.getValue()) {
            if (messages.getValue()) {
                Managers.CHAT.sendDeleteMessage(TextColor.RED + "<Auto32k> Disable freecam.",
                        this.getDisplayName(),
                        ChatIDs.MODULE);
            }
            if (autoSwitch.getValue()) {
                resetFields();
                if (!withBind.getValue()) {
                    this.disable();
                }
            } else {
                this.disable();
            }
            return;
        }

        hopperSlot = InventoryUtil.findHotbarBlock(Blocks.HOPPER);
        shulkerSlot = InventoryUtil.findInHotbar(item -> item.getItem() instanceof ItemShulkerBox);
        // shulkerSlot = InventoryUtil.findHotbarBlock(BlockShulkerBox.class);

        if (mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock) mc.player.getHeldItemOffhand().getItem()).getBlock();
            if (block instanceof BlockShulkerBox) {
                shulkerSlot = -2;
            } else if (block instanceof BlockHopper) {
                hopperSlot = -2;
            }
        }

        if (shulkerSlot == -1 || hopperSlot == -1) {
            if (messages.getValue()) {
                Managers.CHAT.sendDeleteMessage(TextColor.RED + "<Auto32k> Materials not found.",
                        this.getDisplayName(),
                        ChatIDs.MODULE);
            }
            if (autoSwitch.getValue()) {
                resetFields();
                if (!withBind.getValue()) {
                    this.disable();
                }
            } else {
                this.disable();
            }
            return;
        }

        target = EntityUtil.getClosestEnemy();
        if (target == null || mc.player.getDistanceSq(target) > MathUtil.square(targetRange.getValue())) {
            if (autoSwitch.getValue()) {
                if (switching) {
                    resetFields();
                    switching = true;
                } else {
                    resetFields();
                }
                return;
            }
            type = placeType.getValue() == PlaceType.MOUSE ? PlaceType.MOUSE : PlaceType.CLOSE;
        }

        hopperPos = findBestPos(type, target);
        if (hopperPos != null) {
            if (mc.world.getBlockState(hopperPos).getBlock() instanceof BlockHopper) {
                currentStep = Step.SHULKER;
            } else {
                currentStep = Step.HOPPER;
            }
        } else {
            if (messages.getValue()) {
                Managers.CHAT.sendDeleteMessage(TextColor.RED + "<Auto32k> Block not found.",
                        this.getDisplayName(),
                        ChatIDs.MODULE);
                // Command.sendMessage(TextUtil.RED + "<Auto32k> Block not found.");
            }
            if (autoSwitch.getValue()) {
                resetFields();
                if (!withBind.getValue()) {
                    this.disable();
                }
            } else {
                this.disable();
            }
        }
    }

    private void runHopperStep() {
        if (!this.isEnabled()) {
            return;
        }

        if (currentStep == Step.HOPPER) {
            runPlaceStep(hopperPos, hopperSlot);
            currentStep = Step.SHULKER;
        }
    }

    private void runShulkerStep() {
        if (!this.isEnabled()) {
            return;
        }

        if (currentStep == Step.SHULKER) {
            runPlaceStep(hopperPos.up(), shulkerSlot);
            currentStep = Step.CLICKHOPPER;
        }
    }

    private void runClickHopper() {
        if (!this.isEnabled()) {
            return;
        }

        if (currentStep != Step.CLICKHOPPER) {
            return;
        }

        if (mode.getValue() == Mode.NORMAL && !(mc.world.getBlockState(hopperPos.up()).getBlock() instanceof BlockShulkerBox) && checkForShulker.getValue()) {
            if (placeTimer.passed(checkDelay.getValue())) {
                currentStep = Step.SHULKER;
            }
            return;
        }

        clickBlock(hopperPos);
        currentStep = Step.HOPPERGUI;
    }

    private void runHopperGuiStep() {
        if (!this.isEnabled()) {
            return;
        }

        if (currentStep != Step.HOPPERGUI) {
            return;
        }

        if (mc.player.openContainer instanceof ContainerHopper) {
            if (!holding32k(mc.player)) {
                int swordIndex = -1;
                for (int i = 0; i < 5; i++) {
                    if (is32k(mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i))) {
                        swordIndex = i;
                        break;
                    }
                }

                if (swordIndex == -1) {
                    return;
                }

                if (trashSlot.getValue() != 0) {
                    InventoryUtil.switchTo(trashSlot.getValue() - 1);
                } else {
                    if (mode.getValue() != Mode.NORMAL && shulkerSlot > 35 && shulkerSlot != 45) {
                        InventoryUtil.switchTo(44 - shulkerSlot);
                    }
                }
                mc.playerController.windowClick(mc.player.openContainer.windowId, swordIndex, trashSlot.getValue() == 0 ? mc.player.inventory.currentItem : (trashSlot.getValue() - 1), ClickType.SWAP, mc.player);
            } else {
                if (closeGui.getValue() && secretClose.getValue()) {
                    mc.player.closeScreen();
                }
            }
        } else if (holding32k(mc.player)) {
            if (autoSwitch.getValue() && mode.getValue() == Mode.NORMAL) {
                switching = false;
            } else if (!autoSwitch.getValue() || mode.getValue() == Mode.DISPENSER) {
                shouldDisable = true;
                disableTimer.reset();
            }
        }
    }

    private void runPlaceStep(BlockPos pos, int slot) {
        if (!this.isEnabled()) {
            return;
        }

        //TODO: HOLY SHIT WRITE PROPER UTIL FOR BLOCKPLACING EVERYTHING IN BLOCKUTIL IS CHINESE
        EnumFacing side = EnumFacing.UP;
        if (antiHopper.getValue() && currentStep == Step.HOPPER) {
            boolean foundfacing = false;
            for (EnumFacing facing : EnumFacing.values()) {
                if (mc.world.getBlockState(pos.offset(facing)).getBlock() == Blocks.HOPPER) {
                    continue;
                }

                if (!mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable()) {
                    foundfacing = true;
                    side = facing;
                    break;
                }
            }

            if (!foundfacing) {
                resetFields();
                return;
            }
        } else {
            side = BlockUtil.getFacing(pos);
            if (side == null) {
                resetFields();
                return;
            }
        }

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        //if (!mc.player.isSneaking() && (BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock))) {
        PingBypass.sendToActualServer(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        //mc.player.setSneaking(true);
        //}

        if (rotate.getValue()) {
            if (blocksPerPlace.getValue() > 1) {
                final float[] angle = RotationUtil.getRotations(hitVec);
                //TODO: FIND SMART HITVEC HERE (SIMPLE USE mc.world.raytrace AND REMOVE BLOCKS UNTIL U HIT THE BLOCK)
                if (extra.getValue()) {
                    faceYawAndPitch(angle[0], angle[1]); //GONNA LAG BACK
                }
            } else {
                rotateToPos(null, hitVec);
            }
        }

        InventoryUtil.switchTo(slot);
        rightClickBlock(neighbour, hitVec, slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, opposite, packet.getValue(), swing.getValue());
        PingBypass.sendToActualServer(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        placeTimer.reset();
        actionsThisTick++;
    }

    private BlockPos findBestPos(PlaceType type, EntityPlayer target) {
        BlockPos pos = null;
        NonNullList<BlockPos> positions = NonNullList.create();


        BlockPos middle = PositionUtil.getPosition();
        int maxRadius = Sphere.getRadius(range.getValue());
        for (int i = 1; i < maxRadius; i++)
        {
            BlockPos pos1 = middle.add(Sphere.get(i));
            if (canPlace(pos1)) {
                positions.add(pos1);
            }
        }

        // positions.addAll(BlockUtil.getSphere(PositionUtil.getPosition(), range.getValue(), range.getValue().intValue(), false, true, 0).stream().filter(this::canPlace).collect(Collectors.toList()));
        if (positions.isEmpty()) {
            return null;
        }
        switch (type) {
            case MOUSE:
                if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                    BlockPos mousePos = mc.objectMouseOver.getBlockPos();
                    if (!canPlace(mousePos)) {
                        BlockPos mousePosUp = mousePos.up();
                        if (canPlace(mousePosUp)) {
                            pos = mousePosUp;
                        }
                    } else {
                        pos = mousePos;
                    }
                }
                if (pos != null) {
                    break;
                }
            case CLOSE:
                positions.sort(Comparator.comparingDouble(pos2 -> mc.player.getDistanceSq(pos2)));
                pos = positions.get(0);
                break;
            case ENEMY:
                positions.sort(Comparator.comparingDouble(target::getDistanceSq));
                pos = positions.get(0);
                break;
            case MIDDLE:
                List<BlockPos> toRemove = new ArrayList<>();
                NonNullList<BlockPos> copy = NonNullList.create();
                copy.addAll(positions);
                for (BlockPos position : copy) {
                    double difference = mc.player.getDistanceSq(position) - target.getDistanceSq(position);
                    if (difference > 1 || difference < -1) {
                        toRemove.add(position);
                    }
                }
                copy.removeAll(toRemove);
                if (copy.isEmpty()) {
                    copy.addAll(positions);
                }
                copy.sort(Comparator.comparingDouble(pos2 -> mc.player.getDistanceSq(pos2)));
                pos = copy.get(0);
                break;
            case FAR:
                positions.sort(Comparator.comparingDouble(pos2 -> -target.getDistanceSq(pos2)));
                pos = positions.get(0);
                break;
            case SAFE:
                positions.sort(Comparator.comparingInt(pos2 -> -safetyFactor(pos2)));
                pos = positions.get(0);
            default:
        }

        return pos;
    }

    private boolean canPlace(BlockPos pos) {
        if (pos == null) {
            return false;
        }

        BlockPos boost = pos.up();

        if (isBadMaterial(mc.world.getBlockState(pos).getBlock(), onOtherHoppers.getValue()) || isBadMaterial(mc.world.getBlockState(boost).getBlock(), false)) {
            return false;
        }

        if (raytrace.getValue() && (!rayTracePlaceCheck(pos, raytrace.getValue()) || !rayTracePlaceCheck(pos, raytrace.getValue()))) {
            return false;
        }

        if (badEntities(pos) || badEntities(boost)) {
            return false;
        }

        if (onOtherHoppers.getValue() && mc.world.getBlockState(pos).getBlock() instanceof BlockHopper) {
            return true;
        }

        return findFacing(pos);
    }

    private void check() {
        if (currentStep != Step.PRE && currentStep != Step.HOPPER && hopperPos != null && !(mc.currentScreen instanceof GuiHopper) && !holding32k(mc.player) && (mc.player.getDistanceSq(hopperPos) > MathUtil.square(hopperDistance.getValue()) || mc.world.getBlockState(hopperPos).getBlock() != Blocks.HOPPER)) {
            resetFields();
            if (!autoSwitch.getValue() || !withBind.getValue() || mode.getValue() != Mode.NORMAL) {
                this.disable();
            }
        }
    }

    private void checkState() {
        if (!checkStatus.getValue() || checkedThisTick || (currentStep != Step.HOPPER && currentStep != Step.SHULKER && currentStep != Step.CLICKHOPPER)) {
            checkedThisTick = false;
            return;
        }

        if (hopperPos == null || isBadMaterial(mc.world.getBlockState(hopperPos).getBlock(), true) || (isBadMaterial(mc.world.getBlockState(hopperPos.up()).getBlock(), false) && !(mc.world.getBlockState(hopperPos.up()).getBlock() instanceof BlockShulkerBox)) || badEntities(hopperPos) || badEntities(hopperPos.up())) {
            if (autoSwitch.getValue() && mode.getValue() == Mode.NORMAL) {
                if (switching) {
                    resetFields();
                    if (repeatSwitch.getValue()) {
                        switching = true;
                    }
                } else {
                    resetFields();
                }
                if (!withBind.getValue()) {
                    this.disable();
                }
            } else {
                this.disable();
            }
            checkedThisTick = true;
        }
    }


    /*DISPENSER32K*/


    private void processDispenser32k() {
        if (!this.isEnabled()) {
            return;
        }

        if (placeTimer.passed(delay.getValue())) {
            check();
            switch (currentStep) {
                case PRE:
                    runDispenserPreStep();
                    if (currentStep == Step.PRE) {
                        break;
                    }
                case HOPPER:
                    runHopperStep();
                    currentStep = Step.DISPENSER;
                    if ((actionsThisTick >= delayDispenser.getValue() && !placeTimer.passed(delay.getValue()))) {
                        break;
                    }
                case DISPENSER:
                    runDispenserStep();
                    boolean quickCheck = !mc.world.getBlockState(finalDispenserData.getHelpingPos()).getMaterial().isReplaceable();
                    if ((actionsThisTick >= delayDispenser.getValue() && !placeTimer.passed(delay.getValue())) || (currentStep != Step.DISPENSER_HELPING && currentStep != Step.CLICK_DISPENSER)) {
                        break;
                    }
                    if (rotate.getValue() && quickCheck) {
                        break;
                    }
                case DISPENSER_HELPING:
                    runDispenserStep();
                    if ((actionsThisTick >= delayDispenser.getValue() && !placeTimer.passed(delay.getValue())) || (currentStep != Step.CLICK_DISPENSER && currentStep != Step.DISPENSER_HELPING)) {
                        break;
                    }
                    if (rotate.getValue()) {
                        break;
                    }
                case CLICK_DISPENSER:
                    clickDispenser();
                    if (actionsThisTick >= delayDispenser.getValue() && !placeTimer.passed(delay.getValue())) {
                        break;
                    }
                case DISPENSER_GUI:
                    dispenserGui();
                    if (currentStep == Step.DISPENSER_GUI) {
                        break;
                    }
                case REDSTONE:
                    placeRedstone();
                    if (actionsThisTick >= delayDispenser.getValue() && !placeTimer.passed(delay.getValue())) {
                        break;
                    }
                case CLICKHOPPER:
                    runClickHopper();
                    if (actionsThisTick >= delayDispenser.getValue() && !placeTimer.passed(delay.getValue())) {
                        break;
                    }
                case HOPPERGUI:
                    runHopperGuiStep();
                    if (actionsThisTick >= delayDispenser.getValue() && !placeTimer.passed(delay.getValue())) {
                        break;
                    }
                default:
                    break;
            }
        }
    }

    private void placeRedstone() {
        if (!this.isEnabled()) {
            return;
        }

        if (badEntities(hopperPos.up()) && !(mc.world.getBlockState(hopperPos.up()).getBlock() instanceof BlockShulkerBox)) {
            return;
        }

        runPlaceStep(finalDispenserData.getRedStonePos(), redstoneSlot);
        currentStep = Step.CLICKHOPPER;
    }

    private void clickDispenser() {
        if (!this.isEnabled()) {
            return;
        }

        clickBlock(finalDispenserData.getDispenserPos());
        currentStep = Step.DISPENSER_GUI;
    }

    private void dispenserGui() {
        if (!this.isEnabled()) {
            return;
        }

        if (!(mc.currentScreen instanceof GuiDispenser)) {
            return;
        }

        //TODO: QUICK_MOVE can be dumb check this
        mc.playerController.windowClick(mc.player.openContainer.windowId, shulkerSlot, 0, ClickType.QUICK_MOVE, mc.player);
        mc.player.closeScreen();
        currentStep = Step.REDSTONE;
    }

    private void clickBlock(BlockPos pos) {
        if (!this.isEnabled() || pos == null) {
            return;
        }
        //if (mc.player.isSneaking()) {
        PingBypass.sendToActualServer(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        //mc.player.setSneaking(false);
        //}
        Vec3d hitVec = new Vec3d(pos).add(0.5, -0.5, 0.5);

        if (rotate.getValue()) {
            rotateToPos(null, hitVec);
        }

        EnumFacing facing = EnumFacing.UP;
        if (finalDispenserData != null && finalDispenserData.getDispenserPos() != null && finalDispenserData.getDispenserPos().equals(pos) && pos.getY() > new BlockPos(mc.player.getPositionVector()).up().getY()) {
            facing = EnumFacing.DOWN;
        }
        rightClickBlock(pos, hitVec, shulkerSlot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, facing, packet.getValue(), swing.getValue());
        mc.player.swingArm(EnumHand.MAIN_HAND);
        // mc.rightClickDelayTimer = 4;
        actionsThisTick++;
    }

    private void runDispenserStep() {
        if (!this.isEnabled()) {
            return;
        }

        if (finalDispenserData == null || finalDispenserData.getDispenserPos() == null || finalDispenserData.getHelpingPos() == null) {
            resetFields();
            return;
        }

        if ((currentStep != Step.DISPENSER && currentStep != Step.DISPENSER_HELPING)) {
            return;
        }

        BlockPos dispenserPos = finalDispenserData.getDispenserPos();
        BlockPos helpingPos = finalDispenserData.getHelpingPos();
        if (mc.world.getBlockState(helpingPos).getMaterial().isReplaceable()) {
            currentStep = Step.DISPENSER_HELPING;
            EnumFacing facing = EnumFacing.DOWN;
            boolean foundHelpingPos = false;
            for (EnumFacing enumFacing : EnumFacing.values()) {
                BlockPos position = helpingPos.offset(enumFacing);
                if (!position.equals(hopperPos)
                        && !position.equals(hopperPos.up())
                        && !position.equals(dispenserPos)
                        && !position.equals(finalDispenserData.getRedStonePos())
                        && mc.player.getDistanceSq(position) <= MathUtil.square(range.getValue())
                        && (!raytrace.getValue() || rayTracePlaceCheck(position, raytrace.getValue()))
                        && !mc.world.getBlockState(position).getMaterial().isReplaceable()) {
                    foundHelpingPos = true;
                    facing = enumFacing;
                    break;
                }
            }

            if (!foundHelpingPos) {
                this.disable();
                return;
            }

            BlockPos neighbour = helpingPos.offset(facing);
            EnumFacing opposite = facing.getOpposite();
            Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
            Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

            //if (!mc.player.isSneaking() && (BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock))) {
            PingBypass.sendToActualServer(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            //mc.player.setSneaking(true);
            //}

            if (rotate.getValue()) {
                if (blocksPerPlace.getValue() > 1) {
                    final float[] angle = RotationUtil.getRotations(hitVec);
                    //TODO: FIND SMART HITVEC HERE (SIMPLE USE mc.world.raytrace AND REMOVE BLOCKS UNTIL U HIT THE BLOCK)
                    if (extra.getValue()) {
                        faceYawAndPitch(angle[0], angle[1]); //GONNA LAG BACK
                    }
                } else {
                    rotateToPos(null, hitVec);
                }
            }

            int slot = (preferObby.getValue() && obbySlot != -1) ? obbySlot : dispenserSlot;
            InventoryUtil.switchTo(slot);
            rightClickBlock(neighbour, hitVec, slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, opposite, packet.getValue(), swing.getValue());
            PingBypass.sendToActualServer(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            placeTimer.reset();
            actionsThisTick++;
            return;
        }

        placeDispenserAgainstBlock(dispenserPos, helpingPos);
        actionsThisTick++;
        currentStep = Step.CLICK_DISPENSER;
    }

    private void placeDispenserAgainstBlock(BlockPos dispenserPos, BlockPos helpingPos) {
        if (!this.isEnabled()) {
            return;
        }

        EnumFacing facing = EnumFacing.DOWN;
        for (EnumFacing enumFacing : EnumFacing.values()) {
            BlockPos position = dispenserPos.offset(enumFacing);
            if (position.equals(helpingPos)) {
                facing = enumFacing;
                break;
            }
        }

        EnumFacing opposite = facing.getOpposite();
        Vec3d hitVec = new Vec3d(helpingPos).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(helpingPos).getBlock();
        //if (BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
        PingBypass.sendToActualServer(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        //mc.player.setSneaking(true);
        //}

        Vec3d rotationVec = null;
        EnumFacing facings = EnumFacing.UP;
        if (rotate.getValue()) {
            if (blocksPerPlace.getValue() > 1) {
                final float[] angle = RotationUtil.getRotations(hitVec);
                //TODO: FIND SMART HITVEC HERE (SIMPLE USE mc.world.raytrace AND REMOVE BLOCKS UNTIL U HIT THE BLOCK)
                if (extra.getValue()) {
                    faceYawAndPitch(angle[0], angle[1]); //GONNA LAG BACK
                }
            } else {
                rotateToPos(null, hitVec);
            }
            rotationVec = new Vec3d(helpingPos).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        } else {
            if (dispenserPos.getY() <= new BlockPos(mc.player.getPositionVector()).up().getY()) {
                for (EnumFacing enumFacing : EnumFacing.values()) {
                    BlockPos position = hopperPos.up().offset(enumFacing);
                    if (position.equals(dispenserPos)) {
                        facings = enumFacing;
                        break;
                    }
                }
                float[] rotations = simpleFacing(facings);
                yaw = rotations[0];
                pitch = rotations[1];
                spoof = true;
                //rotateToPos(null, rotationVec);
            } else {
                float[] rotations = simpleFacing(facings);
                yaw = rotations[0];
                pitch = rotations[1];
                spoof = true;
                //rotationVec = new Vec3d(facings.getDirectionVec());
            }
        }

        //TODO: wtf
        rotationVec = new Vec3d(helpingPos).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        float[] rotations = simpleFacing(facings);
        final float[] angle = RotationUtil.getRotations(hitVec);
        //rotateToPos(null, rotationVec);
        if (superPacket.getValue()) {
            faceYawAndPitch(!rotate.getValue() ? rotations[0] : angle[0], !rotate.getValue() ? rotations[1] : angle[1]);
        }
        //mc.player.rotationYaw = angle[0];
        //mc.player.rotationPitch = angle[1];
        //Phobos.rotationManager.lookAtVec3d(rotationVec);

        InventoryUtil.switchTo(dispenserSlot);
        rightClickBlock(helpingPos, rotationVec, dispenserSlot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, opposite, packet.getValue(), swing.getValue());
        PingBypass.sendToActualServer(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        placeTimer.reset();
        actionsThisTick++;
        currentStep = Step.CLICK_DISPENSER;
    }

    private void runDispenserPreStep() {
        if (!this.isEnabled()) {
            return;
        }

        if (FREECAM.isEnabled() && !freecam.getValue()) {
            if (messages.getValue()) {
                Managers.CHAT.sendDeleteMessage(TextColor.RED + "<Auto32k> Disable freecam.",
                        this.getDisplayName(),
                        ChatIDs.MODULE);

            }
            this.disable();
            return;
        }

        hopperSlot = InventoryUtil.findHotbarBlock(Blocks.HOPPER);
        shulkerSlot = InventoryUtil.findInInventory(item -> item.getItem() instanceof ItemShulkerBox, false);
        dispenserSlot = InventoryUtil.findHotbarBlock(Blocks.DISPENSER);
        redstoneSlot = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK);
        obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);

        if (mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock) mc.player.getHeldItemOffhand().getItem()).getBlock();
            if (block instanceof BlockHopper) {
                hopperSlot = -2;
            } else if (block instanceof BlockDispenser) {
                dispenserSlot = -2;
            } else if (block == Blocks.REDSTONE_BLOCK) {
                redstoneSlot = -2;
            } else if (block instanceof BlockObsidian) {
                obbySlot = -2;
            }
        }

        if (shulkerSlot == -1 || hopperSlot == -1 || dispenserSlot == -1 || redstoneSlot == -1) {
            if (messages.getValue()) {
                Managers.CHAT.sendDeleteMessage(TextColor.RED + "<Auto32k> Materials not found.",
                        this.getDisplayName(),
                        ChatIDs.MODULE);

            }
            this.disable();
            return;
        }

        finalDispenserData = findBestPos();
        if (finalDispenserData.isPlaceable()) {
            hopperPos = finalDispenserData.getHopperPos();
            if (mc.world.getBlockState(hopperPos).getBlock() instanceof BlockHopper) {
                currentStep = Step.DISPENSER;
            } else {
                currentStep = Step.HOPPER;
            }
        } else {
            if (messages.getValue()) {
                Managers.CHAT.sendDeleteMessage(TextColor.RED + "<Auto32k> Block not found.",
                        this.getDisplayName(),
                        ChatIDs.MODULE);
            }
            this.disable();
        }
    }

    private DispenserData findBestPos() {
        PlaceType type = placeType.getValue();
        target = EntityUtil.getClosestEnemy();
        if (target == null || mc.player.getDistanceSq(target) > MathUtil.square(targetRange.getValue())) {
            type = placeType.getValue() == PlaceType.MOUSE ? PlaceType.MOUSE : PlaceType.CLOSE;
        }

        NonNullList<BlockPos> positions = NonNullList.create();

        BlockPos middle = PositionUtil.getPosition();
        int maxRadius = Sphere.getRadius(range.getValue());
        for (int i = 1; i < maxRadius; i++)
        {
            positions.add(middle.add(Sphere.get(i)));
        }

        DispenserData data = new DispenserData();
        switch (type) {
            case MOUSE:
                if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                    BlockPos mousePos = mc.objectMouseOver.getBlockPos();
                    data = analyzePos(mousePos);
                    if (!data.isPlaceable()) {
                        data = analyzePos(mousePos.up());
                    }
                }

                if (data.isPlaceable()) {
                    return data;
                }
            case CLOSE:
                positions.sort(Comparator.comparingDouble(pos2 -> mc.player.getDistanceSq(pos2)));
                break;
            case ENEMY:
                positions.sort(Comparator.comparingDouble(target::getDistanceSq));
                break;
            case MIDDLE:
                List<BlockPos> toRemove = new ArrayList<>();
                NonNullList<BlockPos> copy = NonNullList.create();
                copy.addAll(positions);
                for (BlockPos position : copy) {
                    double difference = mc.player.getDistanceSq(position) - target.getDistanceSq(position);
                    if (difference > 1 || difference < -1) {
                        toRemove.add(position);
                    }
                }
                copy.removeAll(toRemove);
                if (copy.isEmpty()) {
                    copy.addAll(positions);
                }
                copy.sort(Comparator.comparingDouble(pos2 -> mc.player.getDistanceSq(pos2)));
                break;
            case FAR:
                positions.sort(Comparator.comparingDouble(pos2 -> -target.getDistanceSq(pos2)));
                break;
            case SAFE:
                positions.sort(Comparator.comparingInt(pos2 -> -safetyFactor(pos2)));
                break; //IDK for dispenser this doesnt make much sense
        }
        data = findData(positions);
        return data;
    }

    private DispenserData findData(NonNullList<BlockPos> positions) {
        for (BlockPos position : positions) {
            DispenserData data = analyzePos(position);
            if (data.isPlaceable()) {
                return data;
            }
        }
        return new DispenserData();
    }

    private DispenserData analyzePos(BlockPos pos) {
        DispenserData data = new DispenserData(pos);
        if (pos == null) {
            return data;
        }

        if (isBadMaterial(mc.world.getBlockState(pos).getBlock(), onOtherHoppers.getValue()) || isBadMaterial(mc.world.getBlockState(pos.up()).getBlock(), false)) {
            return data;
        }

        if (raytrace.getValue() && (!rayTracePlaceCheck(pos, raytrace.getValue()))) {
            return data;
        }

        if (badEntities(pos) || badEntities(pos.up())) {
            return data;
        }

        if (hasAdjancedRedstone(pos)) {
            return data;
        }

        if (!findFacing(pos)) {
            return data;
        }

        BlockPos[] otherPositions = checkForDispenserPos(pos);
        if (otherPositions[0] == null || otherPositions[1] == null || otherPositions[2] == null) {
            return data;
        }

        data.setDispenserPos(otherPositions[0]);
        data.setRedStonePos(otherPositions[1]);
        data.setHelpingPos(otherPositions[2]);
        data.setPlaceable(true);
        return data;
    }

    private boolean findFacing(BlockPos pos) {
        boolean foundFacing = false;
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == EnumFacing.UP) {
                continue;
            }

            if (facing == EnumFacing.DOWN && antiHopper.getValue() && mc.world.getBlockState(pos.offset(facing)).getBlock() == Blocks.HOPPER) {
                foundFacing = false;
                break;
            }

            if (!mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable() && (!antiHopper.getValue() || mc.world.getBlockState(pos.offset(facing)).getBlock() != Blocks.HOPPER)) {
                foundFacing = true;
            }
        }
        return foundFacing;
    }

    private BlockPos[] checkForDispenserPos(BlockPos posIn) {
        BlockPos[] pos = new BlockPos[3];
        BlockPos playerPos = new BlockPos(mc.player.getPositionVector());

        if (posIn.getY() < playerPos.down().getY()) {
            return pos;
        }

        List<BlockPos> possiblePositions = getDispenserPositions(posIn);
        if (posIn.getY() < playerPos.getY()) {
            possiblePositions.remove(posIn.up().up());
        } else if (posIn.getY() > playerPos.getY()) {
            possiblePositions.remove(posIn.west().up());
            possiblePositions.remove(posIn.north().up());
            possiblePositions.remove(posIn.south().up());
            possiblePositions.remove(posIn.east().up());
        }

        if (rotate.getValue() || simulate.getValue()) {
            possiblePositions.sort(Comparator.comparingDouble(pos2 -> -mc.player.getDistanceSq(pos2)));

            BlockPos posToCheck = possiblePositions.get(0); //TODO: in some cases(diagonally for example) we can accept more positions

            if (isBadMaterial(mc.world.getBlockState(posToCheck).getBlock(), false)) {
                return pos;
            }

            if (mc.player.getDistanceSq(posToCheck) > MathUtil.square(range.getValue())) {
                return pos;
            }

            if (raytrace.getValue() && (!rayTracePlaceCheck(posToCheck, raytrace.getValue()))) {
                return pos;
            }

            if (badEntities(posToCheck)) {
                return pos;
            }

            if (hasAdjancedRedstone(posToCheck)) {
                return pos;
            }

            List<BlockPos> possibleRedStonePositions = checkRedStone(posToCheck, posIn);
            if (possiblePositions.isEmpty()) {
                return pos;
            }
            BlockPos[] helpingStuff = getHelpingPos(posToCheck, posIn, possibleRedStonePositions);
            if (helpingStuff != null && helpingStuff[0] != null && helpingStuff[1] != null) {
                pos[0] = posToCheck;
                pos[1] = helpingStuff[1]; //Redstone
                pos[2] = helpingStuff[0]; //Helping
            }
        } else {
            possiblePositions.removeIf(position -> mc.player.getDistanceSq(position) > MathUtil.square(range.getValue()));
            possiblePositions.removeIf(position -> isBadMaterial(mc.world.getBlockState(position).getBlock(), false));
            possiblePositions.removeIf(position -> raytrace.getValue() && (!rayTracePlaceCheck(position, raytrace.getValue())));
            possiblePositions.removeIf(this::badEntities);
            possiblePositions.removeIf(this::hasAdjancedRedstone);
            for (BlockPos position : possiblePositions) {
                List<BlockPos> possibleRedStonePositions = checkRedStone(position, posIn);
                if (possiblePositions.isEmpty()) {
                    continue;
                }
                BlockPos[] helpingStuff = getHelpingPos(position, posIn, possibleRedStonePositions);
                if (helpingStuff != null && helpingStuff[0] != null && helpingStuff[1] != null) {
                    pos[0] = position;
                    pos[1] = helpingStuff[1]; //Redstone
                    pos[2] = helpingStuff[0]; //Helping
                    break;
                }
            }
        }

        return pos;
    }

    private List<BlockPos> checkRedStone(BlockPos pos, BlockPos hopperPos) {
        List<BlockPos> toCheck = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.values()) {
            toCheck.add(pos.offset(facing));
        }

        toCheck.removeIf(position -> position.equals(hopperPos.up()));
        toCheck.removeIf(position -> mc.player.getDistanceSq(position) > MathUtil.square(range.getValue()));
        toCheck.removeIf(position -> isBadMaterial(mc.world.getBlockState(position).getBlock(), false));
        toCheck.removeIf(position -> raytrace.getValue() && (!rayTracePlaceCheck(position, raytrace.getValue())));
        toCheck.removeIf(this::badEntities);
        toCheck.sort(Comparator.comparingDouble(pos2 -> mc.player.getDistanceSq(pos2)));
        return toCheck;
    }

    private boolean hasAdjancedRedstone(BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos position = pos.offset(facing);
            //TODO: WEAK CHECK, the methods for checking power of a block seem depreceated but maybe we can find something there
            if (mc.world.getBlockState(position).getBlock() == Blocks.REDSTONE_BLOCK || mc.world.getBlockState(position).getBlock() == Blocks.REDSTONE_TORCH) {
                return true;
            }
        }
        return false;
    }

    private List<BlockPos> getDispenserPositions(BlockPos pos) {
        List<BlockPos> list = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing != EnumFacing.DOWN) {
                list.add(pos.offset(facing).up());
            }
        }
        return list;
    }

    private BlockPos[] getHelpingPos(BlockPos pos, BlockPos hopperPos, List<BlockPos> redStonePositions) {
        BlockPos[] result = new BlockPos[2];
        List<BlockPos> possiblePositions = new ArrayList<>();
        if (redStonePositions.isEmpty()) {
            return null;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos facingPos = pos.offset(facing);
            if (!facingPos.equals(hopperPos) && !facingPos.equals(hopperPos.up())) {
                if (!mc.world.getBlockState(facingPos).getMaterial().isReplaceable()) {
                    if (redStonePositions.contains(facingPos)) {
                        redStonePositions.remove(facingPos);
                        if (redStonePositions.isEmpty()) {
                            redStonePositions.add(facingPos);
                        } else {
                            result[0] = facingPos;
                            result[1] = redStonePositions.get(0);
                            return result;
                        }
                    } else {
                        result[0] = facingPos;
                        result[1] = redStonePositions.get(0);
                        return result;
                    }
                } else {
                    for (EnumFacing facing1 : EnumFacing.values()) {
                        BlockPos facingPos1 = facingPos.offset(facing1);
                        if (!facingPos1.equals(hopperPos) && !facingPos1.equals(hopperPos.up()) && !facingPos1.equals(pos) && !mc.world.getBlockState(facingPos1).getMaterial().isReplaceable()) {
                            if (redStonePositions.contains(facingPos)) {
                                redStonePositions.remove(facingPos);
                                if (redStonePositions.isEmpty()) {
                                    redStonePositions.add(facingPos);
                                } else {
                                    possiblePositions.add(facingPos);
                                }
                            } else {
                                possiblePositions.add(facingPos);
                            }
                        }
                    }
                }
            }
        }
        possiblePositions.removeIf(position -> mc.player.getDistanceSq(position) > MathUtil.square(range.getValue()));
        possiblePositions.sort(Comparator.comparingDouble(position -> mc.player.getDistanceSq(position)));

        if (!possiblePositions.isEmpty()) {
            redStonePositions.remove(possiblePositions.get(0));
            if (!redStonePositions.isEmpty()) {
                result[0] = possiblePositions.get(0);
                result[1] = redStonePositions.get(0);
            }
            return result;
        }

        return null;
    }


    /*UTILITY*/


    private void rotateToPos(BlockPos pos, Vec3d vec3d) {
        final float[] angle;
        if (vec3d == null) {
            angle = calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f));
        } else {
            angle = RotationUtil.getRotations(vec3d);
        }
        yaw = angle[0];
        pitch = angle[1];
        spoof = true;
    }

    public static float[] calcAngle(Vec3d from, Vec3d to) {
        final double difX = to.x - from.x;
        final double difY = (to.y - from.y) * -1.0F;
        final double difZ = to.z - from.z;
        final double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0f), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }

    private boolean isBadMaterial(Block block, boolean allowHopper) {
        return !(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow) && (!allowHopper || !(block instanceof BlockHopper));
    }

    private void resetFields() {
        shouldDisable = false;
        spoof = false;
        switching = false;
        shulkerSlot = -1;
        hopperSlot = -1;
        hopperPos = null;
        target = null;
        currentStep = Step.PRE;
        obbySlot = -1;
        dispenserSlot = -1;
        redstoneSlot = -1;
        finalDispenserData = null;
        actionsThisTick = 0;
    }

    public static class DispenserData {

        private BlockPos dispenserPos;
        private BlockPos redStonePos;
        private BlockPos hopperPos;
        private BlockPos helpingPos;
        private boolean isPlaceable;

        public DispenserData() {
            this.isPlaceable = false;
        }

        public DispenserData(BlockPos pos) {
            this.isPlaceable = false;
            this.hopperPos = pos;
        }

        public void setPlaceable(boolean placeable) {
            this.isPlaceable = placeable;
        }

        public boolean isPlaceable() {
            return dispenserPos != null && hopperPos != null && redStonePos != null && helpingPos != null;
        }

        public BlockPos getDispenserPos() {
            return dispenserPos;
        }

        public void setDispenserPos(BlockPos dispenserPos) {
            this.dispenserPos = dispenserPos;
        }

        public BlockPos getRedStonePos() {
            return redStonePos;
        }

        public void setRedStonePos(BlockPos redStonePos) {
            this.redStonePos = redStonePos;
        }

        public BlockPos getHopperPos() {
            return hopperPos;
        }

        public void setHopperPos(BlockPos hopperPos) {
            this.hopperPos = hopperPos;
        }

        public BlockPos getHelpingPos() {
            return helpingPos;
        }

        public void setHelpingPos(BlockPos helpingPos) {
            this.helpingPos = helpingPos;
        }
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet, boolean swing) {
        if (packet) {
            float f = (float)(vec.x - (double)pos.getX());
            float f1 = (float)(vec.y - (double)pos.getY());
            float f2 = (float)(vec.z - (double)pos.getZ());
            PingBypass.sendToActualServer(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, direction, vec, hand);
        }
        if (swing) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        // mc.rightClickDelayTimer = 4; //?
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
        return !shouldCheck || mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX(), pos.getY() + height, pos.getZ()), false, true, false) == null;
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck) {
        return rayTracePlaceCheck(pos, shouldCheck, 1.0f);
    }

    public static boolean rayTracePlaceCheck(BlockPos pos) {
        return rayTracePlaceCheck(pos, true);
    }

    public static void faceYawAndPitch(float yaw, float pitch) {
        PingBypass.sendToActualServer(new CPacketPlayer.Rotation(yaw, pitch, mc.player.onGround));
    }

    private boolean badEntities(BlockPos pos) {
        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityExpBottle) && !(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                return true;
            }
        }
        return false;
    }

    private int safetyFactor(BlockPos pos) {
        return safety(pos) + safety(pos.up());
    }

    private int safety(BlockPos pos) {
        int safety = 0;
        for (EnumFacing facing : EnumFacing.values()) {
            if (!mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable()) {
                safety++;
            }
        }
        return safety;
    }

    public static float[] simpleFacing(EnumFacing facing) {
        switch (facing) {
            case DOWN:
                return new float[]{mc.player.rotationYaw, 90.0f};
            case UP:
                return new float[]{mc.player.rotationYaw, -90.0f};
            case NORTH:
                return new float[]{180.0f, 0.0f};
            case SOUTH:
                return new float[]{0.0f, 0.0f};
            case WEST:
                return new float[]{90.0f, 0.0f};
            default:
                return new float[]{270.0f, 0.0f};
        }
    }

    public static boolean holding32k(EntityPlayer player) {
        return is32k(player.getHeldItemMainhand());
    }

    public static boolean is32k(ItemStack stack) {
        if (stack == null) {
            return false;
        }

        if (stack.getTagCompound() == null) {
            return false;
        }
        NBTTagList enchants = (NBTTagList) stack.getTagCompound().getTag("ench");

        for (int i = 0; i < enchants.tagCount(); i++) {
            NBTTagCompound enchant = enchants.getCompoundTagAt(i);
            if (enchant.getInteger("id") == 16) {
                int lvl = enchant.getInteger("lvl");
                if (lvl >= 42) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public static boolean simpleIs32k(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) >= 1000;
    }

    public enum PlaceType {
        MOUSE,
        CLOSE,
        ENEMY,
        MIDDLE,
        FAR,
        SAFE
    }

    public enum Mode {
        NORMAL, DISPENSER
    }

    public enum Step {
        PRE,
        HOPPER,
        SHULKER,
        CLICKHOPPER,
        HOPPERGUI,
        DISPENSER_HELPING,
        DISPENSER_GUI,
        DISPENSER,
        CLICK_DISPENSER,
        REDSTONE
    }

}
