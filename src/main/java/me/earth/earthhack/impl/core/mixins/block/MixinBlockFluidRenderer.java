package me.earth.earthhack.impl.core.mixins.block;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.xray.XRay;
import me.earth.earthhack.impl.modules.render.xray.mode.XrayMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockFluidRenderer.class)
public abstract class MixinBlockFluidRenderer
{
    private static final ModuleCache<XRay> XRAY = Caches.getModule(XRay.class);

    @Inject(
        method = "renderFluid",
        at = @At("HEAD"),
        cancellable = true)
    public void renderFluidHook(IBlockAccess blockAccess,
                                IBlockState blockState,
                                BlockPos blockPos,
                                BufferBuilder bufferBuilder,
                                CallbackInfoReturnable<Boolean> info)
    {
        if (XRAY.isEnabled()
                && XRAY.get().getMode() == XrayMode.Simple
                && !XRAY.get().shouldRender(blockState.getBlock()))
        {
            info.setReturnValue(false);
        }
    }

}
