package me.earth.earthhack.installer.srg2notch.remappers;

import me.earth.earthhack.installer.srg2notch.Mapping;
import org.objectweb.asm.tree.ClassNode;

public interface Remapper
{
    void remap(ClassNode cn, Mapping mapping);

}
