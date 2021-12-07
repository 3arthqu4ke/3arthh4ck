package me.earth.earthhack.impl.core.util;

import org.objectweb.asm.ClassWriter;

public class NoSuperClassWriter extends ClassWriter
{
    public NoSuperClassWriter(int flags)
    {
        super(flags);
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2)
    {
        if (type1.equals("blr"))
        {
            return "blk";
        }

        return "java/lang/Object";
    }

}
