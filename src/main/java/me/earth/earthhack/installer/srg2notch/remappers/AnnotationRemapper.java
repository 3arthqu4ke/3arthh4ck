package me.earth.earthhack.installer.srg2notch.remappers;

import me.earth.earthhack.installer.srg2notch.Mapping;
import me.earth.earthhack.installer.srg2notch.MappingUtil;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;

public class AnnotationRemapper implements Remapper
{
    @Override
    public void remap(ClassNode cn, Mapping mapping)
    {
        if (cn.invisibleAnnotations != null)
        {
            for (AnnotationNode node : cn.invisibleAnnotations)
            {
                remapAnnotation(node, mapping);
            }
        }

        if (cn.visibleAnnotations != null)
        {
            for (AnnotationNode node : cn.visibleAnnotations)
            {
                remapAnnotation(node, mapping);
            }
        }

        /*

        if (cn.invisibleTypeAnnotations != null)
        {
            for (TypeAnnotationNode node : cn.invisibleTypeAnnotations)
            {
                TODO: too lazy rn to do TypeAnnotations, idk what these are tbh
                 and we dont seem to have any
            }
        }

        if (cn.visibleTypeAnnotations != null)
        {
            for (TypeAnnotationNode node : cn.visibleTypeAnnotations)
            {

            }
        }

        */
    }

    private void remapAnnotation(AnnotationNode node, Mapping mapping)
    {
        if (node.values == null || node.values.isEmpty())
        {
            return;
        }

        List<Object> values = new ArrayList<>(node.values.size());
        remapList(node.values, values, mapping);
        node.values = values;
    }

    @SuppressWarnings("unchecked")
    private void remapList(List<Object> objects,
                           List<Object> collecting,
                           Mapping mapping)
    {
        for (Object t : objects)
        {
            if (t instanceof Type)
            {
                t = MappingUtil.map((Type) t, mapping);
            }
            else if (t instanceof List)
            {
                List<Object> list = (List<Object>) t;
                t = new ArrayList<>(list.size());
                remapList(list, (List<Object>) t, mapping);
            }

            collecting.add(t);
        }
    }

}
