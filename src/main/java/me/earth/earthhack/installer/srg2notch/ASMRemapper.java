package me.earth.earthhack.installer.srg2notch;

import me.earth.earthhack.impl.core.util.AsmUtil;
import me.earth.earthhack.installer.srg2notch.remappers.*;
import org.objectweb.asm.tree.ClassNode;

public class ASMRemapper
{
    private final Remapper[] reMappers;

    public ASMRemapper()
    {
        reMappers    = new Remapper[5];
        reMappers[0] = new ClassRemapper();
        reMappers[1] = new FieldRemapper();
        reMappers[2] = new MethodRemapper();
        reMappers[3] = new InstructionRemapper();
        reMappers[4] = new AnnotationRemapper();
    }

    public byte[] transform(byte[] clazz, Mapping mapping)
    {
        ClassNode cn;
        try
        {
            cn = AsmUtil.read(clazz);
        }
        catch (IllegalArgumentException e)
        {
            // bad class file version.
            // kinda lazy solution, we could just change AsmUtil
            // in general but whever.
            return clazz;
        }

        for (Remapper remapper : reMappers)
        {
            remapper.remap(cn, mapping);
        }

        return AsmUtil.write(cn);
    }

}
