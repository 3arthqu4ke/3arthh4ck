package me.earth.earthhack.impl.core.mixins.audio;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.audio.PlaySoundEvent;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public abstract class MixinSoundManager
{
    @Shadow private boolean loaded;

    @Inject(method = "playSound", at = @At("HEAD"), cancellable = true)
    public void playSoundHook(ISound p_sound, CallbackInfo ci)
    {
        if (this.loaded)
        {
            PlaySoundEvent event = new PlaySoundEvent(p_sound);
            Bus.EVENT_BUS.post(event);
            if (event.isCancelled())
            {
                ci.cancel();
            }
        }
    }

}
