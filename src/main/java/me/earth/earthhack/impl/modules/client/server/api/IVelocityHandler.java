package me.earth.earthhack.impl.modules.client.server.api;

public interface IVelocityHandler
{
    void onVelocity(double x, double y, double z);

    double getLastX();

    double getLastY();

    double getLastZ();

}
