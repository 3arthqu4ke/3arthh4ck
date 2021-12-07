package me.earth.earthhack.impl.core.ducks.util;

/**
 * Duck interface for {@link net.minecraft.inventory.Container}.
 */
public interface IContainer
{

    void setTransactionID(short id);

    short getTransactionID();

}
