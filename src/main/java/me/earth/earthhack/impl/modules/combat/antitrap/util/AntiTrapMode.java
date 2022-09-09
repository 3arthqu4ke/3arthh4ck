package me.earth.earthhack.impl.modules.combat.antitrap.util;

import net.minecraft.util.math.Vec3i;

public enum AntiTrapMode
{
    Crystal(new Vec3i[]
    {
        new Vec3i(1, 0, 0),
        new Vec3i(0, 0, 1),
        new Vec3i(-1, 0, 0),
        new Vec3i(0, 0, -1),
        new Vec3i(1, 0, -1),
        new Vec3i(1, 0, 1),
        new Vec3i(-1, 0, -1),
        new Vec3i(-1, 0, 1),
        new Vec3i(1, 1, 0),
        new Vec3i(0, 1, 1),
        new Vec3i(-1, 1, 0),
        new Vec3i(0, 1, -1),
        new Vec3i(1, 1, -1),
        new Vec3i(1, 1, 1),
        new Vec3i(-1, 1, -1),
        new Vec3i(-1, 1, 1)
    }),
    FacePlace(new Vec3i[]
    {
        new Vec3i(1, 1, 0),
        new Vec3i(0, 1, 1),
        new Vec3i(-1, 1, 0),
        new Vec3i(0, 1, -1),
        new Vec3i(1, 2, 0),
        new Vec3i(0, 2, 1),
        new Vec3i(-1, 2, 0),
        new Vec3i(0, 2, -1),
    }),
    Bomb(new Vec3i[]
    {
        new Vec3i(0, 3, 0)
    }),
    Fill(new Vec3i[]
    {
        new Vec3i(2, 0, 0),
        new Vec3i(0, 0, 2),
        new Vec3i(-2, 0, 0),
        new Vec3i(0, 0, -2),
        new Vec3i(2, 1, 0),
        new Vec3i(0, 1, 2),
        new Vec3i(-2, 1, 0),
        new Vec3i(0, 1, -2),
    });

    private final Vec3i[] offsets;

    AntiTrapMode(Vec3i[] offsets)
    {
        this.offsets = offsets;
    }

    public Vec3i[] getOffsets()
    {
        return offsets;
    }

}
