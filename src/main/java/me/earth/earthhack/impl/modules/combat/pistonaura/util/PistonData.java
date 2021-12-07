package me.earth.earthhack.impl.modules.combat.pistonaura.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class PistonData implements Globals
{
    private final BlockPos crystalPos;
    private final EntityPlayer target;
    private final BlockPos startPos;
    private final EnumFacing facing;
    private PistonStage[] order;
    private BlockPos redstonePos;
    private BlockPos pistonPos;
    private boolean valid;
    private boolean multi;

    public PistonData(EntityPlayer target,
                      BlockPos crystalPos,
                      EnumFacing facing)
    {
        this.crystalPos = crystalPos;
        this.target     = target;
        this.startPos   = PositionUtil.getPosition(target);
        this.facing     = facing;
    }

    public boolean isValid()
    {
        return valid
                && order != null
                && EntityUtil.isValid(target, 9.0f)
                && startPos.equals(PositionUtil.getPosition(target));
    }

    public BlockPos getStartPos()
    {
        return startPos;
    }

    public BlockPos getCrystalPos()
    {
        return crystalPos;
    }

    public EntityPlayer getTarget()
    {
        return target;
    }

    public EnumFacing getFacing()
    {
        return facing;
    }

    public BlockPos getRedstonePos()
    {
        return redstonePos;
    }

    public void setRedstonePos(BlockPos redstonePos)
    {
        this.redstonePos = redstonePos;
    }

    public BlockPos getPistonPos()
    {
        return pistonPos;
    }

    public void setPistonPos(BlockPos pistonPos)
    {
        this.pistonPos = pistonPos;
    }

    public void setValid(boolean valid)
    {
        this.valid = valid;
    }

    public PistonStage[] getOrder()
    {
        return order;
    }

    public void setOrder(PistonStage[] order)
    {
        this.order = order;
    }

    public boolean isMulti()
    {
        return multi;
    }

    public void setMulti(boolean multi)
    {
        this.multi = multi;
    }

}
