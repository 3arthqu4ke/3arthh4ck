package me.earth.earthhack.impl.modules.combat.bedbomb;

import com.google.common.util.concurrent.AtomicDouble;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

// TODO: Rewrite
public class BedBomb extends Module
{
    private final Setting<Boolean> place = register(new BooleanSetting("Place", false));
    private final Setting<Integer> placeDelay = register(new NumberSetting<>("Placedelay", 50, 0, 500));
    private final Setting<Float> placeRange = register(new NumberSetting<>("PlaceRange", 6.0f, 1.0f, 10.0f));
    private final Setting<Boolean> extraPacket = register(new BooleanSetting("InsanePacket", false));
    private final Setting<Boolean> packet = register(new BooleanSetting("Packet", false));

    private final Setting<Boolean> explode = register(new BooleanSetting("Break", true));
    private final Setting<BreakLogic> breakMode = register(new EnumSetting<>("BreakMode", BreakLogic.ALL));
    private final Setting<Integer> breakDelay = register(new NumberSetting<>("Breakdelay", 50, 0, 500));
    private final Setting<Float> breakRange = register(new NumberSetting<>("BreakRange", 6.0f, 1.0f, 10.0f));
    private final Setting<Float> minDamage = register(new NumberSetting<>("MinDamage", 5.0f, 1.0f, 36.0f));
    private final Setting<Float> range = register(new NumberSetting<>("Range", 10.0f, 1.0f, 12.0f));
    private final Setting<Boolean> suicide = register(new BooleanSetting("Suicide", false));
    private final Setting<Boolean> removeTiles = register(new BooleanSetting("RemoveTiles", false));
    private final Setting<Boolean> rotate = register(new BooleanSetting("Rotate", false));
    private final Setting<Boolean> oneDot15 = register(new BooleanSetting("1.15", false));
    private final Setting<Logic> logic = register(new EnumSetting<>("Logic", Logic.BREAKPLACE));

    private final Setting<Boolean> craft = register(new BooleanSetting("Craft", false));
    private final Setting<Boolean> placeCraftingTable = register(new BooleanSetting("PlaceTable", false));
    private final Setting<Boolean> openCraftingTable = register(new BooleanSetting("OpenTable", false));
    private final Setting<Boolean> craftTable = register(new BooleanSetting("CraftTable", false));
    private final Setting<Float> tableRange = register(new NumberSetting<>("TableRange", 6.0f, 1.0f, 10.0f));
    private final Setting<Integer> craftDelay = register(new NumberSetting<>("CraftDelay", 4, 1, 10));
    private final Setting<Integer> tableSlot = register(new NumberSetting<>("TableSlot", 8, 0, 8));

    private final StopWatch breakTimer = new StopWatch();
    private final StopWatch placeTimer = new StopWatch();
    private final StopWatch craftTimer = new StopWatch();

    private EntityPlayer target = null;
    private boolean sendRotationPacket = false;

    private final AtomicDouble yaw = new AtomicDouble(-1D);
    private final AtomicDouble pitch = new AtomicDouble(-1D);
    private final AtomicBoolean shouldRotate = new AtomicBoolean(false);

    private MotionUpdateEvent current;

    private boolean one;
    private boolean two;
    private boolean three;
    private boolean four;
    private boolean five;
    private boolean six;

    private BlockPos maxPos = null;
    private boolean shouldCraft;
    private int craftStage = 0;
    private int bedSlot = -1;

    private BlockPos finalPos;
    private EnumFacing finalFacing;

    public BedBomb()
    {
        super("BedBomb", Category.Combat);
        this.setData(new SimpleData(this, "Quick and dirty Port of the awful old Phobos BedBomb."));
        this.listeners.add(new EventListener<MotionUpdateEvent>(MotionUpdateEvent.class)
        {
            @Override
            public void invoke(MotionUpdateEvent event)
            {
                onUpdateWalkingPlayer(event);
            }
        });
        this.listeners.addAll(new CPacketPlayerListener()
        {
            @Override
            protected void onPacket(PacketEvent.Send<CPacketPlayer> event)
            {
                BedBomb.this.onPacket(event.getPacket());
            }

            @Override
            protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event)
            {
                BedBomb.this.onPacket(event.getPacket());
            }

            @Override
            protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event)
            {
                BedBomb.this.onPacket(event.getPacket());
            }

            @Override
            protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event)
            {
                BedBomb.this.onPacket(event.getPacket());
            }
        }.getListeners());
    }

    @Override
    protected void onEnable()
    {
        current = null;
        bedSlot = -1;
        sendRotationPacket = false;
        target = null;
        yaw.set(-1D);
        pitch.set(-1D);
        shouldRotate.set(false);
        shouldCraft = false;
    }

    public void onPacket(CPacketPlayer packet) {
        if (shouldRotate.get()) {
            ((ICPacketPlayer) packet).setYaw((float) this.yaw.get());
            ((ICPacketPlayer) packet).setPitch((float) this.pitch.get());
            shouldRotate.set(false);
        }
    }

    public static int findInventoryWool() {
        return InventoryUtil.findInInventory(s ->
        {
            if (s.getItem() instanceof ItemBlock)
            {
                Block block = ((ItemBlock) s.getItem()).getBlock();
                return block.getDefaultState().getMaterial() == Material.CLOTH;
            }

            return false;
        }, true);
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float)(vec.x - (double)pos.getX());
            float f1 = (float)(vec.y - (double)pos.getY());
            float f2 = (float)(vec.z - (double)pos.getZ());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, direction, vec, hand);
        }
        mc.player.swingArm(EnumHand.MAIN_HAND);
    }

    public void onUpdateWalkingPlayer(MotionUpdateEvent event) {
        current = event;
        if ((mc.player.dimension != -1 && mc.player.dimension != 1)) {
            return;
        }

        if (event.getStage() == Stage.PRE)
        {
            doBedBomb();

            if (shouldCraft)
            {
                if (mc.currentScreen instanceof GuiCrafting)
                {
                    int woolSlot = findInventoryWool();
                    int woodSlot = InventoryUtil.findInInventory(s -> s.getItem() instanceof ItemBlock && ((ItemBlock) s.getItem()).getBlock() instanceof BlockPlanks, true);
                    if (woolSlot == -1 || woodSlot == -1 || woolSlot == -2 || woodSlot == -2)
                    {
                        mc.displayGuiScreen(null);
                        mc.currentScreen = null;
                        shouldCraft = false;
                        return;
                    }
                    if (craftStage > 1 && !one)
                    {
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, 1, 1, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, mc.player);
                        one = true;
                    } else if (craftStage > (1 + craftDelay.getValue()) && !two)
                    {
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, 2, 1, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, mc.player);
                        two = true;
                    } else if (craftStage > (1 + craftDelay.getValue() * 2) && !three)
                    {
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, 3, 1, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, mc.player);
                        three = true;
                    } else if (craftStage > (1 + craftDelay.getValue() * 3) && !four)
                    {
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, 4, 1, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, mc.player);
                        four = true;
                    } else if (craftStage > (1 + craftDelay.getValue() * 4) && !five)
                    {
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, 5, 1, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, mc.player);
                        five = true;
                    } else if (craftStage > (1 + craftDelay.getValue() * 5) && !six)
                    {
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, 6, 1, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, mc.player);
                        recheckBedSlots(woolSlot, woodSlot);
                        mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, 0, 0, ClickType.QUICK_MOVE, mc.player);
                        six = true;
                        one = false;
                        two = false;
                        three = false;
                        four = false;
                        five = false;
                        six = false;
                        craftStage = -2;
                        shouldCraft = false;
                    }
                    craftStage++;
                    /*if (craftTimer.passedMs(craftDelay.getValue() * 10)) {
                        mc.playerController.windowClick(1, 0, 0, ClickType.QUICK_MOVE, mc.player);
                        shouldCraft = false;
                        craftTimer.reset();
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue() * 9)) {
                        mc.playerController.windowClick(1, InventoryUtil.findEmptySlot(), 0, ClickType.PICKUP, mc.player);
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue() * 8)) {
                        mc.playerController.windowClick(1, 6, 1, ClickType.PICKUP, mc.player);
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue() * 7)) {
                        mc.playerController.windowClick(1, 5, 1, ClickType.PICKUP, mc.player);
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue() * 6)) {
                        mc.playerController.windowClick(1, 4, 1, ClickType.PICKUP, mc.player);
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue() * 5)) {
                        mc.playerController.windowClick(1, woodSlot, 0, ClickType.PICKUP, mc.player);
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue() * 4)) {
                        mc.playerController.windowClick(1, 3, 1, ClickType.PICKUP, mc.player);
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue() * 3)) {
                        mc.playerController.windowClick(1, 2, 1, ClickType.PICKUP, mc.player);
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue() * 2)) {
                        mc.playerController.windowClick(1, 1, 1, ClickType.PICKUP, mc.player);
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue())) {
                        mc.playerController.windowClick(1, woolSlot, 0, ClickType.PICKUP, mc.player);
                        return;
                    }*/
                    // if (lastCraftStage == craftStage) return;
                    /*switch(craftStage) {
                        case 0:
                            mc.playerController.windowClick(1, woolSlot, 0, ClickType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 1:
                            mc.playerController.windowClick(1, 1, 1, ClickType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 2:
                            mc.playerController.windowClick(1, 2, 1, ClickType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 3:
                            mc.playerController.windowClick(1, 3, 1, ClickType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 4:
                            mc.playerController.windowClick(1, woodSlot, 0, ClickType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 5:
                            mc.playerController.windowClick(1, 4, 1, ClickType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 6:
                            mc.playerController.windowClick(1, 5, 1, ClickType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 7:
                            mc.playerController.windowClick(1, 6, 1, ClickType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 8:
                            mc.playerController.windowClick(1, InventoryUtil.findEmptySlot(), 0, ClickType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 9:
                            mc.playerController.windowClick(1, 0, 0, ClickType.QUICK_MOVE, mc.player);
                            incrementCraftStage();
                            shouldCraft = false;
                            break;
                    }*/
                }
            }
        } else if (event.getStage() == Stage.POST && finalPos != null) {
            Vec3d hitVec = new Vec3d(finalPos.down()).add(0.5, 0.5, 0.5).add(new Vec3d(finalFacing.getOpposite().getDirectionVec()).scale(0.5)); //ökjlsdhblknsö
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            InventoryUtil.switchTo(bedSlot);
            rightClickBlock(finalPos.down(), hitVec, bedSlot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, EnumFacing.UP, packet.getValue());
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            placeTimer.reset();
            finalPos = null;
        }
    }

    public void recheckBedSlots(int woolSlot, int woodSlot) {
        for (int i = 1; i <= 3; i++) {
            if (mc.player.openContainer.getInventory().get(i) == ItemStack.EMPTY) {
                mc.playerController.windowClick(1, woolSlot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(1, i, 1, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(1, woolSlot, 0, ClickType.PICKUP, mc.player);
            }
        }
        for (int i = 4; i <= 6; i++) {
            if (mc.player.openContainer.getInventory().get(i) == ItemStack.EMPTY) {
                mc.playerController.windowClick(1, woodSlot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(1, i, 1, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(1, woodSlot, 0, ClickType.PICKUP, mc.player);
            }
        }
    }

    public void incrementCraftStage() {
        if (craftTimer.passed(craftDelay.getValue())) {
            // lastCraftStage = craftStage;
            craftStage++;
            if (craftStage > 9) {
                craftStage = 0;
            }
            craftTimer.reset();
        }
    }

    private void doBedBomb() {
        switch (logic.getValue()) {
            case BREAKPLACE:
                mapBeds();
                breakBeds();
                placeBeds();
                break;
            case PLACEBREAK:
                mapBeds();
                placeBeds();
                breakBeds();
                break;
        }
    }

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = PositionUtil.getEyePos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[] {
                mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
                mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)
        };
    }

    private void breakBeds() {
        if (explode.getValue() && breakTimer.passed(breakDelay.getValue())) {
            if (breakMode.getValue() == BreakLogic.CALC) {
                if (maxPos != null) {
                    //mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    //BlockUtil.rightClickBlockLegit(maxPos, range.getValue(), rotate.getValue() && !place.getValue(), EnumHand.MAIN_HAND, yaw, pitch, shouldRotate, true);
                    Vec3d hitVec = new Vec3d(maxPos).add(0.5, 0.5, 0.5);
                    float[] rotations = getLegitRotations(hitVec);
                    yaw.set(rotations[0]);
                    if (rotate.getValue()) {
                        shouldRotate.set(true);
                        pitch.set(rotations[1]);
                    }
                    RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(maxPos.getX() + .5, maxPos.getY() - .5d, maxPos.getZ() + .5));
                    EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
                    //if (mc.player.isSneaking()) {
                    rightClickBlock(maxPos, hitVec, EnumHand.MAIN_HAND, facing, true);
                    //mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    //}
                    breakTimer.reset();
                }
            } else {
                for (TileEntity entityBed : mc.world.loadedTileEntityList) {
                    if (!(entityBed instanceof TileEntityBed)) continue;
                    if (mc.player.getDistanceSq(entityBed.getPos()) > MathUtil.square(breakRange.getValue())) continue;
                    //mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    Vec3d hitVec = new Vec3d(entityBed.getPos()).add(0.5, 0.5, 0.5);
                    //BlockUtil.rightClickBlockLegit(maxPos, range.getValue(), rotate.getValue() && !place.getValue(), EnumHand.MAIN_HAND, yaw, pitch, shouldRotate, true);
                    float[] rotations = getLegitRotations(hitVec);
                    yaw.set(rotations[0]);
                    if (rotate.getValue()) {
                        shouldRotate.set(true);
                        pitch.set(rotations[1]);
                    }
                    RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(entityBed.getPos().getX() + .5, entityBed.getPos().getY() - .5d, entityBed.getPos().getZ() + .5));
                    //if (mc.player.isSneaking()) {
                    EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
                    rightClickBlock(entityBed.getPos(), hitVec, EnumHand.MAIN_HAND, facing, true);
                    //mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    //}
                    breakTimer.reset();
                }
            }
        }
    }

    public static boolean cantTakeDamage(boolean suicide) {
        return mc.player.capabilities.isCreativeMode || suicide;
    }

    private void mapBeds() {
        maxPos = null;
        float maxDamage = 0.5f;

        if (removeTiles.getValue()) {
            List<BedData> removedBlocks = new ArrayList<>();
            for (TileEntity tile : mc.world.loadedTileEntityList) {
                if (tile instanceof TileEntityBed) {
                    TileEntityBed bed = (TileEntityBed) tile;
                    BedData data = new BedData(tile.getPos(), mc.world.getBlockState(tile.getPos()), bed, bed.isHeadPiece());
                    removedBlocks.add(data);
                }
            }

            for (BedData data : removedBlocks) {
                mc.world.setBlockToAir(data.getPos());
                //data.getEntity().onChunkUnload();
            }

            for (BedData data : removedBlocks) {
                if (data.isHeadPiece()) {
                    BlockPos pos = data.getPos();
                    if (mc.player.getDistanceSq(pos) <= MathUtil.square(breakRange.getValue())) {
                        float selfDamage = DamageUtil.calculate(pos, mc.player);
                        if (selfDamage + 1.0 < EntityUtil.getHealth(mc.player) || cantTakeDamage(suicide.getValue())) {
                            for (EntityPlayer player : mc.world.playerEntities) {
                                if (player.getDistanceSq(pos) < MathUtil.square((range.getValue())) && EntityUtil.isValid(player, (range.getValue() + breakRange.getValue()))) {
                                    float damage = DamageUtil.calculate(pos, player);
                                    if (damage > selfDamage || (damage > minDamage.getValue() && cantTakeDamage(suicide.getValue())) || damage > EntityUtil.getHealth(player)) {
                                        if (damage > maxDamage) {
                                            maxDamage = damage;
                                            maxPos = pos;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (BedData data : removedBlocks) {
                //mc.world.addTileEntity(data.getEntity());
                mc.world.setBlockState(data.getPos(), data.getState());
            }
        } else {
            for(TileEntity tile : mc.world.loadedTileEntityList) {
                if (tile instanceof TileEntityBed) {
                    TileEntityBed bed = (TileEntityBed) tile;
                    //You could make a check here if the headpiece is out of range get the footpiece and click that blabla whatever
                    if (bed.isHeadPiece()) { //Damage comes from the headpiece
                        BlockPos pos = bed.getPos();
                        if (mc.player.getDistanceSq(pos) <= MathUtil.square(breakRange.getValue())) {
                            float selfDamage = DamageUtil.calculate(pos, mc.player);
                            //added 1.0 for some more safety
                            if (selfDamage + 1.0 < EntityUtil.getHealth(mc.player) || cantTakeDamage(suicide.getValue())) {
                                for (EntityPlayer player : mc.world.playerEntities) {
                                    if (player.getDistanceSq(pos) < MathUtil.square((range.getValue())) && EntityUtil.isValid(player, (range.getValue() + breakRange.getValue()))) {
                                        float damage = DamageUtil.calculate(pos, player);
                                        if (damage > selfDamage || (damage > minDamage.getValue() && cantTakeDamage(suicide.getValue())) || damage > EntityUtil.getHealth(player)) {
                                            if (damage > maxDamage) {
                                                maxDamage = damage;
                                                maxPos = pos;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void placeBeds() {
        if (place.getValue() && placeTimer.passed(placeDelay.getValue()) && maxPos == null) {
            bedSlot = findBedSlot();
            if (bedSlot == -1) {
                if (mc.player.getHeldItemOffhand().getItem() == Items.BED) {
                    bedSlot = -2;
                } else {
                    if (craft.getValue() && !shouldCraft && EntityUtil.getClosestEnemy(mc.world.playerEntities) != null) {
                        doBedCraft();
                    }
                    return;
                }
            }

            target = EntityUtil.getClosestEnemy(mc.world.playerEntities);
            if (target != null && target.getDistanceSq(mc.player) < 49) {
                BlockPos targetPos = new BlockPos(target.getPositionVector());
                placeBed(targetPos, true);
                if (craft.getValue()) {
                    doBedCraft();
                }
            }
        }
    }

    private void placeBed(BlockPos pos, boolean firstCheck) {
        if (mc.world.getBlockState(pos).getBlock() == Blocks.BED) {
            return;
        }

        float damage = DamageUtil.calculate(pos, mc.player);
        if (damage > EntityUtil.getHealth(mc.player) + 0.5) {
            if (firstCheck && oneDot15.getValue()) {
                placeBed(pos.up(), false);
            }
            return;
        }

        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            if (firstCheck && oneDot15.getValue()) {
                placeBed(pos.up(), false);
            }
            return;
        }

        List<BlockPos> positions = new ArrayList<>();
        Map<BlockPos, EnumFacing> facings = new HashMap<>();
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
                continue;
            }

            BlockPos position = pos.offset(facing);
            if (mc.player.getDistanceSq(position) <= MathUtil.square(placeRange.getValue()) && mc.world.getBlockState(position).getMaterial().isReplaceable() && !mc.world.getBlockState(position.down()).getMaterial().isReplaceable()) {
                positions.add(position);
                facings.put(position, facing.getOpposite());
            }
        }

        if (positions.isEmpty()) {
            if (firstCheck && oneDot15.getValue()) {
                placeBed(pos.up(), false);
            }
            return;
        }

        positions.sort(Comparator.comparingDouble(pos2 -> mc.player.getDistanceSq(pos2)));
        finalPos = positions.get(0);
        finalFacing = facings.get(finalPos);
        float[] rotation = simpleFacing(finalFacing);
        if (!sendRotationPacket && extraPacket.getValue()) {
            faceYawAndPitch(rotation[0], rotation[1]);
            sendRotationPacket = true;
        }

        yaw.set(rotation[0]);
        pitch.set(rotation[1]);
        shouldRotate.set(true);

        if (current != null)
        {
            current.setYaw(rotation[0]);
            current.setPitch(rotation[1]);
        }
    }

    public static void faceYawAndPitch(float yaw, float pitch) {
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, mc.player.onGround));
    }

    public static float[] simpleFacing(EnumFacing facing) {
        switch(facing) {
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

    @Override
    public String getDisplayInfo() {
        if (target != null) {
            return target.getName();
        }
        return null;
    }

    public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static List<BlockPos> getBlockSphere(float breakRange, Class<?> clazz) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(mc.player.getPosition(), breakRange, (int)breakRange, false, true, 0).stream().filter(pos -> clazz.isInstance(mc.world.getBlockState(pos).getBlock())).collect(Collectors.toList()));
        return positions;
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace) {
        return isPositionPlaceable(pos, rayTrace, true);
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
        return !shouldCheck || mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX(), pos.getY() + height, pos.getZ()), false, true, false) == null;
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        List<EnumFacing> facings = new ArrayList<>();
        if (mc.world == null || pos == null) {
            return facings;
        }

        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            IBlockState blockState = mc.world.getBlockState(neighbour);
            if (blockState != null && blockState.getBlock().canCollideCheck(blockState, false)) {
                if (!blockState.getMaterial().isReplaceable()) {
                    facings.add(side);
                }
            }
        }
        return facings;
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    private static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    private static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }


    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace, boolean entityCheck) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) {
            return 0;
        }

        if (!rayTracePlaceCheck(pos, rayTrace, 0.0f)) {
            return -1;
        }

        if (entityCheck) {
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                    return 1;
                }
            }
        }

        for(EnumFacing side : getPossibleSides(pos)) {
            if (canBeClicked(pos.offset(side))) {
                return 3;
            }
        }

        return 2;
    }

    public static EnumFacing getFirstFacing(BlockPos pos) {
        for(EnumFacing facing : getPossibleSides(pos)) {
            return facing;
        }
        return null;
    }

    public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        if (!mc.player.isSneaking() && (SpecialBlocks.BAD_BLOCKS.contains(neighbourBlock) || SpecialBlocks.SHULKERS.contains(neighbourBlock))) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.setSneaking(true);
            sneaking = true;
        }

        if (rotate) {
            faceVector(hitVec, true);
        }

        rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        return sneaking || isSneaking;
    }

    public static void faceVector(Vec3d vec, boolean normalizeAngle) {
        float[] rotations = getLegitRotations(vec);
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? MathHelper.normalizeAngle((int) rotations[1], 360) : rotations[1], mc.player.onGround));
    }

    public void doBedCraft() {
        int woolSlot = findInventoryWool();
        int woodSlot = InventoryUtil.findInInventory(s -> s.getItem() instanceof ItemBlock && ((ItemBlock) s.getItem()).getBlock() instanceof BlockPlanks, true);
        if (woolSlot == -1 || woodSlot == -1) {
            if (mc.currentScreen instanceof GuiCrafting) {
                mc.displayGuiScreen(null);
                mc.currentScreen = null;
            }
            return;
        }
        if (placeCraftingTable.getValue() && getBlockSphere(tableRange.getValue() - 1, BlockWorkbench.class).size() == 0) {
            List<BlockPos> targets = getSphere(mc.player.getPosition(), tableRange.getValue(), tableRange.getValue().intValue(), false, true, 0)
                    .stream()
                    .filter(pos -> isPositionPlaceable(pos, false) == 3)
                    .sorted(Comparator.comparingInt(pos -> -safety(pos)))
                    .collect(Collectors.toList());
            if (!targets.isEmpty()) {
                BlockPos target = targets.get(0);
                int tableSlot = InventoryUtil.findInInventory(s -> s.getItem() instanceof ItemBlock && ((ItemBlock) s.getItem()).getBlock() instanceof BlockPlanks, true);
                if (tableSlot != -1) {
                    mc.player.inventory.currentItem = tableSlot;
                    /*float[] rotations = RotationUtil.getLegitRotations(new Vec3d(target));
                    yaw.set(rotations[0]);
                    if (rotate.getValue()) {
                        shouldRotate.set(true);
                        pitch.set(rotations[1]);
                    }*/
                    placeBlock(target, EnumHand.MAIN_HAND, rotate.getValue(), true, false);
                } else {
                    if (craftTable.getValue()) {
                        craftTable();
                    }
                    tableSlot = InventoryUtil.findInHotbar(s -> s.getItem() instanceof ItemBlock && ((ItemBlock) s.getItem()).getBlock() instanceof BlockPlanks);
                    if (tableSlot != -1 && tableSlot != -2) {
                        mc.player.inventory.currentItem = tableSlot;
                        placeBlock(target, EnumHand.MAIN_HAND, rotate.getValue(), true, false);
                    }
                    /*float[] rotations = RotationUtil.getLegitRotations(new Vec3d(target));
                    yaw.set(rotations[0]);
                    if (rotate.getValue()) {
                        shouldRotate.set(true);
                        pitch.set(rotations[1]);
                    }*/
                }
            }
        }
        if (openCraftingTable.getValue()) {
            List<BlockPos> tables = getBlockSphere(tableRange.getValue(), BlockWorkbench.class);
            tables.sort(Comparator.comparingDouble(pos -> mc.player.getDistanceSq(pos)));
            if (!tables.isEmpty() && !(mc.currentScreen instanceof GuiCrafting)) {
                BlockPos target = tables.get(0);
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                // BlockUtil.rightClickBlock(target, tableRange.getValue(), rotate.getValue() && !place.getValue(), EnumHand.MAIN_HAND, yaw, pitch, shouldRotate, true);
                if (mc.player.getDistanceSq(target) > MathUtil.square(breakRange.getValue())) return;
                //mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                Vec3d hitVec = new Vec3d(target); // .add(0.5, 0.5, 0.5);
                //BlockUtil.rightClickBlockLegit(maxPos, range.getValue(), rotate.getValue() && !place.getValue(), EnumHand.MAIN_HAND, yaw, pitch, shouldRotate, true);
                float[] rotations = getLegitRotations(hitVec);
                yaw.set(rotations[0]);
                if (rotate.getValue()) {
                    shouldRotate.set(true);
                    pitch.set(rotations[1]);
                }
                RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(target.getX() + .5, target.getY() - .5d, target.getZ() + .5));
                //if (mc.player.isSneaking()) {
                EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
                rightClickBlock(target, hitVec, EnumHand.MAIN_HAND, facing, true);
                //mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                //}
                breakTimer.reset();
                if (mc.player.isSneaking()) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }
            }
            shouldCraft = mc.currentScreen instanceof GuiCrafting;
            craftStage = 0;
            craftTimer.reset();
            /*if (mc.currentScreen instanceof GuiCrafting) {
                mc.player.connection.sendPacket(new CPacketPlaceRecipe(mc.player.openContainer.windowId, CraftingManager.getRecipe(new ResourceLocation("white_bed")), true));
                mc.playerController.windowClick(mc.player.openContainer.windowId, 0, 0, ClickType.QUICK_MOVE, mc.player);
                mc.playerController.updateController();
            }*/
        }
    }

    public void craftTable() {
        int woodSlot = InventoryUtil.findInInventory(s -> s.getItem() instanceof ItemBlock && ((ItemBlock) s.getItem()).getBlock() instanceof BlockPlanks, true);
        if (woodSlot != -1) {
            mc.playerController.windowClick(0, woodSlot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 1, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 2, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 3, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 4, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 0, 0, ClickType.QUICK_MOVE, mc.player);
            int table = InventoryUtil.findInInventory(s -> s.getItem() instanceof ItemBlock && ((ItemBlock) s.getItem()).getBlock() instanceof BlockPlanks, true);
            if (table != -1) {
                mc.playerController.windowClick(0, table, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, tableSlot.getValue(), 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, table, 0, ClickType.PICKUP, mc.player);
            }
        }
    }

    private int findBedSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) {
                continue;
            }

            if (stack.getItem() == Items.BED) {
                return i;
            }
        }
        return -1;
    }

    public static class BedData {

        private final BlockPos pos;
        private final IBlockState state;
        private final boolean isHeadPiece;
        private final TileEntityBed entity;

        public BedData(BlockPos pos, IBlockState state, TileEntityBed bed, boolean isHeadPiece) {
            this.pos = pos;
            this.state = state;
            this.entity = bed;
            this.isHeadPiece = isHeadPiece;
        }

        public BlockPos getPos() {
            return pos;
        }

        public IBlockState getState() {
            return state;
        }

        public boolean isHeadPiece() {
            return isHeadPiece;
        }

        public TileEntityBed getEntity() {
            return entity;
        }
    }

    private int safety(BlockPos pos) {
        int safety = 0;
        for(EnumFacing facing : EnumFacing.values()) {
            if (!mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable()) {
                safety++;
            }
        }
        return safety;
    }

    public enum Logic {
        BREAKPLACE,
        PLACEBREAK
    }

    public enum BreakLogic {
        ALL,
        CALC
    }
}
