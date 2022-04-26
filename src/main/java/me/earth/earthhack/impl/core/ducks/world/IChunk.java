package me.earth.earthhack.impl.core.ducks.world;

public interface IChunk {
    void setCompilingHoles(boolean compilingHoles);

    boolean isCompilingHoles();

    void addHoleTask(Runnable task);

}
