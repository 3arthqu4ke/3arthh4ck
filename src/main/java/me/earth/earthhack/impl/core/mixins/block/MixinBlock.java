package me.earth.earthhack.impl.core.mixins.block;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.block.IBlock;
import me.earth.earthhack.impl.event.events.misc.CollisionEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.jesus.Jesus;
import me.earth.earthhack.impl.modules.movement.phase.Phase;
import me.earth.earthhack.impl.modules.render.xray.XRay;
import me.earth.earthhack.impl.modules.render.xray.mode.XrayMode;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;

@Mixin(Block.class)
public abstract class MixinBlock implements IBlock
{
    private static final ModuleCache<XRay> XRAY = Caches.getModule(XRay.class);
    private static final ModuleCache<Jesus> JESUS = Caches.getModule(Jesus.class);
    private static final ModuleCache<Phase> PHASE = Caches.getModule(Phase.class);
    private static final Minecraft MC = Minecraft.getMinecraft();

    private final String[] harvestToolNonForge = new String[16];
    private final int[] harvestLevelNonForge   = new int[16];

    @Shadow
    @Final
    protected Material material;

    @Shadow
    protected static void addCollisionBoxToList(BlockPos pos,
                                                AxisAlignedBB entityBox,
                                                List<AxisAlignedBB> cBoxes,
                                                AxisAlignedBB blockBox)
    {
        throw new IllegalStateException(
                "MixinBlock.addCollisionBoxToList has not been shadowed");
    }

    @Shadow
    public abstract BlockStateContainer getBlockState();

    @Shadow
    public abstract int getMetaFromState(IBlockState state);

    @Unique // TODO: test and use everywhere
    @Override
    public void setHarvestLevelNonForge(String toolClass, int level)
    {
        for (IBlockState state : getBlockState().getValidStates())
        {
            int idx = this.getMetaFromState(state);
            this.harvestToolNonForge[idx]  = toolClass;
            this.harvestLevelNonForge[idx] = level;
        }
    }

    @Unique
    @Override
    public String getHarvestToolNonForge(IBlockState state)
    {
        return harvestToolNonForge[getMetaFromState(state)];
    }

    @Unique
    @Override
    public int getHarvestLevelNonForge(IBlockState state)
    {
        return harvestLevelNonForge[getMetaFromState(state)];
    }

    @Inject(
        method = "<init>(Lnet/minecraft/block/material/Material;Lnet/minecraft/block/material/MapColor;)V",
        at = @At("RETURN"))
    public void ctrHook(Material blockMaterialIn,
                         MapColor blockMapColorIn,
                         CallbackInfo ci)
    {
        Arrays.fill(harvestLevelNonForge, -1);
    }

    /**
     * {@link Block#addCollisionBoxToList(IBlockState,
     * World, BlockPos, AxisAlignedBB, List, Entity, boolean)}
     */
    @Deprecated
    @Inject(
        method = "addCollisionBoxToList" +
                "(Lnet/minecraft/block/state/IBlockState;" +
                "Lnet/minecraft/world/World;" +
                "Lnet/minecraft/util/math/BlockPos;" +
                "Lnet/minecraft/util/math/AxisAlignedBB;" +
                "Ljava/util/List;" +
                "Lnet/minecraft/entity/Entity;" +
                "Z)V",
        at = @At("HEAD"),
        cancellable = true)
    @SuppressWarnings("DuplicatedCode")
    public void addCollisionBoxToListHook_Pre(IBlockState state,
                                               World world,
                                               BlockPos pos,
                                               AxisAlignedBB entityBox,
                                               List<AxisAlignedBB> cBoxes,
                                               Entity entity,
                                               boolean isActualState,
                                               CallbackInfo info)
    {
        if (!JESUS.isEnabled() && !PHASE.isEnabled())
        {
            return;
        }

        Block block = Block.class.cast(this);
        AxisAlignedBB bb = block.getCollisionBoundingBox(state, world, pos);
        CollisionEvent event = new CollisionEvent(pos, bb, entity, block);

        JESUS.get().onCollision(event);
        PHASE.get().onCollision(event);

        if (bb != event.getBB())
        {
            bb = event.getBB();
        }

        if (bb != null && entityBox.intersects(bb))
        {
            cBoxes.add(bb);
        }

        addCollisionBoxToList(pos, entityBox, cBoxes, bb);
        info.cancel();
    }

    @Inject(
        method = "addCollisionBoxToList" +
                "(Lnet/minecraft/util/math/BlockPos;" +
                "Lnet/minecraft/util/math/AxisAlignedBB;" +
                "Ljava/util/List;" +
                "Lnet/minecraft/util/math/AxisAlignedBB;)V",
        at = @At("HEAD"),
        cancellable = true)
    @SuppressWarnings("DuplicatedCode")
    private static void addCollisionBoxToListHook(BlockPos pos,
                                                  AxisAlignedBB entityBox,
                                                  List<AxisAlignedBB> cBoxes,
                                                  AxisAlignedBB blockBox,
                                                  CallbackInfo info)
    {
        if (blockBox != Block.NULL_AABB
                && (JESUS.isEnabled() || PHASE.isEnabled()))
        {
            AxisAlignedBB bb = blockBox.offset(pos);
            CollisionEvent event = new CollisionEvent(pos,
                                                      bb,
                                                      null,
                                                      MC.world != null
                                                        ? MC.world
                                                            .getBlockState(pos)
                                                            .getBlock()
                                                        : null);
            JESUS.get().onCollision(event);
            PHASE.get().onCollision(event);

            if (bb != event.getBB())
            {
                bb = event.getBB();
            }

            if (bb != null && entityBox.intersects(bb))
            {
                cBoxes.add(bb);
            }

            info.cancel();
        }
    }

    @Inject(
        method = "isFullCube",
        at = @At("HEAD"),
        cancellable = true)
    public void isFullCubeHook(IBlockState blockState,
                               CallbackInfoReturnable<Boolean> info)
    {
        if (XRAY.isEnabled() && XRAY.get().getMode() == XrayMode.Simple)
        {
            info.setReturnValue(XRAY.get()
                                    .shouldRender(Block.class.cast(this)));
        }
    }

    @Inject(
        method = "getAmbientOcclusionLightValue",
        at = @At("HEAD"),
        cancellable = true)
    public void ambientValueHook(CallbackInfoReturnable<Float> info)
    {
        if (XRAY.isEnabled() && XRAY.get().getMode() == XrayMode.Opacity)
        {
            info.setReturnValue(1.0F);
        }
    }

}
