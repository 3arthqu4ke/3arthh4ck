package me.earth.earthhack.impl.modules.render.holeesp.invalidation;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleHoleManager implements HoleManager
{
    private final Map<BlockPos, Hole> holes;
    private final List<Hole> _1x1_safe;
    private final List<Hole> _1x1_unsafe;
    private final List<Hole> _2x1;
    private final List<Hole> _2x2;

    public SimpleHoleManager()
    {
        this(new HashMap<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public SimpleHoleManager(Map<BlockPos, Hole> holes,
                             List<Hole> _1x1_safe,
                             List<Hole> _1x1_unsafe,
                             List<Hole> _2x1,
                             List<Hole> _2x2)
    {
        this.holes = holes;
        this._1x1_safe = _1x1_safe;
        this._1x1_unsafe = _1x1_unsafe;
        this._2x1 = _2x1;
        this._2x2 = _2x2;
    }

    @Override
    public Map<BlockPos, Hole> getHoles()
    {
        return holes;
    }

    @Override
    public List<Hole> get1x1()
    {
        return _1x1_safe;
    }

    @Override
    public List<Hole> get1x1Unsafe()
    {
        return _1x1_unsafe;
    }

    @Override
    public List<Hole> get2x1()
    {
        return _2x1;
    }

    @Override
    public List<Hole> get2x2()
    {
        return _2x2;
    }

}
