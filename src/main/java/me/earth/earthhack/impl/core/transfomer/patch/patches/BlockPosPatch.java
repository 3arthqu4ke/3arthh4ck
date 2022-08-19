package me.earth.earthhack.impl.core.transfomer.patch.patches;

import me.earth.earthhack.impl.core.Core;
import me.earth.earthhack.impl.core.transfomer.patch.ArgumentPatch;
import me.earth.earthhack.impl.core.util.AsmUtil;
import me.earth.earthhack.impl.util.misc.ReflectionUtil;
import net.minecraft.util.math.Vec3i;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Method;
import java.util.Collections;

import static org.objectweb.asm.Opcodes.*;

/**
 * Implements leijurvs BetterBlockPos directly.
 */
public class BlockPosPatch extends ArgumentPatch
{
    public BlockPosPatch()
    {
        super("et", "net.minecraft.util.math.BlockPos", "leijurvpos");
    }

    @Override
    protected void applyPatch(ClassNode node)
    {
        OffsetPatch[] patches =
        {
            new OffsetPatch(Direction.UP,
                    "b", "(I)Let;", "func_177981_b", "up",    true),
            new OffsetPatch(Direction.UP,
                    "a", "()Let;", "func_177984_a",  "up",    false),

            new OffsetPatch(Direction.DOWN,
                    "c", "(I)Let;", "func_177979_c", "down",  true),
            new OffsetPatch(Direction.DOWN,
                    "b", "()Let;", "func_177977_b",  "down",  false),

            new OffsetPatch(Direction.NORTH,
                    "d", "(I)Let;", "func_177964_d", "north", true),
            new OffsetPatch(Direction.NORTH,
                    "c", "()Let;", "func_177978_c",  "north", false),

            new OffsetPatch(Direction.SOUTH,
                    "e", "(I)Let;", "func_177970_e", "south", true),
            new OffsetPatch(Direction.SOUTH,
                    "d", "()Let;", "func_177968_d",  "south", false),

            new OffsetPatch(Direction.EAST,
                    "g", "(I)Let;", "func_177965_g", "east",  true),
            new OffsetPatch(Direction.EAST,
                    "f", "()Let;", "func_177974_f",  "east",  false),

            new OffsetPatch(Direction.WEST,
                    "f", "(I)Let;", "func_177985_f", "west",  true),
            new OffsetPatch(Direction.WEST,
                    "e", "()Let;", "func_177976_e",  "west",  false)
        };

        for (OffsetPatch patch : patches)
        {
            patch(node, patch);
        }

        patchOffset(node);
    }

    private void patchOffset(ClassNode node)
    {
        MethodNode offset = AsmUtil.findMappedMethod(
         node, "a", "(Lfa;)Let;", "func_177972_a", "offset",
         "(Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/util/math/BlockPos;");
        MethodNode offsetI = AsmUtil.findMappedMethod(
         node, "a", "(Lfa;I)Let;", "func_177967_a", "offset",
        "(Lnet/minecraft/util/EnumFacing;I)Lnet/minecraft/util/math/BlockPos;");

        if (offset == null || offsetI == null)
        {
            Core.LOGGER.error(
                    "Couldn't find " + offset + " or " + offsetI + "!");
            return;
        }

        offset.instructions.clear();
        boolean newFound = false;
        for (int i = 0; i < offsetI.instructions.size(); i++)
        {
            AbstractInsnNode insn = offsetI.instructions.get(i);
            if (insn instanceof TypeInsnNode
                && insn.getOpcode() == NEW
                && ((TypeInsnNode) insn).desc.equals(node.name))
            {
                newFound = true;
            }
            else if (!newFound
                || insn instanceof FrameNode
                || insn instanceof LabelNode
                || insn instanceof VarInsnNode && ((VarInsnNode) insn).var == 2
                || insn.getOpcode() == IMUL)
            {
                continue;
            }

            offset.instructions.add(insn.clone(Collections.emptyMap()));
        }
    }

    private void patch(ClassNode cn, OffsetPatch op)
    {
        MethodNode mn = AsmUtil.findMappedMethod(
                cn, op.notch, op.notchDesc, op.searge, op.mcp, op.srgMcpDesc);

        // We are allowed to load the Vec3i class here,
        // since its the superclass and should already be loaded.
        Method x = ReflectionUtil.getMethod(
                Vec3i.class, "p", "func_177958_n", "getX");
        Method y = ReflectionUtil.getMethod(
                Vec3i.class, "q", "func_177956_o", "getY");
        Method z = ReflectionUtil.getMethod(
                Vec3i.class, "r", "func_177952_p", "getZ");

        if (mn == null)
        {
            Core.LOGGER.error(
                    "Couldn't find " + op + " in BlockPos!");
            return;
        }

        mn.instructions.clear();
        mn.visitCode();
        if (op.notchDesc.startsWith("(I)"))
        {
            Label L1 = new Label();
            mn.visitVarInsn(ILOAD, 1);
            mn.visitJumpInsn(IFNE, L1);
            mn.visitVarInsn(ALOAD, 0);
            mn.visitInsn(ARETURN);
            mn.visitLabel(L1);
        }

        mn.visitTypeInsn(NEW, cn.name);
        mn.visitInsn(DUP);
        mn.visitVarInsn(ALOAD, 0);
        mn.visitMethodInsn(
                INVOKEVIRTUAL, cn.name, x.getName(), "()I", false);

        switch (op.direction)
        {
            case UP:
            case DOWN:
                mn.visitVarInsn(ALOAD, 0);
                mn.visitMethodInsn(
                        INVOKEVIRTUAL, cn.name, y.getName(), "()I", false);
                addOrSub(mn, op);
                mn.visitVarInsn(ALOAD, 0);
                mn.visitMethodInsn(
                        INVOKEVIRTUAL, cn.name, z.getName(), "()I", false);
                break;
            case NORTH:
            case SOUTH:
                mn.visitVarInsn(ALOAD, 0);
                mn.visitMethodInsn(
                        INVOKEVIRTUAL, cn.name, y.getName(), "()I", false);
                mn.visitVarInsn(ALOAD, 0);
                mn.visitMethodInsn(
                        INVOKEVIRTUAL, cn.name, z.getName(), "()I", false);
                addOrSub(mn, op);
                break;
            case EAST:
            case WEST:
                addOrSub(mn, op);
                mn.visitVarInsn(ALOAD, 0);
                mn.visitMethodInsn(
                        INVOKEVIRTUAL, cn.name, y.getName(), "()I", false);
                mn.visitVarInsn(ALOAD, 0);
                mn.visitMethodInsn(
                        INVOKEVIRTUAL, cn.name, z.getName(), "()I", false);
                break;
        }

        mn.visitMethodInsn(INVOKESPECIAL, cn.name, "<init>", "(III)V", false);
        mn.visitInsn(ARETURN);

        mn.visitMaxs(0, 0); // let the ClassWriter do this
        mn.visitEnd();
    }

    private void addOrSub(MethodNode mn, OffsetPatch op)
    {
        if (op.notchDesc.startsWith("(I)"))
        {
            mn.visitVarInsn(ILOAD, 1);
        }
        else
        {
            mn.visitInsn(ICONST_1);
        }

        mn.visitInsn(op.direction.offset > 0 ? IADD : ISUB);
    }

    private static final class OffsetPatch
    {
        public final Direction direction;
        public final String notch;
        public final String notchDesc;
        public final String searge;
        public final String mcp;
        public final String srgMcpDesc;

        public OffsetPatch(Direction direction,
                           String notch,
                           String notchDesc,
                           String searge,
                           String mcp,
                           boolean takesInt)
        {
            this.direction  = direction;
            this.notch      = notch;
            this.notchDesc  = notchDesc;
            this.searge     = searge;
            this.mcp        = mcp;
            this.srgMcpDesc = takesInt ? "(I)Lnet/minecraft/util/math/BlockPos;"
                                       : "()Lnet/minecraft/util/math/BlockPos;";
        }

        @Override
        public String toString()
        {
            return notch + notchDesc + " -> "
                    + mcp + srgMcpDesc + " (" + searge + ")";
        }
    }

    private enum Direction
    {
        UP(1),
        DOWN(-1),
        NORTH(-1),
        SOUTH(1),
        WEST(-1),
        EAST(1);

        public final int offset;

        Direction(int offset)
        {
            this.offset = offset;
        }
    }

}
