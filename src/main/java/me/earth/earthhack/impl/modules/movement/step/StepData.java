package me.earth.earthhack.impl.modules.movement.step;

import me.earth.earthhack.impl.util.helpers.render.data.BlockESPModuleData;

final class StepData extends BlockESPModuleData<Step>
{
    public StepData(Step module)
    {
        super(module);
        register(module.mode, "-Vanilla:, not allowed by some AntiCheats." +
            "\n-Normal: sends a few packets at once." +
            "\n-Slow: distributes the packets from mode Normal.");
        register(module.height,
                "Maximum height in blocks that you want to step up.");
        register(module.entityStep,
                "Step with entities that you are riding on as well.");
        register(module.autoOff, "Turn the module off after it stepped.");
        register(module.lagTime, "Timeout in milliseconds to wait after we" +
                " got lagged back by the server.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Allows you to step up blocks.";
    }

}
