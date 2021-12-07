package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.chams.Chams;
import org.lwjgl.opengl.GL20;

public class RainbowChamsShader extends FramebufferShader {

    public static final RainbowChamsShader RAINBOW_CHAMS_SHADER = new RainbowChamsShader();

    private final ModuleCache<Chams> CHAMS =
            Caches.getModule(Chams.class);

    private final long initTime = System.currentTimeMillis();

    private RainbowChamsShader() {
        super("rainbowchams.frag");
    }

    @Override
    public void setupUniforms() {
        setupUniform("time");
        setupUniform("resolution");
        setupUniform("alpha");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform1f(getUniform("time"), (System.currentTimeMillis() - initTime) / 1000.0f);
        GL20.glUniform2f(getUniform("resolution"), (mc.displayWidth * 2) / 20.0f, (mc.displayHeight * 2) / 20.0f);
        GL20.glUniform1f(getUniform("alpha"), 1.0f);
    }

}
