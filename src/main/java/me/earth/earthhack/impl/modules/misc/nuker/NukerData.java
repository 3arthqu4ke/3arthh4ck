package me.earth.earthhack.impl.modules.misc.nuker;

import me.earth.earthhack.api.module.data.DefaultData;

final class NukerData extends DefaultData<Nuker>
{
    public NukerData(Nuker module)
    {
        super(module);
        register(module.nuke, "Classical Nuker. Other options would be" +
                " the Shulkers and Hoppers setting down below.");
        register(module.blocks, "Amount of blocks that should be attacked in one tick.");
        register(module.delay, "Helps you not to accidentally spam the Nuker.");
        register(module.rotate, "-None no rotations\n-Normal normal legit" +
                " rotations, you can only break 1 block per" +
                " tick when using these.\n-Packet spams" +
                " rotation packets, might lag you back.");
        register(module.width, "The width of the nuked selection.");
        register(module.height, "The height of the nuked selection.");
        register(module.range, "Blocks within this range will be nuked.");
        register(module.render, "Renders the selection that should be nuked.");
        register(module.color, "Color of the Render.");
        register(module.shulkers, "Nukes shulkers around you.");
        register(module.hoppers, "Nukes hoppers around you.");
        register(module.instant, "For the shulker/hopper nuker." +
                " Instantly mines them when they spawn.");
        register(module.swing, "Swings your arm.");
        register(module.speedMine, "Currently required.");
        register(module.autoTool,
                "Automatically finds the best Tool to nuke with.");
        register(module.timeout, "Interval in milliseconds between 2 nukes.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Mines blocks around you.";
    }

}
