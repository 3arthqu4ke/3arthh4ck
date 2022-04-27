package me.earth.earthhack.impl.util.render.shader;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.util.render.GlShader;
import net.minecraft.util.math.Vec2f;

import java.io.InputStream;

// Kinda stupid
public class SettingShader implements Nameable, Globals
{

    private static final long startTime = System.currentTimeMillis();
    private final GlShader shader;
    private long lastTime = -1;

    public SettingShader(InputStream sourceStream, String name)
    {
        shader = new GlShader(sourceStream, name);
    }

    public SettingShader(String name)
    {
        shader = new GlShader(name);
    }

    public SettingShader(int programId, int vertexShaderId, int fragmentShaderId)
    {
        shader = new GlShader(programId, vertexShaderId, fragmentShaderId);
    }

    /**
     * Updates the shader's uniforms.
     * Assumes shader is bound.
     * @param texture For multisampling.
     */
    public void updateUniforms(int texture)
    {
        long currentTime = System.currentTimeMillis();
        if (lastTime == -1) lastTime = currentTime;
        long delta = currentTime - lastTime;
        shader.set("sampler", texture);
        shader.set("resolution", new Vec2f(mc.displayWidth, mc.displayHeight));
        shader.set("time", currentTime - startTime); // in milliseconds!
    }

    public void bind()
    {
        shader.bind();
    }

    public void unbind()
    {
        shader.unbind();
    }

    @Override
    public String getName()
    {
        return shader.getName();
    }
}
