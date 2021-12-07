package me.earth.earthhack.impl.core.mixins.network.server;

import net.minecraft.network.play.server.SPacketPlayerPosLook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketPlayerPosLook.class)
public interface ISPacketPlayerPosLook
{
    @Accessor(value = "teleportId")
    int getTeleportId();

    @Accessor(value = "x")
    double getX();

    @Accessor(value = "y")
    double getY();

    @Accessor(value = "z")
    double getZ();

    @Accessor(value = "yaw")
    void setYaw(float yaw);

    @Accessor(value = "pitch")
    void setPitch(float pitch);

}
