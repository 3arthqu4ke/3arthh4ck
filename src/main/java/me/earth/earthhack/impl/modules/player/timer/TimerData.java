package me.earth.earthhack.impl.modules.player.timer;

import me.earth.earthhack.api.module.data.DefaultData;

final class TimerData extends DefaultData<Timer>
{
    public TimerData(Timer module)
    {
        super(module);
        register(module.mode, "-Normal normal Timer" +
                "\n-Physics a timer that doesn't make all the other" +
                " entities in the world move quickly." +
                "\n-Switch a timer that switches between different values " +
                "quickly.\n-Blink (BETA)");
        register(module.autoOff, "Turns the module off after this value " +
                "in milliseconds passed.");
        register(module.lagTime, "Waits for this delay in milliseconds" +
                " after we got lagged back by the server" +
                " before attempting to timer again.");
        register(module.speed, "Speed for the standard timer.");
        register(module.updates, "Updates for the Physics timer.");
        register(module.fast, "Fast Speed for Mode-Switch.");
        register(module.fastTime, "Fast Speed will be active for this time" +
                " in milliseconds when using Mode-Switch.");
        register(module.slow, "Slow Speed for Mode-Switch.");
        register(module.slowTime, "Slow Speed will be active for this time" +
                " in milliseconds when using Mode-Switch.");
        register(module.maxPackets, "Maximum Packets send by Mode-Blink.");
        register(module.offset, "Packet Offset for Mode-Blink.");
        register(module.letThrough, "Lets through every n-th packet." +
                " A value of 0 means no packets will be let through.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Makes you move faster by sending more packets to the server.";
    }

}
