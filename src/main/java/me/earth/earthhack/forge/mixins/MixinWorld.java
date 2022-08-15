package me.earth.earthhack.forge.mixins;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.world.IChunk;
import me.earth.earthhack.impl.event.events.misc.BlockStateChangeEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.BlockSnapshot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(World.class)
public abstract class MixinWorld
{
    @Shadow
    @Final
    public boolean isRemote;

    @Inject(
        method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/state/IBlockState;getLightOpacity(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)I",
            shift = At.Shift.BEFORE,
            ordinal = 1,
            remap = false),
        locals = LocalCapture.CAPTURE_FAILHARD)
    private void onSetBlockState(
            BlockPos pos, IBlockState newState, int flags,
            CallbackInfoReturnable<Boolean> cir, Chunk chunk,
            BlockSnapshot blockSnapshot, IBlockState oldState,
            int oldLight, int oldOpacity, IBlockState iblockstate)
    {
        if (isRemote)
        {
            BlockStateChangeEvent event = new BlockStateChangeEvent(pos, newState, (IChunk) chunk);
            Bus.EVENT_BUS.post(event);
        }
    }

}
