package me.earth.earthhack.impl.core.mixins.world;

import me.earth.earthhack.impl.core.ducks.world.IChunk;
import net.minecraft.world.chunk.EmptyChunk;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EmptyChunk.class)
public abstract class MixinEmptyChunk implements IChunk {
    @Override
    public void setCompilingHoles(boolean compilingHoles) {

    }

    @Override
    public boolean isCompilingHoles() {
        return false;
    }

    @Override
    public void addHoleTask(Runnable task) {

    }

}
