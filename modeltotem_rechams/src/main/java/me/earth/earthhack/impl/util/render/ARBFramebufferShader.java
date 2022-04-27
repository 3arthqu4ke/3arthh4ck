package me.earth.earthhack.impl.util.render;

import net.minecraft.client.shader.Framebuffer;

/**
 * @author megyn
 * the other framebuffer shader uses the inefficient minecraft framebuffer class
 * this one uses arb framebuffer objects which are much more performant
 */
public abstract class ARBFramebufferShader {

    private Framebuffer framebuffer;

    public ARBFramebufferShader(String fragmentShader)
    {

    }

    public void start(float partialTicks) {

    }

    public void stop() {

    }

    private void recheckFramebufferSetup()
    {

    }

}
