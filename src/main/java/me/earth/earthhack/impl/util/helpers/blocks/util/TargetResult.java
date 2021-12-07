package me.earth.earthhack.impl.util.helpers.blocks.util;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class TargetResult
{
    private List<BlockPos> targets = new ArrayList<>();
    private boolean valid = true;

    public List<BlockPos> getTargets()
    {
        return targets;
    }

    public TargetResult setTargets(List<BlockPos> targets)
    {
        this.targets = targets;
        return this;
    }

    public boolean isValid()
    {
        return valid;
    }

    public TargetResult setValid(boolean valid)
    {
        this.valid = valid;
        return this;
    }

}
