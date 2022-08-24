package me.earth.earthhack.impl.core.mixins.block;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.xray.XRay;
import me.earth.earthhack.impl.modules.render.xray.mode.XrayMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockModelRenderer.class)
public abstract class MixinBlockModelRenderer
{
    private static final ModuleCache<XRay> XRAY = Caches.getModule(XRay.class);

    /**
     * {@link BlockModelRenderer#renderModel(IBlockAccess,
     * IBakedModel, IBlockState, BlockPos, BufferBuilder, boolean)}
     */
    @Inject(
        method = "renderModel" +
                "(Lnet/minecraft/world/IBlockAccess;" +
                "Lnet/minecraft/client/renderer/block/model/IBakedModel;" +
                "Lnet/minecraft/block/state/IBlockState;" +
                "Lnet/minecraft/util/math/BlockPos;" +
                "Lnet/minecraft/client/renderer/BufferBuilder;Z)Z",
        at = @At("HEAD"),
        cancellable = true)
    public void renderModelHook(IBlockAccess blockAccess,
                                 IBakedModel bakedModel,
                                 IBlockState blockState,
                                 BlockPos blockPos,
                                 BufferBuilder bufferBuilder,
                                 boolean b,
                                 CallbackInfoReturnable<Boolean> info)
    {
        if (XRAY.isEnabled()
                && XRAY.get().getMode() == XrayMode.Simple
                && !XRAY.get().shouldRender(blockState.getBlock()))
        {
            info.setReturnValue(false);
        }
    }

    /**
     * method = {@link BlockModelRenderer#renderModel(IBlockAccess,
     * IBakedModel, IBlockState, BlockPos, BufferBuilder, boolean, long)}
     * <p>
     * target = {@link BlockModelRenderer#renderModelFlat(IBlockAccess,
     * IBakedModel, IBlockState, BlockPos, BufferBuilder, boolean, long)}
     */
    @ModifyArg(
        method = "renderModel" +
                 "(Lnet/minecraft/world/IBlockAccess;" +
                 "Lnet/minecraft/client/renderer/block/model/IBakedModel;" +
                 "Lnet/minecraft/block/state/IBlockState;" +
                 "Lnet/minecraft/util/math/BlockPos;" +
                 "Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/BlockModelRenderer;" +
                     "renderModelFlat(Lnet/minecraft/world/IBlockAccess;" +
                     "Lnet/minecraft/client/renderer/block/model/IBakedModel;" +
                     "Lnet/minecraft/block/state/IBlockState;" +
                     "Lnet/minecraft/util/math/BlockPos;" +
                     "Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z"))
    public boolean renderModelFlatHook(boolean result)
    {
        if (XRAY.isEnabled() && XRAY.get().getMode() == XrayMode.Simple)
        {
            return false;
        }

        return result;
    }

    /**
     * method = {@link BlockModelRenderer#renderModel(IBlockAccess,
     * IBakedModel, IBlockState, BlockPos, BufferBuilder, boolean, long)}
     * <p>
     * target = {@link BlockModelRenderer#renderModelSmooth(IBlockAccess,
     * IBakedModel, IBlockState, BlockPos, BufferBuilder, boolean, long)}
     */
    @ModifyArg(
        method = "renderModel(Lnet/minecraft/world/IBlockAccess;" +
                "Lnet/minecraft/client/renderer/block/model/IBakedModel;" +
                "Lnet/minecraft/block/state/IBlockState;" +
                "Lnet/minecraft/util/math/BlockPos;" +
                "Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/BlockModelRenderer;" +
                    "renderModelSmooth(Lnet/minecraft/world/IBlockAccess;" +
                    "Lnet/minecraft/client/renderer/block/model/IBakedModel;" +
                    "Lnet/minecraft/block/state/IBlockState;" +
                    "Lnet/minecraft/util/math/BlockPos;" +
                    "Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z"))
    public boolean renderModelSmoothHook(boolean result)
    {
        if (XRAY.isEnabled() && XRAY.get().getMode() == XrayMode.Simple)
        {
            return false;
        }

        return result;
    }

}
