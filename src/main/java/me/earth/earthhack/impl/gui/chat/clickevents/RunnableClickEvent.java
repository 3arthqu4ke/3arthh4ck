package me.earth.earthhack.impl.gui.chat.clickevents;

import me.earth.earthhack.impl.core.ducks.util.IClickEvent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;

/**
 * Implementation of ClickEvent, that makes use of
 * {@link IClickEvent#getRunnable()} to make the {@link Style}
 * this is added to run the given Runnable when clicked.
 */
public class RunnableClickEvent extends ClickEvent
{
    /**
     * Calls the SuperConstructor for {@link Action#RUN_COMMAND}, and
     * $runnable$, then calls {@link IClickEvent#setRunnable(Runnable)}
     * for the given Runnable.
     *
     * @param runnable the runnable.
     */
    public RunnableClickEvent(Runnable runnable)
    {
        super(Action.RUN_COMMAND, "$runnable$");
        ((IClickEvent) this).setRunnable(runnable);
    }

}
