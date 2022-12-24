package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.combat.anvilaura.AnvilAura;
import me.earth.earthhack.impl.modules.combat.surround.Surround;
import me.earth.earthhack.impl.modules.movement.step.Step;
import me.earth.earthhack.impl.modules.player.automine.mode.AutoMineMode;
import me.earth.earthhack.impl.modules.player.automine.util.BigConstellation;
import me.earth.earthhack.impl.modules.player.automine.util.Constellation;
import me.earth.earthhack.impl.modules.player.automine.util.CrystalConstellation;
import me.earth.earthhack.impl.modules.player.automine.util.EchestConstellation;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;

final class ListenerUpdate extends ModuleListener<AutoMine, UpdateEvent>
{
    private static final ModuleCache<Speedmine> SPEED_MINE =
        Caches.getModule(Speedmine.class);
    private static final ModuleCache<AnvilAura> ANVIL_AURA =
        Caches.getModule(AnvilAura.class);
    private static final ModuleCache<AntiSurround> ANTISURROUND =
        Caches.getModule(AntiSurround.class);
    private static final ModuleCache<Surround> SURROUND =
        Caches.getModule(Surround.class);
    private static final ModuleCache<Step> STEP =
        Caches.getModule(Step.class);

    private Set<BlockPos> surrounding = Collections.emptySet();

    public ListenerUpdate(AutoMine module)
    {
        super(module, UpdateEvent.class, 1);
    }

    @Override
    public void invoke(UpdateEvent event)
    {
        surrounding = Collections.emptySet();
        if (ANTISURROUND.returnIfPresent(AntiSurround::isActive, false)
            || ANVIL_AURA.isEnabled() && ANVIL_AURA.get().isMining())
        {
            return;
        }

        if (!SPEED_MINE.isPresent())
        {
            ModuleUtil.disable(module, TextColor.RED
                + "Disabled, Speedmine isn't registered on"
                + " this version of the client!");
            return;
        }

        if ((module.mode.getValue() == AutoMineMode.Combat
            || module.mode.getValue() == AutoMineMode.AntiTrap)
            && (!SPEED_MINE.isEnabled()
            || !(SPEED_MINE.get().getMode() == MineMode.Smart
                || SPEED_MINE.get().getMode() == MineMode.Fast
                || SPEED_MINE.get().getMode() == MineMode.Instant)))
        {
            if (module.disableOnNoSpeedmine.getValue())
            {
                ModuleUtil.disable(module, TextColor.RED
                 + "Disabled, enable Speedmine - Smart for AutoMine - Combat!");
            }

            return;
        }

        if (mc.player.isCreative()
            || mc.player.isSpectator()
            || !module.timer.passed(module.delay.getValue())
            || (module.mode.getValue() == AutoMineMode.Combat
            && SPEED_MINE.get().getPos() != null
            && (module.current == null
            || !module.current.equals(SPEED_MINE.get().getPos()))))
        {
            return;
        }

        BlockPos invalid = null;
        if (module.constellation != null)
        {
            module.constellation.update(module);
        }

        if (module.constellationCheck.getValue()
            && module.constellation != null)
        {
            if (module.constellation.isValid(mc.world,
                                             module.checkPlayerState.getValue())
                && !module.constellationTimer.passed(module.maxTime.getValue())
                && module.constellation.cantBeImproved())
            {
                return;
            }

            if (module.constellation.cantBeImproved())
            {
                invalid = module.current;
                module.constellation = null;
                module.current = null;
            }
        }

        if (!module.improve.getValue()
            && module.constellation != null
            && (!module.improveInvalid.getValue()
            || module.constellation.isValid(mc.world, module.checkPlayerState.getValue()))
            && module.constellation.cantBeImproved())
        {
            return;
        }

        module.blackList.entrySet().removeIf(e ->
                                                 (System.currentTimeMillis() - e.getValue()) / 1000.0f
                                                     > module.blackListFor.getValue());

        if (module.mode.getValue() == AutoMineMode.Combat)
        {
            if (module.noSelfMine.getValue() && SURROUND.isPresent())
            {
                surrounding = SURROUND.get().createSurrounding(SURROUND.get().createBlocked(),
                                                               Managers.ENTITIES.getPlayers());
            }

            if (module.prioSelf.getValue()
                && (!module.prioSelfWithStep.getValue() || STEP.isEnabled())
                && checkSelfTrap()
                || checkEnemies(false))
            {
                return;
            }

            BlockPos position = PositionUtil.getPosition();
            if (module.self.getValue()
                && ((!module.prioSelf.getValue()
                    || module.prioSelfWithStep.getValue() && !STEP.isEnabled())
                && checkSelfTrap()
                || checkPos(mc.player, position)))
            {
                return;
            }

            if (module.mineBurrow.getValue() && checkEnemies(true))
            {
                return;
            }

            IBlockState state;
            if (module.selfEchestMine.getValue()
                && module.isValid(Blocks.ENDER_CHEST.getDefaultState())
                && (state = mc.world.getBlockState(position))
                .getBlock() == Blocks.ENDER_CHEST)
            {
                attackPos(position,
                          new Constellation(mc.world,
                                            mc.player,
                                            position,
                                            position,
                                            state,
                                            module));
                return;
            }

            if (invalid != null
                && invalid.equals(SPEED_MINE.get().getPos())
                && module.resetIfNotValid.getValue())
            {
                SPEED_MINE.get().reset();
            }

            if (module.constellation == null
                && module.echest.getValue())
            {
                TileEntity closest = null;
                double minDist = Double.MAX_VALUE;
                for (TileEntity entity : mc.world.loadedTileEntityList)
                {
                    if (entity instanceof TileEntityEnderChest
                        && BlockUtil.getDistanceSq(entity.getPos())
                        < MathUtil.square(module.echestRange.getValue()))
                    {
                        double dist = entity.getPos().distanceSqToCenter(
                            RotationUtil.getRotationPlayer().posX,
                            RotationUtil.getRotationPlayer().posY
                                + mc.player.getEyeHeight(),
                            RotationUtil.getRotationPlayer().posZ);

                        if (dist < minDist)
                        {
                            minDist = dist;
                            closest = entity;
                        }
                    }
                }

                if (closest != null)
                {
                    module.offer(new EchestConstellation(closest.getPos()));
                    module.attackPos(closest.getPos());
                    return;
                }
            }

            if ((module.constellation == null
                || !module.constellation.cantBeImproved()
                && !(module.constellation instanceof BigConstellation))
                && module.terrain.getValue()
                && module.terrainTimer.passed(module.terrainDelay.getValue())
                && module.future == null
                && (!module.checkCrystalDownTime.getValue()
                || module.downTimer.passed(module.downTime.getValue())))
            {
                boolean c = module.closestPlayer.getValue();
                double closest = Double.MAX_VALUE;
                EntityPlayer best = null;
                List<EntityPlayer> players = new ArrayList<>(c ? 0 : 10);
                for (EntityPlayer p : mc.world.playerEntities)
                {
                    if (p == null
                        || EntityUtil.isDead(p)
                        || p.getDistanceSq(RotationUtil.getRotationPlayer())
                        > 400
                        || Managers.FRIENDS.contains(p))
                    {
                        continue;
                    }

                    if (c)
                    {
                        double dist =
                            p.getDistanceSq(RotationUtil.getRotationPlayer());
                        if (dist < closest)
                        {
                            closest = dist;
                            best = p;
                        }
                    }
                    else
                    {
                        players.add(p);
                    }
                }

                if (c && best == null || !c && players.isEmpty())
                {
                    return;
                }

                List<Entity> entities = mc
                    .world
                    .loadedEntityList
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(e -> !(e instanceof EntityItem)) // we ignore items
                    .filter(e -> !EntityUtil.isDead(e))
                    .filter(e ->
                                e.getDistanceSq(RotationUtil.getRotationPlayer())
                                    < MathUtil.square(module.range.getValue()))
                    .collect(Collectors.toList());

                AutoMineCalc calc = new AutoMineCalc(
                    module,
                    players,
                    surrounding,
                    entities,
                    best,
                    module.minDmg.getValue(),
                    module.maxSelfDmg.getValue(),
                    module.range.getValue(),
                    module.obbyPositions.getValue(),
                    module.newV.getValue(),
                    module.newVEntities.getValue(),
                    module.mineObby.getValue(),
                    module.breakTrace.getValue(),
                    module.suicide.getValue());

                module.future = Managers.THREAD.submit(calc);
                module.terrainTimer.reset();
            }
        }
        else if (module.mode.getValue() == AutoMineMode.AntiTrap)
        {
            BlockPos boost = PositionUtil.getPosition().up(2);
            if (!boost.equals(module.last) && !MovementUtil.isMoving())
            {
                SPEED_MINE.get().getTimer().setTime(0);
                module.current = boost;
                mc.playerController.onPlayerDamageBlock(boost, EnumFacing.DOWN);
                module.timer.reset();
                module.last = boost;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private boolean checkEnemies(boolean burrow)
    {
        BlockPos closestPos = null;
        Constellation closest = null;
        double distance = Double.MAX_VALUE;
        for (EntityPlayer player : mc.world.playerEntities)
        {
            if (EntityUtil.isValid(player, module.range.getValue() + 1)
                && !player.equals(mc.player))
            {
                BlockPos playerPos = PositionUtil.getPosition(player);
                if (burrow)
                {
                    double dist = mc.player.getDistanceSq(playerPos);
                    if (dist >= distance)
                    {
                        continue;
                    }

                    IBlockState state;
                    if (!isValid(playerPos,
                                 (state = mc.world.getBlockState(playerPos))))
                    {
                        continue;
                    }

                    closestPos = playerPos;
                    closest = new Constellation(mc.world,
                                                player,
                                                playerPos,
                                                playerPos,
                                                state,
                                                module);
                    closest.setBurrow(true);
                    distance = dist;
                    continue;
                }

                IBlockState playerPosState = mc.world.getBlockState(playerPos);
                if (playerPosState.getMaterial().isReplaceable()
                    || playerPosState
                    .getBlock()
                    .getExplosionResistance(mc.player) < 100)
                {
                    // TODO: up in case player phases
                    BlockPos upUp = playerPos.up(2);
                    IBlockState headState = mc.world.getBlockState(upUp);
                    if (module.head.getValue() || module.crystal.getValue())
                    {
                        if (module.head.getValue() && isValid(upUp, headState)
                            || module.crystal.getValue()
                            && headState.getBlock() == Blocks.OBSIDIAN
                            && module.isValidCrystalPos(upUp))
                        {
                            attackPos(upUp,
                                      new CrystalConstellation(mc.world,
                                                               player,
                                                               upUp,
                                                               playerPos,
                                                               headState,
                                                               module));
                            return true;
                        }
                    }

                    for (EnumFacing facing : EnumFacing.HORIZONTALS)
                    {
                        BlockPos tempUpUp;
                        IBlockState tempHeadState = headState;

                        BlockPos offset = playerPos.offset(facing);
                        IBlockState state = mc.world.getBlockState(offset);
                        // in a 2x1 this won't cover these blocks (p)
                        //      p x
                        //    x a a x
                        //      p x
                        // but its fine, because that's covered by mineL
                        if (state.getBlock() == Blocks.AIR
                            && player.getEntityBoundingBox().intersects(new AxisAlignedBB(offset)))
                        {
                            // TODO: we should also take offset.up(1) for crystal kinda
                            tempUpUp  = offset.up(2);
                            tempHeadState = mc.world.getBlockState(tempUpUp);
                            offset = offset.offset(facing);
                            state = mc.world.getBlockState(offset);
                        }

                        double dist = mc.player.getDistanceSq(offset);
                        if (dist >= distance)
                        {
                            continue;
                        }

                        boolean valid = isValid(offset, state);
                        if (valid)
                        {
                            if (module.mineL.getValue()
                                && mc.world.getBlockState(offset.up())
                                           .getMaterial()
                                           .isReplaceable())
                            {
                                boolean found = false;
                                for (EnumFacing l : EnumFacing.HORIZONTALS)
                                {
                                    if (l == facing || l == facing.getOpposite())
                                    {
                                        continue;
                                    }

                                    if (module.checkCrystalPos(offset.offset(l)
                                                                     .down()))
                                    {
                                        closestPos = offset;
                                        closest = new Constellation(mc.world,
                                                                    player,
                                                                    offset,
                                                                    playerPos,
                                                                    state,
                                                                    module);
                                        closest.setL(true);
                                        distance = dist;
                                        found = true;
                                        break;
                                    }
                                }

                                if (found)
                                {
                                    continue;
                                }
                            }

                            BlockPos finalOffset = offset;
                            if (module.checkCrystalPos(offset.offset(facing).down())
                                && (!(module.dependOnSMCheck.getValue() || module.speedmineCrystalDamageCheck.getValue())
                                    || SPEED_MINE.returnIfPresent(sm -> sm.crystalHelper.calcCrystal(finalOffset, player, true), null) != null))
                            {
                                closestPos = offset;
                                closest = new Constellation(mc.world,
                                                            player,
                                                            offset,
                                                            playerPos,
                                                            state,
                                                            module);
                                distance = dist;
                            }
                        }

                        if (module.crystal.getValue() && (valid && module.isValidCrystalPos(offset)
                            || module.isValidCrystalPos((offset = offset.up()))
                            && tempHeadState.getBlock() == Blocks.AIR
                            && isValid(offset, (state = mc.world.getBlockState(offset)))))
                        {
                            closestPos = offset;
                            closest = new CrystalConstellation(mc.world,
                                                               player,
                                                               offset,
                                                               playerPos,
                                                               state,
                                                               module);
                            distance = dist;
                        }
                    }
                }
            }
        }

        if (closest != null)
        {
            attackPos(closestPos, closest);
            return true;
        }

        return false;
    }

    private boolean checkSelfTrap()
    {
        BlockPos playerPos = PositionUtil.getPosition();
        BlockPos upUp = playerPos.up(2);
        IBlockState state = mc.world.getBlockState(upUp);
        if (isValid(upUp, state))
        {
            Constellation c = new Constellation(mc.world,
                                                mc.player,
                                                upUp,
                                                playerPos,
                                                state,
                                                module);
            attackPos(upUp, c);
            c.setSelfUntrap(true);
            return true;
        }

        return false;
    }

    private boolean checkPos(EntityPlayer player, BlockPos playerPos)
    {
        for (EnumFacing facing : EnumFacing.HORIZONTALS)
        {
            BlockPos offset = playerPos.offset(facing);
            IBlockState state = mc.world.getBlockState(offset);
            if (isValid(offset, state)
                && module.checkCrystalPos(offset.offset(facing).down()))
            {
                attackPos(offset,
                          new Constellation(mc.world,
                                            player,
                                            offset,
                                            playerPos,
                                            state,
                                            module));
                return true;
            }
        }

        return false;
    }

    private boolean isValid(BlockPos pos, IBlockState state)
    {
        return !module.blackList.containsKey(pos)
            && !surrounding.contains(pos)
            && MineUtil.canBreak(state, pos)
            && module.isValid(state)
            && mc.player.getDistanceSq(pos) <= MathUtil
            .square(SPEED_MINE
                        .get()
                        .getRange())
            && !state.getMaterial().isReplaceable();
    }

    public void attackPos(BlockPos pos, Constellation c)
    {
        // just so I can test if this is necessary ?
        if (module.checkCurrent.getValue()
            && pos.equals(module.current))
        {
            return;
        }

        module.offer(c);
        module.attackPos(pos);
    }

}
