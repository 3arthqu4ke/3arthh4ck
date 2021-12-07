package me.earth.earthhack.impl.modules.combat.autocrystal.util;

@FunctionalInterface
public interface RotationFunction
{
    float[] apply(double x, double y, double z, float yaw, float pitch);

}
