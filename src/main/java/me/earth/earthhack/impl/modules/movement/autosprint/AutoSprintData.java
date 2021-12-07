package me.earth.earthhack.impl.modules.movement.autosprint;

import me.earth.earthhack.api.module.data.DefaultData;

final class AutoSprintData extends DefaultData<AutoSprint>
{
    public AutoSprintData(AutoSprint module)
    {
        super(module);
        register(module.mode, "-Rage will sprint into all directions.\n-" +
                "Legit normal sprint but you don't have to press the button.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Sprints for you.";
    }

}
