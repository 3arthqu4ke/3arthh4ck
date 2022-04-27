package me.earth.earthhack.impl.core.mixins.world;

import me.earth.earthhack.impl.core.ducks.world.IChunk;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayDeque;
import java.util.Deque;

@Mixin(Chunk.class)
public abstract class MixinChunk implements IChunk
{
    private final Deque<Runnable> postHoleCompilationTasks = new ArrayDeque<>();
    private boolean compilingHoles = false;

    @Override
    public void setCompilingHoles(boolean compilingHoles)
    {
        this.compilingHoles = compilingHoles;
        if (!compilingHoles)
        {
            CollectionUtil.emptyQueue(postHoleCompilationTasks);
        }
    }

    @Override
    public boolean isCompilingHoles()
    {
        return compilingHoles;
    }

    @Override
    public void addHoleTask(Runnable task)
    {
        if (isCompilingHoles())
        {
            postHoleCompilationTasks.add(task);
        }
        else
        {
            task.run();
        }
    }

}
