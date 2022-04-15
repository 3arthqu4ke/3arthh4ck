package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.Confirmer;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.*;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.*;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.helpers.Finishable;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceFactory;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.misc.MutableWrapper;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.util.*;

public abstract class AbstractCalculation<T extends CrystalData>
        extends Finishable implements Globals
{
    protected static final ModuleCache<Offhand> OFFHAND =
            Caches.getModule(Offhand.class);
    protected static final ModuleCache<LegSwitch> LEG_SWITCH =
            Caches.getModule(LegSwitch.class);
    protected static final ModuleCache<Speedmine> SPEEDMINE =
            Caches.getModule(Speedmine.class);
    protected static final ModuleCache<AntiSurround> ANTISURROUND =
            Caches.getModule(AntiSurround.class);

    protected final Set<BlockPos> blackList;
    protected final List<Entity> entities; // maybe filter these by distance?
    protected final AutoCrystal module;
    protected final List<EntityPlayer> raw;

    protected double maxY = Double.MAX_VALUE;
    protected List<EntityPlayer> friends;
    protected List<EntityPlayer> enemies;
    protected List<EntityPlayer> players;
    protected List<EntityPlayer> all;
    protected BreakData<T> breakData;
    protected PlaceData placeData;
    protected boolean scheduling;
    protected boolean attacking;
    protected boolean noPlace;
    protected boolean noBreak;
    protected boolean rotating;
    protected boolean placing;
    protected boolean fallback;
    protected int motionID;
    protected int count;
    protected int shieldCount;
    protected int shieldRange;

    public AbstractCalculation(AutoCrystal module,
                               List<Entity> entities,
                               List<EntityPlayer> players,
                               BlockPos...blackList)
    {
        noPlace = false;
        noBreak = false;
        motionID = module.motionID.get();
        if (blackList.length == 0)
        {
            this.blackList = new EmptySet<>();
        }
        else
        {
            this.blackList = new HashSet<>();
            for (BlockPos pos : blackList)
            {
                if (pos != null)
                {
                    this.blackList.add(pos);
                }
            }
        }

        this.module   = module;
        this.entities = entities;
        this.raw  = players;
    }

    public AbstractCalculation(AutoCrystal module,
                               List<Entity> entities,
                               List<EntityPlayer> players,
                               boolean breakOnly,
                               boolean noBreak,
                               BlockPos...blackList)
    {
        this(module, entities, players, blackList);
        this.noPlace = breakOnly;
        this.noBreak = noBreak;
    }

    protected abstract IBreakHelper<T> getBreakHelper();

    @Override
    protected void execute()
    {
        try
        {
            if (module.clearPost.getValue())
            {
                // hmm, this could cause us to not get anything done
                module.post.clear();
            }

            runCalc();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    private void runCalc()
    {
        if (check())
        {
            return;
        }

        if (module.forceAntiTotem.getValue()
                && module.antiTotem.getValue()
                && checkForceAntiTotem())
        {
            return;
        }

        float minDamage = module.getMinDamage();
        if (module.focusRotations.getValue()
                && !module.rotate.getValue().noRotate(ACRotate.Break)
                && focusBreak())
        {
            return;
        }
        else
        {
            module.focus = null;
        }

        if (breakData == null && breakCheck())
        {
            breakData = getBreakHelper()
                .getData(getBreakDataSet(), entities, all, friends);

            setCount(breakData, minDamage);
            if (evaluate(breakData))
            {
                return;
            }
        }
        else if (module.multiPlaceCalc.getValue())
        {
            if (breakData != null)
            {
                setCount(breakData, minDamage);
                breakData = null;
            }
            else
            {
                BreakData<T> onlyForCountData =
                    getBreakHelper()
                        .getData(new ArrayList<>(5), entities, all, friends);
                setCount(onlyForCountData, minDamage);
            }
        }

        if (placeCheck())
        {
            placeData = module.placeHelper.getData(all,
                                                   players,
                                                   enemies,
                                                   friends,
                                                   entities,
                                                   minDamage,
                                                   blackList,
                                                   maxY);

            // Check LegSwitch again theres some time passing during calc
            if (LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false)
                || ANTISURROUND.returnIfPresent(AntiSurround::isActive, false))
            {
                return;
            }

            if (place(placeData))
            {
                boolean passed = module.obbyCalcTimer
                                       .passed(module.obbyCalc.getValue());
                if (obbyCheck()
                    && passed
                    && placeObby(placeData, null))
                {
                    return;
                }

                if (preSpecialCheck()
                    && (!module.requireOnGround.getValue()
                        || RotationUtil.getRotationPlayer().onGround)
                    && (module.interruptSpeedmine.getValue()
                        || !SPEEDMINE.isEnabled()
                        || SPEEDMINE.get().getPos() == null)
                    && (!module.pickaxeOnly.getValue()
                        || mc.player.getHeldItemMainhand().getItem()
                                 instanceof ItemPickaxe)
                    && module.liquidTimer.passed(module.liqDelay.getValue())
                    && (module.lava.getValue() || module.water.getValue()))
                {
                    MineSlots mineSlots = HelperLiquids.getSlots(
                            module.requireOnGround.getValue());
                    if (mineSlots.getBlockSlot() == -1
                        || mineSlots.getDamage() < 1.0f)
                    {
                        return;
                    }

                    PlaceData liquidData = module.liquidHelper
                                                 .calculate(module.placeHelper,
                                                            placeData,
                                                            friends,
                                                         all,
                                                            module.minDamage
                                                                  .getValue());

                    // Check LegSwitch again some time passed during calc
                    if (LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false)
                        || ANTISURROUND.returnIfPresent(AntiSurround::isActive,
                                                                        false))
                    {
                        return;
                    }

                    boolean attackingBefore = attacking;
                    if (placeNoAntiTotem(liquidData, mineSlots)
                        && attackingBefore == attacking
                        && module.liquidObby.getValue()
                        && obbyCheck()
                        && passed)
                    {
                        placeObby(placeData, mineSlots);
                    }
                }
            }
        }
    }

    protected void setCount(BreakData<T> breakData, float minDmg)
    {
        shieldCount = breakData.getShieldCount();
        if (module.multiPlaceMinDmg.getValue())
        {
            count = (int) breakData.getData()
                                   .stream()
                                   .filter(d -> d.getDamage() > minDmg)
                                   .count();
            return;
        }

        count = breakData.getData().size();
    }

    protected boolean evaluate(BreakData<T> breakData)
    {
        // count = breakData.getData().size();
        boolean shouldDanger = module.shouldDanger();
        boolean slowReset = !shouldDanger;
        BreakValidity validity;
        if (this.breakData.getAntiTotem() != null
                && (validity =
                    HelperUtil.isValid(module, this.breakData.getAntiTotem()))
                        != BreakValidity.INVALID)
        {
            attack(this.breakData.getAntiTotem(), validity);
            module.breakTimer.reset(module.breakDelay.getValue());
            module.antiTotemHelper.setTarget(null);
            module.antiTotemHelper.setTargetPos(null);
        }
        else
        {
            int packets = !module.rotate.getValue().noRotate(ACRotate.Break)
                            ? 1
                            : module.packets.getValue();

            T firstRotation = null;
            List<T> valids = new ArrayList<>(packets);
            for (T data : this.breakData.getData())
            {
                validity = HelperUtil.isValid(module, data.getCrystal());
                if (validity == BreakValidity.VALID && valids.size() < packets)
                {
                    valids.add(data);
                }
                else if (validity == BreakValidity.ROTATIONS
                        && firstRotation == null)
                {
                    firstRotation = data;
                }
            }

            int slowDelay = module.slowBreakDelay.getValue();
            float slow = module.slowBreakDamage.getValue();
            if (valids.isEmpty())
            {
                if (firstRotation != null
                    && (module.shouldDanger()
                        || !(slowReset = firstRotation.getDamage() <= slow)
                        || module.breakTimer.passed(slowDelay)))
                {
                    attack(firstRotation.getCrystal(),
                           BreakValidity.ROTATIONS);
                }
            }
            else
            {
                for (T valid : valids)
                {
                    boolean high = valid.getDamage()
                                    > module.slowBreakDamage.getValue();
                    if (high
                        || shouldDanger
                        || module.breakTimer
                                 .passed(module.slowBreakDelay.getValue()))
                    {
                        slowReset = slowReset && !high;
                        attack(valid.getCrystal(), BreakValidity.VALID);
                    }
                }
            }
        }

        if (attacking)
        {
            module.breakTimer.reset(slowReset
                    ? module.slowBreakDelay.getValue()
                    : module.breakDelay.getValue());
        }

        return rotating && !module.rotate.getValue().noRotate(ACRotate.Place);
    }

    protected boolean breakCheck()
    {
        return module.attack.getValue()
                && !noBreak
                && Managers.SWITCH.getLastSwitch() >= module.cooldown.getValue()
                && module.breakTimer.passed(module.breakDelay.getValue());
    }

    protected boolean placeCheck()
    {
        if (module.damageSync.getValue())
        {
            Confirmer c = module.damageSyncHelper.getConfirmer();
            if (c.isValid() // This is mostly to confirm place/break
                && !(c.isPlaceConfirmed(module.placeConfirm.getValue())
                    && c.isBreakConfirmed(module.breakConfirm.getValue())))
            {
                // Could've been set to not valid
                if (c.isValid() && module.preSynCheck.getValue())
                {
                    return false;
                }
            }
        }

        return count < module.multiPlace.getValue()
                && Managers.SWITCH.getLastSwitch()
                    >= module.placeCoolDown.getValue()
                && module.place.getValue()
                && (!attacking || module.multiTask.getValue())
                && (!rotating
                    || module.rotate.getValue().noRotate(ACRotate.Place))
                && module.placeTimer.passed(module.placeDelay.getValue())
                && !noPlace;
    }

    protected boolean obbyCheck()
    {
        return preSpecialCheck()
                && module.obsidian.getValue()
                && module.obbyTimer.passed(module.obbyDelay.getValue());
    }

    protected boolean preSpecialCheck()
    {
        return !placing
                && placeData != null
                && (placeData.getTarget() != null
                    || module.targetMode.getValue() == Target.Damage)
                && !fallback;
    }

    protected boolean check()
    {
        if (!module.spectator.getValue() && mc.player.isSpectator()
            || ANTISURROUND.returnIfPresent(AntiSurround::isActive, false)
            || LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false)
            || raw == null
            || entities == null)
        {
            return true;
        }

        setFriendsAndEnemies();
        if (all.isEmpty() || module.isPingBypass())
        {
            return true;
        }

        if (!module.attackMode.getValue().shouldCalc()
                && module.autoSwitch.getValue() != AutoSwitch.Always
                && !module.weaknessHelper.canSwitch()
                && !module.switching)
        {
            return true;
        }

        return module.weaknessHelper.isWeaknessed()
                && module.antiWeakness.getValue() == AntiWeakness.None;
    }

    protected void setFriendsAndEnemies()
    {
        List<List<EntityPlayer>> split = CollectionUtil.split(raw,
            p -> p == null
                || EntityUtil.isDead(p)
                || p.equals(mc.player)
                || p.getDistanceSq(mc.player) >
                        MathUtil.square(module.targetRange.getValue())
                || DamageUtil.cacheLowestDura(p) && module.antiNaked.getValue(),
            Managers.FRIENDS::contains,
            Managers.ENEMIES::contains);
        // split.get(0) are the invalid players.
        this.friends = split.get(1);
        this.enemies = split.get(2);
        this.players = split.get(3);
        this.all = new ArrayList<>(enemies.size() + players.size());
        shieldRange += enemies.stream().peek(e -> all.add(e)).filter(e -> e.getDistanceSq(mc.player) <= MathUtil.square(module.shieldRange.getValue())).count();
        shieldRange += players.stream().peek(e -> all.add(e)).filter(e -> e.getDistanceSq(mc.player) <= MathUtil.square(module.shieldRange.getValue())).count();
        if (module.yCalc.getValue())
        {
            maxY = Double.MIN_VALUE;
            for (EntityPlayer player : all)
            {
                if (player.posY > maxY)
                {
                    maxY = player.posY;
                }
            }
        }
    }

    protected boolean attack(Entity entity,
                             BreakValidity validity)
    {
        /*if (first TODO: something like this, when we want
                     to autoswitch but position is blocked.
            && !module.attackMode.getValue().shouldAttack()
            && module.autoSwitch.getValue() != AutoSwitch.Always
            && !module.switching)
        {
            ifBlocked = () -> attack(entity, validity, false);
            return false;
        }*/

        module.setCrystal(entity);
        switch (validity)
        {
            case VALID:
                if (module.weaknessHelper.isWeaknessed())
                {
                    if (module.antiWeakness.getValue() == AntiWeakness.None)
                    {
                        return false;
                    }
                    else
                    {
                        Runnable r = module.rotationHelper.post(entity,
                                                   new MutableWrapper<>(false));
                        r.run();
                        attacking = true;

                        if (!module.rotate.getValue().noRotate(ACRotate.Break))
                        {
                            module.rotation =
                                    module.rotationHelper.forBreaking(entity,
                                            new MutableWrapper<>(true));
                        }

                        return true;
                    }
                }

                if (module.breakSwing.getValue() == SwingTime.Pre)
                {
                    Swing.Packet.swing(EnumHand.MAIN_HAND);
                }

                mc.player.connection.sendPacket(new CPacketUseEntity(entity));

                if (module.pseudoSetDead.getValue())
                {
                    ((IEntity) entity).setPseudoDead(true);
                }
                else if (module.setDead.getValue())
                {
                    Managers.SET_DEAD.setDead(entity);
                }

                if (module.breakSwing.getValue() == SwingTime.Post)
                {
                    Swing.Packet.swing(EnumHand.MAIN_HAND);
                }

                Swing.Client.swing(EnumHand.MAIN_HAND);
                attacking = true;

                if (!module.rotate.getValue().noRotate(ACRotate.Break))
                {
                    module.rotation =
                        module.rotationHelper.forBreaking(entity,
                                                    new MutableWrapper<>(true));
                }
                return true;
            case ROTATIONS:
                attacking = true;
                rotating = true;
                MutableWrapper<Boolean> attacked = new MutableWrapper<>(false);
                Runnable post =
                        module.rotationHelper.post(entity, attacked);
                RotationFunction function =
                        module.rotationHelper.forBreaking(entity, attacked);

                if (module.multiThread.getValue()
                    && module.rotationThread.getValue()
                                == RotationThread.Cancel
                    && module.rotationCanceller.setRotations(function)
                        && HelperUtil.isValid(module, entity)
                                == BreakValidity.VALID)
                {
                    rotating = false;
                    post.run();
                }
                else
                {
                    module.rotation = function;
                    module.post.add(post);
                }

                return true;
            case INVALID:
            default:
                return false;
        }
    }

    protected boolean checkForceAntiTotem()
    {
        BlockPos forcePos = module.forceHelper.getPos();
        if (forcePos != null
                && module.forceHelper.isForcing(module.syncForce.getValue()))
        {
            for (Entity entity : entities)
            {
                if (entity instanceof EntityEnderCrystal
                        && !EntityUtil.isDead(entity)
                        && entity.getEntityBoundingBox()
                                 .intersects(new AxisAlignedBB(forcePos.up())))
                {
                    attack(entity, HelperUtil.isValid(module, entity));
                    return true;
                }
            }

            return true;
        }

        return false;
    }

    protected boolean place(PlaceData data)
    {
        AntiTotemData antiTotem = null;
        boolean god = module.godAntiTotem.getValue()
                            && module.idHelper.isSafe(raw,
                                              module.holdingCheck.getValue(),
                                              module.toolCheck.getValue());
        for (AntiTotemData antiTotemData : data.getAntiTotem())
        {
            if (!antiTotemData.getCorresponding().isEmpty()
                    && !antiTotemData.isBlocked())
            {
                BlockPos pos = antiTotemData.getPos();
                Entity entity = new EntityEnderCrystal(
                    mc.world,
                    pos.getX() + 0.5f,
                    pos.getY() + 1,
                    pos.getZ() + 0.5f);

                if (god)
                {
                    for (PositionData positionData :
                            antiTotemData.getCorresponding())
                    {
                        if (positionData.isBlocked())
                        {
                            continue;
                        }

                        BlockPos up = positionData.getPos().up();
                        double y = module.newVerEntities.getValue() ? 1.0 : 2.0;
                        if (entity.getEntityBoundingBox().intersects(
                            // double check this sometime
                            new AxisAlignedBB(up.getX(),
                                              up.getY(),
                                              up.getZ(),
                                              up.getX() + 1.0,
                                              up.getY() + y,
                                              up.getZ() + 1.0)))
                        {
                            continue;
                        }

                        if (module.totemSync.getValue() &&
                                module.damageSyncHelper.isSyncing(0.0f, true))
                        {
                            return false;
                        }

                        module.noGod = true;
                        module.antiTotemHelper
                              .setTargetPos(antiTotemData.getPos());

                        EntityPlayer player = antiTotemData.getFirstTarget();
                        Earthhack.getLogger().info("Attempting God-AntiTotem: "
                                + (player == null ? "null" : player.getName()));

                        place(antiTotemData, player, false, false, false);

                        module.idHelper.attack(module.breakSwing.getValue(),
                                               module.godSwing.getValue(),
                                               1,
                                               module.idPackets.getValue(),
                                               0);

                        place(positionData, player, false, false, false);

                        module.idHelper.attack(module.breakSwing.getValue(),
                                               module.godSwing.getValue(),
                                               2,
                                               module.idPackets.getValue(),
                                               0);

                        module.breakTimer.reset(module.breakDelay.getValue());
                        module.noGod = false;
                        return false;
                    }
                }

                if (antiTotem == null)
                {
                    antiTotem = antiTotemData;
                    if (!god)
                    {
                        break;
                    }
                }
            }
        }

        if (antiTotem != null)
        {
            EntityPlayer player = antiTotem.getFirstTarget();
            module.setTarget(player);

            if (module.totemSync.getValue()
                    && module.damageSyncHelper.isSyncing(0.0f, true))
            {
                return false;
            }

            Earthhack.getLogger().info("Attempting AntiTotem: "
                    + (player == null ? "null" : player.getName()));

            module.antiTotemHelper.setTargetPos(antiTotem.getPos());
            place(antiTotem,
                  player,
                  !module.rotate.getValue().noRotate(ACRotate.Place),
                  rotating || scheduling,
                  false);

            return false;
        }

        if (module.forceAntiTotem.getValue()
                && module.antiTotem.getValue()
                && module.forceTimer.passed(module.attempts.getValue()))
        {
            // TODO: Could find the best ForceData by Player
            for (Map.Entry<EntityPlayer, ForceData> entry :
                                                data.getForceData().entrySet())
            {
                ForceData forceData = entry.getValue();
                PositionData first = forceData.getForceData()
                                              .stream()
                                              .findFirst()
                                              .orElse(null);
                if (first == null
                    || !forceData.hasPossibleAntiTotem()
                    || !forceData.hasPossibleHighDamage())
                {
                    continue;
                }

                if (module.syncForce.getValue()
                        && module.damageSyncHelper.isSyncing(0.0f, true))
                {
                    return false;
                }

                module.forceHelper.setSync(first.getPos(),
                                           module.newVerEntities.getValue());
                place(first,
                      entry.getKey(),
                      !module.rotate.getValue().noRotate(ACRotate.Place),
                      rotating || scheduling,
                      module.forceSlow.getValue());

                module.forceTimer.reset();
                return false;
            }
        }

        return placeNoAntiTotem(data, null);
    }

    protected boolean placeNoAntiTotem(PlaceData data, MineSlots liquid)
    {
        float maxBlockedDamage = 0.0f;
        PositionData firstData = null;
        for (PositionData d : data.getData())
        {
            if (d.isBlocked())
            {
                if (maxBlockedDamage < d.getMaxDamage())
                {
                    maxBlockedDamage = d.getMaxDamage();
                }

                continue;
            }

            firstData = d;
            break;
        }

        if (breakData != null && !attacking)
        {
            Entity fallback = breakData.getFallBack();
            if (module.fallBack.getValue()
                    && breakData.getFallBackDmg()
                        < module.fallBackDmg.getValue()
                    && fallback != null
                    && maxBlockedDamage != 0.0f
                    && (firstData == null
                    || maxBlockedDamage - firstData.getMaxDamage()
                        >= module.fallBackDiff.getValue()))
            {
                attack(fallback, HelperUtil.isValid(module, fallback));
                return false;
            }
        }

        if (firstData != null
                && !module.damageSyncHelper.isSyncing(firstData.getMaxDamage(),
                                                module.damageSync.getValue())
                && (liquid == null || module.minDamage.getValue()
                                            <= firstData.getMaxDamage()))
        {
            boolean slow = false;
            if (firstData.getMaxDamage() <= module.slowPlaceDmg.getValue()
                    && !module.shouldDanger())
            {
                if (module.placeTimer.passed(module.slowPlaceDelay.getValue()))
                {
                    slow = true;
                }
                else
                {
                    return !module.damageSyncHelper.isSyncing(0.0f,
                            module.damageSync.getValue());
                }
            }

            MutableWrapper<Boolean> liquidBreak = null;
            if (liquid != null)
            {
                liquidBreak = placeAndBreakLiquid(firstData, liquid, rotating);
            }

            place(firstData,
                  firstData.getTarget(),
                  !module.rotate.getValue().noRotate(ACRotate.Place),
                  rotating || scheduling,
                  slow,
                  slow ? firstData.getMaxDamage() : Float.MAX_VALUE,
                  liquidBreak);

            return false;
        }

        Optional<PositionData> shield;
        if (module.shield.getValue()
            && module.shieldTimer.passed(module.shieldDelay.getValue())
            && (shieldCount < module.shieldCount.getValue() || !attacking)
            && (shield = data.getShieldData().stream().findFirst()).isPresent()
            && placeData.getHighestSelfDamage()
                >= module.shieldMinDamage.getValue()
            && shieldRange > 0)
        {
            place(shield.get(),
                  shield.get().getTarget(),
                  !module.rotate.getValue().noRotate(ACRotate.Place),
                  rotating || scheduling,
                  false,
                  Float.MAX_VALUE,
                  null,
                  true);

            module.shieldTimer.reset();
            return false;
        }

        return !module.damageSyncHelper.isSyncing(0.0f,
                                                  module.damageSync.getValue());
    }

    protected void place(PositionData data,
                         EntityPlayer target,
                         boolean rotate,
                         boolean schedule,
                         boolean resetSlow)
    {
        place(data, target, rotate, schedule, resetSlow, Float.MAX_VALUE, null);
    }

    protected void place(PositionData data,
                         EntityPlayer target,
                         boolean rotate,
                         boolean schedule,
                         boolean resetSlow,
                         float damage,
                         MutableWrapper<Boolean> liquidBreak) {
        place(data, target, rotate, schedule, resetSlow, damage, liquidBreak,
              false);
    }

    protected void place(PositionData data,
                         EntityPlayer target,
                         boolean rotate,
                         boolean schedule,
                         boolean resetSlow,
                         float damage,
                         MutableWrapper<Boolean> liquidBreak,
                         boolean shield) {
        if (liquidBreak != null) {
            module.liquidTimer.reset();
        }

        module.placeTimer.reset(resetSlow ? module.slowPlaceDelay.getValue()
                                    : module.placeDelay.getValue());
        BlockPos pos = data.getPos();
        BlockPos crystalPos = new BlockPos(
            pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f);

        module.placed.put(crystalPos, new CrystalTimeStamp(damage, shield));
        module.damageSyncHelper.setSync(pos,
                                        data.getMaxDamage(),
                                        module.newVerEntities.getValue());
        module.setTarget(target);
        boolean realtime = module.realtime.getValue();
        if (!realtime) {
            module.setRenderPos(pos, data.getMaxDamage());
        }

        MutableWrapper<Boolean> hasPlaced = new MutableWrapper<>(false);
        if (!InventoryUtil.isHolding(Items.END_CRYSTAL)) {
            if (module.autoSwitch.getValue() == AutoSwitch.Always
                || module.autoSwitch.getValue() == AutoSwitch.Bind
                && module.switching) {
                if (!module.mainHand.getValue()) {
                    mc.addScheduledTask(() ->
                                        {
                                            OFFHAND.computeIfPresent(o ->
                                                                         o.setMode(OffhandMode.CRYSTAL));
                                        });
                }
            }
        }

        Runnable post = module.rotationHelper.post(
            module, data.getMaxDamage(),
            realtime, pos, hasPlaced, liquidBreak);
        if (rotate) {
            RotationFunction function =
                module.rotationHelper.forPlacing(pos, hasPlaced);

            if (module.rotationCanceller.setRotations(function)) {
                module.runPost();
                post.run();

                if (module.attack.getValue() && hasPlaced.get()) {
                    module.rotation = function;
                }

                return;
            }

            module.rotation = function;
        }

        if (schedule || !placeCheckPre(pos)) {
            module.post.add(post);
        } else {
            post.run();
        }
    }

    /**
     * @return <tt>true</tt> if obby has been placed.
     */
    protected boolean placeObby(PlaceData data,
                                MineSlots liquid)
    {
        PositionData bestData = module.obbyHelper
                                      .findBestObbyData(liquid != null
                                                        ? data.getLiquidObby()
                                                        : data.getAllObbyData(),
                                              all,
                                                        friends,
                                                        entities,
                                                        data.getTarget(),
                                                        module.newVer
                                                              .getValue());

        // Check LegSwitch again theres some time passing during calc
        if (LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false)
                || ANTISURROUND.returnIfPresent(AntiSurround::isActive, false))
        {
            return true;
        }

        module.obbyCalcTimer.reset();
        if (bestData != null
            && bestData.getMaxDamage() > module.obbyMinDmg.getValue())
        {
            module.setTarget(bestData.getTarget());
            if (module.obbyRotate.getValue() != Rotate.None
                    && !rotating
                    && bestData.getPath().length > 0)
            {
                module.rotation = module.rotationHelper.forObby(bestData);
                rotating = true;
            }

            Runnable r = module.rotationHelper.postBlock(bestData);
            if (!rotating)
            {
                r.run();
            }
            else
            {
                module.post.add(r);
            }

            if (liquid != null)
            {
                placeAndBreakLiquid(bestData, liquid, rotating);
            }

            place(bestData,
                  bestData.getTarget(),
                  !module.rotate.getValue().noRotate(ACRotate.Place),
                  rotating || scheduling,
                  false);

            module.obbyTimer.reset();
            return true;
        }

        return false;
    }

    @Override
    public void setFinished(boolean finished)
    {
        if (module.multiThread.getValue()
            && module.smartPost.getValue()
            && module.motionID.get() != motionID)
        {
            module.runPost();
        }

        super.setFinished(finished);
        if (finished)
        {
            synchronized (module)
            {
                module.notifyAll();
            }
        }
    }

    protected boolean placeCheckPre(BlockPos pos)
    {
        double x = Managers.POSITION.getX();
        double y = Managers.POSITION.getY();
        double z = Managers.POSITION.getZ();

        if (pos.distanceSqToCenter(x, y, z)
                >= MathUtil.square(module.placeRange.getValue()))
        {
            return false;
        }

        if (!module.rotate.getValue().noRotate(ACRotate.Place))
        {
            RayTraceResult result = RotationUtil.rayTraceTo(pos, mc.world);
            if (result == null || !result.getBlockPos().equals(pos))
            {
                return false;
            }
        }

        if (pos.distanceSqToCenter(x, y, z)
                >= MathUtil.square(module.placeTrace.getValue()))
        {
            RayTraceResult result = RotationUtil.rayTraceTo(pos,
                                                            mc.world,
                                                            (b,p) -> true);

            if (result != null && !result.getBlockPos().equals(pos))
            {
                // TODO: what even is this?
                //noinspection deprecation
                return module.ignoreNonFull.getValue()
                    && !mc.world.getBlockState(result.getBlockPos())
                                .getBlock()
                                .isFullBlock(mc.world.getBlockState(
                                    result.getBlockPos()));
            }

            return result != null && result.getBlockPos().equals(pos);
        }

        return true;
    }

    protected MutableWrapper<Boolean> placeAndBreakLiquid(PositionData data,
                                                          MineSlots liquid,
                                                          boolean rotating)
    {
        boolean newVer = module.newVer.getValue();
        boolean absorb = module.absorb.getValue();
        List<Ray> path = new ArrayList<>((newVer ? 1 : 2) + (absorb ? 1 : 0));
        BlockStateHelper access = new BlockStateHelper();
        path.add(RayTraceFactory.rayTrace(data.getFrom(),
                                           data.getPos(),
                                           EnumFacing.UP,
                                           access,
                                           Blocks.NETHERRACK.getDefaultState(),
                                           module.liquidRayTrace.getValue()
                                                ? -1.0
                                                : 2.0));
        int[] order;
        BlockPos up = data.getPos().up();
        access.addBlockState(up, Blocks.NETHERRACK.getDefaultState());
        IBlockState upState = mc.world.getBlockState(up);
        if (!newVer && upState.getMaterial().isLiquid())
        {
            path.add(RayTraceFactory.rayTrace(data.getFrom(),
                                           up,
                                           EnumFacing.UP,
                                           access,
                                           Blocks.NETHERRACK.getDefaultState(),
                                           module.liquidRayTrace.getValue()
                                                ? -1.0
                                                : 2.0));

            access.addBlockState(up.up(), Blocks.NETHERRACK.getDefaultState());
            order = new int[] { 0, 1 };
        }
        else
        {
            order = new int[] { 0 };
        }

        if (absorb)
        {
            BlockPos absorpPos = up;
            EnumFacing absorbFacing = module.liquidHelper.getAbsorbFacing(
                    absorpPos, entities, access, module.placeRange.getValue());
            if (absorbFacing == null && !newVer)
            {
                absorpPos = up.up();
                absorbFacing = module.liquidHelper.getAbsorbFacing(
                    absorpPos, entities, access, module.placeRange.getValue());
            }

            if (absorbFacing != null)
            {
                path.add(RayTraceFactory.rayTrace(data.getFrom(),
                                            absorpPos,
                                            absorbFacing,
                                            access,
                                            Blocks.NETHERRACK.getDefaultState(),
                                            module.liquidRayTrace.getValue()
                                                    ? -1.0
                                                    : 2.0));
                order = order.length == 2
                        ? new int[] { 2, 1, 0 }
                        : new int[] { 1, 0 };
            }
        }

        Ray[] pathArray = path.toArray(new Ray[0]);
        data.setPath(pathArray);
        data.setValid(true);
        MutableWrapper<Boolean> placed = new MutableWrapper<>(false);
        MutableWrapper<Integer> postBlock = new MutableWrapper<>(-1);
        Runnable r = module.rotationHelper.postBlock(
                      data, liquid.getBlockSlot(), module.liqRotate.getValue(),
                      placed, postBlock);
        Runnable b = module.rotationHelper.breakBlock(
                      liquid.getToolSlot(), placed, postBlock, order, pathArray);
        Runnable a = null;
        if (module.setAir.getValue())
        {
            a = () ->
            {
                for (Ray ray : path)
                {
                    mc.world.setBlockState(ray.getPos().offset(ray.getFacing()),
                                           Blocks.AIR.getDefaultState());
                }
            };
        }

        if (rotating)
        {
            synchronized (module.post)
            {
                module.post.add(r);
                module.post.add(b);
                if (a != null)
                {
                    mc.addScheduledTask(a);
                }
            }
        }
        else
        {
            r.run();
            b.run();
            if (a != null)
            {
                mc.addScheduledTask(a);
            }
        }

        return placed;
    }

    protected boolean focusBreak()
    {
        Entity focus = module.focus;
        if (focus != null)
        {
            if (EntityUtil.isDead(focus)
                || Managers.POSITION.getDistanceSq(focus)
                        > MathUtil.square(module.breakRange.getValue())
                    && RotationUtil.getRotationPlayer().getDistanceSq(focus)
                        > MathUtil.square(module.breakRange.getValue()))
            {
                module.focus = null;
                return false;
            }
            else
            {
                double exponent = module.focusExponent.getValue();
                breakData = getBreakHelper()
                    .getData(module.focusAngleCalc.getValue()
                             && exponent != 0.0
                                ?   RotationComparator.asSet(
                                        exponent,
                                        module.focusDiff.getValue())
                                :   getBreakDataSet(),
                           entities,
                           all,
                           friends);

                List<T> focusList = new ArrayList<>(1);
                BreakData<T> focusData = getBreakHelper()
                    .newData(focusList);

                T minData = null;
                double minAngle = Double.MAX_VALUE;
                for (T data : breakData.getData())
                {
                    if (data.hasCachedRotations() && data.getAngle() < minAngle)
                    {
                        minAngle = data.getAngle();
                        minData  = data;
                    }

                    if (!data.getCrystal().equals(focus))
                    {
                        continue;
                    }

                    if (data.getSelfDmg()
                                > module.maxSelfBreak.getValue()
                            || data.getDamage()
                                < module.minBreakDamage.getValue())
                    {
                        return false;
                    }

                    focusData.getData().add(data);
                }

                Optional<T> first = focusData.getData().stream().findFirst();
                if (!first.isPresent())
                {
                    module.focus = null;
                    return false;
                }

                if (module.focusAngleCalc.getValue()
                        && minData != null
                        && !minData.equals(first.get()))
                {
                    focusList.set(0, minData);
                }

                evaluate(focusData);
                return rotating || attacking;
            }
        }

        return false;
    }

    protected Set<T> getBreakDataSet()
    {
        double exponent = module.rotationExponent.getValue();
        if (Double.compare(exponent, 0.0) == 0
            || module.rotate.getValue().noRotate(ACRotate.Break))
        {
            return new TreeSet<>();
        }

        return RotationComparator.asSet(exponent, module.minRotDiff.getValue());
    }

}
