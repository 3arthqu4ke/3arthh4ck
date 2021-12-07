package me.earth.earthhack.impl.event.events.audio;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.client.audio.ISound;

public class PlaySoundEvent extends Event
{
    private final ISound sound;

    public PlaySoundEvent(ISound sound)
    {
        this.sound = sound;
    }

    public ISound getSound()
    {
        return sound;
    }

}
