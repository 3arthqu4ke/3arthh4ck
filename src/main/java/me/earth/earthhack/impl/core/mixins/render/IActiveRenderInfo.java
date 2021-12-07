package me.earth.earthhack.impl.core.mixins.render;

import net.minecraft.client.renderer.ActiveRenderInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.FloatBuffer;

@Mixin(ActiveRenderInfo.class)
public interface IActiveRenderInfo {

    @SuppressWarnings("ALL")
    @Accessor(value = "MODELVIEW")
    static FloatBuffer getViewport() {
        throw new IllegalStateException();
    }

    @SuppressWarnings("ALL")
    @Accessor(value = "PROJECTION")
    static FloatBuffer getProjection() {
        throw new IllegalStateException();
    }

    @SuppressWarnings("ALL")
    @Accessor(value = "MODELVIEW")
    static FloatBuffer getModelview() {
        throw new IllegalStateException();
    }

}
