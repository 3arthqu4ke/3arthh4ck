package me.earth.earthhack.impl.core.ducks.entity;

public interface IEntityRemoteAttack
{
    default boolean shouldRemoteAttack()
    {
        return false;
    }

}
