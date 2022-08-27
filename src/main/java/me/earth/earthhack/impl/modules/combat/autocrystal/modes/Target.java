package me.earth.earthhack.impl.modules.combat.autocrystal.modes;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public enum Target implements Globals
{
    Closest
    {
        @Override
        public EntityPlayer getTarget(List<EntityPlayer> players,
                                      List<EntityPlayer> enemies,
                                      double maxRange)
        {
            return EntityUtil.getClosestEnemy(mc.player.posX,
                                              mc.player.posY,
                                              mc.player.posZ,
                                              maxRange,
                                              enemies,
                                              players);
        }
    },
    FOV
    {
        @Override
        public EntityPlayer getTarget(List<EntityPlayer> players,
                                      List<EntityPlayer> enemies,
                                      double maxRange)
        {
           EntityPlayer enemy = getByFov(enemies, maxRange);
           if (enemy == null)
           {
               return getByFov(players, maxRange);
           }

           return enemy;
        }
    },
    Angle
    {
        @Override
        public EntityPlayer getTarget(List<EntityPlayer> players,
                                      List<EntityPlayer> enemies,
                                      double maxRange)
        {
            EntityPlayer enemy = getByAngle(enemies, maxRange);
            return enemy == null ? getByAngle(players, maxRange) : enemy;
        }
    },
    Damage
    {
        @Override
        public EntityPlayer getTarget(List<EntityPlayer> players,
                                      List<EntityPlayer> enemies,
                                      double maxRange)
        {
            return null;
        }
    };

    public abstract EntityPlayer getTarget(List<EntityPlayer> players,
                                           List<EntityPlayer> enemies,
                                           double maxRange);

    public static EntityPlayer getByFov(List<EntityPlayer> players,
                                        double maxRange)
    {
        EntityPlayer closest = null;
        double closestAngle  = 360.0;
        for (EntityPlayer player : players)
        {
            if (!EntityUtil.isValid(player, maxRange))
            {
                continue;
            }

            double angle = RotationUtil.getAngle(player, 1.4);
            if (angle < closestAngle
                    && angle < mc.gameSettings.fovSetting / 2)
            {
                closest = player;
                closestAngle = angle;
            }
        }

        return closest;
    }

    public static EntityPlayer getByAngle(List<EntityPlayer> players,
                                          double maxRange)
    {
        EntityPlayer closest = null;
        double closestAngle  = 360.0;
        for (EntityPlayer player : players)
        {
            if (!EntityUtil.isValid(player, maxRange))
            {
                continue;
            }

            double angle = RotationUtil.getAngle(player, 1.4);
            if (angle < closestAngle
                    && angle < mc.gameSettings.fovSetting / 2)
            {
                closest = player;
                closestAngle = angle;
            }
        }

        return closest;
    }

    public static final String DESCRIPTION =
        "-Closest, will target the closest Enemy.\n" +
        "-FOV, will target the player you are looking at (by Angle).\n" +
        "-Angle, similar to FOV but will also target players outside" +
        " your FOV.\n-Damage, Calculates Damages for all Players in" +
        " Range and takes the best one (intensive).";

}
