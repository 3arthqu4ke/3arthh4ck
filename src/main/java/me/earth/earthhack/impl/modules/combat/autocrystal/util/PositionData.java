package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.util.math.path.BasePath;
import me.earth.earthhack.impl.util.math.path.BlockingEntity;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PositionData extends BasePath
        implements Globals, Comparable<PositionData>
{
    private final AutoCrystal module;
    private final List<EntityPlayer> forced = new ArrayList<>();
    private final Set<EntityPlayer> antiTotems;
    private EntityPlayer target;
    private EntityPlayer facePlace;
    private IBlockState state;
    private float selfDamage;
    private float damage;
    private boolean obby;
    private boolean obbyValid;
    private boolean blocked;
    private boolean liquidValid;
    private boolean liquid;
    private float minDiff;
    private boolean raytraceBypass;

    /**
     * Use the factory method.
     */
    public PositionData(BlockPos pos, int blocks,
                        AutoCrystal module)
    {
        this(pos, blocks, module, new HashSet<>());
    }

    public PositionData(BlockPos pos, int blocks,
                        AutoCrystal module,
                        Set<EntityPlayer> antiTotems)
    {
        super(RotationUtil.getRotationPlayer(), pos, blocks);
        this.module = module;
        this.antiTotems = antiTotems;
        this.minDiff = Float.MAX_VALUE;
    }

    /** @return <tt>true</tt> if this Position will need Obsidian. */
    public boolean usesObby()
    {
        return obby;
    }

    public boolean isObbyValid()
    {
        return obbyValid;
    }

    public float getMaxDamage()
    {
        return damage;
    }

    public void setDamage(float damage)
    {
        this.damage = damage;
    }

    public float getSelfDamage()
    {
        return selfDamage;
    }

    public void setSelfDamage(float selfDamage)
    {
        this.selfDamage = selfDamage;
    }

    public EntityPlayer getTarget()
    {
        return target;
    }

    public void setTarget(EntityPlayer target)
    {
        this.target = target;
    }

    public EntityPlayer getFacePlacer()
    {
        return facePlace;
    }

    public void setFacePlacer(EntityPlayer facePlace)
    {
        this.facePlace = facePlace;
    }

    public Set<EntityPlayer> getAntiTotems()
    {
        return antiTotems;
    }

    public void addAntiTotem(EntityPlayer player)
    {
        this.antiTotems.add(player);
    }

    public boolean isBlocked()
    {
        return blocked;
    }

    public float getMinDiff()
    {
        return minDiff;
    }

    public void setMinDiff(float minDiff)
    {
        this.minDiff = minDiff;
    }

    public boolean isForce()
    {
        return !forced.isEmpty();
    }

    public void addForcePlayer(EntityPlayer player)
    {
        this.forced.add(player);
    }

    public List<EntityPlayer> getForced()
    {
        return forced;
    }

    public boolean isLiquidValid()
    {
        return liquidValid;
    }

    public boolean isLiquid()
    {
        return liquid;
    }

    public float getHealth()
    {
        EntityLivingBase target = getTarget();
        return target == null ? 36.0f : EntityUtil.getHealth(target);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public int compareTo(PositionData o)
    {
        if (module.useSafetyFactor.getValue())
        {
            double thisFactor = this.damage * module.safetyFactor.getValue()
                - this.selfDamage * module.selfFactor.getValue();
            double otherFactor = o.damage * module.safetyFactor.getValue()
                - o.selfDamage * module.selfFactor.getValue();

            if (thisFactor != otherFactor)
            {
                return Double.compare(otherFactor, thisFactor);
            }
        }

        if (Math.abs(o.damage - this.damage) < module.compareDiff.getValue()
            && (!module.facePlaceCompare.getValue()
                || this.damage >= module.minDamage.getValue()))
        {
            if (this.usesObby() && o.usesObby())
            {
                // Find a good comparison here
                return Integer.compare(this.getPath().length, o.getPath().length)
                        + Float.compare(this.selfDamage, o.selfDamage);
            }

            return Float.compare(this.selfDamage, o.getSelfDamage());
        }

        return Float.compare(o.damage, this.damage);
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof PositionData)
        {
            return ((PositionData) o).getPos().equals(this.getPos());
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return this.getPos().hashCode();
    }

    public static PositionData create(BlockPos pos,
                                      boolean obby,
                                      int helpingBlocks,
                                      boolean newVer,
                                      boolean newVerEntities,
                                      int deathTime,
                                      List<Entity> entities,
                                      boolean lava,
                                      boolean water,
                                      boolean lavaItems,
                                      AutoCrystal module)
    {
        PositionData data = new PositionData(pos, helpingBlocks, module);
        data.state = mc.world.getBlockState(pos);
        if (data.state.getBlock() != Blocks.BEDROCK
                && data.state.getBlock() != Blocks.OBSIDIAN)
        {
            if (!obby
                || !data.state.getMaterial().isReplaceable()
                || checkEntities(data, pos, entities, 0, true, true, false))
            {
                return data;
            }

            data.obby = true;
        }

        BlockPos up = pos.up();
        IBlockState upState = mc.world.getBlockState(up);
        if (upState.getBlock() != Blocks.AIR)
        {
            if (checkLiquid(upState.getBlock(), water, lava))
            {
                data.liquid = true;
            }
            else
            {
                return data;
            }
        }

        IBlockState upUpState;
        if (!newVer
                && (upUpState = mc.world.getBlockState(up.up()))
                                        .getBlock() != Blocks.AIR)
        {
            if (checkLiquid(upUpState.getBlock(), water, lava))
            {
                data.liquid = true;
            }
            else
            {
                return data;
            }
        }

        boolean checkLavaItems = lavaItems
                && upState.getMaterial() == Material.LAVA;
        if (checkEntities(
                data, up, entities, deathTime, false, false, checkLavaItems)
            || !newVerEntities && checkEntities(data, up.up(),
                            entities, deathTime, false, false, checkLavaItems))
        {
            return data;
        }

        if (data.obby)
        {
            if (data.liquid)
            {
                data.liquidValid = true;
            }

            data.obbyValid = true;
            return data;
        }

        if (data.liquid)
        {
            data.liquidValid = true;
            return data;
        }

        data.setValid(true);
        return data;
    }

    private static boolean checkEntities(PositionData data,
                                         BlockPos pos,
                                         List<Entity> entities,
                                         int deathTime,
                                         boolean dead,
                                         boolean spawning,
                                         boolean lavaItems)
    {
        AxisAlignedBB bb = new AxisAlignedBB(pos);
        for (Entity entity : entities)
        {
            if (entity == null
                || spawning && !entity.preventEntitySpawning
                || dead && EntityUtil.isDead(entity)
                || !data.module.bbBlockingHelper.blocksBlock(bb, entity))
            {
                continue;
            }

            if (lavaItems && entity instanceof EntityItem)
            {
                continue;
            }
            else if (entity instanceof EntityEnderCrystal)
            {
                if (!dead)
                {
                    boolean crystalIsDead = entity.isDead;
                    boolean crystalIsPseudoDead = ((IEntity) entity).isPseudoDead();
                    if (crystalIsDead || crystalIsPseudoDead)
                    {
                        if (crystalIsDead && Managers.SET_DEAD.passedDeathTime(entity, deathTime)
                            || crystalIsPseudoDead && ((IEntity) entity).getPseudoTime().passed(deathTime))
                        {
                            continue; // Entity is like very dead now
                        }
                        else
                        {
                            // No need to Fallback since it will die soon
                            //  but we can't place yet
                            return true;
                        }
                    }
                    else
                    {
                        data.blocked = true; // Fallback required
                    }
                }

                data.getBlockingEntities().add(new BlockingEntity(entity, pos));
                continue;
            }

            return true;
        }

        return false;
    }

    private static boolean checkLiquid(Block block, boolean water, boolean lava)
    {
        return water && (block == Blocks.WATER
                            || block == Blocks.FLOWING_WATER)
                || lava && (block == Blocks.LAVA
                            || block == Blocks.FLOWING_LAVA);
    }

    public boolean isRaytraceBypass() {
        return raytraceBypass;
    }

    public void setRaytraceBypass(boolean raytraceBypass) {
        this.raytraceBypass = raytraceBypass;
    }

}