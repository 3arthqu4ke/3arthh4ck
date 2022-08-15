package me.earth.earthhack.impl.core.mixins.network.client;

import me.earth.earthhack.impl.core.ducks.network.ICPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CPacketPlayerDigging.class)
public abstract class MixinCPacketPlayerDigging implements ICPacketPlayerDigging
{
    @Unique
    private boolean clientSideBreaking;
    @Unique
    private boolean normalDigging;

    @Unique
    @Override
    public void setClientSideBreaking(boolean breaking)
    {
        clientSideBreaking = breaking;
    }

    @Unique
    @Override
    public boolean isClientSideBreaking()
    {
        return clientSideBreaking;
    }

    @Unique
    @Override
    public void setNormalDigging(boolean normalDigging)
    {
        this.normalDigging = normalDigging;
    }

    @Unique
    @Override
    public boolean isNormalDigging()
    {
        return normalDigging;
    }

}
