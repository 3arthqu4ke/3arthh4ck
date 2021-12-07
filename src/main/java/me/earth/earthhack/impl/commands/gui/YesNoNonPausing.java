package me.earth.earthhack.impl.commands.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;

/**
 * {@link GuiYesNo}, but {@link GuiScreen#doesGuiPauseGame()} returns
 * <tt>false</tt>.
 */
@SuppressWarnings("unused")
public class YesNoNonPausing extends GuiYesNo
{
    /**
     *  Calls super constructor
     *  {@link GuiYesNo#GuiYesNo(GuiYesNoCallback, String, String, int)}.
     */
    public YesNoNonPausing(GuiYesNoCallback parentScreenIn,
                           String messageLine1In,
                           String messageLine2In,
                           int parentButtonClickedIdIn)
    {
        super(parentScreenIn,
                messageLine1In,
                messageLine2In,
                parentButtonClickedIdIn);
    }

    /**
     *  Calls super constructor
     *  {@link GuiYesNo#GuiYesNo(GuiYesNoCallback, String, String, String, String, int)}.
     */
    public YesNoNonPausing(GuiYesNoCallback parentScreenIn,
                           String messageLine1In,
                           String messageLine2In,
                           String confirmButtonTextIn,
                           String cancelButtonTextIn,
                           int parentButtonClickedIdIn)
    {
        super(parentScreenIn,
                messageLine1In,
                messageLine2In,
                confirmButtonTextIn,
                cancelButtonTextIn,
                parentButtonClickedIdIn);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

}
