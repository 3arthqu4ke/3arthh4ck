package me.earth.earthhack.impl.core.mixins.network.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.login.client.CPacketLoginStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CPacketLoginStart.class)
public interface ICPacketLoginStart {
    @Accessor("profile")
    void setProfile(GameProfile profile);

}
