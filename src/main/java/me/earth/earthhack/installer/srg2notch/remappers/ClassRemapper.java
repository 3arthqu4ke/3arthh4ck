package me.earth.earthhack.installer.srg2notch.remappers;

import me.earth.earthhack.installer.srg2notch.Mapping;
import me.earth.earthhack.installer.srg2notch.MappingUtil;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;

public class ClassRemapper implements Remapper
{
    @Override
    public void remap(ClassNode cn, Mapping mapping)
    {
        if (cn.superName != null)
        {
            cn.superName = mapping.getClasses()
                                  .getOrDefault(cn.superName, cn.superName);
        }

        if (cn.interfaces != null && !cn.interfaces.isEmpty())
        {
            List<String> interfaces = new ArrayList<>(cn.interfaces.size());
            for (String i : cn.interfaces)
            {
                interfaces.add(mapping.getClasses().getOrDefault(i, i));
            }

            cn.interfaces = interfaces;
        }

        if (cn.signature != null)
        {
            cn.signature = MappingUtil.mapSignature(cn.signature, mapping);
        }
    }

}
