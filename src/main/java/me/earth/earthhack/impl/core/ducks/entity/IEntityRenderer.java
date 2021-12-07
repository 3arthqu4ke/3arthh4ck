package me.earth.earthhack.impl.core.ducks.entity;

/**
 * Duck interface for {@link net.minecraft.client.renderer.EntityRenderer}.
 */
public interface IEntityRenderer
{
    void invokeSetupCameraTransform(float partialTicks, int pass);

    void invokeOrientCamera(float partialTicks);

    void invokeRenderHand(float partialTicks, int pass);

    void setLightmapUpdateNeeded(boolean needed);

}
