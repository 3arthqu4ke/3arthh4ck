package me.earth.earthhack.impl.core.transfomer;

public interface PatchManager
{
    void addPatch(Patch patch);

    byte[] transform(String name, String transformedName, byte[] bytes);

}
