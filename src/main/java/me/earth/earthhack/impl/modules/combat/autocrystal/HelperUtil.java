package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.BreakValidity;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.ServerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class HelperUtil implements Globals
{
    public static BreakValidity isValid(AutoCrystal module, Entity crystal)
    {
        return isValid(module, crystal, false);
    }

    // TODO: make sure that we use the correct lastPos everywhere!
    public static BreakValidity isValid(AutoCrystal module, Entity crystal, boolean lastPos)
    {
        if (module.existed.getValue() != 0
                && System.currentTimeMillis()
                    - ((IEntity) crystal).getTimeStamp()
                        + (module.pingExisted.getValue()
                            ? ServerUtil.getPingNoPingSpoof() / 2.0
                            : 0)
                        < module.existed.getValue())
        {
            return BreakValidity.INVALID;
        }

        if (lastPos && !module.rangeHelper.isCrystalInRangeOfLastPosition(crystal)
            || !lastPos && !module.rangeHelper.isCrystalInRange(crystal))
        {
            return BreakValidity.INVALID;
        }

        if (lastPos && Managers.POSITION.getDistanceSq(crystal)
            >= MathUtil.square(module.breakTrace.getValue())
            || !lastPos && RotationUtil.getRotationPlayer().getDistanceSq(crystal)
            >= MathUtil.square(module.breakTrace.getValue()))
        {
            if (lastPos && !Managers.POSITION.canEntityBeSeen(crystal)
                || !lastPos && !RayTraceUtil.canBeSeen(
                    new Vec3d(crystal.posX,
                              crystal.posY + 1.7,
                              crystal.posZ),
                    RotationUtil.getRotationPlayer()))
            {
                return BreakValidity.INVALID;
            }
        }

        // TODO: lastPos and then check isLegit???????? not sure if this is ok
        if (module.rotate.getValue().noRotate(ACRotate.Break)
                || module.isNotCheckingRotations()
                || (RotationUtil.isLegit(crystal, crystal)
                    && AutoCrystal.POSITION_HISTORY
                             .arePreviousRotationsLegit(crystal,
                                                        module.rotationTicks
                                                              .getValue(),
                                                        true)))
        {
            return BreakValidity.VALID;
        }

        return BreakValidity.ROTATIONS;
    }

    public static void simulateExplosion(AutoCrystal module, double x, double y, double z)
    {
        List<Entity> entities = Managers.ENTITIES.getEntities();
        if (entities == null)
        {
            return;
        }

        for (Entity entity : entities)
        {
            if (entity instanceof EntityEnderCrystal
                    && entity.getDistanceSq(x, y, z) < 144)
            {
                if (module.pseudoSetDead.getValue())
                {
                    ((IEntity) entity).setPseudoDead(true);
                }
                else
                {
                    Managers.SET_DEAD.setDead(entity);
                }
            }
        }
    }

    /**
     * Checks if a change in blockChange at the given position
     * would be valid, that means that a player close to it
     * would have his feet exposed.
     *
     * @param pos the changed position.
     * @return <tt>true</tt> if the position exposes a player.
     */
    public static boolean validChange(BlockPos pos, List<EntityPlayer> players)
    {
        for (EntityPlayer player : players)
        {
            if (player == null
                || player.equals(mc.player)
                || player.equals(RotationUtil.getRotationPlayer())
                || EntityUtil.isDead(player)
                || Managers.FRIENDS.contains(player))
            {
                continue;
            }

            if (player.getDistanceSqToCenter(pos) <= 4
                    && player.posY >= pos.getY())
            {
                return true;
            }
        }

        return false;
    }

    public static boolean valid(Entity entity, double range, double trace)
    {
        EntityPlayer player = RotationUtil.getRotationPlayer();
        double d = entity.getDistanceSq(player);
        if (d >= MathUtil.square(range))
        {
            return false;
        }

        if (d >= trace)
        {
            return RayTraceUtil.canBeSeen(entity, player);
        }

        return true;
    }

}
