package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.api.module.data.DefaultData;

final class AnnouncerData extends DefaultData<Announcer>
{
    public AnnouncerData(Announcer module)
    {
        super(module);
        register(module.delay, "Interval in seconds that messages" +
                " will be send with. Some Antispams might" +
                " kick you for low delays.");
        register(module.distance, "Announces the distance you travelled.");
        register(module.mine, "Announces the blocks you just mined.");
        register(module.place, "Announces the blocks you've placed.");
        register(module.eat, "Announces the food you eat.");
        register(module.join, "Greets players that join the server.");
        register(module.leave, "Says bye to players that leave the server.");
        register(module.totems, "Announces totem pops.");
        register(module.autoEZ, "Announces deaths.");
        register(module.miss, "Announces when someone misses a shot with a bow.");
        register(module.friends, "Announce totem pops of friends.");
        register(module.antiKick,
                "Appends a random suffix to trick antispams.");
        register(module.green, "Prepends a \">\".");
        register(module.refresh,
                "Refreshes the files in the earthhack/util folder.");
        register(module.random, "Works if your files in the earthhack/util" +
                " folder have multiple lines. Then one of those" +
                " gets selected randomly instead of the first line.");
        register(module.minDist, "The minimum distance travelled to announce.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "When you want to be really annoying." +
                "Announces different things you do." +
                " You'll find configurable files in the earthhack/util folder.";
    }

}

