package me.earth.earthhack.impl.core.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

@SuppressWarnings("unused")
public class AsmUtil
{
    public static ClassNode read(byte[] clazz, int...flags)
    {
        ClassNode result = new ClassNode();
        ClassReader reader = new ClassReader(clazz);
        reader.accept(result, toFlag(flags));

        return result;
    }

    public static byte[] write(ClassNode classNode, int...flags)
    {
        ClassWriter writer = new ClassWriter(toFlag(flags));
        classNode.accept(writer);

        return writer.toByteArray();
    }

    public static byte[] writeNoSuperClass(ClassNode classNode, int...flags)
    {
        ClassWriter writer = new NoSuperClassWriter(toFlag(flags));
        classNode.accept(writer);

        return writer.toByteArray();
    }

    public static MethodNode findMappedMethod(ClassNode node,
                                              String notch,
                                              String notchDesc,
                                              String searge,
                                              String mcp,
                                              String srgMcpDesc)
    {
        MethodNode result = findMethod(node, notch, notchDesc);
        if (result == null)
        {
            result = findMethod(node, searge, srgMcpDesc);
            if (result == null)
            {
                return findMethod(node, mcp, srgMcpDesc);
            }
        }

        return result;
    }

    public static MethodNode findMethod(ClassNode node,
                                        String name,
                                        String description)
    {
        for (MethodNode mn : node.methods)
        {
            if (mn.name.equals(name) && mn.desc.equals(description))
            {
                return mn;
            }
        }

        return null;
    }

    public static FieldNode findField(ClassNode node, String...names)
    {
        for (String name : names)
        {
            FieldNode result = findField(node, name);
            if (result != null)
            {
                return result;
            }
        }

        return null;
    }

    public static FieldNode findField(ClassNode node, String name)
    {
        for (FieldNode field : node.fields)
        {
            if (field.name.equals(name))
            {
                return field;
            }
        }

        return null;
    }

    public static int toFlag(int...flags)
    {
        int flag = 0;
        for (int f : flags)
        {
            flag |= f;
        }

        return flag;
    }

}
