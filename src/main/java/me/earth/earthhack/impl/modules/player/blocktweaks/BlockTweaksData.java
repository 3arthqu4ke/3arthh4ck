package me.earth.earthhack.impl.modules.player.blocktweaks;

import me.earth.earthhack.api.module.data.DefaultData;

final class BlockTweaksData extends DefaultData<BlockTweaks>
{
    public BlockTweaksData(BlockTweaks module)
    {
        super(module);
        register(module.delay, "When holding left click Vanilla minecraft" +
                " has a delay of 4 ticks after you mined a block in which" +
                " the next block won't be attacked. Use this setting to" +
                " set that delay.");
        register(module.noBreakAnim, "Exploit that hides the cracks on" +
                " the block that you are currently mining.");
        register(module.entityMine,
                "Mine through entities instead of attacking them.");
        register(module.m1Attack, "When using EntityMine attack the" +
                " Entity by clicking the right mouse button.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Tweaks that make the interaction with blocks easier.";
    }

}
