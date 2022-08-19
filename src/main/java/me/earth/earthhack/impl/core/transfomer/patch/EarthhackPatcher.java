package me.earth.earthhack.impl.core.transfomer.patch;

import me.earth.earthhack.impl.core.Core;
import me.earth.earthhack.impl.core.transfomer.Patch;
import me.earth.earthhack.impl.core.transfomer.PatchManager;
import me.earth.earthhack.impl.core.util.AsmUtil;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

public class EarthhackPatcher implements PatchManager
{
    private static final EarthhackPatcher INSTANCE = new EarthhackPatcher();

    private final List<Patch> patches = new ArrayList<>();

    private EarthhackPatcher() { }

    public static EarthhackPatcher getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void addPatch(Patch patch)
    {
        patches.add(patch);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        List<Patch> found = patches.stream()
                                   .filter(p -> p.getName().equals(name)
                                                || p.getTransformedName()
                                                    .equals(transformedName))
                                   .collect(Collectors.toList());
        if (!found.isEmpty())
        {
            Core.LOGGER.info("Found " + found.size() + " patch"
                             + (found.size() == 1 ? "" : "es") + " for: "
                             + name +  " : " + transformedName);

            ClassNode cn = AsmUtil.read(bytes);
            found.forEach(p -> p.apply(cn));
            patches.removeIf(Patch::isFinished);
            return AsmUtil.writeNoSuperClass(cn, COMPUTE_MAXS, COMPUTE_FRAMES);
            //ClassWriter writer = new NoSuperClassWriter();
            //node.accept(writer);
            //return writer.toByteArray();
        }

        return bytes;
    }

}
