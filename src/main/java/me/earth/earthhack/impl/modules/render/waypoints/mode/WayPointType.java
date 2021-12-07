package me.earth.earthhack.impl.modules.render.waypoints.mode;

import net.minecraft.world.DimensionType;

public enum WayPointType
{
    OVW,
    End,
    Nether,
    None;

    public static WayPointType fromString(String string)
    {
        switch (string.toLowerCase())
        {
            case "ovw":
                return OVW;
            case "end":
                return End;
            case "nether":
                return Nether;
        }

        return None;
    }

    public static WayPointType fromDimension(DimensionType type)
    {
        switch (type)
        {
            case OVERWORLD:
                return OVW;
            case NETHER:
                return Nether;
            case THE_END:
                return End;
            default:
        }

        return None;
    }

}
