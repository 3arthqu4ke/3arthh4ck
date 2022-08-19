package me.earth.earthhack.impl.modules.combat.anvilaura;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.combat.anvilaura.modes.AnvilMode;
import me.earth.earthhack.impl.modules.combat.anvilaura.modes.AnvilStage;
import me.earth.earthhack.impl.modules.combat.anvilaura.util.AnvilResult;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyUtil;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.path.BasePath;
import me.earth.earthhack.impl.util.math.path.PathFinder;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static me.earth.earthhack.impl.util.helpers.blocks.ObbyModule.HELPER;

final class ListenerAnvilAura extends ObbyListener<AnvilAura>
{
    private static final ModuleCache<AntiSurround> ANTISURROUND =
            Caches.getModule(AntiSurround.class);
    private static final Vec3i[] CRYSTAL_OFFSETS =
    {
        new Vec3i(1, 0, -1),
        new Vec3i(1, 0, 1),
        new Vec3i(-1, 0, -1),
        new Vec3i(-1, 0, 1),
        new Vec3i(1, 1, -1),
        new Vec3i(1, 1, 1),
        new Vec3i(-1, 1, -1),
        new Vec3i(-1, 1, 1)
    };

    private AnvilMode mode = AnvilMode.Mine;
    private String disableString;

    public ListenerAnvilAura(AnvilAura module, int priority)
    {
        super(module, priority);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (ANTISURROUND.returnIfPresent(AntiSurround::isActive, false))
        {
            return;
        }

        super.invoke(event);
    }

    @Override
    protected void pre(MotionUpdateEvent event)
    {
        mode = module.mode.getValue();
        module.action = null;
        module.renderBBs = Collections.emptyList();
        module.target = null;
        super.pre(event);
    }

    @Override
    protected TargetResult getTargets(TargetResult result)
    {
        if (mode == AnvilMode.Mine
            && !module.confirmMine.getValue()
            && !module.awaitTimer.passed(module.confirm.getValue())
            && module.awaiting.get())
        {
            BlockPos awaitPos = module.awaitPos;
            if (awaitPos != null)
            {
                HELPER.addBlockState(module.awaitPos,
                                     Blocks.AIR.getDefaultState());
            }
        }

        Set<AnvilResult> results = AnvilResult.create(mc.world.playerEntities,
                                                      mc.world.loadedEntityList,
                                                      module.yHeight.getValue(),
                                                      module.range.getValue());
        switch (mode)
        {
            case Render:
                List<AxisAlignedBB> renderBBs = new ArrayList<>(
                        module.renderBest.getValue() ? 1 : results.size());

                for (AnvilResult r : results)
                {
                    if (r.getMine()
                         .stream()
                         .anyMatch(p -> p.getY() > r.getPlayerPos().getY()))
                    {
                        continue;
                    }

                    BlockPos first = null;
                    BlockPos last  = null;
                    for (BlockPos pos : r.getPositions())
                    {
                        if (BlockUtil.getDistanceSq(pos)
                            >= MathUtil.square(module.range.getValue()))
                        {
                            continue;
                        }

                        if (first == null)
                        {
                            first = pos;
                        }

                        last = pos;
                    }

                    if (first == null)
                    {
                        continue;
                    }
                    // bbs could be put together?
                    AxisAlignedBB bb = new AxisAlignedBB(
                            first.getX(), first.getY(), first.getZ(),
                            last.getX() + 1, last.getY() + 1, last.getZ() + 1);

                    renderBBs.add(bb);

                    if (module.renderBest.getValue())
                    {
                        break;
                    }
                }

                module.renderBBs = renderBBs;
                break;
            case Pressure:
                for (AnvilResult r : results)
                {
                    if (!r.hasValidPressure() && !r.hasSpecialPressure()
                        || module.checkFalling.getValue()
                            && r.hasFallingEntities()
                            && (!r.hasSpecialPressure()
                                || !module.pressureFalling.getValue()))
                    {
                        continue;
                    }

                    boolean badMine = false;
                    for (BlockPos pos : r.getMine())
                    {
                        if (pos.getY() > r.getPlayerPos().getY())
                        {
                            badMine = true;
                            break;
                        }
                    }

                    if (badMine)
                    {
                        continue;
                    }

                    if (doTrap(r))
                    {
                        break;
                    }

                    BlockPos pressure = r.getPressurePos();
                    if (r.hasSpecialPressure()
                        || SpecialBlocks
                           .PRESSURE_PLATES
                           .contains(HELPER.getBlockState(pressure).getBlock()))
                    {
                        if (placeTop(r, result))
                        {
                            module.setCurrentResult(r);
                            break;
                        }

                        continue;
                    }

                    if (r.hasValidPressure() && module.pressureSlot != -1)
                    {
                        module.stage = AnvilStage.PRESSURE;
                        result.getTargets().add(pressure);
                        break;
                    }
                }

                break;
            case Mine:
                if (module.stage == AnvilStage.MINE)
                {
                    if (module.minePos != null && module.mineFacing != null)
                    {
                        module.rotations = RotationUtil.getRotations(
                                module.minePos, module.mineFacing);
                        if (module.mineTimer.passed(module.mineTime.getValue()))
                        {
                            module.awaitPos = module.minePos;
                            module.awaiting.set(true);
                            module.action = () ->
                            {
                                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                                {
                                    int last = mc.player.inventory.currentItem;
                                    InventoryUtil.switchTo(module.pickSlot);

                                    PacketUtil.stopDigging(
                                            module.minePos, module.mineFacing);

                                    mc.player.swingArm(EnumHand.MAIN_HAND);
                                    InventoryUtil.switchTo(last);
                                });

                                module.awaitTimer.reset();
                                Managers.BLOCKS.addCallback(module.minePos, s ->
                                {
                                    module.awaiting.set(false);
                                    module.awaitPos = null;
                                });

                                module.minePos    = null;
                                module.mineFacing = null;
                                module.action     = null;
                            };
                        }

                        break;
                    }
                    else if (module.confirmMine.getValue()
                        && !module.awaitTimer.passed(module.confirm.getValue())
                        && module.awaiting.get())
                    {
                        break;
                    }
                }

                for (AnvilResult r : results)
                {
                    if (module.checkFalling.getValue()
                            && (!r.hasSpecialPressure()
                                || !module.pressureFalling.getValue())
                            && r.hasFallingEntities())
                    {
                        continue;
                    }

                    if (doMine(r, result))
                    {
                        module.setCurrentResult(r);
                        break;
                    }
                }

                break;
            case High:
                for (AnvilResult r : results)
                {
                    if (module.checkFalling.getValue()
                            && (!r.hasSpecialPressure()
                                || !module.pressureFalling.getValue())
                            && r.hasFallingEntities())
                    {
                        continue;
                    }

                    boolean badMine = false;
                    for (BlockPos pos : r.getMine())
                    {
                        if (pos.getY() > r.getPlayerPos().getY())
                        {
                            badMine = true;
                            break;
                        }
                    }

                    if (!badMine && (doTrap(r) || placeTop(r, result)))
                    {
                        module.setCurrentResult(r);
                        break;
                    }
                }

                break;
            default:
        }

        return result;
    }

    @Override
    protected int getSlot()
    {
        module.obbySlot = InventoryUtil.findHotbarBlock(
                                            Blocks.OBSIDIAN);
        module.crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);
        module.crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);
        switch (mode)
        {
            case Pressure:
                module.pressureSlot = InventoryUtil.findHotbarBlock(
                        Blocks.WOODEN_PRESSURE_PLATE,
                        Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE,
                        Blocks.STONE_PRESSURE_PLATE,
                        Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);

                if (module.pressureSlot == -1
                        && !module.pressurePass.getValue())
                {
                    disableString = "Disabled, No Pressure Plates found!";
                    return -1;
                }

                break;
            case Mine:
                module.pickSlot = InventoryUtil.findInHotbar(s ->
                                            s.getItem() instanceof ItemPickaxe);
                if (module.pickSlot == -1)
                {
                    disableString = "Disabled, No Pickaxe found!";
                    return -1;
                }

                break;
            default:
        }

        return InventoryUtil.findHotbarBlock(Blocks.ANVIL);
    }

    @Override
    protected void disableModule()
    {
        if (module.holdingAnvil.getValue())
        {
            module.packets.clear();
            module.rotations = null;
            module.action = null;
            return;
        }

        super.disableModule();
    }

    @Override
    protected boolean update()
    {
        disableString = null;
        if (module.holdingAnvil.getValue()
                && !InventoryUtil.isHolding(Blocks.ANVIL))
        {
            return false;
        }

        return super.update();
    }

    @Override
    protected String getDisableString()
    {
        if (disableString != null)
        {
            return disableString;
        }

        return "Disabled, no Anvils.";
    }

    private boolean doTrap(AnvilResult anvilResult)
    {
        if (!module.trap.getValue() || module.obbySlot == -1)
        {
            return false;
        }

        BlockPos highest = findHighest(anvilResult);
        if (highest == null)
        {
            return false;
        }

        boolean didPlace = false;
        BlockPos[] ignore = toIgnore(anvilResult, highest);
        for (BlockPos pos : anvilResult.getTrap())
        {
            if (placed.containsKey(pos)
                    || !HELPER.getBlockState(pos)
                              .getMaterial()
                              .isReplaceable())
            {
                continue;
            }

            BasePath path = new BasePath(
                    RotationUtil.getRotationPlayer(),
                    pos,
                    module.trapHelping.getValue());

            PathFinder.findPath(
                    path,
                    module.range.getValue(),
                    mc.world.loadedEntityList,
                    module.smartRay.getValue(),
                    HELPER,
                    Blocks.OBSIDIAN.getDefaultState(),
                    PathFinder.CHECK,
                    ignore);

            int before = module.slot;
            module.slot = module.obbySlot;
            didPlace = ObbyUtil.place(module, path) || didPlace;
            module.slot = before;
        }

        if (didPlace)
        {
            module.stage = AnvilStage.OBSIDIAN;
            return true;
        }
        else if (module.crystal.getValue()
                && module.crystalSlot != -1
                && module.crystalTimer.passed(module.crystalDelay.getValue()))
        {
            BlockPos upUp = anvilResult.getPlayerPos().up(2);
            for (EntityEnderCrystal entity : mc.world.getEntitiesWithinAABB(
                    EntityEnderCrystal.class, new AxisAlignedBB(upUp)))
            {
                if (entity != null && !EntityUtil.isDead(entity))
                {
                    return false;
                }
            }

            for (Vec3i vec3i : CRYSTAL_OFFSETS)
            {
                BlockPos pos = anvilResult.getPlayerPos().add(vec3i);
                if (BlockUtil.canPlaceCrystal(pos, false, false))
                {
                    Entity entity = RotationUtil.getRotationPlayer();
                    int before = module.slot;
                    module.slot = module.crystalSlot;
                    module.rotations = RotationUtil.getRotations(
                                        pos.getX() + 0.5,
                                        pos.getY() + 1.0,
                                        pos.getZ() + 0.5,
                                        entity.posX,
                                        entity.posY,
                                        entity.posZ,
                                        mc.player.getEyeHeight());

                    RayTraceResult result = RayTraceUtil.getRayTraceResult(
                        module.rotations[0], module.rotations[1], 6.0f, entity);

                    if (!result.getBlockPos().equals(pos))
                    {
                        result = new RayTraceResult(
                                RayTraceResult.Type.MISS,
                                new Vec3d(0.5, 1.0, 0.5),
                                EnumFacing.UP,
                                BlockPos.ORIGIN);
                    }

                    module.placeBlock(
                        pos, result.sideHit, module.rotations, result.hitVec);

                    module.slot = before;
                    module.stage = AnvilStage.CRYSTAL;
                    module.crystalTimer.reset();
                    return true;
                }
            }
        }

        return false;
    }

    private boolean doMine(AnvilResult anvilResult,
                             TargetResult result)
    {
        if (doTrap(anvilResult))
        {
            return true;
        }

        BlockPos highest = null;
        for (BlockPos pos : anvilResult.getMine())
        {
            if (BlockUtil.getDistanceSq(pos)
                    >= MathUtil.square(module.mineRange.getValue()))
            {
                continue;
            }

            if (highest == null || highest.getY() < pos.getY())
            {
                highest = pos;
            }
        }

        if (highest != null)
        {
            module.mineFacing =
                    RayTraceUtil.getFacing(mc.player, highest, true);
            module.rotations =
                    RotationUtil.getRotations(highest, module.mineFacing);
            module.minePos = highest;
            IBlockState mineState = HELPER.getBlockState(highest);
            module.mineBB = mineState
                            .getSelectedBoundingBox(mc.world, highest)
                            .grow(0.0020000000949949026);
            module.mineTimer.reset();
            module.stage = AnvilStage.MINE;
            module.action = () ->
            {
                PacketUtil.startDigging(module.minePos, module.mineFacing);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                module.mineTimer.reset();
                module.action = null;
            };

            return true;
        }

        return placeTop(anvilResult, result);
    }

    private boolean placeTop(AnvilResult anvilResult, TargetResult result)
    {
        BlockPos highest = findHighest(anvilResult);
        if (highest == null)
        {
            return false;
        }

        for (EnumFacing facing : EnumFacing.HORIZONTALS)
        {
            BlockPos pos = highest.offset(facing);
            if (!HELPER.getBlockState(pos).getMaterial().isReplaceable())
            {
                result.getTargets().add(highest);
                module.stage = AnvilStage.ANVIL;
                return true;
            }
        }

        if (module.obbySlot == -1)
        {
            return false;
        }

        BlockPos[] ignore = toIgnore(anvilResult, highest);
        for (EnumFacing facing : EnumFacing.HORIZONTALS)
        {
            BlockPos pos = highest.offset(facing);

            BasePath path = new BasePath(
                    RotationUtil.getRotationPlayer(),
                    pos,
                    module.helpingBlocks.getValue());

            PathFinder.findPath(
                    path,
                    module.range.getValue(),
                    mc.world.loadedEntityList,
                    module.smartRay.getValue(),
                    HELPER,
                    Blocks.OBSIDIAN.getDefaultState(),
                    PathFinder.CHECK,
                    ignore);

            int before = module.slot;
            module.slot = module.obbySlot;
            if (ObbyUtil.place(module, path))
            {
                module.slot = before;
                module.stage = AnvilStage.OBSIDIAN;
                return true;
            }

            module.slot = before;
        }

        return false;
    }

    private BlockPos findHighest(AnvilResult anvilResult)
    {
        BlockPos highest = null;
        for (BlockPos pos : anvilResult.getPositions())
        {
            if (BlockUtil.getDistanceSq(pos)
                    >= MathUtil.square(module.range.getValue()))
            {
                continue;
            }

            if (highest == null || highest.getY() < pos.getY())
            {
                highest = pos;
            }
        }

        return highest;
    }

    private BlockPos[] toIgnore(AnvilResult anvilResult, BlockPos highest)
    {
        BlockPos base = anvilResult.getPressurePos();
        int length = highest.getY() - base.getY();
        BlockPos[] ignore = new BlockPos[length];
        for (int i = 0; i < length; i++)
        {
            ignore[i] = base.up(i + 1);
        }

        return ignore;
    }

}
