package me.earth.earthhack.impl.gui.chat.util;

import me.earth.earthhack.impl.core.ducks.util.IHoverEvent;
import net.minecraft.util.text.event.HoverEvent;

public class ChatComponentUtil
{
    /**
     * Convenience function calling
     * {@link IHoverEvent#setOffset(boolean)}.
     */
    public static HoverEvent setOffset(HoverEvent event)
    {
        return ((IHoverEvent) event).setOffset(false);
    }

}
