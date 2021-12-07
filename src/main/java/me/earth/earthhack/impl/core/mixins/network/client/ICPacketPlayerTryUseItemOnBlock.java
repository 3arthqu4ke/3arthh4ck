package me.earth.earthhack.impl.core.mixins.network.client;

import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CPacketPlayerTryUseItemOnBlock.class)
public interface ICPacketPlayerTryUseItemOnBlock
{
    @Accessor(value = "placedBlockDirection")
    void setFacing(EnumFacing facing);

    @Accessor(value = "hand")
    void setHand(EnumHand hand);

}
