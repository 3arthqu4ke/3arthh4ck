package me.earth.earthhack.installer.srg2notch.remappers;

import me.earth.earthhack.installer.srg2notch.Mapping;
import me.earth.earthhack.installer.srg2notch.MappingUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import java.util.ArrayList;
import java.util.List;

public class MethodRemapper implements Remapper
{
    @Override
    public void remap(ClassNode cn, Mapping mapping)
    {
        for (MethodNode mn : cn.methods)
        {
            mn.desc = MappingUtil.mapDescription(mn.desc, mapping);
            mn.name = MappingUtil.map(null, mn.name, mn.desc, mapping);
            if (mn.signature != null)
            {
                mn.signature = MappingUtil.mapSignature(mn.signature, mapping);
            }

            if (mn.tryCatchBlocks != null)
            {
                for (TryCatchBlockNode t : mn.tryCatchBlocks)
                {
                    t.type = mapping.getClasses().getOrDefault(t.type, t.type);
                }
            }

            if (mn.exceptions != null && !mn.exceptions.isEmpty())
            {
                List<String> exceptions = new ArrayList<>(mn.exceptions.size());
                for (String e : mn.exceptions)
                {
                    exceptions.add(mapping.getClasses().getOrDefault(e, e));
                }

                mn.exceptions = exceptions;
            }

            if (mn.localVariables != null)
            {
                for (LocalVariableNode l : mn.localVariables)
                {
                    l.desc = MappingUtil.mapDescription(l.desc, mapping);
                    if (l.signature != null)
                    {
                        l.signature =
                            MappingUtil.mapSignature(l.signature, mapping);
                    }
                }
            }
        }
    }

}
