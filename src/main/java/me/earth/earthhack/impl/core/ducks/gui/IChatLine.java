package me.earth.earthhack.impl.core.ducks.gui;

import net.minecraft.util.text.ITextComponent;

public interface IChatLine
{
    String getTimeStamp();

    void setComponent(ITextComponent component);
}
