package me.earth.earthhack.impl.modules.movement.flight;

import me.earth.earthhack.api.module.data.DefaultData;

final class FlightData extends DefaultData<Flight>
{
    public FlightData(Flight module)
    {
        super(module);
        register(module.mode, "-Normal normal flight\n-Creative fly" +
                " like in creative mode\n-Jump uses jumps to" +
                " fly\n-AAC a flight mode for the AAC anticheat.");
        register(module.speed, "The speed to fly with.");
        register(module.animation, "Trick the server into thinking you " +
                "are standing on the ground.");
        register(module.damage, "Bypass that attempts to deal" +
                " damage to you when enabling the module.");
        register(module.antiKick, "Slowly glides down to" +
                " prevent you from getting kicked.");
        register(module.glide, "Glide down with the Glide-Speed.");
        register(module.glideSpeed, "Speed to glide down with while" +
                " Glide is enabled.");
        register(module.aacY, "Vertical speed for the AAC mode.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Makes you fly.";
    }

}
