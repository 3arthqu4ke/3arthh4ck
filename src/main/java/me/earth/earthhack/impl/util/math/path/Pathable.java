package me.earth.earthhack.impl.util.math.path;

import me.earth.earthhack.impl.util.math.raytrace.Ray;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface Pathable
{
    BlockPos getPos();

    Entity getFrom();

    Ray[] getPath();

    void setPath(Ray...path);

    int getMaxLength();

    boolean isValid();

    void setValid(boolean valid);

    List<BlockingEntity> getBlockingEntities();

}
