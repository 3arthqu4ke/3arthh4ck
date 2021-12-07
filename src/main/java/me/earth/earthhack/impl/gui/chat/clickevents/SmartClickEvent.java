package me.earth.earthhack.impl.gui.chat.clickevents;

import net.minecraft.util.text.event.ClickEvent;

/**
 * A {@link ClickEvent}, that can return a different
 * values.
 */
public abstract class SmartClickEvent extends ClickEvent
{
    /**
     * Calls the SuperConstructor for the given action
     * and "$smart_click_value$".
     *
     * @param theAction the action.
     */
    public SmartClickEvent(Action theAction)
    {
        super(theAction, "$smart_click_value$");
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public abstract String getValue();

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof SmartClickEvent)
        {
            return super.equals(o);
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return super.hashCode() + 1;
    }

}
