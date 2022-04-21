package me.earth.earthhack.impl.modules.render.esp;

import me.earth.earthhack.api.module.data.DefaultData;

final class ESPData extends DefaultData<ESP>
{
    public ESPData(ESP module)
    {
        super(module);
        register(module.mode,
                "Currently no other than Outline modes are supported.");
        register(module.players, "Render the ESP for players.");
        register(module.mode, "Render the ESP for Monsters.");
        register(module.animals, "Render the ESP for Animals.");
        register(module.vehicles, "Render the ESP for Vehicles.");
        register(module.misc, "Render the ESP for other entities.");
        register(module.items, "Render the ESP for item names.");
        register(module.storage, "Draw an ESP for storages.");
        register(module.lineWidth, "Line width for the Outline-ESP.");
        register(module.hurt, "Turn the ESP dark when the entity is hurt.");
        register(module.color, "Select the color for the esp.");
        register(module.invisibleColor, "Select the color of invisible players for the esp.");
        register(module.friendColor, "Select the color of friends for the esp.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Highlights Players and Entities through walls.";
    }

}
