package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntityLivingBase;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.safety.Safety;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiFriendPop;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.Target;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.*;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceFactory;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Set;

public class HelperPlace implements Globals
{
    private static final SettingCache<Float, NumberSetting<Float>, Safety> MD =
        Caches.getSetting(Safety.class, NumberSetting.class, "MaxDamage", 4.0f);

    private final AutoCrystal module;

    public HelperPlace(AutoCrystal module)
    {
        this.module = module;
    }

    public PlaceData getData(List<EntityPlayer> general,
                             List<EntityPlayer> players,
                             List<EntityPlayer> enemies,
                             List<EntityPlayer> friends,
                             List<Entity> entities,
                             float minDamage,
                             Set<BlockPos> blackList,
                             double maxY)
    {
        PlaceData data = new PlaceData(minDamage);
        EntityPlayer target = module.targetMode.getValue().getTarget(
                players, enemies, module.targetRange.getValue());

        if (target == null && module.targetMode.getValue() != Target.Damage)
        {
            return data;
        }

        data.setTarget(target);
        evaluate(data, general, friends, entities, blackList, maxY);
        data.addAllCorrespondingData();
        return data;
    }

    private void evaluate(PlaceData data,
                          List<EntityPlayer> players,
                          List<EntityPlayer> friends,
                          List<Entity> entities,
                          Set<BlockPos> blackList,
                          double maxY)
    {
        boolean obby = module.obsidian.getValue()
                && module.obbyTimer.passed(module.obbyDelay.getValue())
                && (InventoryUtil.isHolding(Blocks.OBSIDIAN)
                    || module.obbySwitch.getValue()
                    && InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN) != -1);

        switch (module.preCalc.getValue())
        {
            case Damage:
                for (EntityPlayer player : players)
                {
                    preCalc(data, player, obby, entities, friends, blackList);
                }
            case Target:
                if (data.getTarget() == null)
                {
                    if (data.getData().isEmpty())
                    {
                        break;
                    }
                }
                else
                {
                    preCalc(data, data.getTarget(),
                            obby, entities, friends, blackList);
                }

                for (PositionData positionData : data.getData())
                {
                    if (positionData.getMaxDamage()
                                > data.getMinDamage()
                            && positionData.getMaxDamage()
                                > module.preCalcDamage.getValue())
                    {
                        return;
                    }
                }

                break;
            default:
        }

        BlockPos middle =
                PositionUtil.getPosition(RotationUtil.getRotationPlayer());

        int maxRadius = Sphere.getRadius(module.placeRange.getValue());
        for (int i = 1; i < maxRadius; i++)
        {
            calc(middle.add(Sphere.get(i)), data, players, friends,
                 entities, obby, blackList, maxY);
        }
    }

    private void preCalc(PlaceData data,
                         EntityPlayer player,
                         boolean obby,
                         List<Entity> entities,
                         List<EntityPlayer> friends,
                         Set<BlockPos> blackList)
    {
        BlockPos pos = PositionUtil.getPosition(player).down();
        for (EnumFacing facing : EnumFacing.HORIZONTALS)
        {
            PositionData pData = selfCalc(data, pos.offset(facing),
                                          entities, friends, obby, blackList);
            if (pData == null)
            {
                continue;
            }

            checkPlayer(data, player, pData);
        }
    }

    private PositionData selfCalc(PlaceData placeData,
                                  BlockPos pos,
                                  List<Entity> entities,
                                  List<EntityPlayer> friends,
                                  boolean obby,
                                  Set<BlockPos> blackList)
    {
        if (blackList.contains(pos))
        {
            return null;
        }

        PositionData data = PositionData.create(
                            pos,
                            obby,
                            module.rotate.getValue() != ACRotate.None
                                && module.rotate.getValue() != ACRotate.Break
                                ? 0 // TODO: ???
                                : module.helpingBlocks.getValue(),
                            module.newVer.getValue(),
                            module.newVerEntities.getValue(),
                            module.deathTime.getValue(),
                            entities,
                            module.lava.getValue(),
                            module.water.getValue(),
                            module.ignoreLavaItems.getValue());

        if (data.isBlocked() && !module.fallBack.getValue())
        {
            return null;
        }

        if (data.isLiquid())
        {
            if (!data.isLiquidValid()
                // we wont be able to raytrace the
                // 2 blocks on top if its above us
                || module.liquidRayTrace.getValue()
                    && (module.newVer.getValue()
                        && data.getPos().getY()
                            >= RotationUtil.getRotationPlayer().posY + 2
                        || !module.newVer.getValue()
                            && data.getPos().getY()
                                >= RotationUtil.getRotationPlayer().posY + 1)
                || BlockUtil.getDistanceSq(pos.up())
                    >= MathUtil.square(module.placeRange.getValue())
                || BlockUtil.getDistanceSq(pos.up(2))
                    >= MathUtil.square(module.placeRange.getValue()))
            {
                return null;
            }

            if (data.usesObby())
            {
                if (data.isObbyValid())
                {
                    placeData.getLiquidObby().put(data.getPos(), data);
                }

                return null;
            }

            placeData.getLiquid().add(data);
            return null;
        }
        else if (data.usesObby())
        {
            if (data.isObbyValid())
            {
                placeData.getAllObbyData().put(data.getPos(), data);
            }

            return null;
        }

        if (!data.isValid())
        {
            return null;
        }

        return validate(placeData, data, friends);
    }

    public PositionData validate(PlaceData placeData, PositionData data,
                                 List<EntityPlayer> friends)
    {
        if (BlockUtil.getDistanceSq(data.getPos())
                >= MathUtil.square(module.placeTrace.getValue())
            && noPlaceTrace(data.getPos()))
        {
            return null;
        }

        float selfDamage = module.damageHelper.getDamage(data.getPos());
        if (selfDamage > placeData.getHighestSelfDamage())
        {
            placeData.setHighestSelfDamage(selfDamage);
        }

        if (selfDamage > EntityUtil.getHealth(mc.player) - 1.0)
        {
            if (!data.usesObby() && !data.isLiquid())
            {
                Managers.SAFETY.setSafe(false);
            }

            if (!module.suicide.getValue())
            {
                return null;
            }
        }

        if (selfDamage > MD.getValue()
                && (!data.usesObby() && !data.isLiquid()))
        {
            Managers.SAFETY.setSafe(false);
        }

        if (selfDamage > module.maxSelfPlace.getValue()
                && !module.override.getValue())
        {
            return null;
        }

        if (checkFriends(data, friends))
        {
            return null;
        }

        data.setSelfDamage(selfDamage);
        return data;
    }

    private boolean noPlaceTrace(BlockPos pos)
    {
        if (module.smartTrace.getValue())
        {
            for (EnumFacing facing : EnumFacing.values())
            {
                Ray ray = RayTraceFactory.rayTrace(
                                            mc.player,
                                            pos,
                                            facing,
                                            mc.world,
                                            Blocks.OBSIDIAN.getDefaultState(),
                                            module.traceWidth.getValue());
                if (ray.isLegit())
                {
                    return false;
                }
            }

            return true;
        }

        if (module.ignoreNonFull.getValue())
        {
            for (EnumFacing facing : EnumFacing.values())
            {
                Ray ray = RayTraceFactory.rayTrace(
                        mc.player,
                        pos,
                        facing,
                        mc.world,
                        Blocks.OBSIDIAN.getDefaultState(),
                        module.traceWidth.getValue());

                //noinspection deprecation
                if (!mc.world.getBlockState(ray.getResult().getBlockPos())
                             .getBlock()
                             .isFullBlock(mc.world.getBlockState(
                                 ray.getResult().getBlockPos())))
                {
                    return false;
                }
            }
        }

        return !RayTraceUtil.raytracePlaceCheck(mc.player, pos);
    }

    private void calc(BlockPos pos,
                      PlaceData data,
                      List<EntityPlayer> players,
                      List<EntityPlayer> friends,
                      List<Entity> entities,
                      boolean obby,
                      Set<BlockPos> blackList,
                      double maxY)
    {
        if (placeCheck(pos, maxY)
                || (data.getTarget() != null
                        && data.getTarget().getDistanceSq(pos)
                                > MathUtil.square(module.range.getValue())))
        {
            return;
        }

        PositionData positionData = selfCalc(
                data, pos, entities, friends, obby, blackList);

        if (positionData == null)
        {
            return;
        }

        calcPositionData(data, positionData, players);
    }

    public void calcPositionData(PlaceData data,
                                 PositionData positionData,
                                 List<EntityPlayer> players)
    {
        boolean isAntiTotem = false;
        if (data.getTarget() == null)
        {
            for (EntityPlayer player : players)
            {
                isAntiTotem = checkPlayer(data, player, positionData)
                        || isAntiTotem;
            }
        }
        else
        {
            isAntiTotem = checkPlayer(data, data.getTarget(), positionData);
        }

        if (positionData.isForce())
        {
            ForcePosition forcePosition = new ForcePosition(positionData);
            for (EntityPlayer forced : positionData.getForced())
            {
                data.addForceData(forced, forcePosition);
            }
        }

        if (isAntiTotem)
        {
            data.addAntiTotem(new AntiTotemData(positionData));
        }

        if (positionData.getFacePlacer() != null
                || positionData.getMaxDamage() > data.getMinDamage())
        {
            data.getData().add(positionData);
        }
        else if (module.shield.getValue()
            && !positionData.usesObby()
            && !positionData.isLiquid()
            && positionData.isValid()
            && positionData.getSelfDamage()
                <= module.shieldSelfDamage.getValue())
        {
            if (module.shieldPrioritizeHealth.getValue())
            {
                positionData.setDamage(0.0f);
            }

            positionData.setTarget(data.getShieldPlayer());
            data.getShieldData().add(positionData);
        }
    }

    private boolean placeCheck(BlockPos pos, double maxY)
    {
        if (pos.getY() < 0
            || pos.getY() - 1 >= maxY
            || BlockUtil.getDistanceSq(pos)
                > MathUtil.square(module.placeRange.getValue()))
        {
            return true;
        }

        if (BlockUtil.getDistanceSq(pos)
                > MathUtil.square(module.pbTrace.getValue()))
        {
            return !RayTraceUtil.canBeSeen(
                        new Vec3d(pos.getX() + 0.5,
                                  pos.getY() + 2.7,
                                  pos.getZ() + 0.5),
                        mc.player);
        }

        return false;
    }

    private boolean checkFriends(PositionData data, List<EntityPlayer> friends)
    {
        if (!module.antiFriendPop.getValue().shouldCalc(AntiFriendPop.Place))
        {
            return false;
        }

        for (EntityPlayer friend : friends)
        {
            if (friend != null
                    && !EntityUtil.isDead(friend)
                    && module.damageHelper.getDamage(data.getPos(), friend)
                            > EntityUtil.getHealth(friend) - 0.5f)
            {
                return true;
            }
        }

        return false;
    }

    private boolean checkPlayer(PlaceData data,
                                EntityPlayer player,
                                PositionData positionData)
    {
        BlockPos pos = positionData.getPos();
        if (data.getTarget() == null
                && player.getDistanceSq(pos)
                > MathUtil.square(module.range.getValue()))
        {
            return false;
        }

        boolean result = false;
        float health = EntityUtil.getHealth(player);
        float damage = module.damageHelper.getDamage(pos, player);
        if (module.antiTotem.getValue()
                && !positionData.usesObby()
                && !positionData.isLiquid())
        {
            if (module.antiTotemHelper.isDoublePoppable(player))
            {
                if (damage > module.popDamage.getValue())
                {
                    data.addCorrespondingData(player, positionData);
                }
                else if (damage < health + module.maxTotemOffset.getValue()
                        && damage > health + module.minTotemOffset.getValue())
                {
                    positionData.addAntiTotem(player);
                    result = true;
                }
            }
            else if (module.forceAntiTotem.getValue()
                    && Managers.COMBAT.lastPop(player) > 500)
            {
                if (damage > module.popDamage.getValue())
                {
                    data.confirmHighDamageForce(player);
                }

                if (damage > 0.0f
                    && damage < module.totemHealth.getValue()
                                    + module.maxTotemOffset.getValue())
                {
                    data.confirmPossibleAntiTotem(player);
                }

                float force = health - damage;
                if (force > 0.0f && force < module.totemHealth.getValue())
                {
                    positionData.addForcePlayer(player);
                    if (force < positionData.getMinDiff())
                    {
                        positionData.setMinDiff(force);
                    }
                }
            }
        }

        if (damage > module.minFaceDmg.getValue())
        {
            if (health < module.facePlace.getValue()
                    || ((IEntityLivingBase) player).getLowestDurability()
                        <= module.armorPlace.getValue())
            {
                positionData.setFacePlacer(player);
            }
        }

        if (damage > positionData.getMaxDamage())
        {
            positionData.setDamage(damage);
            positionData.setTarget(player);
        }

        return result;
    }

}
