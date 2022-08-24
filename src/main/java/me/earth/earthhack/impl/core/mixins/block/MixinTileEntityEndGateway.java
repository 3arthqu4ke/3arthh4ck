package me.earth.earthhack.impl.core.mixins.block;

import me.earth.earthhack.impl.Earthhack;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityEndPortal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityEndGateway.class)
public abstract class MixinTileEntityEndGateway extends TileEntityEndPortal
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    /**
     * Fixes a crash when entering the End on 2b2t.dev
     */
    @Redirect(
        method = "shouldRenderFace",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/tileentity/TileEntityEndGateway;getBlockType()Lnet/minecraft/block/Block;"))
    public Block shouldRenderFaceHook(TileEntityEndGateway tileEntityEndGateway)
    {
        Block block = tileEntityEndGateway.getBlockType();
        //noinspection ConstantConditions
        if (block == null)
        {
            if (this.world == null && MC.world != null)
            {
                this.setWorld(MC.world);
                block = MC.world.getBlockState(this.getPos()).getBlock();
                if (block == null)
                {
                    Earthhack.getLogger().warn("EndGateway still null!");
                    return Blocks.END_GATEWAY;
                }

                return block;
            }

            return Blocks.END_GATEWAY;
        }

        return block;
    }

}
