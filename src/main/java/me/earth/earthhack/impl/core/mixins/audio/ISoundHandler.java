package me.earth.earthhack.impl.core.mixins.audio;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SoundHandler.class)
public interface ISoundHandler
{
    @Accessor("sndManager")
    SoundManager getManager();

    @Accessor("soundRegistry")
    SoundRegistry getRegistry();

}
