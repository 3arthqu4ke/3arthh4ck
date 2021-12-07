package me.earth.earthhack.impl.modules.render.lagometer;

import me.earth.earthhack.impl.util.helpers.render.data.BlockESPModuleData;

final class LagOMeterData extends BlockESPModuleData<LagOMeter>
{
    public LagOMeterData(LagOMeter module)
    {
        super(module);
        register(module.esp, "Displays an ESP.");
        register(module.response,
                "Displays a warning when the server is lagging.");
        register(module.lagTime, "Displays a warning you got lagbacked.");
        register(module.nametag, "Displays a small Nametag at the ESP.");
        register(module.scale, "Scale of the Nametag.");
        register(module.textColor, "Color of the Nametag.");
        register(module. responseTime, "Time in ms the server has been" +
                " lagging for before a warning is displayed.");
        register(module.time, "The Lag-ESP will be shown for this time.");
        register(module.chat, "Displays warnings in chat.");
        register(module.chatTime, "Time to display the Text-Warning for.");
        register(module.render, "Displays the warnings on top of your screen.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Shows lag and the server position.";
    }

}
