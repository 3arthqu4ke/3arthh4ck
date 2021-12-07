package me.earth.earthhack.impl.core.ducks.render;

import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;

import java.util.List;

/**
 * @author Gerald
 * @since 6/14/2021
 **/

public interface IShaderGroup {

    List<Framebuffer> getListFramebuffers();

    List<Shader> getListShaders();
}
