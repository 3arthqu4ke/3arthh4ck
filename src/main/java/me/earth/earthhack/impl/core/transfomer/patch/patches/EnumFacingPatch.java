package me.earth.earthhack.impl.core.transfomer.patch.patches;

import me.earth.earthhack.impl.core.Core;
import me.earth.earthhack.impl.core.transfomer.patch.ArgumentPatch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import static org.objectweb.asm.Opcodes.ACC_STATIC;

public class EnumFacingPatch extends ArgumentPatch
{
    private int applied = 0;

    public EnumFacingPatch()
    {
        super("fa",
                "net.minecraft.util.EnumFacing",
                "vanilla");
    }

    @Override
    protected void applyPatch(ClassNode node)
    {
        applied++;
        for (FieldNode field : node.fields)
        {
            if (field.signature == null
                    && "o".equals(field.name) // this patch is only for vanilla
                    && (ACC_STATIC & field.access) == ACC_STATIC)
            {
                Core.LOGGER.info("Made EnumFacing.HORIZONTALS public!");
                field.access &= ~Opcodes.ACC_PRIVATE;   // remove private
                field.access &= ~Opcodes.ACC_PROTECTED; // remove protected
                field.access |= Opcodes.ACC_PUBLIC;     // add public
            }
        }

        this.setFinished(applied >= 2);
    }

}
