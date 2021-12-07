package me.earth.earthhack.impl.core.mixins.network.server;

import net.minecraft.network.play.server.SPacketExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketExplosion.class)
public interface ISPacketExplosion
{

    @Accessor(value = "motionX")
    void setX(float x);

    @Accessor(value = "motionY")
    void setY(float y);

    @Accessor(value = "motionZ")
    void setZ(float z);

    @Accessor(value = "motionX")
    float getX();

    @Accessor(value = "motionY")
    float getY();

    @Accessor(value = "motionZ")
    float getZ();

}
