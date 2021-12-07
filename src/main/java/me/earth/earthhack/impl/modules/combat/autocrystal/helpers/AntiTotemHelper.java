package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class AntiTotemHelper
{
    private final Setting<Float> health;
    private EntityPlayer target;
    private BlockPos targetPos;

    public AntiTotemHelper(Setting<Float> health)
    {
        this.health = health;
    }

    public boolean isDoublePoppable(EntityPlayer player)
    {
        return Managers.COMBAT.lastPop(player) > 500
                && EntityUtil.getHealth(player) <= health.getValue();
    }

    public BlockPos getTargetPos()
    {
        return targetPos;
    }

    public void setTargetPos(BlockPos targetPos)
    {
        this.targetPos = targetPos;
    }

    public EntityPlayer getTarget()
    {
        return target;
    }

    public void setTarget(EntityPlayer target)
    {
        this.target = target;
    }

}
