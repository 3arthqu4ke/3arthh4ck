package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.impl.core.ducks.render.IFramebuffer;
import net.minecraft.client.shader.Framebuffer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Framebuffer.class)
public abstract class MixinFramebuffer implements IFramebuffer
{



}
