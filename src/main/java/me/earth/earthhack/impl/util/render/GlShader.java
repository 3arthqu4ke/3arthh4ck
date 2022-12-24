package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.api.util.interfaces.Nameable;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector4f;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author cookiedragon234
 * TOOD: antipaste
 */
public class GlShader implements Nameable {

    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private String name;

    private Map<String, Integer> uniforms;

    public GlShader(InputStream sourceStream, String name)
    {
        this.name = name;
        this.uniforms = new HashMap<>();
        if (sourceStream == null) return;
        String source;
        try {
            source = readStreamToString(sourceStream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        StringBuilder vertexSource = new StringBuilder(source.length() / 2);
        StringBuilder fragmentSource = new StringBuilder(source.length() / 2);

        int mode = -1;

        for (String line : source.split("\n")) {
            if (line.contains("#shader vert")) {
                mode = 0;
            } else if (line.contains("#shader frag")) {
                mode = 1;
            } else {
                if (mode == 0) {
                    vertexSource.append(line).append("\n");
                } else if (mode == 1) {
                    fragmentSource.append(line).append("\n");
                }
            }
        }

        int vertId = glCreateShader(GL_VERTEX_SHADER);
        int fragId = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(vertId, vertexSource);
        glShaderSource(fragId, fragmentSource);

        glCompileShader(vertId);
        glCompileShader(fragId);

        if (glGetShaderi(vertId, GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String error = GL20.glGetShaderInfoLog(vertId, 1024);
            System.err.println("Vertex shader " + name + " could not compile: " + error);
        }
        if (glGetShaderi(fragId, GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String error = GL20.glGetShaderInfoLog(fragId, 1024);
            System.err.println("Fragment shader " + name + " could not compile: " + error);
        }

        int programId = glCreateProgram();

        this.programId = programId;
        this.vertexShaderId = vertId;
        this.fragmentShaderId = fragId;

        glAttachShader(programId, vertId);
        glAttachShader(programId, fragId);
        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == GL11.GL_FALSE) {
            String error = GL20.glGetShaderInfoLog(programId, 1024);
            System.err.println("Shader " + name + " could not be linked: " + error);
        }

        glDetachShader(programId, vertId);
        glDetachShader(programId, fragId);

        glValidateProgram(programId);
    }

    public GlShader(String name)
    {
        this(GlShader.class.getResourceAsStream("/shaders/" + name + ".shader"), name);
    }

    public GlShader(int programId, int vertexShaderId, int fragmentShaderId) {
        this.programId = programId;
        this.vertexShaderId = vertexShaderId;
        this.fragmentShaderId = fragmentShaderId;
        this.uniforms = new HashMap<>();
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    protected void finalize() {
        unbind();
        glDeleteProgram(programId);
    }

    public int createUniform(String uniformName) {
        if (uniforms.containsKey(uniformName)) {
            return uniforms.get(uniformName);
        } else {
            int location = glGetUniformLocation(programId, uniformName);
            uniforms.put(uniformName, location);
            return location;
        }
    }

    public void set(String uniformName, int value) {
        glUniform1i(createUniform(uniformName), value);
    }

    public void set(String uniformName, float value) {
        glUniform1f(createUniform(uniformName), value);
    }

    public void set(String uniformName, boolean value) {
        glUniform1i(createUniform(uniformName), value ? 1 : 0);
    }

    public void set(String uniformName, Vec2f value) {
        glUniform2f(createUniform(uniformName), value.x, value.y);
    }

    public void set(String uniformName, Vec3d value) {
        glUniform3f(createUniform(uniformName), (float) value.x, (float) value.y, (float) value.z);
    }

    public void set(String uniformName, Vector4f value) {
        glUniform4f(createUniform(uniformName), value.x, value.y, value.z, value.w);
    }

    public void set(String uniformName, Color value) {
        glUniform4f(createUniform(uniformName), value.getRed() / 255.0f, value.getBlue() / 255.0f, value.getGreen() / 255.0f, value.getAlpha() / 255.0f);
    }

    public void set(String uniformName, FloatBuffer matrix)
    {
        glUniformMatrix4(createUniform(uniformName), false, matrix);
    }

    public void set(String uniformName, int[] integers) {
        glUniform4i(createUniform(uniformName), integers[0], integers[1], integers[2], integers[3]);
    }

    public int getVertexShaderId() {
        return vertexShaderId;
    }

    public int getFragmentShaderId() {
        return fragmentShaderId;
    }

    public int getProgramId() {
        return programId;
    }

    private static String readStreamToString(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buffer = new byte[512];
        int read;
        while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, read);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    // TODO: replace kotlin design with more conventional java design
    public static GlShader createShader(String name) {
        InputStream sourceStream = GlShader.class.getResourceAsStream("/shaders/" + name + ".shader");
        return createShader(name, sourceStream);
    }

    public static GlShader createShader(String name, InputStream sourceStream) {
        if (sourceStream == null) return null;
        String source;
        try {
            source = readStreamToString(sourceStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder vertexSource = new StringBuilder(source.length() / 2);
        StringBuilder fragmentSource = new StringBuilder(source.length() / 2);

        int mode = -1;

        for (String line : source.split("\n")) {
            if (line.contains("#shader vert")) {
                mode = 0;
            } else if (line.contains("#shader frag")) {
                mode = 1;
            } else {
                if (mode == 0) {
                    vertexSource.append(line).append("\n");
                } else if (mode == 1) {
                    fragmentSource.append(line).append("\n");
                }
            }
        }

        int vertId = glCreateShader(GL_VERTEX_SHADER);
        int fragId = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(vertId, vertexSource);
        glShaderSource(fragId, fragmentSource);

        glCompileShader(vertId);
        glCompileShader(fragId);

        if (glGetShaderi(vertId, GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String error = GL20.glGetShaderInfoLog(vertId, 1024);
            System.err.println("Vertex shader " + name + " could not compile: " + error);
        }
        if (glGetShaderi(fragId, GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String error = GL20.glGetShaderInfoLog(fragId, 1024);
            System.err.println("Fragment shader " + name + " could not compile: " + error);
        }

        int programId = glCreateProgram();

        GlShader shader = new GlShader(programId, vertId, fragId);

        glAttachShader(programId, vertId);
        glAttachShader(programId, fragId);
        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == GL11.GL_FALSE) {
            String error = GL20.glGetShaderInfoLog(programId, 1024);
            System.err.println("Shader " + name + " could not be linked: " + error);
        }

        glDetachShader(programId, vertId);
        glDetachShader(programId, fragId);

        glValidateProgram(programId);

        return shader;
    }

    @Override
    public String getName()
    {
        return name;
    }

}
