package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

/**
 * Fired when {@link Minecraft#displayGuiScreen(GuiScreen)}
 * is called. The Screen that's going to be closed can be
 * checked why {@link Minecraft#currentScreen}.
 *
 * @param <T> the type of screen that's gonna be displayed.
 */
public class GuiScreenEvent<T extends GuiScreen> extends Event
{
    private final T screen;

    public GuiScreenEvent(T screen)
    {
        this.screen = screen;
    }

    public T getScreen()
    {
        return screen;
    }

}
