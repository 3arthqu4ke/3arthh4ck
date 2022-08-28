package me.earth.earthhack.impl.util.render.shader;

import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.Earthhack;
import org.lwjgl.opengl.GL11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * To be implemented on a case-by-case basis
 * TODO: parse shaders and types from the fragment and vertex shaders considering we already have the sources
 * No uniform blocks yet
 */
public abstract class Shader implements Nameable
{

    private int program;
    private int vertex;
    private int fragment;
    private boolean initialized;
    private final String name;

    private final Map<String, Integer> uniformMap = new HashMap<>();

    public Shader(String name, String path, String[] uniforms)
    {
        this(name, path);
        for (String uniformName : uniforms)
        {
            int uniform = glGetUniformLocation(program, uniformName);
            if (uniform > -1) uniformMap.put(uniformName, uniform);
        }
    }

    public Shader(String name, String path)
    {
        this.name = name;
        String source;
        try (InputStream stream = Shader.class.getResourceAsStream("/shaders/" + path))
        {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final byte[] buffer = new byte[512];
            int read;
            while ((read = stream.read(buffer, 0, buffer.length)) != -1)
            {
                out.write(buffer, 0, read);
            }
            source = new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
        catch (IOException | NullPointerException e)
        {
            Earthhack.getLogger().error("Shader " + name + " failed to read.");
            e.printStackTrace();
            return;
        }

        boolean vertexShader = false;
        final StringBuilder vertexBuilder = new StringBuilder();
        final StringBuilder fragmentBuilder = new StringBuilder();

        for (String line : source.split("\n"))
        {
            if (line.contains("#shader vertex"))
            {
                vertexShader = true;
                continue;
            }
            else if (line.contains("#shader fragment"))
            {
                vertexShader = false;
                continue;
            }

            if (vertexShader)
            {
                vertexBuilder.append(line);
            }
            else
            {
                fragmentBuilder.append(line);
            }
        }

        vertex = glCreateShader(GL_VERTEX_SHADER);
        fragment = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(vertex, vertexBuilder);
        glShaderSource(fragment, fragmentBuilder);

        glCompileShader(vertex);
        glCompileShader(fragment);

        if (glGetShaderi(vertex, GL_COMPILE_STATUS) == GL11.GL_FALSE)
        {
            Earthhack.getLogger().error("Shader " + name + "'s vertex shader failed to compile! Reason: " + glGetShaderInfoLog(vertex, 1024));
            return;
        }

        if (glGetShaderi(vertex, GL_COMPILE_STATUS) == GL11.GL_FALSE)
        {
            Earthhack.getLogger().error("Shader " + name + "'s fragment shader failed to compile! Reason: " + glGetShaderInfoLog(fragment, 1024));
            return;
        }

        program = glCreateProgram();
        glAttachShader(program, vertex);
        glAttachShader(program, fragment);
        glLinkProgram(program);

        if (glGetProgrami(program, GL_LINK_STATUS) == GL11.GL_FALSE)
        {
            Earthhack.getLogger().error("Shader " + name + "failed to link! Reason: " + glGetProgramInfoLog(fragment, 1024));
            return;
        }

        glDetachShader(program, vertex);
        glDetachShader(program, fragment);

        glValidateProgram(program);

        if (glGetProgrami(program, GL_VALIDATE_STATUS) == GL11.GL_FALSE)
        {
            Earthhack.getLogger().error("Shader " + name + "failed to validate! Reason: " + glGetProgramInfoLog(fragment, 1024));
            return;
        }

        initialized = true;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public boolean isInitialized()
    {
        return initialized;
    }

}
