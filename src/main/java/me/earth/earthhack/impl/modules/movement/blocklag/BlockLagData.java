package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.api.module.data.DefaultData;

final class BlockLagData extends DefaultData<BlockLag>
{
    public BlockLagData(BlockLag module)
    {
        super(module);
        register(module.vClip,
                "V-clips the specified amount down to cause a lagback." +
                        " Don't touch, 9 should be perfect.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "The OG Burrow.";
    }

}