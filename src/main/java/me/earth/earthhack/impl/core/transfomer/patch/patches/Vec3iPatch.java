package me.earth.earthhack.impl.core.transfomer.patch.patches;

import me.earth.earthhack.impl.core.Core;
import me.earth.earthhack.impl.core.transfomer.patch.ArgumentPatch;
import me.earth.earthhack.impl.core.util.AsmUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.*;

/**
 * Implements leijurvs BetterBlockPos directly.
 * {@link net.minecraftforge.common.util.BlockSnapshot}
 * {@link net.minecraft.world.NextTickListEntry}
 */
public class Vec3iPatch extends ArgumentPatch
{
    public Vec3iPatch()
    {
        super("fq", "net.minecraft.util.math.Vec3i", "leijurvpos");
    }

    @Override
    protected void applyPatch(ClassNode cn)
    {
        MethodNode x = AsmUtil.findMappedMethod(
                cn, "p", "()I", "func_177958_n", "getX", "()I");
        MethodNode y = AsmUtil.findMappedMethod(
                cn, "q", "()I", "func_177956_o", "getY", "()I");
        MethodNode z = AsmUtil.findMappedMethod(
                cn, "r", "()I", "func_177952_p", "getZ", "()I");

        MethodNode hashCode = AsmUtil.findMethod(cn, "hashCode", "()I");

        if (x == null || y == null || z == null || hashCode == null)
        {
            Core.LOGGER.error("Vec3i is missing one of: " + x + ", "
                                            + y + ", " + z + ", " + hashCode);
            return;
        }

        /*
            These instructions implement leijurvs BetterBlockPos longHashCode:

            @Override
            public int hashCode()
            {
                long hash = 3241;
                hash = 3457689L * hash + getX();
                hash = 8734625L * hash + getY();
                hash = 2873465L * hash + getZ();
                return (int) hash;
            }

            More compact in this form:

            return (int) (((11206370049L + getX()) * 8734625L + getY())
                                    * 2873465L + getZ());

            It is really important not to access the x, y, z fields here,
            but use the getter methods. Because MutableBlockPos exists. Took
            me forever to figure out why this wasn't working because of that...
        */
        hashCode.visitCode();

        // (((11206370049L + getX())
        hashCode.visitLdcInsn(11206370049L);
        hashCode.visitVarInsn(ALOAD, 0);
        hashCode.visitMethodInsn(INVOKEVIRTUAL, cn.name, x.name, x.desc, false);
        hashCode.visitInsn(I2L);
        hashCode.visitInsn(LADD);

        // * 8734625L + getY())
        hashCode.visitLdcInsn(8734625L);
        hashCode.visitInsn(LMUL);
        hashCode.visitVarInsn(ALOAD, 0);
        hashCode.visitMethodInsn(INVOKEVIRTUAL, cn.name, y.name, y.desc, false);
        hashCode.visitInsn(I2L);
        hashCode.visitInsn(LADD);

        //  * 2873465L + getZ())
        hashCode.visitLdcInsn(2873465L);
        hashCode.visitInsn(LMUL);
        hashCode.visitVarInsn(ALOAD, 0);
        hashCode.visitMethodInsn(INVOKEVIRTUAL, cn.name, z.name, z.desc, false);
        hashCode.visitInsn(I2L);
        hashCode.visitInsn(LADD);

        // return (int) (...);
        hashCode.visitInsn(L2I);
        hashCode.visitInsn(IRETURN);

        hashCode.visitMaxs(4, 1);
        hashCode.visitEnd();
    }

}
