package me.earth.earthhack.impl.core.ducks.world;

public interface IChunk
{
    boolean isCompilingHoles();

    void setCompilingHoles(boolean compilingHoles);

    void addHoleTask(Runnable task);

    /**
     * @return the current version of this chunk. Whenever this chunk is loaded or unloaded this number will be
     * incremented by one. That way every Hole found in a previous version of this Chunk becomes invalid.
     */
    int getHoleVersion();

    void setHoleVersion(int version);

}
