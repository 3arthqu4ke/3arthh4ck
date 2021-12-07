package me.earth.earthhack.api.event.bus.api;

public interface ICancellable
{
    void setCancelled(boolean cancelled);

    boolean isCancelled();

}
