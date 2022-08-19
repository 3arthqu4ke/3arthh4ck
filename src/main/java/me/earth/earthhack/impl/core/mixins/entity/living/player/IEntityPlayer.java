package me.earth.earthhack.impl.core.mixins.entity.living.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPlayer.class)
public interface IEntityPlayer
{
    @Accessor("ABSORPTION")
    static DataParameter<Float> getAbsorption()
    {
        throw new IllegalStateException("ABSORPTION accessor wasn't shadowed.");
    }

    @Accessor("gameProfile")
    void setGameProfile(GameProfile gameProfile);

}
