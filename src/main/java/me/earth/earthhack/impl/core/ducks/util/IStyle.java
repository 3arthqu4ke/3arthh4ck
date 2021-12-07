package me.earth.earthhack.impl.core.ducks.util;

import net.minecraft.util.text.event.ClickEvent;

import java.util.function.Supplier;

/**
 * Duck interface for {@link net.minecraft.util.text.Style}.
 */
public interface IStyle
{
    void setRightClickEvent(ClickEvent event);

    void setMiddleClickEvent(ClickEvent event);

    ClickEvent getRightClickEvent();

    ClickEvent getMiddleClickEvent();

    void setSuppliedInsertion(Supplier<String> insertion);

    void setRightInsertion(Supplier<String> rightInsertion);

    void setMiddleInsertion(Supplier<String> middleInsertion);

    String getRightInsertion();

    String getMiddleInsertion();

}
