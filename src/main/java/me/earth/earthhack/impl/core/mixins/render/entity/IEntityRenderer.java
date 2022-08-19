package me.earth.earthhack.impl.core.mixins.render.entity;

import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderer.class)
public interface IEntityRenderer {

    @Accessor("rendererUpdateCount")
    int getRendererUpdateCount();

    @Accessor("rainXCoords")
    float[] getRainXCoords();

    @Accessor("rainYCoords")
    float[] getRainYCoords();

    @Accessor("farPlaneDistance")
    float getFarPlaneDistance();

    @Accessor("fovModifierHandPrev")
    float getFovModifierHandPrev();

    @Accessor("fovModifierHand")
    float getFovModifierHand();

    @Accessor("debugView")
    boolean isDebugView();

    /*@Invoker("getFOVModifier")
    float getFOVModifier(float partialTicks, boolean useFOVSetting);*/

    /*@Invoker("setupCameraTransform")
    void setupCameraTransform(float partialTicks, int pass);*/

}
