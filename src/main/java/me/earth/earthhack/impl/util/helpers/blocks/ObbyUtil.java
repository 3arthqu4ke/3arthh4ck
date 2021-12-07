package me.earth.earthhack.impl.util.helpers.blocks;

import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.path.BlockingEntity;
import me.earth.earthhack.impl.util.math.path.Pathable;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockingType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketUseEntity;

public class ObbyUtil
{
    public static boolean place(ObbyModule module, Pathable path)
    {
        if (!path.isValid())
        {
            return false;
        }

        Entity target = null;
        boolean crystalFound = false;
        float maxDamage = Float.MAX_VALUE;
        for (BlockingEntity entity : path.getBlockingEntities())
        {
            if (module.attack.getValue()
                    && entity.getEntity() instanceof EntityEnderCrystal)
            {
                crystalFound = true;
                float damage = DamageUtil.calculate(entity.getEntity(),
                        module.getPlayer());
                if (damage < maxDamage
                        && module
                        .pop
                        .getValue()
                        .shouldPop(damage, module.popTime.getValue()))
                {
                    maxDamage = damage;
                    target = entity.getEntity();
                }
            }
            else
            {
                return false;
            }
        }

        if (target != null)
        {
            module.attacking = new CPacketUseEntity(target);
        }
        else if (crystalFound
                && module.blockingType.getValue() != BlockingType.Crystals)
        {
            return false;
        }

        for (Ray ray : path.getPath())
        {
            module.placeBlock(ray.getPos(),
                              ray.getFacing(),
                              ray.getRotations(),
                              ray.getResult().hitVec);

            if (module.blocksPlaced >= module.blocks.getValue()
                    || module.rotate.getValue() == Rotate.Normal)
            {
                return true;
            }
        }

        return true;
    }

}
