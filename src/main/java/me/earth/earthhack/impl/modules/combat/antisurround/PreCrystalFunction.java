package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.combat.antisurround.util.AntiSurroundFunction;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.MineSlots;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

final class PreCrystalFunction implements AntiSurroundFunction, Globals
{
    private final AntiSurround module;

    public PreCrystalFunction(AntiSurround module)
    {
        this.module = module;
    }

    @Override
    public void accept(BlockPos pos,
                       BlockPos down,
                       BlockPos on,
                       EnumFacing onFacing,
                       int obbySlot,
                       MineSlots slots,
                       int crystalSlot,
                       Entity blocking,
                       EntityPlayer found,
                       boolean execute)
    {
        if (blocking != null)
        {
            return;
        }

        // TODO: support Obby???
        IBlockState state = mc.world.getBlockState(down);
        if (state.getBlock() != Blocks.OBSIDIAN
                && state.getBlock() != Blocks.BEDROCK)
        {
            return;
        }

        synchronized (module)
        {
            // check again, this time synchronized
            if (module.isActive()
                || AntiSurround.LEG_SWITCH
                               .returnIfPresent(LegSwitch::isActive, false))
            {
                return;
            }

            if (module.normal.getValue() || module.async.getValue())
            {
                module.semiActiveTime = System.nanoTime();
                module.semiActive.set(true);
                module.semiPos = down;
            }

            RayTraceResult ray = null;
            if (module.rotations != null)
            {
                ray = RotationUtil.rayTraceWithYP(down, mc.world,
                        module.rotations[0], module.rotations[1],
                        (b, p) -> p.equals(down));
            }

            if (ray == null)
            {
                module.rotations = RotationUtil.getRotations(
                        down.getX() + 0.5f,
                        down.getY() + 1,
                        down.getZ() + 0.5f,
                        RotationUtil.getRotationPlayer().posX,
                        RotationUtil.getRotationPlayer().posY,
                        RotationUtil.getRotationPlayer().posZ,
                        mc.player.getEyeHeight());

                ray = RotationUtil.rayTraceWithYP(down, mc.world,
                        module.rotations[0], module.rotations[1],
                        (b, p) -> p.equals(down));

                if (ray == null)
                {
                    ray = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5),
                            EnumFacing.UP,
                            down);
                }
            }

            RayTraceResult finalResult = ray;
            float[] f = RayTraceUtil.hitVecToPlaceVec(down, ray.hitVec);
            EnumHand h = InventoryUtil.getHand(crystalSlot);
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
            {
                int lastSlot = mc.player.inventory.currentItem;
                module.cooldownBypass.getValue().switchTo(crystalSlot);
                mc.player.connection.sendPacket(
                    new CPacketPlayerTryUseItemOnBlock(
                        down, finalResult.sideHit, h, f[0], f[1], f[2]));
                module.cooldownBypass.getValue().switchBack(
                    lastSlot, crystalSlot);
            });
        }
    }

}
