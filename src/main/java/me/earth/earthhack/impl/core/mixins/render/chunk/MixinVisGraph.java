package me.earth.earthhack.impl.core.mixins.render.chunk;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.modules.render.xray.XRay;
import me.earth.earthhack.impl.modules.render.xray.mode.XrayMode;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumSet;
import java.util.Set;

@Mixin(VisGraph.class)
public abstract class MixinVisGraph
{
    private static final ModuleCache<XRay> XRAY =
        Caches.getModule(XRay.class);
    private static final ModuleCache<Freecam> FREECAM =
        Caches.getModule(Freecam.class);
    private static final ModuleCache<Spectate> SPECTATE =
        Caches.getModule(Spectate.class);

    @Inject(
        method = "getVisibleFacings",
        at = @At("HEAD"),
        cancellable = true)
    public void getVisibleFacingsHook(
        CallbackInfoReturnable<Set<EnumFacing>> cir)
    {
        if (FREECAM.isEnabled() || SPECTATE.isEnabled())
        {
            cir.setReturnValue(EnumSet.allOf(EnumFacing.class));
        }
    }

    @Inject(
        method = "setOpaqueCube",
        at = @At("HEAD"),
        cancellable = true)
    public void setOpaqueCubeHook(BlockPos pos, CallbackInfo info)
    {
        if (XRAY.isEnabled() && XRAY.get().getMode() == XrayMode.Simple)
        {
            info.cancel();
        }
    }

}
