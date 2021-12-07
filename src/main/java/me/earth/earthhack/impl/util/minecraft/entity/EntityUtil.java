package me.earth.earthhack.impl.util.minecraft.entity;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class EntityUtil implements Globals
{
    /**
     * Checks if {@link Entity#isDead} is true
     * for the given entity, or if the entity
     * extends EntityLiving base, its health is
     * lower than 0;
     *
     * @param entity the given entity.
     * @return <tt>true</tt> if the entity is dead.
     */ // TODO: Replace .isDead everywhere where needed
    public static boolean isDead(Entity entity)
    {
        return entity.isDead
                || ((IEntity) entity).isPseudoDead()
                || entity instanceof EntityLivingBase
                    && ((EntityLivingBase) entity).getHealth() <= 0.0f;
    }

    /**
     * Returns the full health (health + absorption amount)
     * for the give entity.
     *
     * @param base the player.
     * @return health + absorption amount.
     */
    public static float getHealth(EntityLivingBase base)
    {
        return base.getHealth() + base.getAbsorptionAmount();
    }

    public static float getHealth(EntityLivingBase base, boolean absorption)
    {
        if (absorption)
        {
            return base.getHealth() + base.getAbsorptionAmount();
        }

        return base.getHealth();
    }

    /**
     * Convenience method, calls
     * {@link EntityUtil#getClosestEnemy(Vec3d, List)}
     * for the players positionVector and mc.world.playerEntities.
     */
    public static EntityPlayer getClosestEnemy()
    {
        return getClosestEnemy(mc.world.playerEntities);
    }

    /**
     * Convenience method, calls
     * {@link EntityUtil#getClosestEnemy(Vec3d, List)}
     * for the players positionVector and the given list.
     */
    public static EntityPlayer getClosestEnemy(List<EntityPlayer> list)
    {
        return getClosestEnemy(mc.player.getPositionVector(), list);
    }

    /**
     * Convenience method, calls
     * {@link EntityUtil#getClosestEnemy(double, double, double, List)}
     * for the coordinates of the position and the given list.
     */
    public static EntityPlayer getClosestEnemy(BlockPos pos,
                                               List<EntityPlayer> list)
    {
        return getClosestEnemy(pos.getX(), pos.getY(), pos.getZ(), list);
    }

    /**
     * Convenience method, calls
     * {@link EntityUtil#getClosestEnemy(double, double, double, List)}
     * for the coordinates of the vec3d and the given list.
     */
    public static EntityPlayer getClosestEnemy(Vec3d vec3d,
                                               List<EntityPlayer> list)
    {
        return getClosestEnemy(vec3d.x, vec3d.y, vec3d.z, list);
    }

    /**
     * Returns the closest not friended EntityPlayer to the given
     * coords from the given list.
     *
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the z coordinate.
     * @param players the list in which we look for the entity.
     * @return the closest player from the list.
     */
    public static EntityPlayer getClosestEnemy(double x,
                                               double y,
                                               double z,
                                               List<EntityPlayer> players)
    {
        EntityPlayer closest = null;
        double distance = Float.MAX_VALUE;

        for (EntityPlayer player : players)
        {
            if (player != null
                    && !isDead(player)
                    && !player.equals(mc.player)
                    && !Managers.FRIENDS.contains(player))
            {
                double dist = player.getDistanceSq(x, y, z);
                if (dist < distance)
                {
                    closest = player;
                    distance = dist;
                }
            }
        }

        return closest;
    }

    public static EntityPlayer getClosestEnemy(double x,
                                               double y,
                                               double z,
                                               double maxRange,
                                               List<EntityPlayer> players)
    {
        List<List<EntityPlayer>> split =
                CollectionUtil.split(players, Managers.ENEMIES::contains);

        return getClosestEnemy(x, y, z, maxRange, split.get(0), split.get(1));
    }

    public static EntityPlayer getClosestEnemy(double x,
                                               double y,
                                               double z,
                                               double maxRange,
                                               List<EntityPlayer> enemies,
                                               List<EntityPlayer> players)
    {
        EntityPlayer closestEnemied = getClosestEnemy(x, y, z, enemies);
        if (closestEnemied != null
                && closestEnemied.getDistanceSq(x, y, z)
                                    < MathUtil.square(maxRange))
        {
            return closestEnemied;
        }

        return getClosestEnemy(x, y, z, players);
    }

    /**
     * Returns true if the give player is != null,
     * not died, not friended and within the given range.
     *
     * @param player the player to check.
     * @param range the range he should be in.
     * @return <tt>true</tt> if the player fulfills the conditions.
     */
    public static boolean isValid(Entity player, double range)
    {
        return player != null
                && !isDead(player)
                && mc.player.getDistanceSq(player) <= MathUtil.square(range)
                && !Managers.FRIENDS.contains(player);
    }

}
