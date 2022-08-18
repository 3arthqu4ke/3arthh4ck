package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.safety.Safety;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiFriendPop;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.PositionData;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.path.PathFinder;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceFactory;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.blocks.states.IBlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class HelperObby implements Globals
{
    private static final SettingCache<Float, NumberSetting<Float>, Safety> MD =
        Caches.getSetting(Safety.class, NumberSetting.class, "MaxDamage", 4.0f);

    private final AutoCrystal module;

    public HelperObby(AutoCrystal module)
    {
        this.module = module;
    }

    public PositionData findBestObbyData(Map<BlockPos, PositionData> obbyData,
                                         List<EntityPlayer> players,
                                         List<EntityPlayer> friends,
                                         List<Entity> entities,
                                         EntityPlayer target,
                                         boolean newVer)
    {
        double maxY = 0;
        List<EntityPlayer> filteredPlayers = new LinkedList<>();
        for (EntityPlayer player : players)
        {
            if (player == null
                || EntityUtil.isDead(player)
                || player.posY > mc.player.posY + 18
                || player.getDistanceSq(mc.player)
                    > MathUtil.square(module.targetRange.getValue()))
            {
                continue;
            }

            filteredPlayers.add(player);
            if (player.posY > maxY)
            {
                maxY = player.posY;
            }
        }

        int fastObby = module.fastObby.getValue();
        if (fastObby != 0)
        {
            Set<BlockPos> positions;
            if (target != null)
            {
                positions = new HashSet<>((int) (4 * fastObby / 0.75) + 1);
                addPositions(positions, target, fastObby);
            }
            else
            {
                positions = new HashSet<>((int)
                        (filteredPlayers.size() * 4 * fastObby / 0.75 + 1));

                for (EntityPlayer player : filteredPlayers)
                {
                    addPositions(positions, player, fastObby);
                }
            }

            obbyData.keySet().retainAll(positions);
        }

        int maxPath = module.helpingBlocks.getValue();
        int shortest = maxPath;
        float maxDamage = 0.0f;
        float maxSelfDamage = 0.0f;
        PositionData bestData = null;

        for (PositionData positionData : obbyData.values())
        {
            if (positionData.isBlocked())
            {
                // TODO: Implement ObbyFallback here!
                continue;
            }

            BlockPos pos = positionData.getPos();
            if (pos.getY() >= maxY)
            {
                continue;
            }

            float self = Float.MAX_VALUE;
            boolean preSelf = module.obbyPreSelf.getValue();
            IBlockStateHelper helper = new BlockStateHelper(new HashMap<>());
            if (preSelf)
            {
                helper.addBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
                self = module.damageHelper.getObbyDamage(pos, helper);
                if (checkSelfDamage(self))
                {
                    continue;
                }

                positionData.setSelfDamage(self);
            }

            // TODO: with the new liquid mine we could also
            //  use the 2 blocks above as path if possible
            BlockPos[] ignore = new BlockPos[newVer ? 1 : 2];
            ignore[0] = pos.up();
            if (!newVer)
            {
                ignore[1] = pos.up(2);
            }

            if (module.interact.getValue())
            {
                RayTraceMode mode = module.obbyTrace.getValue();
                for (EnumFacing facing : EnumFacing.values())
                {
                    BlockPos offset = pos.offset(facing);
                    if (BlockUtil.getDistanceSq(offset)
                            >= MathUtil.square(module.placeRange.getValue()))
                    {
                        continue;
                    }

                    IBlockState state = mc.world.getBlockState(offset);
                    if (state.getMaterial().isReplaceable()
                            && !state.getMaterial().isLiquid())
                    {
                        continue;
                    }

                    Ray ray = RayTraceFactory.rayTrace(
                            positionData.getFrom(),
                            offset,
                            facing.getOpposite(),
                            mc.world,
                            Blocks.OBSIDIAN.getDefaultState(),
                            mode == RayTraceMode.Smart
                                    ? -1.0
                                    : 2.0);

                    if (!ray.isLegit() && mode == RayTraceMode.Smart)
                    {
                        continue;
                    }

                    if (module.inside.getValue()
                            && state.getMaterial().isLiquid())
                    {
                        ray.getResult().sideHit = ray.getResult().sideHit
                                                                 .getOpposite();
                        ray = new Ray(ray.getResult(),
                                      ray.getRotations(),
                                      ray.getPos().offset(ray.getFacing()),
                                      ray.getFacing().getOpposite(),
                                      ray.getVector());
                    }

                    positionData.setValid(true);
                    positionData.setPath(ray);
                    break;
                }
            }

            if (!positionData.isValid())
            {
                PathFinder.findPath(
                        positionData,
                        module.placeRange.getValue(),
                        entities,
                        module.obbyTrace.getValue(),
                        helper,
                        Blocks.OBSIDIAN.getDefaultState(),
                        PathFinder.CHECK,
                        ignore);
            }

            if (!positionData.isValid()
                || positionData.getPath() == null
                || positionData.getPath().length > maxPath)
            {
                continue;
            }

            for (Ray ray : positionData.getPath())
            {
                helper.addBlockState(ray.getPos().offset(ray.getFacing()),
                                     Blocks.OBSIDIAN.getDefaultState());
            }

            if (!preSelf) // result can only deal less now
            {
                self = module.damageHelper.getObbyDamage(pos, helper);
                if (checkSelfDamage(self))
                {
                    continue;
                }

                positionData.setSelfDamage(self);
            }

            if (module.antiFriendPop.getValue().shouldCalc(AntiFriendPop.Place))
            {
                boolean poppingFriend = false;
                for (EntityPlayer friend : friends)
                {
                    float damage = module.damageHelper
                                         .getObbyDamage(pos, friend, helper);
                    if (damage > EntityUtil.getHealth(friend))
                    {
                        poppingFriend = true;
                        break;
                    }
                }

                if (poppingFriend)
                {
                    continue;
                }
            }

            float damage = 0.0f;
            if (target != null)
            {
                positionData.setTarget(target);
                damage = module.damageHelper.getObbyDamage(pos, target, helper);
                if (damage < module.minDamage.getValue())
                {
                    continue;
                }
            }
            else
            {
                for (EntityPlayer p : filteredPlayers)
                {
                    float d = module.damageHelper.getObbyDamage(pos, p, helper);
                    if (d < module.minDamage.getValue() || d < damage)
                    {
                        continue;
                    }

                    damage = d;
                    positionData.setTarget(p);
                }
            }

            if (damage < module.minDamage.getValue())
            {
                continue;
            }

            positionData.setDamage(damage);
            int length = positionData.getPath().length;
            if (bestData == null)
            {
                bestData = positionData;
                maxDamage = damage;
                maxSelfDamage = self;
                shortest = length;
                continue;
            }

            boolean betterLen = length - module.maxDiff.getValue() < shortest;
            boolean betterDmg = damage + module.maxDmgDiff.getValue() > maxDamage
                && damage - module.maxDmgDiff.getValue() >= module.minDamage.getValue();
            // meh way of getting a good position but whatever
            if (betterLen && damage > maxDamage
                || betterDmg && length < shortest
                || betterDmg && length == shortest && self < maxSelfDamage)
            {
                bestData = positionData;
                if (length < shortest)
                {
                    shortest = length;
                }

                if (damage > maxDamage)
                {
                    maxDamage = damage;
                }

                if (self < maxSelfDamage)
                {
                    maxSelfDamage = self;
                }
            }
        }

        return bestData;
    }

    private void addPositions(Set<BlockPos> positions,
                              EntityPlayer player,
                              int fastObby)
    {
        BlockPos down = PositionUtil.getPosition(player).down();
        for (EnumFacing facing : EnumFacing.HORIZONTALS)
        {
            BlockPos offset = down;
            for (int i = 0; i < fastObby; i++)
            {
                offset = offset.offset(facing);
                positions.add(offset);
            }
        }
    }

    private boolean checkSelfDamage(float self)
    {
        if (self > MD.getValue() && module.obbySafety.getValue())
        {
            Managers.SAFETY.setSafe(false);
        }

        if (self > EntityUtil.getHealth(mc.player) - 1.0)
        {
            if (module.obbySafety.getValue())
            {
                Managers.SAFETY.setSafe(false);
            }

            return true;
        }

        return self > module.maxSelfPlace.getValue();
    }

}
