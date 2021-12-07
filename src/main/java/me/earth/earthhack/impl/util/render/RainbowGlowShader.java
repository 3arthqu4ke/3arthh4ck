package me.earth.earthhack.impl.util.render;

import org.lwjgl.opengl.GL20;

public class RainbowGlowShader extends FramebufferShader {

    public static final RainbowGlowShader RAINBOW_GLOW_SHADER = new RainbowGlowShader();

    private final long initTime = System.currentTimeMillis();

    public RainbowGlowShader() {
        super("rainbow.frag");
    }

    @Override
    public void setupUniforms() {
        setupUniform("texture");
        setupUniform("texelSize");
        setupUniform("divider");
        setupUniform("radius");
        setupUniform("maxSample");
        setupUniform("time");
        setupUniform("resolution");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform1i(getUniform("texture"), 0);
        GL20.glUniform2f(getUniform("texelSize"), 1F / mc.displayWidth * (radius * quality), 1F / mc.displayHeight * (radius * quality));
        GL20.glUniform1f(getUniform("divider"), 140F);
        GL20.glUniform1f(getUniform("radius"), radius);
        GL20.glUniform1f(getUniform("maxSample"), 10F);
        GL20.glUniform1f(getUniform("time"), (System.currentTimeMillis() - initTime) / 1000.0f);
        GL20.glUniform2f(getUniform("resolution"), (mc.displayWidth * 2) / 20.0f, (mc.displayHeight * 2) / 20.0f);
    }

}
