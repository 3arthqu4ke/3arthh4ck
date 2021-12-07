package me.earth.earthhack.impl.modules.movement.highjump;

import me.earth.earthhack.api.module.data.DefaultData;

final class HighJumpData extends DefaultData<HighJump>
{
    public HighJumpData(HighJump module)
    {
        super(module);
        register(module.height, "Speed to jump up with.");
        register(module.onGround, "Only applies HighJump when you" +
                " are standing on the ground.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Allows you to jump higher.";
    }

}
