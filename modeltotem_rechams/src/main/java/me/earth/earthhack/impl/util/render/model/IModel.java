package me.earth.earthhack.impl.util.render.model;

import jassimp.AiScene;
import me.earth.earthhack.api.util.interfaces.Nameable;

public interface IModel extends Nameable
{

    void setupMesh(Mesh mesh);

    Mesh[] genMeshes(AiScene scene);

    void render(double x, double y, double z, double partialTicks);

    Mesh[] getMeshes();

    default void setupMeshes()
    {
        for (Mesh mesh : getMeshes())
        {
            setupMesh(mesh);
        }
    }

    void setName(String name);

}
