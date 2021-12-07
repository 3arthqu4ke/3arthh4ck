package me.earth.earthhack.impl.core.mixins.network.server;

import net.minecraft.network.play.server.SPacketEntityVelocity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketEntityVelocity.class)
public interface ISPacketEntityVelocity
{
    @Accessor(value = "entityID")
    int getEntityID();

    @Accessor(value = "motionX")
    int getX();

    @Accessor(value = "motionX")
    void setX(int motionX);

    @Accessor(value = "motionY")
    int getY();

    @Accessor(value = "motionY")
    void setY(int motionY);

    @Accessor(value = "motionZ")
    int getZ();

    @Accessor(value = "motionZ")
    void setZ(int motionZ);

}
