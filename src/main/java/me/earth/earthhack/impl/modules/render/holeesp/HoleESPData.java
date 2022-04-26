package me.earth.earthhack.impl.modules.render.holeesp;

import me.earth.earthhack.api.module.data.DefaultData;

final class HoleESPData extends DefaultData<HoleESP>
{
    public HoleESPData(HoleESP module)
    {
        super(module);
        register(module.mode, "-Polling, recalculates holes around you every tick.\n" +
                "-Invalidation, a new experimental way of calculating holes, calculates all holes in a chunk first," +
                " then only calculates small areas around every block that's being placed/broken. Probably more" +
                " performant than Polling, but still can't solve the bottleneck of rendering many holes. Note that " +
                " changing to this mode or changing settings for this mode requires you to rejoin the server.");
        register(module.range, "Range that holes should be rendered in." +
                " High ranges (30+) mean more workload for the CPU and might" +
                " slow down modules like HoleFiller.");
        register(module.holes, "Amount of normal holes to render.");
        register(module.safeHole, "Amount of safe holes" +
                " (bedrock only) to render.");
        register(module.wide, "Amount of 2x1 holes to render.");
        register(module.big, "Amount of 2x2 holes to render.");
        register(module.fov, "Only renders Holes inside your fov.");
        register(module.own, "Render the hole you are currently standing in.");
        register(module.height, "Height of the ESP for safe holes." +
                " A value of 0 means a flat square will be rendered" +
                " at the bottom of the hole.");
        register(module.unsafeHeight, "Height of the ESP for unsafe holes." +
                " A value of 0 means a flat square will be rendered" +
                " at the bottom of the hole.");
        register(module.wideHeight, "Height of the ESP for 2x1 holes." +
                " A value of 0 means a flat square will be rendered" +
                " at the bottom of the hole.");
        register(module.bigHeight, "Height of the ESP for 2x2 holes." +
                " A value of 0 means a flat square will be rendered" +
                " at the bottom of the hole.");
        register(module.unsafeColor,
                "The color unsafe holes should be rendered in.");
        register(module.safeColor,
                "The color safe holes should be rendered in.");
        register(module.wideColor,
                "The color 2x1 holes should be rendered in.");
        register(module.bigColor,
                "The color 2x2 holes should be rendered in.");
        register(module.async, "When mode is Invalidation, holes in chunks can be calculated on another thread.");
        register(module.limit, "Limits the amount of threads Async is able to spawn.");
        register(module.chunk_height, "For Invalidation: On some practice servers you probably don't need to calculate"
                + " holes above a certain y coordinate.");
        register(module.sort_time, "How often (ms) mode Invalidation should sort holes by distance.");
        register(module.remove_time, "How often (ms) mode Invalidation should garbage collect invalidated holes.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Highlights Holes around you.";
    }

}
