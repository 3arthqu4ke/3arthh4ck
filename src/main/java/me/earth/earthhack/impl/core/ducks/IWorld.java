package me.earth.earthhack.impl.core.ducks;

/**
 * Duck interface for {@link net.minecraft.world.World}.
 */
public interface IWorld
{

    boolean isChunkLoaded(int x, int z, boolean allowEmpty);

}
