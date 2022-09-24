package me.earth.earthhack.impl.core.mixins.block;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.avoid.Avoid;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({
    Block.class,
    BlockAir.class,
    BlockFire.class,
    BlockCactus.class,
    BlockLiquid.class
})
public abstract class MixinAvoid {
    private static final ModuleCache<Avoid> AVOID =
        Caches.getModule(Avoid.class);
    private final Minecraft mc = Minecraft.getMinecraft();

    @Inject(
        method = "getCollisionBoundingBox",
        at = @At("HEAD"),
        cancellable = true)
    private void getCollisionBoundingBoxHook(IBlockState blockState,
                                             IBlockAccess worldIn,
                                             BlockPos pos,
                                             CallbackInfoReturnable<AxisAlignedBB> cir) {
        World world = mc.world;
        if (world != null
            && AVOID.isEnabled()
            && !(worldIn instanceof World && !((World) worldIn).isRemote)
            && AVOID.get().check(pos, world)) {
            // offset based on the BB instead?
            cir.setReturnValue(Block.FULL_BLOCK_AABB);
        }
    }

}
