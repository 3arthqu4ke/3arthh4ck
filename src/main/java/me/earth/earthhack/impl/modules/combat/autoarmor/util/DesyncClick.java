package me.earth.earthhack.impl.modules.combat.autoarmor.util;

import me.earth.earthhack.impl.modules.combat.autocrystal.util.TimeStamp;

public class DesyncClick extends TimeStamp
{
    private final WindowClick click;

    public DesyncClick(WindowClick click)
    {
        this.click = click;
    }

    public WindowClick getClick()
    {
        return click;
    }

}
