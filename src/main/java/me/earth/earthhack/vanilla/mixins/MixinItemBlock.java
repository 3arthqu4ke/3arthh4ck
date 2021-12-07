package me.earth.earthhack.vanilla.mixins;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.noglitchblocks.NoGlitchBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemBlock.class)
public abstract class MixinItemBlock
{
    @Shadow
    @Final
    protected Block block;

    private static final ModuleCache<NoGlitchBlocks> NO_GLITCH_BLOCKS =
            Caches.getModule(NoGlitchBlocks.class);

    @SuppressWarnings({"UnresolvedMixinReference", "deprecation"})
    @Inject(
        method = "onItemUse",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    private void setBlockStateHook(EntityPlayer player,
                                   World worldIn,
                                   BlockPos pos,
                                   EnumHand hand,
                                   EnumFacing facing,
                                   float hitX,
                                   float hitY,
                                   float hitZ,
                                   CallbackInfoReturnable<EnumActionResult> cir,
                                   ItemStack itemStack_1)
    {
        if (worldIn.isRemote && NO_GLITCH_BLOCKS.returnIfPresent(
                                            NoGlitchBlocks::noPlace, false))
        {
            SoundType soundtype = this.block.getSoundType();
            worldIn.playSound(player,
                              pos,
                              soundtype.getPlaceSound(),
                              SoundCategory.BLOCKS,
                              (soundtype.getVolume() + 1.0F) / 2.0F,
                              soundtype.getPitch() * 0.8F);
            itemStack_1.shrink(1);
            cir.setReturnValue(EnumActionResult.SUCCESS);
        }
    }

}
