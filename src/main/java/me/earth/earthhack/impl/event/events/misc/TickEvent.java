package me.earth.earthhack.impl.event.events.misc;

import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.client.Minecraft;

/**
 * An event, fired everytime {@link Minecraft#runTick()} is called,
 * after the rightClickDelayTimer got updated.
 */
public class TickEvent implements Globals
{
    /**
     * Checks if <tt>{@link Minecraft#world} != null</tt>
     * and <tt>{@link Minecraft#player} != null</tt> (at the
     * timer this event was posted!).
     *
     * @return <tt>true</tt> if mc.player != null && mc.world != null.
     */
    public boolean isSafe()
    {
        return mc.player != null && mc.world != null;
    }

    public static final class PostWorldTick extends TickEvent { }

    /** Fired at the End of a tick. */
    public static final class Post extends TickEvent { }

}
