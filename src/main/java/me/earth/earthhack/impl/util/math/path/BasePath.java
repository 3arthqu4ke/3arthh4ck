package me.earth.earthhack.impl.util.math.path;

import me.earth.earthhack.impl.util.math.raytrace.Ray;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class BasePath implements Pathable
{
    private final List<BlockingEntity> blocking = new ArrayList<>();
    private final int maxLength;
    private final BlockPos pos;
    private final Entity from;

    private boolean valid;
    private Ray[] path;

    public BasePath(Entity from, BlockPos pos, int maxLength)
    {
        this.from = from;
        this.pos = pos;
        this.maxLength = maxLength;
    }

    @Override
    public BlockPos getPos()
    {
        return pos;
    }

    @Override
    public Entity getFrom()
    {
        return from;
    }

    @Override
    public Ray[] getPath()
    {
        return path;
    }

    @Override
    public void setPath(Ray...path)
    {
        this.path = path;
    }

    @Override
    public int getMaxLength()
    {
        return maxLength;
    }

    @Override
    public boolean isValid()
    {
        return valid;
    }

    @Override
    public void setValid(boolean valid)
    {
        this.valid = valid;
    }

    @Override
    public List<BlockingEntity> getBlockingEntities()
    {
        return blocking;
    }

}
