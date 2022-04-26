package me.earth.earthhack.impl.modules.render.holeesp.invalidation;

public interface InvalidationConfig
{
    boolean isUsingInvalidationHoleManager();

    boolean shouldCalcChunksAsnyc();

    boolean limitChunkThreads();

    int getHeight();

    int getSortTime();

    int getRemoveTime();

}
