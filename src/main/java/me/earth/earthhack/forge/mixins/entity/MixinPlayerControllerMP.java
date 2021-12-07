package me.earth.earthhack.forge.mixins.entity;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.misc.BlockDestroyEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP
{
    /**
     * target = {@link Block#removedByPlayer(IBlockState,
     * World, BlockPos, EntityPlayer, boolean)}
     */
    @Dynamic
    @Inject(
        method = "onPlayerDestroyBlock",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/block/Block.removedByPlayer" +
                    "(Lnet/minecraft/block/state/IBlockState;" +
                     "Lnet/minecraft/world/World;" +
                     "Lnet/minecraft/util/math/BlockPos;" +
                     "Lnet/minecraft/entity/player/EntityPlayer;Z)Z",
            remap = false),
        cancellable = true)
    private void onPlayerDestroyBlockHook(BlockPos pos,
                                          CallbackInfoReturnable<Boolean> info)
    {
        BlockDestroyEvent event = new BlockDestroyEvent(Stage.PRE, pos);
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled())
        {
            info.setReturnValue(false);
        }
    }

    @Inject(
        method = "onPlayerDestroyBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;onPlayerDestroy(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V",
            shift = At.Shift.BEFORE))
    private void onPlayerDestroyHook(BlockPos pos,
                                     CallbackInfoReturnable<Boolean> cir)
    {
        Bus.EVENT_BUS.post(new BlockDestroyEvent(Stage.POST, pos));
    }

}
