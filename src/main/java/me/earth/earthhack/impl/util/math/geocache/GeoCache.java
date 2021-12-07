package me.earth.earthhack.impl.util.math.geocache;

import net.minecraft.util.math.Vec3i;

public interface GeoCache
{
    void cache();

    int getRadius(double radius);

    Vec3i get(int index);

    Vec3i[] array();

}
